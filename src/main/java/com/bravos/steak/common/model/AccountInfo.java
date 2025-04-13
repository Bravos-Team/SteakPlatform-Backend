package com.bravos.steak.common.model;

import com.bravos.steak.account.model.enums.AccountStatus;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

public interface AccountInfo {

    Long getId();

    String getUsername();

    String getEmail();

    String getPassword();

    AccountStatus getStatus();

    LocalDateTime getCreatedTime();

    LocalDateTime getUpdatedTime();

    Collection<? extends GrantedAuthority> getAuthorities();

}
