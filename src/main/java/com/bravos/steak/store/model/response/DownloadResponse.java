package com.bravos.steak.store.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class DownloadResponse {

    String downloadUrl;

    String fileName;

    String execPath;

    String checksum;

    Long fileSize;

}
