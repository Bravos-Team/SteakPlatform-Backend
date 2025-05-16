package com.bravos.steak.exceptions;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {

    private String detail;

    public ErrorResponse(String detail) {
        this.detail = detail;
    }

}
