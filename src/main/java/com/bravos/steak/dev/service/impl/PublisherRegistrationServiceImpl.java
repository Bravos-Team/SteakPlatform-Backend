package com.bravos.steak.dev.service.impl;

import com.bravos.steak.common.model.EmailPayload;
import com.bravos.steak.common.model.EmailTemplate;
import com.bravos.steak.common.service.email.EmailService;
import com.bravos.steak.common.service.encryption.EncryptionService;
import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.dev.entity.Publisher;
import com.bravos.steak.dev.entity.PublisherAccount;
import com.bravos.steak.dev.entity.PublisherRole;
import com.bravos.steak.dev.model.request.PublisherRegistrationRequest;
import com.bravos.steak.dev.repo.PublisherAccountRepository;
import com.bravos.steak.dev.repo.PublisherRepository;
import com.bravos.steak.dev.service.PublisherRegistrationService;
import com.bravos.steak.exceptions.BadRequestException;
import com.bravos.steak.exceptions.ConflictDataException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PublisherRegistrationServiceImpl implements PublisherRegistrationService {

    private final PublisherAccountRepository publisherAccountRepository;
    private final PublisherRepository publisherRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final EncryptionService encryptionService;
    private final ObjectMapper objectMapper;
    private final EmailService emailService;
    private final SnowflakeGenerator snowflakeGenerator;

    private final PublisherRole MASTER_ROLE = new PublisherRole(9056664623308800L);

    @Autowired
    public PublisherRegistrationServiceImpl(PublisherAccountRepository publisherAccountRepository, PublisherRepository publisherRepository,
                                            PasswordEncoder passwordEncoder, RedisService redisService, EncryptionService encryptionService,
                                            ObjectMapper objectMapper, EmailService emailService, SnowflakeGenerator snowflakeGenerator) {
        this.publisherAccountRepository = publisherAccountRepository;
        this.publisherRepository = publisherRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisService = redisService;
        this.encryptionService = encryptionService;
        this.objectMapper = objectMapper;
        this.emailService = emailService;
        this.snowflakeGenerator = snowflakeGenerator;

    }

    @Override
    public void preRegisterPublisher(PublisherRegistrationRequest publisherRegistrationRequest) {
        validateData(publisherRegistrationRequest);
        String encryptedData = encryptRegisterRequest(publisherRegistrationRequest);
        String token = saveRegistrationRequestToRedis(encryptedData);
        sendVerificationEmail(token,publisherRegistrationRequest.getMasterEmail());
    }

    private String encryptRegisterRequest(PublisherRegistrationRequest publisherRegistrationRequest) {
        publisherRegistrationRequest.setMasterPassword(passwordEncoder.encode(publisherRegistrationRequest.getMasterPassword()));
        String encryptedData;
        try {
            encryptedData = encryptionService.aesEncrypt(objectMapper.writeValueAsString(publisherRegistrationRequest));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when handling data");
        }
        return encryptedData;
    }

    private String saveRegistrationRequestToRedis(String encryptedData) {
        String token = UUID.randomUUID().toString();
        try {
            redisService.save("p-register:" + token, encryptedData,15, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when handling data");
        }
        return token;
    }

    private void validateData(PublisherRegistrationRequest publisherRegistrationRequest) {
        String masterUsername = publisherRegistrationRequest.getMasterUsername();
        String masterEmail = publisherRegistrationRequest.getMasterEmail();
        String businessName = publisherRegistrationRequest.getName();
        String businessEmail = publisherRegistrationRequest.getBusinessEmail();

        if(publisherAccountRepository.existsByEmailOrUsername(masterEmail,masterUsername)) {
            throw new ConflictDataException("Master username or master email is already used");
        }
        if(publisherRepository.existsByNameOrEmail(businessName,businessEmail)) {
            throw new ConflictDataException("Business name or business email is already existed");
        }
    }

    private void sendVerificationEmail(String token, String email) {
        String verificationUrl = System.getProperty("DOMAIN") + "/" + "verificate/dev/register/" + token;
        EmailPayload emailPayload = EmailPayload.builder()
                .to(email)
                .subject("Email Verification")
                .templateID(EmailTemplate.VERIFICATE_EMAIL)
                .param("verification_link",verificationUrl)
                .build();
        emailService.sendEmail(emailPayload);
    }

    @Transactional
    @Override
    public void postRegisterPublisher(String token) {

        Publisher publisher;
        try {
            PublisherRegistrationRequest request = getRegistrationRequestFromRedis(token);
            validateData(request);

            publisher = createPublisher(request);
            createMasterAccount(request, publisher);
        } catch (Exception e) {
            log.error("Registration failed: {}",e.getMessage());
            throw new RuntimeException("Registration failed");
        }

        redisService.delete("p-register:" + token);
        log.info("Created new publisher with id: {}", publisher.getId());

    }

    private PublisherRegistrationRequest getRegistrationRequestFromRedis(String token) {
        String key = "p-register:" + token;

        String encryptedData;
        try {
            encryptedData = redisService.get(key, String.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when handling data");
        }

        if (encryptedData == null) {
            throw new BadRequestException("Token is invalid or expired");
        }

        String decryptedData = encryptionService.aesDecrypt(encryptedData);
        try {
            return objectMapper.readValue(decryptedData, PublisherRegistrationRequest.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when parsing registration data");
        }

    }

    private Publisher createPublisher(PublisherRegistrationRequest request) {
        Publisher publisher = Publisher.builder()
                .id(snowflakeGenerator.generateId())
                .name(request.getName())
                .email(request.getBusinessEmail())
                .phone(request.getPhone())
                .build();

        try {
            return publisherRepository.save(publisher);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Failed to save publisher");
        }
    }

    private void createMasterAccount(PublisherRegistrationRequest request, Publisher publisher) {
        PublisherAccount account = PublisherAccount.builder()
                .id(publisher.getId())
                .username(request.getMasterUsername())
                .email(request.getMasterEmail())
                .password(request.getMasterPassword())
                .publisher(publisher)
                .roles(Set.of(MASTER_ROLE))
                .build();

        try {
            publisherAccountRepository.save(account);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Failed to save master account");
        }
    }


}
