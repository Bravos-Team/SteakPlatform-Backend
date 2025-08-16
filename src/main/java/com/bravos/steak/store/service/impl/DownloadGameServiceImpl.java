package com.bravos.steak.store.service.impl;

import com.bravos.steak.common.model.CdnKeyPair;
import com.bravos.steak.common.model.GameS3Config;
import com.bravos.steak.store.service.DownloadGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.model.CustomSignerRequest;
import software.amazon.awssdk.services.cloudfront.url.SignedUrl;

import java.time.Instant;

@Service
public class DownloadGameServiceImpl implements DownloadGameService {

    private final CdnKeyPair cdnKeyPair;
    private final GameS3Config gameS3Config;

    @Autowired
    public DownloadGameServiceImpl(CdnKeyPair cdnKeyPair, GameS3Config gameS3Config) {
        this.cdnKeyPair = cdnKeyPair;
        this.gameS3Config = gameS3Config;
    }

    @Override
    public String getGameDownloadUrl(String gameUrl, String ipAddress) {
        gameUrl = convertS3UrlToCdnUrl(gameUrl);
        CloudFrontUtilities cloudFrontUtilities = CloudFrontUtilities.create();
        Instant expirationTime = Instant.now().plusSeconds(300);
        CustomSignerRequest customSignerRequest = CustomSignerRequest.builder()
                .resourceUrl(gameUrl)
                .keyPairId(cdnKeyPair.getKeyPairId())
                .privateKey(cdnKeyPair.getPrivateKey())
                .expirationDate(expirationTime)
                .build();

        SignedUrl signedUrl = cloudFrontUtilities.getSignedUrlWithCustomPolicy(customSignerRequest);
        return signedUrl.url();
    }

    private String convertS3UrlToCdnUrl(String s3Url) {
        return s3Url.replace(gameS3Config.getBaseUrl(), gameS3Config.getCdnUrl());
    }

}
