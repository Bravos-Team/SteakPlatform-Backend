package com.bravos.steak.useraccount.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountDTO {
    String username;
    String email;
    String status;
    LocalDateTime createdTime;
    LocalDateTime updatedAt;
}