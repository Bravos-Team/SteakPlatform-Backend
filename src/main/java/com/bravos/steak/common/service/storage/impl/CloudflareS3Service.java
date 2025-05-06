package com.bravos.steak.common.service.storage.impl;

import com.bravos.steak.common.service.storage.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;

@Service
public class CloudflareS3Service extends S3Service {

    private final S3Client cloudflareS3Client;

    @Autowired
    public CloudflareS3Service(S3Client cloudflareS3Client) {
        super();
        this.cloudflareS3Client = cloudflareS3Client;
    }

    @Override
    protected S3Client s3Client() {
        return cloudflareS3Client;
    }

}
