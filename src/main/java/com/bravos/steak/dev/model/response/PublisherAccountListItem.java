package com.bravos.steak.dev.model.response;

import com.bravos.steak.useraccount.model.enums.AccountStatus;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PublisherAccountListItem implements Serializable {
    private Long id;
    private String username;
    private String email;
    private AccountStatus status;
}