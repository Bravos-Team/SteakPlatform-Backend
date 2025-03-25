package com.bravos.steak.account.entity;

import com.bravos.steak.account.model.AccountStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Account {

    @Id
    private Long id;

    private String username;

    private String password;

    private String email;

    private AccountStatus status;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

}
