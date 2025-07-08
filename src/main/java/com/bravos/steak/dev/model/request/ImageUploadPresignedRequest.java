package com.bravos.steak.dev.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Range;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class ImageUploadPresignedRequest {

    @NotBlank(message = "File name cannot be blank")
    @Size(min = 1, max = 255, message = "File name must be between 1 and 255 characters")
    String fileName;

    @NotNull
    Long fileSize;

}
