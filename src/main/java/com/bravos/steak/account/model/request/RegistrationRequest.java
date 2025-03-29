package com.bravos.steak.account.model.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Valid
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {

    @NotNull
    @Pattern(
            regexp = "^[a-zA-Z0-9]{5,32}$",
            message = "Username cannot contain special characters and between 6 and 32 characters"
    )
    @NotBlank(message = "Username cannot be blank")
    String username;

    @NotNull
    @Email(message = "Email is invalid")
    String email;

    @NotNull
    @Pattern(
            regexp = "^(?=\\S{6,32})(?=\\S*\\d)(?=\\S*[A-Z])(?=\\S*[a-z])(?=\\S*[!@#$%^&*? ])\\S*$",
            message = "Password must be 6-32 characters long, include at least one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&). No spaces allowed."
    )
    String password;

}
