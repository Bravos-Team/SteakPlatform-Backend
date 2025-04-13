package com.bravos.steak.account.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Valid
public class RefreshRequest {

    @NotNull
    @NotBlank
    String refreshToken;

    @NotNull
    @NotBlank
    String deviceId;

}
