package com.bravos.steak.common.service.encryption.impl;

import com.bravos.steak.common.model.GeneralKeyPair;
import com.bravos.steak.common.security.JwtTokenClaims;
import com.bravos.steak.common.service.encryption.JwtService;
import com.bravos.steak.common.service.encryption.RSAService;
import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.exceptions.UnauthorizeException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    private final ObjectMapper objectMapper;
    private final RSAService rSAService;

    private final String header;
    private final GeneralKeyPair generalKeyPair;

    public JwtServiceImpl(ObjectMapper objectMapper,
                          RSAService rSAService, GeneralKeyPair generalKeyPair)
            throws IOException {

        this.objectMapper = objectMapper;
        this.rSAService = rSAService;

        this.header = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(objectMapper.writeValueAsBytes(Map.of(
                        "alg","RS256",
                        "typ", "JWT"
                ))) + ".";
        this.generalKeyPair = generalKeyPair;
    }

    @Override
    public String generateToken(JwtTokenClaims jwtTokenClaims) {
        try {
            StringBuilder token = new StringBuilder(1024);

            String headerPayload = header + Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(objectMapper.writeValueAsBytes(jwtTokenClaims.toMap()));

            token
                    .append(headerPayload)
                    .append(".")
                    .append(rSAService.getSignatureData(headerPayload,generalKeyPair.getPrivateKey()));

            return token.toString();

        } catch (Exception e) {
            log.error("Failed to generate JWT: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate JWT: " + e.getMessage());
        }
    }

    @Override
    public JwtTokenClaims getClaims(String token) {
        String[] parts = token.split("\\.");

        if(parts.length != 3) {
            throw new UnauthorizeException("Token is invalid");
        }

        if(!rSAService.verifyData(parts[0] + "." + parts[1], parts[2],
                generalKeyPair.getPublicKey())) {
            throw new UnauthorizeException("Token is invalid");
        }

        JwtTokenClaims payload;
        try {
            payload = objectMapper.readValue(
                    Base64.getUrlDecoder().decode(parts[1]),
                    JwtTokenClaims.class
            );

        } catch (IOException e) {
            log.error("Token is invalid: {}", e.getMessage(), e);
            throw new UnauthorizeException("Token is invalid");
        }

        long now = DateTimeHelper.currentTimeMillis();

        if(payload == null || payload.getExp() < now || payload.getIat() > now)  {
            throw new UnauthorizeException("Token is expired or not yet valid");
        }

        return payload;
    }

}
