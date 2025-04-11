package com.bravos.steak.account.service.impl;

import com.bravos.steak.account.entity.Account;
import com.bravos.steak.account.model.request.RegistrationRequest;
import com.bravos.steak.account.repo.AccountRepository;
import com.bravos.steak.account.service.AccountService;
import com.bravos.steak.account.service.RegistrationService;
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

    private final AccountService accountService;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionService encryptionService;
    private final SnowflakeGenerator accountIdGenerator;
    private final AccountRepository accountRepository;
    private final RedisService redisService;

    @Override
    public String preRegisterAccount(RegistrationRequest registrationRequest) {

        if (accountService.isExistByUsernameEmail(registrationRequest.getUsername(), registrationRequest.getEmail())) {
            throw new ConflictDataException("Email or username already exists");
        }

        registrationRequest.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

        String verificationLink = generateVerificationLink(registrationRequest);

        EmailPayload emailPayload = EmailPayload.builder()
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
            if (accountService.isExistByUsernameEmail(registerRequest.getUsername(), registerRequest.getEmail())) {
                redisService.delete(key);
                throw new ConflictDataException("Username or email exists");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when trying to check username and email");
        }

        try {
            accountRepository.save(
                    Account.builder()
                            .id(accountIdGenerator.generateId())
                            .username(registerRequest.getUsername())
                            .password(registerRequest.getPassword())
                            .email(registerRequest.getEmail())
                            .build());
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
        return "http://localhost:8888/verificate/" + token;
    }

}
