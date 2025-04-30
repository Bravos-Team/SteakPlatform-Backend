package com.bravos.steak.useraccount.entity;

import com.bravos.steak.common.entity.Account;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Table(name = "user_account")
public class UserAccount extends Account {

    @Override
    public Collection<? extends GrantedAuthority> getRoles() {
        return List.of(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public List<String> getPermissions() {
        return List.of();
    }

}
