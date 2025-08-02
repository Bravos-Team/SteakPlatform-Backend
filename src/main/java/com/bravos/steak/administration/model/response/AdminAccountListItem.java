package com.bravos.steak.administration.model.response;

import com.bravos.steak.useraccount.model.enums.AccountStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class AdminAccountListItem {

    Long id;

    String username;

    String email;

    AccountStatus status;

}
