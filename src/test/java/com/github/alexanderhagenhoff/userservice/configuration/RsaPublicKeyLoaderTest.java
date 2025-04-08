package com.github.alexanderhagenhoff.userservice.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.interfaces.RSAPublicKey;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RsaPublicKeyLoaderTest {

    @TempDir
    private Path tempDirectory;

    @Test
    void loadShouldReturnValidRsaPublicKey() throws Exception {
        String pem = """
                -----BEGIN PUBLIC KEY-----
                MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp9RwJ+pXrFhI9kSflJcY
                JF2XWrMu0XkL+E9HDKRP9yE/qmowU2pRe4yT+8PP5EdRCG23UXQ3AIgcAXAn4JdL
                RDMND7uD4UOQ6ykhFzS7szKoGUMQlUZXGGBbGHM8+zwB2Qx5B3qzUbH9L2pNclEe
                whXx3xylM66Kvb8wAiVQm7szR0RrXrkEn0vQ8mLK6K4a8GFF0S4+FAjHgPyX0vST
                JvbAjx3qSVgZRv6+ZxViWUK0qtYYE+plAVrJTGzv9E8PzA4VHvU0r0v0Xz06kNbc
                V6pryAeSR7cVCxqNML9RkGqHvWBrfJzIKm07prAyQJKMiYz7XlgZVWTxmbsLKUQw
                QwIDAQAB
                -----END PUBLIC KEY-----
                """;
        Path pemFile = tempDirectory.resolve("public.pem");
        Files.writeString(pemFile, pem);

        RsaPublicKeyLoader loader = new RsaPublicKeyLoader();
        RSAPublicKey key = loader.load(pemFile);

        assertThat(key).isNotNull();
        assertThat(key.getAlgorithm()).isEqualTo("RSA");
        assertThat(key.getModulus()).isNotNull();
        assertThat(key.getPublicExponent()).isNotNull();
    }
}
