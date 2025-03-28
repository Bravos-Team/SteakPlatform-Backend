package com.bravos.steak.account.service.impl;

import com.bravos.steak.account.exception.AccountAlreadyExistsException;
import com.bravos.steak.account.model.request.RegistrationRequest;
import com.bravos.steak.account.service.AccountService;
import com.bravos.steak.account.service.RegistrationService;
import com.bravos.steak.common.service.email.EmailService;
import com.bravos.steak.common.service.email.EmailTemplate;
import com.bravos.steak.common.service.encryption.AesEncryptionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final AccountService accountService;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;
    private final AesEncryptionService aesEncryptionService;

    @Autowired
    public RegistrationServiceImpl(AccountService accountService, EmailService emailService, ObjectMapper objectMapper, PasswordEncoder passwordEncoder, RedisTemplate<String, Object> redisTemplate, AesEncryptionService aesEncryptionService) {
        this.accountService = accountService;
        this.emailService = emailService;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
        this.redisTemplate = redisTemplate;
        this.aesEncryptionService = aesEncryptionService;
    }

    @Override
    public String preRegisterAccount(RegistrationRequest registrationRequest) throws
            AccountAlreadyExistsException{

        if(accountService.isExistByUsernameEmail(registrationRequest.getUsername(),registrationRequest.getEmail())) {
            throw new AccountAlreadyExistsException("Email or username already exists");
        }

        registrationRequest.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

        String verificationLink = generateVerificationLink(registrationRequest);

        emailService.sendEmailUsingTemplate(
                registrationRequest.getEmail(),
                "Email verification",
                EmailTemplate.VERIFICATE_EMAIL,
                new JSONObject().put("verification_link",verificationLink).toMap()
        );

        return registrationRequest.getEmail();

    }

    private String generateVerificationLink(RegistrationRequest registrationRequest) {
        String token = UUID.randomUUID().toString();
        try {
            String jsonData = objectMapper.writeValueAsString(registrationRequest);
            String encryptedData = aesEncryptionService.encrypt(jsonData,System.getProperty("SECRET_KEY"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return "http://localhost:8888/verify-email?token=" + token;
    }

}
