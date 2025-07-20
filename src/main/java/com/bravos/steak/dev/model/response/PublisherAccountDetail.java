package com.bravos.steak.dev.model.response;

import com.bravos.steak.useraccount.model.enums.AccountStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class PublisherAccountDetail {

    private Long id;

    private String username;

    private String email;

    private Long createdAt;

    private AccountStatus status;

    private List<RoleAndId> roles;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class RoleAndId {
        private String role;
        private Long id;
    }

}
