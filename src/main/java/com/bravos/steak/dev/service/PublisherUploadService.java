package com.bravos.steak.dev.service;

import com.bravos.steak.dev.model.request.ImageUploadPresignedRequest;
import com.bravos.steak.dev.model.response.PresignedUrlResponse;

import java.util.Map;

public interface PublisherUploadService {

    PresignedUrlResponse createUploadPublisherImagePresignedUrl(ImageUploadPresignedRequest imageUploadPresignedRequest);

    PresignedUrlResponse createUploadGameImagePresignedUrl(Long gameId, ImageUploadPresignedRequest imageUploadPresignedRequest);

}
