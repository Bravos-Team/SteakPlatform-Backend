package com.bravos.steak.dev.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@Builder
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomRoleRequest {

    @NotNull(message = "Role ID cannot be null")
    Long roleId;

    @NotBlank(message = "Role name cannot be blank")
    String name;

    String description;

    Long[] permissionIds;

}
