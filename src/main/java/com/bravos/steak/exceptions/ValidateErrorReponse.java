package com.bravos.steak.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ValidateErrorReponse extends ErrorResponse{

    private Map<String,String> errors;

    public ValidateErrorReponse(String detail, Map<String,String> errors) {
        super(detail);
        this.errors = errors;
    }

}
