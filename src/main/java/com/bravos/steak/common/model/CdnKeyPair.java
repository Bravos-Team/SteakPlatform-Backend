package com.bravos.steak.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.PrivateKey;
import java.security.PublicKey;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CdnKeyPair {

    private PrivateKey privateKey;

    private PublicKey publicKey;

    private String keyPairId;

}
