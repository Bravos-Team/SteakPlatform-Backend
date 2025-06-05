package com.bravos.steak.administration.entity;

import com.bravos.steak.common.entity.Account;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "admin_account")
public class AdminAccount extends Account {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "admin_account_role",
            joinColumns = @JoinColumn(name = "admin_account_id"),
            inverseJoinColumns = @JoinColumn(name = "admin_role_id")
    )
    Set<AdminRole> roles;

    @Override
    public GrantedAuthority getRole() {
        return new SimpleGrantedAuthority("ROLE_ADMIN");
    }

    @Override
    public Collection<String> getPermissions() {
        Set<String> permissions = new HashSet<>();
        roles.forEach(role -> {
            Set<AdminPermission> adminPermissions = role.getAdminPermissions();
            adminPermissions.forEach(pr -> permissions.addAll(pr.getAuthorities()));
        });
        return permissions;
    }

}
