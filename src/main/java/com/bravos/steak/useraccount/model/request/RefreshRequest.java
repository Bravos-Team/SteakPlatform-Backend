package com.bravos.steak.useraccount.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class RefreshRequest {

    @NotNull
    @NotBlank
    String deviceId;

    @NotBlank
    @NotNull
    String deviceInfo;

}
