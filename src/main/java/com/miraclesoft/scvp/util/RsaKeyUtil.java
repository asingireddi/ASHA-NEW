package com.miraclesoft.scvp.util;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Base64;

@Component
public class RsaKeyUtil {

    private KeyPair keyPair;

    public RsaKeyUtil() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        this.keyPair = generator.generateKeyPair();
    }

    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }

    public String decryptPassword(String password) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey());
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(password));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public String decryptUsername(String loginId) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey());
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(loginId));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
