package com.bravos.steak.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {

    private String detail;
    
    public ErrorResponse(String detail) {
        this.detail = detail;
    }

}
