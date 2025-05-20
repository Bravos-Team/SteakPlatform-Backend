package com.bravos.steak.common.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class RefreshToken {

    @Id
    Long id; // jti, snowflake id

    @Column(nullable = false)
    String deviceId;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    LocalDateTime issuesAt = LocalDateTime.now();

    @Column(nullable = false)
    LocalDateTime expiresAt;

    @Column(nullable = false)
    @Builder.Default
    Boolean revoked = false;

    @Column(nullable = false)
    String token;

    String deviceInfo;

    public abstract Account getAccount();

}
