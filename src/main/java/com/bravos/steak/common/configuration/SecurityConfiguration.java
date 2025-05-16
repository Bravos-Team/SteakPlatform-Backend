package com.bravos.steak.common.configuration;

import com.bravos.steak.common.filter.AdminFilter;
import com.bravos.steak.common.filter.JwtFilter;
import com.bravos.steak.exceptions.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfiguration implements WebMvcConfigurer {

    private final JwtFilter jwtFilter;
    private final AdminFilter adminFilter;
    private final ObjectMapper objectMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public SecurityFilterChain security(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> {

            request.requestMatchers(
                            "/api/v1/user/auth/**",
                            "/api/v1/store/public/**",
                            "/api/v1/dev/auth/**",
                            "/api/v1/admin/auth/**",
                            "/api/v1/hub/public/**",
                            "/api/v1/support/public/**",
                            "/verificate/**")
                    .permitAll();

            request.requestMatchers("/api/v1/dev/**").hasRole("PUBLISHER");
            request.requestMatchers("/api/v1/admin/**").hasRole("ADMIN");

            request.requestMatchers(
                            "/api/v1/user/**",
                            "/api/v1/dev/**",
                            "/api/v1/store/**",
                            "/api/v1/admin/**",
                            "/api/v1/hub/**",
                            "/api/v1/support/**")
                    .authenticated();

            request.anyRequest().denyAll();

        });

        http.exceptionHandling(ex -> {
            ex.accessDeniedHandler(customAccessDeniedHandler());
            ex.authenticationEntryPoint(customAuthEntryPoint());
        });

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.csrf(CsrfConfigurer::disable);
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(adminFilter, JwtFilter.class);
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> null;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(
                List.of(System.getProperty("BASE_URL_FRONTEND"),
                        System.getProperty("BASE_URL_PREVIEW_FRONTEND"),
                        "http://localhost:5173",
                        "http://localhost:5174"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationEntryPoint customAuthEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/problem+json");
            ErrorResponse errorResponse = new ErrorResponse("You need to login to access this resource");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        };
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/problem+json");
            ErrorResponse errorResponse = new ErrorResponse("You do not have permission to access this resource");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        };
    }

}
