package com.vinsguru.netflux.user.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;

@ConfigurationProperties(prefix = "rsa")
public record RsaKeyProperties(RSAPrivateKey privateKey) {
}
