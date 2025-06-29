package com.bravos.steak.dev.model.response;

import com.bravos.steak.dev.model.PartUploadPresignedUrl;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartUploadPresignedResponse {

    private PartUploadPresignedUrl[] presignedUrls;

    private String uploadId;

    private String objectKey;

}
