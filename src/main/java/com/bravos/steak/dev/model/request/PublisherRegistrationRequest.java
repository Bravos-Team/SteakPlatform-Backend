package com.bravos.steak.dev.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class PublisherRegistrationRequest {

    @NotBlank(message = "Publisher name cannot be blank")
    String name;

    @Email(message = "Email is invalid")
    String businessEmail;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number")
    String phone;

    @Pattern(
            regexp = "^[a-zA-Z0-9]{5,32}$",
            message = "Username cannot contain special characters and between 6 and 32 characters"
    )
    String masterUsername;

    @Email(message = "Master email is invalid")
    String masterEmail;

    @Pattern(
            regexp = "^(?=\\S{6,32})(?=\\S*\\d)(?=\\S*[A-Z])(?=\\S*[a-z])(?=\\S*[!@#$%^&*? ])\\S*$",
            message = "Password must be 6-32 characters long, include at least one uppercase letter," +
                    " one lowercase letter, one number, and one special character (@$!%*?&). No spaces allowed."
    )
    String masterPassword;

}
