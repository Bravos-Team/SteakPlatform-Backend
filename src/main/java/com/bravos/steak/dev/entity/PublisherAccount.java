package com.bravos.steak.dev.entity;

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
@Table(name = "publisher_account")
public class PublisherAccount extends Account {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", nullable = false)
    Publisher publisher;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "publisher_account_role",
            joinColumns = @JoinColumn(name = "publisher_account_id"),
            inverseJoinColumns = @JoinColumn(name = "publisher_role_id")
    )
    Set<PublisherRole> roles;

    @Override
    public GrantedAuthority getRole() {
        return new SimpleGrantedAuthority("ROLE_PUBLISHER");
    }

    @Override
    public Collection<String> getPermissions() {
        Set<String> permissions = new HashSet<>();
        roles.forEach(role -> {
            Set<PublisherPermission> publisherPermissions = role.getPublisherPermissions();
            publisherPermissions.forEach(pr -> permissions.addAll(pr.getAuthorities()));
        });
        return permissions;
    }

}
