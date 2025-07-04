package com.bravos.steak.common.filter;

import com.bravos.steak.common.security.JwtTokenClaims;
import com.bravos.steak.common.security.JwtAuthentication;
import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.common.service.encryption.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final List<String> whiteList = List.of(
            "/verificate",
            "/api/v1/user/auth",
            "/api/v1/dev/auth",
            "/api/v1/admin/auth"
    );

    private final SessionService sessionService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        if(whiteList.stream().anyMatch(requestURI::startsWith)) {
            filterChain.doFilter(request,response);
            return;
        }

        String token = getTokenFromRequest(request);
        JwtTokenClaims tokenClaims = null;

        if(token == null || token.isBlank()) {
            filterChain.doFilter(request,response);
            return;
        }

        try {
            tokenClaims = jwtService.getClaims(token.trim());
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }

        if(tokenClaims == null || sessionService.isTokenBlacklisted(tokenClaims.getJti())) {
            filterChain.doFilter(request,response);
            return;
        }

        JwtAuthentication authentication = new JwtAuthentication(tokenClaims);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);

    }

    private String getTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Cookie jwtCookie = Arrays.stream(request.getCookies()).filter(cookie ->
                    cookie.getName().equals("access_token")).findFirst().orElse(null);
            return jwtCookie != null ? jwtCookie.getValue() : null;
        }
        return null;
    }

}
