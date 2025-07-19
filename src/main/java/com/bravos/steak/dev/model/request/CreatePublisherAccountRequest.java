package com.bravos.steak.dev.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class CreatePublisherAccountRequest {

    @Pattern(
            regexp = "^[a-zA-Z0-9]{6,32}$",
            message = "Username cannot contain special characters and between 6 and 32 characters"
    )
    String username;

    @Email
    String email;

    @NotBlank(message = "Password cannot be blank")
    String password;

    List<Long> assignedRoles;

}
