package com.bravos.steak.common.service.encryption.impl;

import com.bravos.steak.common.security.JwtTokenClaims;
import com.bravos.steak.common.service.encryption.JwtService;
import com.bravos.steak.common.service.encryption.KeyVaultService;
import com.bravos.steak.common.service.encryption.RSAService;
import com.bravos.steak.exceptions.UnauthorizeException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    private final KeyVaultService keyVaultService;
    private final ObjectMapper objectMapper;
    private final RSAService rSAService;

    private final String header;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JwtServiceImpl(KeyVaultService keyVaultService, ObjectMapper objectMapper,
                          RSAService rSAService, @Value("${spring.profiles.active:dev}") String profile)
            throws IOException {

        this.keyVaultService = keyVaultService;
        this.objectMapper = objectMapper;
        this.rSAService = rSAService;

        this.header = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(objectMapper.writeValueAsBytes(Map.of(
                        "alg","RS256",
                        "typ", "JWT"
                ))) + ".";

        if(profile.equals("prod")) {
            this.privateKey = rSAService.convertPrivateKey(keyVaultService.getSecretKey(System.getProperty("STEAK_PRIVATE_KEY")));
            this.publicKey = rSAService.convertPublicKey(keyVaultService.getSecretKey(System.getProperty("STEAK_PUBLIC_KEY")));
        } else {
            this.privateKey = rSAService.convertPrivateKey(readPemFile("private-key.pem"));
            this.publicKey = rSAService.convertPublicKey(readPemFile("public-key.pem"));
        }

    }

    private static String readPemFile(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            return reader.lines().collect(Collectors.joining());
        }
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
                    .append(rSAService.getSignatureData(headerPayload,privateKey));

            return token.toString();

        } catch (Exception e) {
            log.error("Failed to generate JWT: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate JWT: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
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
                    .authorities(claims.get("authorities", Collection.class))
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
