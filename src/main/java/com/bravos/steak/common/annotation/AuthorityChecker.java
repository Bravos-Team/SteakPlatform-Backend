package com.bravos.steak.common.annotation;

import com.bravos.steak.common.security.JwtAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component("authorityChecker")
public class AuthorityChecker {

    public boolean hasAnyAuthority(String... authorities) {
        if (authorities == null || authorities.length == 0) {
            return false;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        if(auth instanceof JwtAuthentication authentication) {
            List<GrantedAuthority> authoritiesList = new ArrayList<>(authentication.getAuthorities());
            if(authoritiesList.isEmpty()) return false;
            Set<String> neededAuthorities = Set.of(authorities);
            Set<String> userAuthorities = authoritiesList.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

            if(userAuthorities.contains("PUBLISHER_MASTER") && authorities[0].contains("PUBLISHER_")) {
                return true;
            }
            if(userAuthorities.contains("ADMIN_MASTER") && (authorities[0].contains("ADMIN_"))) {
                return true;
            }
            for (String authority : userAuthorities) {
                if (neededAuthorities.contains(authority)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkAuthority(String authority) {
        return hasAnyAuthority(authority);
    }

}