package com.bravos.steak.dev.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameUploadPresignedRequest {

    @NotBlank
    private String fileName;

    @NotNull
    private Long fileSize;


}
