package com.bravos.steak.common.security;

import com.bravos.steak.common.model.JwtTokenClaims;
import com.bravos.steak.common.service.encryption.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain) throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        if(token == null) {
            filterChain.doFilter(request,response);
            return;
        }

        JwtTokenClaims tokenClaims = jwtService.getClaims(token);


        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        return (headerAuth != null && headerAuth.startsWith("Bearer "))
                ? headerAuth.substring(7) : null;
    }

}
