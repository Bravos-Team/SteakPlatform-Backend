package com.bravos.steak.account.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.mongodb.core.mapping.Field;

import java.sql.Timestamp;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account_refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne()
    @JoinColumn(name = "account_id", nullable = false)
     Account account;

    @Column(nullable = false)
    Long deviceId = 106102005L;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
     Timestamp issueAt;

    @Column(nullable = false)
    Timestamp expiresAt;

    @Column(nullable = false)
    Boolean revoked = false;

    @Column(nullable = false)
    Long jti;
}
