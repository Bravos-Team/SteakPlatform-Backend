package com.bravos.steak.account.entity;

import com.bravos.steak.account.model.AccountStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Account {

    @Id
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @Builder.Default
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(nullable = false, name = "created_at")
    @Builder.Default
    private LocalDateTime createdTime = LocalDateTime.now();

    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedTime = LocalDateTime.now();

}
