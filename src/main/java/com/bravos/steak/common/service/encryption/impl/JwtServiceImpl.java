package com.bravos.steak.common.service.encryption.impl;

import com.bravos.steak.common.model.JwtTokenClaims;
import com.bravos.steak.common.service.encryption.JwtService;
import com.bravos.steak.common.utils.KeyLoader;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

    private final PrivateKey privateKey = KeyLoader.PRIVATE_KEY;

    private final PublicKey publicKey = KeyLoader.PUBLIC_KEY;


    @Override
    public String generateToken(JwtTokenClaims jwtTokenClaims) {
        return Jwts.builder()
                .claims(jwtTokenClaims.toMap())
                .signWith(privateKey)
                .compact();
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
                    .roles(claims.get("roles",String[].class))
                    .permissions(claims.get("permissions",String[].class))
                    .iat(claims.get("iat", Date.class))
                    .exp(claims.get("exp", Date.class))
                    .jti(claims.get("jti", String.class))
                    .deviceId(claims.get("device_id", String.class))
                    .build();
        } catch (IllegalArgumentException | JwtException e) {
            throw new IllegalArgumentException(e);
        }
    }



}
