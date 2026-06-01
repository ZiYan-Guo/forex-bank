package com.forex.common.security.sm;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SM4;

public final class Sm4Util {

    private Sm4Util() {
    }

    public static String encryptCbc(String plainText, String keyHex, String ivHex) {
        SM4 sm4 = SmUtil.sm4(keyHex.getBytes());
        return sm4.encryptBase64(plainText);
    }

    public static String decryptCbc(String cipherText, String keyHex, String ivHex) {
        SM4 sm4 = SmUtil.sm4(keyHex.getBytes());
        return sm4.decryptStr(cipherText);
    }

    public static String encryptEcb(String plainText, String keyHex) {
        SM4 sm4 = SmUtil.sm4(keyHex.getBytes());
        return sm4.encryptBase64(plainText);
    }

    public static String decryptEcb(String cipherText, String keyHex) {
        SM4 sm4 = SmUtil.sm4(keyHex.getBytes());
        return sm4.decryptStr(cipherText);
    }
}
