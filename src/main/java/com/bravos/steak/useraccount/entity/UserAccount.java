package com.bravos.steak.useraccount.entity;

import com.bravos.steak.common.entity.Account;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Table(name = "user_account")
public class UserAccount extends Account {

    @Override
    public GrantedAuthority getRole() {
        return new SimpleGrantedAuthority("ROLE_USER");
    }

    @Override
    public List<String> getPermissions() {
        return List.of();
    }

}
