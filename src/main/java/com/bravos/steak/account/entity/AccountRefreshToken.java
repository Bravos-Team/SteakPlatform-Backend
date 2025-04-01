package com.bravos.steak.account.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Builder
@Table(name = "account_refresh_token")
public class AccountRefreshToken {

    @Id
    Long id; // jti, snowflake id

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    Account account;

    @Column(nullable = false)
    String deviceId;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    Timestamp issuesAt;

    @Column(nullable = false)
    Timestamp expiresAt;

    @Column(nullable = false)
    @Builder.Default
    Boolean revoked = false;

    @Column(nullable = false)
    String token;

}
