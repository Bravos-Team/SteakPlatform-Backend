package com.bravos.steak.common.service.encryption;

import com.azure.security.keyvault.keys.cryptography.models.SignatureAlgorithm;

import java.security.PublicKey;

public interface KeyVaultService {

    String getSecretKey(String keyName);

    String signData(SignatureAlgorithm algorithm, byte[] data);

    PublicKey getPublicKey();
}
