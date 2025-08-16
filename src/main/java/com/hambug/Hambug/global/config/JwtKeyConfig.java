package com.hambug.Hambug.global.config;

import com.hambug.Hambug.global.util.KeyUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;

@Configuration
public class JwtKeyConfig {

    @Value("${jwt.private-key-path}")
    private Resource privateKeyResource;

    @Value("${jwt.public-key-path}")
    private Resource publicKeyResource;

    @Bean
    public PrivateKey privateKey() throws IOException, GeneralSecurityException {
        return KeyUtil.loadPrivateKey(privateKeyResource.getInputStream());
    }

    @Bean
    public PublicKey publicKey() throws IOException, GeneralSecurityException {
        return KeyUtil.loadPublicKey(publicKeyResource.getInputStream());
    }
}
