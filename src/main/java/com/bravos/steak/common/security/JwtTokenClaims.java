package com.bravos.steak.common.security;

import lombok.*;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenClaims {

    @NonNull
    private Long id;

    @NonNull
    private String role;

    private Collection<String> authorities;

    @NonNull
    private Long iat;

    @NonNull
    private Long exp;

    @NonNull
    private Long jti;

    public Map<String, Object> toMap() {
        return Map.of(
                "sub", id + "",
                "role", role,
                "authorities", authorities,
                "iat", iat,
                "exp", exp,
                "jti", jti + ""
        );
    }

}
