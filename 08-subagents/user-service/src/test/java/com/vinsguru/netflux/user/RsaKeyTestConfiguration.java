package com.vinsguru.netflux.user;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Base64;

@TestConfiguration(proxyBeanMethods = false)
class RsaKeyTestConfiguration {

    @Bean
    DynamicPropertyRegistrar rsaPrivateKeyPropertyRegistrar() throws NoSuchAlgorithmException, IOException {
        var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        var path = writePrivateKeyPem(generator.generateKeyPair().getPrivate());
        return registry -> registry.add("rsa.private-key", () -> "file:" + path.toAbsolutePath());
    }

    private Path writePrivateKeyPem(PrivateKey privateKey) throws IOException {
        var base64 = Base64.getMimeEncoder(64, System.lineSeparator().getBytes()).encodeToString(privateKey.getEncoded());
        var pem = "-----BEGIN PRIVATE KEY-----" + System.lineSeparator()
                + base64 + System.lineSeparator()
                + "-----END PRIVATE KEY-----" + System.lineSeparator();
        var path = Path.of("target", "test-rsa-private-key.pem");
        Files.writeString(path, pem);
        return path;
    }

}
