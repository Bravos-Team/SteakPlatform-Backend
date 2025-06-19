package com.bravos.steak.dev.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePreBuildRequest {

    @NotNull
    private Long projectId;

    @NotBlank
    private String versionName;

    @NotBlank
    private String execPath;

    @NotBlank
    @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].\\S*$", message = "Invalid download URL format")
    private String downloadUrl;

    @NotBlank
    private String checksum;

}
