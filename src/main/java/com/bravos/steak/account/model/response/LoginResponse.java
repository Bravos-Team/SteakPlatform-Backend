package com.bravos.steak.account.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginResponse {

    String accessToken;
    String refreshToken;

}
