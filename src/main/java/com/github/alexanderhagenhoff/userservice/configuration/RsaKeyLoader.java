package com.github.alexanderhagenhoff.userservice.configuration;

import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class RsaKeyLoader {

    private static final String ALGORITHM = "RSA";

    private static final String PUBLIC_KEY_BEGIN = "-----BEGIN PUBLIC KEY-----";
    private static final String PUBLIC_KEY_END = "-----END PUBLIC KEY-----";

    private static final String PRIVATE_KEY_BEGIN = "-----BEGIN PRIVATE KEY-----";
    private static final String PRIVATE_KEY_END = "-----END PRIVATE KEY-----";

    public RSAPublicKey loadPublicKey(Path path) throws Exception {
        byte[] decoded = extractKeyBytes(path, PUBLIC_KEY_BEGIN, PUBLIC_KEY_END);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

        return (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(decoded));
    }

    public RSAPrivateKey loadPrivateKey(Path path) throws Exception {
        byte[] decoded = extractKeyBytes(path, PRIVATE_KEY_BEGIN, PRIVATE_KEY_END);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

        return (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    private byte[] extractKeyBytes(Path path, String begin, String end) throws Exception {
        String content = Files.readString(path)
                .replace(begin, "")
                .replace(end, "")
                .replaceAll("\\s", "");

        return Base64.getDecoder().decode(content);
    }
}
