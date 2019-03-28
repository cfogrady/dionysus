package org.dionysus.streamer.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public class JWTBuilderTest {
    private static Logger logger = LoggerFactory.getLogger(JWTBuilderTest.class);

    @Mock
    private DateTimeProvider dateTimeProvider;

    @Mock
    private SecurityConfig securityConfig;

    private JWTBuilder jwtBuilder;

    @Before
    public void testSetup() {
        MockitoAnnotations.initMocks(this);
        jwtBuilder = new JWTBuilder(securityConfig, dateTimeProvider);
    }

    @Test
    public void testJwtBuilder() {
        Instant testStart = Instant.now();
        String username = "username";
        long timeout = Duration.of(5, ChronoUnit.SECONDS).toMillis();
        Mockito.when(dateTimeProvider.getNow()).thenReturn(testStart);
        Mockito.when(securityConfig.getSecretBytes()).thenReturn("This is my secrety!".getBytes());
        Mockito.when(securityConfig.getTimeoutMs()).thenReturn(timeout);
        String token = jwtBuilder.buildJWT(username);
        Mockito.verify(securityConfig, Mockito.times(1)).getSecretBytes();
        Mockito.verify(dateTimeProvider).getNow();
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(securityConfig.getSecretBytes()))
                .build()
                .verify(token);
        Assert.assertThat("Subject is username",
                decodedJWT.getSubject(),
                CoreMatchers.equalTo(username));
        long expectedExpiryTime = Date.from(testStart.plus(Duration.ofMillis(timeout))).getTime();
        // JWT stores in seconds so remove the milliseconds
        expectedExpiryTime = (expectedExpiryTime / 1000) * 1000;
        Assert.assertThat("Expiration matches timeout in seconds",
                decodedJWT.getExpiresAt().getTime(),
                CoreMatchers.equalTo(expectedExpiryTime));
    }
}
