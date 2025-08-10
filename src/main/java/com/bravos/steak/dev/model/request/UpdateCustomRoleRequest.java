package com.bravos.steak.dev.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class UpdateCustomRoleRequest {

    @NotNull(message = "Role ID cannot be null")
    Long roleId;

    @NotBlank(message = "Role name cannot be blank")
    String name;

    String description;

    Long[] permissionIds;


}
