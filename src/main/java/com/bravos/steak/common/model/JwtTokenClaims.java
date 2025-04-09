package com.bravos.steak.common.model;

import lombok.*;

import java.util.List;
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
    private List<String> roles;

    private List<String> permissions;

    @NonNull
    private Long iat;

    @NonNull
    private Long exp;

    @NonNull
    private Long jti;

    @NonNull
    private String deviceId;

    public Map<String, Object> toMap() {
        return Map.of(
                "sub", id + "",
                "roles", roles,
                "permissions", permissions,
                "iat", iat,
                "exp", exp,
                "jti", jti +"",
                "device_id", deviceId
        );
    }

}
