package org.dionysus.streamer.security;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties(prefix="security")
@Singleton
public class SecurityConfig {

    private static final long DEFAULT_TIMEOUT = TimeUnit.MILLISECONDS.convert(4, TimeUnit.HOURS);

    private static final String DEFAULT_HEADER = "jwt-authorization";

    private String secret;
    private String header;
    private Long timeoutMs;
    private byte[] secretBytes;

    public void setSecret(@Nullable String secret) {
        this.secret = secret;
    }

    public String getSecret() {
        return secret;
    }

    public String getHeader() {
        return header != null ? header : DEFAULT_HEADER;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public long getTimeoutMs() {
        return timeoutMs != null ? timeoutMs : DEFAULT_TIMEOUT;
    }

    public void setTimeoutMs(Long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public byte[] getSecretBytes() {
        if(secretBytes == null) {
            if (StringUtils.isBlank(secret)) {
                secretBytes = new byte[1024];
                new Random().nextBytes(secretBytes);
            } else {
                secretBytes = secret.getBytes();
            }
        }
        return secretBytes;
    }
}
