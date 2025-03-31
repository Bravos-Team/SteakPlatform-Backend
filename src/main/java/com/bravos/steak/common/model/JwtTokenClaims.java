package com.bravos.steak.common.model;

import lombok.*;

import java.util.Date;
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
    private String[] roles;

    private String[] permissions;

    @NonNull
    private Date iat;

    @NonNull
    private Date exp;

    @NonNull
    private String jti;

    @NonNull
    private String deviceId;

    public Map<String,Object> toMap() {
        return Map.of(
                "sub", id,
                "roles", roles,
                "permissions", permissions,
                "iat", iat,
                "exp", exp,
                "jti",jti,
                "device_id", deviceId
        );
    }

}
