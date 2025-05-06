package com.bravos.steak.useraccount.service.impl;

import com.bravos.steak.useraccount.entity.UserAccount;
import com.bravos.steak.useraccount.entity.UserProfile;
import com.bravos.steak.useraccount.model.request.RegistrationRequest;
import com.bravos.steak.useraccount.repo.UserAccountRepository;
import com.bravos.steak.useraccount.repo.UserProfileRepository;
import com.bravos.steak.useraccount.service.UserAccountService;
import com.bravos.steak.useraccount.service.RegistrationService;
import com.bravos.steak.common.model.EmailPayload;
import com.bravos.steak.common.service.email.EmailService;
import com.bravos.steak.common.model.EmailTemplate;
import com.bravos.steak.common.service.encryption.EncryptionService;
import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.exceptions.ConflictDataException;
import com.bravos.steak.exceptions.BadRequestException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@AllArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserAccountService userAccountService;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionService encryptionService;
    private final SnowflakeGenerator snowflakeGenerator;
    private final UserAccountRepository userAccountRepository;
    private final RedisService redisService;
    private final UserProfileRepository userProfileRepository;

    @Override
    public String preRegisterAccount(RegistrationRequest registrationRequest) {

        if (userAccountService.isExistByUsernameEmail(registrationRequest.getUsername(), registrationRequest.getEmail())) {
            throw new ConflictDataException("Email or username already exists");
        }

        registrationRequest.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

        String verificationLink = generateVerificationLink(registrationRequest);

        EmailPayload emailPayload = EmailPayload.builder()
                .from("no-reply@steak.io.vn")
                .to(registrationRequest.getEmail())
                .subject("Email Verification")
                .templateID(EmailTemplate.VERIFICATE_EMAIL)
                .param("verification_link",verificationLink)
                .build();

        emailService.sendEmailUsingTemplate(emailPayload);

        return registrationRequest.getEmail();

    }

    @Override
    public void verificateRegisterAccount(String token) {

        String key = "register:" + token;
        RegistrationRequest registerRequest;
        String encrypyedRegisterRequestData = redisService.get(key, String.class); // encrypted data
        String decryptedRegisterRequestData; // json

        if (encrypyedRegisterRequestData == null) {
            throw new BadRequestException("Token is invalid or expired");
        }

        try {
            decryptedRegisterRequestData = encryptionService.aesDecrypt(encrypyedRegisterRequestData);
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new BadRequestException("Token is invalid or expired");
        }

        try {
            registerRequest = objectMapper.readValue(decryptedRegisterRequestData, RegistrationRequest.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when converting data");
        }

        try {
            if (userAccountService.isExistByUsernameEmail(registerRequest.getUsername(), registerRequest.getEmail())) {
                redisService.delete(key);
                throw new ConflictDataException("Username or email exists");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when trying to check username and email");
        }

        try {

            UserAccount account = userAccountRepository.save(
                    UserAccount.builder()
                            .id(snowflakeGenerator.generateId())
                            .username(registerRequest.getUsername())
                            .password(registerRequest.getPassword())
                            .email(registerRequest.getEmail())
                            .build());

            UserProfile userProfile = new UserProfile();
            userProfile.setId(account.getId());
            userProfile.setDisplayName(account.getUsername());
            userProfileRepository.save(userProfile);
            redisService.delete(key);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when trying to save data");
        }

    }

    private String generateVerificationLink(RegistrationRequest registrationRequest) {
        String token = UUID.randomUUID().toString().toLowerCase();
        String jsonData;
        String encryptedData;
        try {
            jsonData = objectMapper.writeValueAsString(registrationRequest);
            encryptedData = encryptionService.aesEncrypt(jsonData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error when creating verification URL");
        }
        redisService.save("register:" + token, encryptedData, 30, TimeUnit.MINUTES);
        return System.getProperty("DOMAIN") + "/verificate/" + token;
    }

}
