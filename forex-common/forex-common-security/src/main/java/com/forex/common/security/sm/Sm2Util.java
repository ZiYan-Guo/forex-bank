package com.forex.common.security.sm;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;

public final class Sm2Util {

    private final SM2 sm2;

    public Sm2Util(String privateKeyHex, String publicKeyHex) {
        this.sm2 = SmUtil.sm2(privateKeyHex, publicKeyHex);
    }

    public Sm2Util() {
        this.sm2 = SmUtil.sm2();
    }

    public String encryptBase64(String plainText) {
        return sm2.encryptBase64(plainText, KeyType.PublicKey);
    }

    public String decryptStr(String cipherText) {
        return sm2.decryptStr(cipherText, KeyType.PrivateKey);
    }

    public String signHex(String data) {
        return sm2.signHex(data);
    }

    public boolean verifyHex(String data, String signHex) {
        return sm2.verifyHex(data, signHex);
    }
}
