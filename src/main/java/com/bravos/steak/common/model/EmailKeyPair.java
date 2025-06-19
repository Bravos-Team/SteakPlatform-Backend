package com.bravos.steak.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailKeyPair {

    private String emailApiKey;
    private String emailSecretKey;

}
