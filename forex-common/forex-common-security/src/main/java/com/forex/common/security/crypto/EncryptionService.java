package com.forex.common.security.crypto;

/**
 * 加密服务接口
 */
public interface EncryptionService {
    
    /**
     * 加密数据
     */
    String encrypt(String plaintext);
    
    /**
     * 解密数据
     */
    String decrypt(String ciphertext);
    
    /**
     * 计算 HMAC 签名
     */
    String sign(String data);
    
    /**
     * 验证 HMAC 签名
     */
    boolean verify(String data, String signature);
}
