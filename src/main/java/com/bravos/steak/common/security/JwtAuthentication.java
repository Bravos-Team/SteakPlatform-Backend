package com.bravos.steak.common.security;

import com.bravos.steak.common.model.JwtTokenClaims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JwtAuthentication implements Authentication {

    private final JwtTokenClaims jwtTokenClaims;

    public JwtAuthentication(JwtTokenClaims jwtTokenClaims) {
        this.jwtTokenClaims = jwtTokenClaims;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(jwtTokenClaims.getRoles().stream().map(SimpleGrantedAuthority::new).toList());
        jwtTokenClaims.getPermissions().forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return jwtTokenClaims;
    }

    @Override
    public Object getPrincipal() {
        return jwtTokenClaims.getId();
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new IllegalArgumentException("No support");
    }

    @Override
    public String getName() {
        return jwtTokenClaims.getId() + "";
    }

}
