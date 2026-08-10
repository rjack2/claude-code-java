package com.vinsguru.netflux.user.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.UUID;

@Configuration
public class JwtEncoderConfig {

    @Bean
    public JwtEncoder jwtEncoder(RsaKeyProperties properties) throws Exception {
        var privateKey = properties.privateKey();
        var crtKey = (RSAPrivateCrtKey) privateKey;
        var publicKeySpec = new RSAPublicKeySpec(crtKey.getModulus(), crtKey.getPublicExponent());
        var publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);
        var rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        var jwkSource = new ImmutableJWKSet<SecurityContext>(new JWKSet(rsaKey));
        return new NimbusJwtEncoder(jwkSource);
    }

}
