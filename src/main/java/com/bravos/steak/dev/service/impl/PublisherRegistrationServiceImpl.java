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

    private final PublisherRole MASTER_ROLE = new PublisherRole(0L);

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
    public String preRegisterPublisher(PublisherRegistrationRequest publisherRegistrationRequest) {

        String masterUsername = publisherRegistrationRequest.getMasterUsername();
        String masterEmail = publisherRegistrationRequest.getMasterEmail();
        String masterPassword = publisherRegistrationRequest.getMasterPassword();
        String businessName = publisherRegistrationRequest.getName();
        String businessEmail = publisherRegistrationRequest.getBusinessEmail();

        if(publisherAccountRepository.existsByEmailOrUsername(masterEmail,masterUsername)) {
            throw new ConflictDataException("Master username or master email is already used");
        }
        if(publisherRepository.existsByNameOrEmail(businessName,businessEmail)) {
            throw new ConflictDataException("Business name or business email is already existed");
        }

        publisherRegistrationRequest.setMasterPassword(passwordEncoder.encode(masterPassword));

        String token = UUID.randomUUID().toString();
        String encryptedData;
        try {
            encryptedData = encryptionService.aesEncrypt(objectMapper.writeValueAsString(publisherRegistrationRequest));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error when handling data");
        }

        try {
            redisService.save("p-register:" + token, encryptedData,15, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("Error when handling data");
        }

        return this.sendVerificationEmail(token,masterEmail);

    }

    @Override
    public String sendVerificationEmail(String token, String email) {
        String domain = System.getProperty("DOMAIN");
        String verificationUrl = domain + "/" + "verification/create-publisher/" + token;
        EmailPayload emailPayload = EmailPayload.builder()
                .to(email)
                .subject("Email Verification")
                .templateID(EmailTemplate.VERIFICATE_EMAIL)
                .param("verification_link",verificationUrl)
                .build();
        emailService.sendEmailUsingTemplate(emailPayload);
        return token;
    }

    @Transactional
    @Override
    public void postRegisterPublisher(String token) {
        String key = "p-register:" + token;
        PublisherRegistrationRequest publisherRegistrationRequest = redisService.get(key, PublisherRegistrationRequest.class);

        if(publisherRegistrationRequest == null) {
            throw new IllegalArgumentException("Token is invalid or expired");
        }

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

        Long id = snowflakeGenerator.generateId();

        Publisher publisher = Publisher.builder()
                .id(id)
                .name(businessName)
                .email(businessEmail)
                .phone(publisherRegistrationRequest.getPhone())
                .build();

        try {
            publisher = publisherRepository.save(publisher);
        } catch (Exception e) {
            throw new RuntimeException("Error when save publisher");
        }

        PublisherAccount publisherAccount = PublisherAccount.builder()
                .id(id)
                .username(masterUsername)
                .email(masterEmail)
                .password(publisherRegistrationRequest.getMasterPassword())
                .publisher(publisher)
                .roles(Set.of(MASTER_ROLE))
                .build();

        try {
            publisherAccountRepository.save(publisherAccount);
        } catch (Exception e) {
            throw new RuntimeException("Error when save publisher");
        }

        log.info("Created new publisher with id: {}", id);
    }

}
