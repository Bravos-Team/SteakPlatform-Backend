package com.bravos.steak.dev.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresignedUrlResponse {

    String fileName;

    String signedUrl;

    String cdnFileName;

}
