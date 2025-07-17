package com.bravos.steak.common.entity;

import com.bravos.steak.common.service.helper.DateTimeHelper;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

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
    Long issuesAt = DateTimeHelper.currentTimeMillis();

    @Column(nullable = false)
    Long expiresAt;

    @Column(nullable = false)
    @Builder.Default
    Boolean revoked = false;

    @Column(nullable = false)
    String token;

    String deviceInfo;

    public abstract Account getAccount();

}
