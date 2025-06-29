package com.bravos.steak.dev.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartUploadPresignedUrl {

    private Integer partNumber;

    private String uploadUrl;

}
