package com.bravos.steak.common.filter;

import com.bravos.steak.common.model.JwtTokenClaims;
import com.bravos.steak.common.security.JwtAuthentication;
import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.common.service.encryption.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final List<String> whiteList = List.of(
            "/verificate",
            "/api/v1/account/auth",
            "/api/v1/store/public",
            "/api/v1/dev/auth",
            "/api/v1/admin/auth",
            "/api/v1/hub/public",
            "/api/v1/support/public"
    );
    private final SessionService sessionService;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        if(whiteList.stream().anyMatch(requestURI::startsWith)) {
            filterChain.doFilter(request,response);
            return;
        }

        String token = getTokenFromRequest(request);
        JwtTokenClaims tokenClaims = null;

        if(token == null) {
            filterChain.doFilter(request,response);
            return;
        }

        try {
            tokenClaims = jwtService.getClaims(token);
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }

        if(tokenClaims == null) {
            filterChain.doFilter(request,response);
            return;
        }

        if(sessionService.isTokenBlacklisted(tokenClaims.getJti())) {
            filterChain.doFilter(request, response);
            return;
        }

        JwtAuthentication authentication = new JwtAuthentication(tokenClaims);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);

    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        return (headerAuth != null && headerAuth.startsWith("Bearer "))
                ? headerAuth.substring(7) : null;
    }



}
