package com.bravos.steak.dev.entity;

import com.bravos.steak.common.entity.Account;
import com.bravos.steak.common.entity.RefreshToken;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;

@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "publisher_refresh_token")
public class PublisherRefreshToken extends RefreshToken {

    public PublisherRefreshToken(Long id, String deviceId, Timestamp issuesAt, Timestamp expiresAt,
                                 Boolean revoked, String token, String deviceInfo, PublisherAccount account) {
        super(id, deviceId, issuesAt, expiresAt, revoked, token, deviceInfo);
        this.account = account;
    }

    public PublisherRefreshToken(RefreshTokenBuilder<?, ?> b, PublisherAccount account) {
        super(b);
        this.account = account;
    }

    @ManyToOne
    @JoinColumn(name = "account_id")
    private PublisherAccount account;

    @Override
    public Account getAccount() {
        return this.account;
    }

}
