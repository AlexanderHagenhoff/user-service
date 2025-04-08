package com.github.alexanderhagenhoff.userservice.configuration;

import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class RsaPublicKeyLoader {
    private static final String BEGIN = "-----BEGIN PUBLIC KEY-----";
    private static final String END = "-----END PUBLIC KEY-----";
    private static final String CRYPTO_ALGORITHM = "RSA";

    public RSAPublicKey load(Path path) throws Exception {
        String key = Files.readString(path)
                .replace(BEGIN, "")
                .replace(END, "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);

        return (RSAPublicKey) KeyFactory.getInstance(CRYPTO_ALGORITHM).generatePublic(keySpec);
    }
}
