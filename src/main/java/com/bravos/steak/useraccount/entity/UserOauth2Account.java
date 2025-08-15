package com.bravos.steak.useraccount.entity;

import com.bravos.steak.useraccount.model.enums.Oauth2Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
@Entity
@Table(name = "user_oauth2_account")
public class UserOauth2Account {

    @Id
    Long id;

    @Column(name = "oauth2_provider")
    String oauth2Provider;

    @Column(name = "oauth2_id")
    String oauth2Id;

    @Enumerated(EnumType.ORDINAL)
    Oauth2Status status;

    @ManyToOne(targetEntity = UserAccount.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_account_id", referencedColumnName = "id")
    UserAccount userAccount;

}
