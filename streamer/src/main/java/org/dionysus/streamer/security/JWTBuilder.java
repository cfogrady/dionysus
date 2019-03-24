package org.dionysus.streamer.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Named
public class JWTBuilder {
    private static Logger logger = LoggerFactory.getLogger(JWTBuilder.class);

    private final SecurityConfig securityConfig;

    @Inject
    public JWTBuilder(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }

    /**
     * Builds a JWT with the provided username.
     * @param username to be provided as the subject of the JWT
     * @return JWT in String form.
     */
    public String buildJWT(String username) {
        Date expiry = Date.from(Instant.now().plus(Duration.ofMillis(securityConfig.getTimeoutMs())));

        return JWT.create().withSubject(username).withExpiresAt(expiry).sign(Algorithm.HMAC512(securityConfig.getSecretBytes()));
    }
}
