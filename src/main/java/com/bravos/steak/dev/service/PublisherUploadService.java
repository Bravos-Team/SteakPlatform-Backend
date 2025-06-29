package com.bravos.steak.dev.service;

import com.bravos.steak.dev.model.*;
import com.bravos.steak.dev.model.request.*;
import com.bravos.steak.dev.model.response.CompleteUploadResponse;
import com.bravos.steak.dev.model.response.PartUploadPresignedResponse;
import com.bravos.steak.dev.model.response.PresignedUrlResponse;

public interface PublisherUploadService {

    PresignedUrlResponse createPublisherPresignedImageUrl(ImageUploadPresignedRequest imageUploadPresignedRequest);

    PresignedUrlResponse[] createPublisherPresignedImageUrls(ImageUploadPresignedRequest[] imageUploadPresignedRequests);

    void deletePublisherImage(DeleteImageRequest deleteImageRequest);

    void deletePublisherImages(DeleteImageRequest[] deleteImageRequests);

    PartUploadPresignedResponse createPublisherPartUploadPresignedUrl(GameUploadPresignedRequest gameUploadPresignedRequest);

    PartUploadPresignedUrl[] recreatePresignedUploadUrl(RecreatePresignedUrlRequest request);

    CompleteUploadResponse completeMultipartUpload(CompleteMultipartRequest completeMultipartRequest);

}
