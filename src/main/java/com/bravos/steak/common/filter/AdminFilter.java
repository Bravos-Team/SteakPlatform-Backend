package com.bravos.steak.common.filter;

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
public class AdminFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        if(request.getMethod().equalsIgnoreCase("GET") || !requestURI.startsWith("/api/v1/admin")) {
            filterChain.doFilter(request,response);
            return;
        }

        logger.info("Implement admin");

        // Implement admin log

        filterChain.doFilter(request,response);

    }

}
