package com.bravos.steak.common.entity;

import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.useraccount.model.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Account {

    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @Builder.Default
    private AccountStatus status = AccountStatus.ACTIVE;

    @Builder.Default
    @Column(nullable = false)
    private Boolean enableMfa = false;

    private String mfaSecret;

    @Column(nullable = false, name = "created_at")
    @Builder.Default
    private Long createdAt = DateTimeHelper.currentTimeMillis();

    @Column(nullable = false, name = "updated_at")
    @Builder.Default
    private Long updatedAt = DateTimeHelper.currentTimeMillis();

    public abstract GrantedAuthority getRole();

    public abstract Collection<String> getPermissions();

}
