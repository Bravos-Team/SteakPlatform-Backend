package com.bravos.steak.useraccount.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class OauthLoginRequest {

    @NotBlank(message = "Provider cannot be blank")
    String provider;

    @NotBlank(message = "Code cannot be blank")
    String code;

    @NotBlank(message = "State cannot be blank")
    String state;

    @NotBlank(message = "Device ID cannot be blank")
    String deviceId;

    @NotBlank(message = "Device info cannot be blank")
    String deviceInfo;

}
