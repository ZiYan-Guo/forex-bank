package com.forex.common.security.sm;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.digest.SM3;

public final class Sm3Util {

    private Sm3Util() {
    }

    public static String hashHex(String data) {
        return SmUtil.sm3(data);
    }

    public static String hashHex(byte[] data) {
        SM3 sm3 = SmUtil.sm3();
        return sm3.digestHex(data);
    }

    public static boolean verify(String data, String hashHex) {
        return hashHex(data).equalsIgnoreCase(hashHex);
    }
}
