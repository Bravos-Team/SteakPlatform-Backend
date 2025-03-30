package com.bravos.steak.account.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Valid
public class EmailLoginRequest {

    @NotNull
    @Email
    @NotBlank(message = "Email cannot be blank")
    String email;


    @NotNull
    @Pattern(
            regexp = "^(?=\\S{6,32})(?=\\S*\\d)(?=\\S*[A-Z])(?=\\S*[a-z])(?=\\S*[!@#$%^&*? ])\\S*$",
            message = "Password must be 6-32 characters long, include at least one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&). No spaces allowed."
    )
    String password;

}
