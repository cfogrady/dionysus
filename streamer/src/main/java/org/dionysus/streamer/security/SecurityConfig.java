package org.dionysus.streamer.security;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties(prefix="security")
public class SecurityConfig {

    private static final long DEFAULT_TIMEOUT = TimeUnit.MILLISECONDS.convert(4, TimeUnit.HOURS);

    private static final String DEFAULT_HEADER = "jwt-authorization";

    private static final String DEFAULT_ORIGIN = "*";

    private String secret;
    private String header;
    private Long timeoutMs;
    private byte[] secretBytes;
    private List<String> allowedOrigins;

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

    public List<String> getAllowedOrigins() {
        return allowedOrigins != null ? allowedOrigins : Lists.newArrayList(DEFAULT_ORIGIN);
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
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
