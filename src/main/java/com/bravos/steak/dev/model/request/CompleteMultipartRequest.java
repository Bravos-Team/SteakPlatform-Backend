package com.bravos.steak.dev.model.request;


import com.bravos.steak.dev.model.PartInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompleteMultipartRequest {

    @NotBlank
    private String objectKey;

    @NotBlank
    private String uploadId;

    private PartInfo[] parts;

    @NotBlank
    private String checksum;

}
