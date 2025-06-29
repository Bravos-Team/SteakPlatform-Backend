package com.bravos.steak.dev.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompleteUploadResponse {

    private String objectUrl;

    private String checksum;

}
