package com.bravos.steak.common.service.storage.impl;

import com.bravos.steak.common.service.storage.S3Service;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Service("cloudflareS3Service")
public class CloudflareS3Service extends S3Service {

    private final S3Client cloudflareS3Client;

    public CloudflareS3Service(S3Client cloudflareS3Client) {
        super();
        this.cloudflareS3Client = cloudflareS3Client;
    }

    @Override
    public S3Presigner getS3Presigned() {
        try(S3Client client = cloudflareS3Client) {
            return S3Presigner.builder()
                    .credentialsProvider(client.serviceClientConfiguration().credentialsProvider())
                    .region(client.serviceClientConfiguration().region())
                    .endpointOverride(client.serviceClientConfiguration().endpointOverride().get())
                    .build();
        }
    }

}
