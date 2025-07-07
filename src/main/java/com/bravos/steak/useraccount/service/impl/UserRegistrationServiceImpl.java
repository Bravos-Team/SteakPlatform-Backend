package com.bravos.steak.useraccount.service.impl;

import com.bravos.steak.common.model.EmailPayload;
import com.bravos.steak.common.model.EmailTemplate;
import com.bravos.steak.common.service.email.EmailService;
import com.bravos.steak.common.service.encryption.EncryptionService;
import com.bravos.steak.common.service.redis.RedisService;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.exceptions.BadRequestException;
import com.bravos.steak.exceptions.ConflictDataException;
import com.bravos.steak.useraccount.entity.UserAccount;
import com.bravos.steak.useraccount.entity.UserProfile;
import com.bravos.steak.useraccount.model.request.UserRegistrationRequest;
import com.bravos.steak.useraccount.repo.UserAccountRepository;
import com.bravos.steak.useraccount.repo.UserProfileRepository;
import com.bravos.steak.useraccount.service.UserAccountService;
import com.bravos.steak.useraccount.service.UserRegistrationService;
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
public class UserRegistrationServiceImpl implements UserRegistrationService {

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
    public void preRegisterAccount(UserRegistrationRequest userRegistrationRequest) {
        validatePreRequest(userRegistrationRequest);
        String encryptedData = encryptedRegisterRequest(userRegistrationRequest);
        String token = saveDataToRedis(encryptedData);
        this.sendVerificationEmail(token,userRegistrationRequest.getEmail());
    }

    private void validatePreRequest(UserRegistrationRequest userRegistrationRequest) {
        boolean check;
        try {
            check = userAccountService.isExistByUsernameEmail(userRegistrationRequest.getUsername(), userRegistrationRequest.getEmail());
        } catch (Exception e) {
            log.error("Error when check data", e);
            throw new RuntimeException("Error when check data");
        }
        if (check) {
            throw new ConflictDataException("Email or username already exists");
        }
    }

    private String encryptedRegisterRequest(UserRegistrationRequest userRegistrationRequest) {
        userRegistrationRequest.setPassword(passwordEncoder.encode(userRegistrationRequest.getPassword()));

        String encryptedData;
        try {
            encryptedData = encryptionService.aesEncrypt(objectMapper.writeValueAsString(userRegistrationRequest));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error when handling data");
        }

        return encryptedData;
    }

    private String saveDataToRedis(String encryptedData) {
        String token = UUID.randomUUID().toString();
        try {
            redisService.save("user-register:" + token, encryptedData, 15, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("Error when handling data");
        }
        return token;
    }

    private void sendVerificationEmail(String token, String email) {
        String verificationUrl = System.getProperty("DOMAIN") + "/" + "verificate/user/register/" + token;
        EmailPayload emailPayload = EmailPayload.builder()
                .to(email)
                .subject("Email Verification")
                .templateID(EmailTemplate.VERIFICATE_EMAIL)
                .param("verification_link",verificationUrl)
                .build();
        emailService.sendEmailUsingTemplate(emailPayload);
    }

    @Override
    public void postRegisterAccount(String token) {
        String key = "user-register:" + token;
        try {
            UserRegistrationRequest registrationRequest = getRegistrationRequestFromRedis(key);
            validatePostRequst(key,registrationRequest);
            saveUserAccount(registrationRequest);
        } catch (Exception e) {
            log.error("Registration failed: {}",e.getMessage());
            throw new RuntimeException("Registration failed " + e.getMessage());
        }
        redisService.delete(key);
    }

    private void validatePostRequst(String key, UserRegistrationRequest registrationRequest) {
        boolean check;
        try {
            check = userAccountService.isExistByUsernameEmail(registrationRequest.getUsername(), registrationRequest.getEmail());
        } catch (Exception e) {
            throw new RuntimeException("Error when checking data");
        }

        if (check) {
            redisService.delete(key);
            throw new ConflictDataException("Username or email exists");
        }
    }

    private void saveUserAccount(UserRegistrationRequest registrationRequest) {

        UserAccount account;
        try {
            account = userAccountRepository.save(
                    UserAccount.builder()
                            .id(snowflakeGenerator.generateId())
                            .username(registrationRequest.getUsername())
                            .password(registrationRequest.getPassword())
                            .email(registrationRequest.getEmail())
                            .build());
        } catch (Exception e) {
            log.error("Error when trying to save user account", e);
            throw new RuntimeException("Error when trying to save data");
        }

        try {
            UserProfile userProfile = new UserProfile();
            userProfile.setId(account.getId());
            userProfile.setDisplayName(account.getUsername());
            userProfileRepository.save(userProfile);
        } catch (Exception e) {
            userAccountRepository.deleteById(account.getId());
            log.error("Error when trying to save user profile", e);
            throw new RuntimeException("Error when trying to save data");
        }

    }

    private UserRegistrationRequest getRegistrationRequestFromRedis(String key) {
        String encrypyedData = redisService.get(key, String.class);
        String decryptedData;

        if (encrypyedData == null) {
            throw new BadRequestException("Token is invalid or expired");
        }

        try {
            decryptedData = encryptionService.aesDecrypt(encrypyedData);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException("Error when handling data");
        }

        try {
            return objectMapper.readValue(decryptedData, UserRegistrationRequest.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error when converting data");
        }
    }

}
