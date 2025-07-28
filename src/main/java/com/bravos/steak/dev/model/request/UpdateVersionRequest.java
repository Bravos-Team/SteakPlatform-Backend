package com.bravos.steak.dev.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class UpdateVersionRequest {

    @NotNull
    Long gameId;

    @NotNull
    Long versionId;

    @NotBlank
    String versionName;

    String changeLog;

    @Builder.Default
    Boolean isReady = false;

    Long releaseDate;

    @NotBlank
    private String execPath;

    @NotBlank
    @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].\\S*$", message = "Invalid download URL format")
    private String downloadUrl;

    @NotBlank
    private String checksum;

    @NotNull
    private Long fileSize;

    @NotNull
    private Long installSize;

}
