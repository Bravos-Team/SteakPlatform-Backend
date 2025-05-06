package com.bravos.steak.useraccount.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLoginResponse {

    String displayName;
    String avatarUrl;

}
