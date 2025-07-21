package com.bravos.steak.dev.model.request;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Role name cannot be blank")
    String name;

    String description;

    Long[] permissionIds;

}
