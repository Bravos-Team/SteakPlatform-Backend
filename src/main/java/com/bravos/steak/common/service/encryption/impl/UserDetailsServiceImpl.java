package com.bravos.steak.common.service.encryption.impl;

import com.bravos.steak.account.entity.Account;
import com.bravos.steak.account.service.AccountService;
import com.bravos.steak.account.service.impl.AccountServiceImpl;
import com.bravos.steak.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountService.getAccountByUsername(username);
        if(account == null) throw new UsernameNotFoundException(username);

        return User.builder()
                .username(account.getUsername())
                .password(account.getPassword())
                .authorities(
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                )
                .build();
    }
}
