package com.bravos.steak.dev.service;

import com.bravos.steak.dev.model.request.DeleteImageRequest;
import com.bravos.steak.dev.model.request.ImageUploadPresignedRequest;
import com.bravos.steak.dev.model.response.PresignedUrlResponse;

public interface PublisherUploadService {

    PresignedUrlResponse createPublisherPresignedImageUrls(ImageUploadPresignedRequest imageUploadPresignedRequest);

    PresignedUrlResponse[] createPublisherPresignedImageUrls(ImageUploadPresignedRequest[] imageUploadPresignedRequests);

    void deletePublisherImage(DeleteImageRequest deleteImageRequest);

    void deletePublisherImages(DeleteImageRequest[] deleteImageRequests);

}
