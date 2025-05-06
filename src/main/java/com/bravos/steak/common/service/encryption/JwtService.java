package com.bravos.steak.common.service.encryption;

import com.bravos.steak.common.security.JwtTokenClaims;

public interface JwtService {

    String generateToken(JwtTokenClaims jwtTokenClaims);

    JwtTokenClaims getClaims(String token);

}
