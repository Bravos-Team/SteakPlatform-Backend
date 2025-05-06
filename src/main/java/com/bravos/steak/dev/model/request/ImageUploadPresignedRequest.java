package com.bravos.steak.dev.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Range;

import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class ImageUploadPresignedRequest {

    @NotBlank
    String fileName;

    @Range(min = 1)
    Long fileSize;

    Map<String,String> metadata;

}
