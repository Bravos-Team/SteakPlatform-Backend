package com.bravos.steak.administration.service;

import com.bravos.steak.dev.model.request.DeleteImageRequest;
import com.bravos.steak.dev.model.request.ImageUploadPresignedRequest;
import com.bravos.steak.dev.model.response.PresignedUrlResponse;

public interface AdminUploadService {

    PresignedUrlResponse createAdminPresignedUploadUrl(ImageUploadPresignedRequest imageUploadPresignedRequest);

    PresignedUrlResponse[] createAdminPresignedUploadUrls(ImageUploadPresignedRequest[] imageUploadPresignedRequests);

    void deleteAdminFile(DeleteImageRequest deleteImageRequest);

    void deleteAdminFile(DeleteImageRequest[] deleteImageRequests);

}
