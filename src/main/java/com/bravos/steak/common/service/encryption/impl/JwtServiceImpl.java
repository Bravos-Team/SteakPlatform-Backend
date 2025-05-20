package com.bravos.steak.common.service.encryption.impl;

import com.azure.security.keyvault.keys.cryptography.models.SignatureAlgorithm;
import com.bravos.steak.common.security.JwtTokenClaims;
import com.bravos.steak.common.service.encryption.JwtService;
import com.bravos.steak.common.service.encryption.KeyVaultService;
import com.bravos.steak.exceptions.UnauthorizeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    private final PublicKey publicKey;
    private final KeyVaultService keyVaultService;
    private final ObjectMapper objectMapper;

    private final String header;

    public JwtServiceImpl(KeyVaultService keyVaultService, ObjectMapper objectMapper) throws JsonProcessingException {
        this.keyVaultService = keyVaultService;
        this.publicKey = keyVaultService.getPublicKey();
        this.objectMapper = objectMapper;

        this.header = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(objectMapper.writeValueAsBytes(Map.of(
                        "alg","RS256",
                        "typ", "JWT"
                ))) + ".";

    }

    @Override
    public String generateToken(JwtTokenClaims jwtTokenClaims) {
        try {
            StringBuilder token = new StringBuilder(1024);

            token.append(header);

            token.append(Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(objectMapper.writeValueAsBytes(jwtTokenClaims.toMap())));

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.toString().getBytes(StandardCharsets.UTF_8));
            String signature = keyVaultService.signData(SignatureAlgorithm.RS256,hash);

            return token.append(".").append(signature).toString();

        } catch (NoSuchAlgorithmException e) {
            log.error("Invalid hash algorithm: {}", e.getMessage(), e);
            throw new RuntimeException("Invalid hash algorithm: " + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to generate JWT: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate JWT: " + e.getMessage());
        }
    }

    @Override
    public JwtTokenClaims getClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return JwtTokenClaims.builder()
                    .id(Long.valueOf(claims.getSubject()))
                    .role(claims.get("role", String.class))
                    .authorities(claims.get("authorities", List.class))
                    .iat(claims.get("iat", Long.class))
                    .exp(claims.get("exp", Long.class))
                    .jti(Long.valueOf(claims.get("jti", String.class)))
                    .build();
        } catch (ExpiredJwtException e) {
            throw new UnauthorizeException("Token has expired");
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Token is invalid: {}", e.getMessage());
            throw new UnauthorizeException("Token is invalid");
        }
    }

}
