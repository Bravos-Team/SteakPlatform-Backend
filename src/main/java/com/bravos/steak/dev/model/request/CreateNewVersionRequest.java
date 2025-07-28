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
public class CreateNewVersionRequest {

    @NotNull
    Long gameId;

    @NotBlank
    String versionName;

    Boolean isReady;

    Long releaseDate;

    String changeLog;

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
