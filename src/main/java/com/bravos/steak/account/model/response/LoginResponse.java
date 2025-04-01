package com.bravos.steak.account.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginResponse {
    @Builder.Default
    String tokenType = "Bearer";
    String accessToken;
    String refreshToken;
    AccountDTO accountDTO;
}
