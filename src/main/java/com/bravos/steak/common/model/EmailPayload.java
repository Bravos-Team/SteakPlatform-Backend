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

    @NonNull
    private String to;

    @NonNull
    private String subject;

    @NonNull
    private String templateID;

    @Singular
    private Map<String, Object> params;

}
