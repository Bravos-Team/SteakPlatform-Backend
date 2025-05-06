package com.bravos.steak.common.service.encryption.impl;

import com.bravos.steak.common.security.JwtTokenClaims;
import com.bravos.steak.common.service.encryption.JwtService;
import com.bravos.steak.common.utils.KeyLoader;
import com.bravos.steak.exceptions.UnauthorizeException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

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
                    .role(claims.get("role", String.class))
                    .authorities(claims.get("authorities", List.class))
                    .iat(claims.get("iat", Long.class))
                    .exp(claims.get("exp", Long.class))
                    .jti(Long.valueOf(claims.get("jti", String.class)))
                    .build();
        } catch (ExpiredJwtException e) {
            throw new UnauthorizeException("Token has expired");
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizeException("Token is invalid");
        }
    }

}
