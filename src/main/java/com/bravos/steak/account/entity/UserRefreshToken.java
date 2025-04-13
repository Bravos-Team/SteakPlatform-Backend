package com.bravos.steak.account.entity;

import com.bravos.steak.common.entity.RefreshToken;
import com.bravos.steak.common.model.AccountInfo;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@SuperBuilder
@Table(name = "user_refresh_token", indexes = {
        @Index(name = "idx_token_deviceid",columnList = "token,device_id")
})
public class UserRefreshToken extends RefreshToken {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", nullable = false)
    UserAccount userAccount;

    @Override
    public AccountInfo getAccountInfo() {
        return userAccount;
    }

    @Override
    public void setAccountInfo(AccountInfo accountInfo) {
        this.userAccount = (UserAccount) accountInfo;
    }

}
