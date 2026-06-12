package com.forex.common.security.crypto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES 加密服务实现
 * 使用 AES-256-GCM 加密和 HMAC-SHA256 签名
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AESEncryptionService implements EncryptionService {
    
    @Value("${security.encryption.key:}")
    private String encryptionKey;
    
    private static final String AES_ALGORITHM = "AES";
    private static final String AES_CIPHER_ALGORITHM = "AES/GCM/NoPadding";
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final int IV_LENGTH = 12;  // GCM 标准 IV 长度
    private static final int KEY_SIZE = 256;
    private static final int TAG_LENGTH_BIT = 128;  // GCM 认证标签长度
    
    @Override
    public String encrypt(String plaintext) {
        try {
            Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
            Key key = getKey();
            
            // 生成随机 IV
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            
            // 返回格式: Base64(IV + ciphertext)
            byte[] result = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(ciphertext, 0, result, iv.length, ciphertext.length);
            
            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            log.error("AES encryption error", e);
            throw new RuntimeException("Encryption failed", e);
        }
    }
    
    @Override
    public String decrypt(String ciphertext) {
        try {
            byte[] decodedData = Base64.getDecoder().decode(ciphertext);
            
            // 提取 IV
            byte[] iv = new byte[IV_LENGTH];
            System.arraycopy(decodedData, 0, iv, 0, IV_LENGTH);
            
            // 提取密文
            byte[] encrypted = new byte[decodedData.length - IV_LENGTH];
            System.arraycopy(decodedData, IV_LENGTH, encrypted, 0, encrypted.length);
            
            Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
            Key key = getKey();
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
            byte[] plaintext = cipher.doFinal(encrypted);
            
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("AES decryption error", e);
            throw new RuntimeException("Decryption failed", e);
        }
    }
    
    @Override
    public String sign(String data) {
        try {
            Key key = new SecretKeySpec(
                getKeyBytes(),
                0,
                getKeyBytes().length,
                HMAC_ALGORITHM
            );
            
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(key);
            
            byte[] signatureBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (Exception e) {
            log.error("HMAC signing error", e);
            throw new RuntimeException("Signing failed", e);
        }
    }
    
    @Override
    public boolean verify(String data, String signature) {
        try {
            String computed = sign(data);
            return constantTimeCompare(computed, signature);
        } catch (Exception e) {
            log.error("HMAC verification error", e);
            return false;
        }
    }
    
    /**
     * 常时间比较（防时序攻击）
     */
    private boolean constantTimeCompare(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
    
    /**
     * 获取密钥
     */
    private Key getKey() {
        return new SecretKeySpec(getKeyBytes(), 0, KEY_SIZE / 8, AES_ALGORITHM);
    }
    
    /**
     * 获取密钥字节
     */
    private byte[] getKeyBytes() {
        try {
            if (encryptionKey == null || encryptionKey.isEmpty()) {
                throw new RuntimeException("Encryption key not configured");
            }
            
            // 如果配置的是文本，需要转换为字节数组（需要用户配置 32 字节的 Base64 密钥）
            if (encryptionKey.length() == 44) {
                // 可能是 Base64 编码的 32 字节密钥
                return Base64.getDecoder().decode(encryptionKey);
            }
            
            // 否则直接使用
            return encryptionKey.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error getting encryption key", e);
            throw new RuntimeException("Invalid encryption key", e);
        }
    }
}
