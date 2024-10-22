package com.xiaoyuer.develop;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

/**
 * Created by george on 2024/10/22 14:57
 *
 * @author george
 */
public class JasyptEncryptionExample {

    public static void main(String[] args) {

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword("password");
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);

        String planText = "abc";
        String encryptedText = encryptor.encrypt(planText);
        System.out.println("Encrypted Text: " + encryptedText);

        // 解密数据
        String decryptedText = encryptor.decrypt(encryptedText);
        System.out.println("Decrypted Text: " + decryptedText);
    }
}
