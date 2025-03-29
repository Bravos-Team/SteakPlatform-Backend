package com.bravos.steak.account.service.impl;

import com.bravos.steak.account.entity.Account;
import com.bravos.steak.account.model.request.RegistrationRequest;
import com.bravos.steak.account.repo.AccountRepository;
import com.bravos.steak.account.service.AccountService;
import com.bravos.steak.account.service.RegistrationService;
import com.bravos.steak.common.service.email.EmailService;
import com.bravos.steak.common.service.email.EmailTemplate;
import com.bravos.steak.common.service.encryption.EncryptionService;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.exceptions.AccountAlreadyExistsException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final RedisTemplate<String, Object> redisTemplate;
    private final EncryptionService encryptionService;
    private final SnowflakeGenerator accountIdGenerator;
    private final AccountRepository accountRepository;

    @Override
    public String preRegisterAccount(RegistrationRequest registrationRequest) throws
            AccountAlreadyExistsException {

        if (accountService.isExistByUsernameEmail(registrationRequest.getUsername(), registrationRequest.getEmail())) {
            throw new AccountAlreadyExistsException("Email or username already exists");
        }

        registrationRequest.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

        String verificationLink = generateVerificationLink(registrationRequest);

        emailService.sendEmailUsingTemplate(
                registrationRequest.getEmail(),
                "Email Verification",
                EmailTemplate.VERIFICATE_EMAIL,
                new JSONObject().put("verification_link", verificationLink).toMap()
        );

        return registrationRequest.getEmail();

    }

    @Override
    public boolean verificateRegisterAccount(String token) {
        String key = "register:" + token;
        RegistrationRequest registerRequest;
        String encrypyedRegisterRequestData = (String) redisTemplate.opsForValue().get(key);
        String decryptedRegisterRequestData;

        if (encrypyedRegisterRequestData == null) {
            return false;
        }

        try {
            decryptedRegisterRequestData = encryptionService.aesDecrypt(encrypyedRegisterRequestData);
        } catch (Exception e) {
            log.error("Failed when decrypted register request {}", e.getMessage());
            return false;
        }

        try {
            registerRequest = objectMapper.readValue(decryptedRegisterRequestData, RegistrationRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (accountService.isExistByUsernameEmail(registerRequest.getUsername(), registerRequest.getEmail())) {
            redisTemplate.opsForValue().getAndDelete(key);
            return false;
        }

        try {
            accountRepository.save(
                    Account.builder()
                            .id(accountIdGenerator.generateId())
                            .username(registerRequest.getUsername())
                            .password(registerRequest.getPassword())
                            .email(registerRequest.getEmail())
                            .build());
            redisTemplate.opsForValue().getAndDelete(key);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    private String generateVerificationLink(RegistrationRequest registrationRequest) {
        String token = UUID.randomUUID().toString().toLowerCase();
        try {
            String jsonData = objectMapper.writeValueAsString(registrationRequest);
            String encryptedData = encryptionService.aesEncrypt(jsonData);
            redisTemplate.opsForValue().set("register:" + token, encryptedData, 30, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return "http://localhost:8888/verificate/" + token;
    }

}
