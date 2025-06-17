package com.bravos.steak.common.service.storage.impl;

import com.bravos.steak.common.service.storage.S3Service;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Service("cloudflareS3Service")
public class CloudflareS3Service extends S3Service {

    private final S3Presigner cloudflareS3Presigner;

    public CloudflareS3Service(S3Presigner cloudflareS3Presigner) {
        super();
        this.cloudflareS3Presigner = cloudflareS3Presigner;
    }

    @Override
    public S3Presigner getS3Presigned() {
        return cloudflareS3Presigner;
    }

}
