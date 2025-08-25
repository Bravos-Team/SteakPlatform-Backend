package com.bravos.steak.common.model;

import lombok.*;

import java.util.Map;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailPayload {

    @Builder.Default
    private String from = "no-reply@steak.io.vn";

    private String to;

    private String subject;

    private String templateID;

    private String htmlPart;

    @Singular
    private Map<String, Object> params;

}
