package com.bravos.steak.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameS3Config {

    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String region;

}
