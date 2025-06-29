package com.bravos.steak.dev.service.impl;

import com.bravos.steak.common.model.CdnKeyPair;
import com.bravos.steak.dev.service.DownloadGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.model.CustomSignerRequest;
import software.amazon.awssdk.services.cloudfront.url.SignedUrl;

import java.time.Instant;

@Service
public class DownloadGameServiceImpl implements DownloadGameService {

    private final CdnKeyPair cdnKeyPair;

    @Autowired
    public DownloadGameServiceImpl(CdnKeyPair cdnKeyPair) {
        this.cdnKeyPair = cdnKeyPair;
    }

    @Override
    public String downloadGame(String gameUrl, String ipAddress) {
        gameUrl = convertS3UrlToCdnUrl(gameUrl);
        CloudFrontUtilities cloudFrontUtilities = CloudFrontUtilities.create();
        Instant expirationTime = Instant.now().plusSeconds(300);
        CustomSignerRequest customSignerRequest = CustomSignerRequest.builder()
                .resourceUrl(gameUrl)
                .keyPairId(cdnKeyPair.getKeyPairId())
                .privateKey(cdnKeyPair.getPrivateKey())
                .expirationDate(expirationTime)
                .ipRange(ipAddress + "/32")
                .build();

        SignedUrl signedUrl = cloudFrontUtilities.getSignedUrlWithCustomPolicy(customSignerRequest);
        return signedUrl.url();
    }

    private String convertS3UrlToCdnUrl(String s3Url) {
        String cdnBaseUrl = "https://cdn.steak.io.vn";
        if(!s3Url.startsWith("https://steak-storage.s3.ap-southeast-1.amazonaws.com/") ||
                !s3Url.startsWith("https://cdn.steak.io.vn/")) {
            throw new IllegalArgumentException("Invalid S3 URL format");
        }
        return s3Url.replace("https://steak-storage.s3.ap-southeast-1.amazonaws.com", cdnBaseUrl);
    }

}
