package com.bravos.steak.dev.service;

import com.bravos.steak.dev.model.request.ImageUploadPresignedRequest;
import com.bravos.steak.dev.model.response.PresignedUrlResponse;

public interface PublisherUploadService {

    /**
     * Upload resource liên quan thông tin nhà phát hành
     * @param imageUploadPresignedRequest req
     * @return <3
     */
    PresignedUrlResponse createUploadPublisherImagePresignedUrl(ImageUploadPresignedRequest imageUploadPresignedRequest);

    /**
     * Upload resource liên quan ảnh game gủng
     * @param gameId id game thoi
     * @param imageUploadPresignedRequest req
     * @return cái signedurl chứ gì nữa
     */
    PresignedUrlResponse createUploadGameImagePresignedUrl(Long gameId, ImageUploadPresignedRequest imageUploadPresignedRequest);

}
