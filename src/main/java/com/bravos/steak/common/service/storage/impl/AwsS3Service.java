package com.bravos.steak.common.service.storage.impl;

import com.bravos.steak.common.service.storage.S3Service;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Service
public class AwsS3Service extends S3Service {

    private final S3Presigner gameS3Presigner;
    private final S3Client gameS3Client;

    public AwsS3Service(S3Presigner gameS3Presigner, S3Client gameS3Client) {
        super();
        this.gameS3Presigner = gameS3Presigner;
        this.gameS3Client = gameS3Client;
    }

    @Override
    public S3Presigner getS3Presigned() {
        return gameS3Presigner;
    }

    @Override
    public S3Client getS3Client() {
        return gameS3Client;
    }

}
