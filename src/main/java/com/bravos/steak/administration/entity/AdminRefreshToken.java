package com.bravos.steak.administration.entity;

import com.bravos.steak.common.entity.Account;
import com.bravos.steak.common.entity.RefreshToken;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "admin_refresh_token")
public class AdminRefreshToken extends RefreshToken {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private AdminAccount account;

    @Override
    public Account getAccount() {
        return account;
    }

}
