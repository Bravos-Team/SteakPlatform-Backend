package com.bravos.steak.dev.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecreatePresignedUrlRequest {

    @NotBlank(message = "Upload ID cannot be blank")
    private String uploadId;

    @NotBlank(message = "Bucket name cannot be blank")
    private String objectKey;

    private Integer[] partNumbers;

}
