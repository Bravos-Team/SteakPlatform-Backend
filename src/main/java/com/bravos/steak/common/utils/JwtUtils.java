package com.bravos.steak.common.utils;

import com.bravos.steak.account.entity.Account;
import com.bravos.steak.account.service.AccountService;
import com.bravos.steak.account.service.impl.AccountServiceImpl;
import com.bravos.steak.common.model.JwtTokenClaims;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.exceptions.ResourceNotFoundException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtUtils {

    static AccountService accountService;

    static PrivateKey PRIVATE_KEY = KeyLoader.loadPrivateKey();
    static PublicKey PUBLIC_KEY = KeyLoader.loadPublicKey();
    static Long ACCESS_TOKEN_EXPIRATION = Long.valueOf(System.getProperty("ACCESS_TOKEN_EXPIRATION_TIME"));
    static Long REFRESH_TOKEN_EXPIRATION = Long.valueOf(System.getProperty("REFRESH_TOKEN_EXPIRATION_TIME"));

    public static String generateToken(Account account) {
        SnowflakeGenerator snowflakeGenerator = new SnowflakeGenerator(1);
        JwtTokenClaims claims = JwtTokenClaims.builder()
                .id(account.getId())
                .roles(new String[]{"ROLE_USER"})
                .permissions(new String[]{"PERMISSION_USER_READ"})
                .iat(Date.from(Instant.now()))
                .exp(Date.from(Instant.now().plusSeconds(ACCESS_TOKEN_EXPIRATION)))
                .jti(String.valueOf(snowflakeGenerator.generateId()))
                .deviceId("610")
                .build();

        return Jwts.builder()
                .claims(claims.toMap())
                .signWith(PRIVATE_KEY, Jwts.SIG.RS256)
                .compact();
    }

    public static JwtTokenClaims parseClaims(Claims claims){
        return JwtTokenClaims.builder()
                    .id(Long.valueOf(claims.getSubject()))
                    .roles(claims.get("roles",String[].class))
                    .permissions(claims.get("permissions",String[].class))
                    .iat(claims.get("iat", Date.class))
                    .exp(claims.get("exp", Date.class))
                    .jti(claims.get("jti", String.class))
                    .deviceId(claims.get("device_id", String.class))
                    .build();
    }

    public static Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(PUBLIC_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claims != null ? claimsResolver.apply(claims) : null;
    }

    public static String extractById(String token) {
         return extractClaim(token, Claims::getSubject);
    }

    public static Account extractAccountIfValid(String token) {
      return Optional.ofNullable(extractById(token))
              .filter(id -> isValidToken(token,id))
              .flatMap(id -> accountService.getAccountById(Long.valueOf(id)))
              .orElseThrow(() -> new ResourceNotFoundException("Cannot found any account when extract account."));
    }

    public static boolean isValidToken(String token, String username) {
       return Optional.ofNullable(extractAllClaims(token))
               .map(claims ->
                       claims.getExpiration().after(Date.from(Instant.now()))
                       && username.equals(claims.getSubject()))
               .orElse(false);
    }

    public static boolean isTokenExp(String token){
        return Optional.ofNullable(extractClaim(token,Claims::getExpiration))
                .map(exp -> exp.before(Date.from(Instant.now())))
                .orElse(false);
    }

}
