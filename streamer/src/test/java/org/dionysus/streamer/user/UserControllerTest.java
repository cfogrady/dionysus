package org.dionysus.streamer.user;

import org.dionysus.streamer.exception.NotFoundException;
import org.dionysus.streamer.security.JWTBuilder;
import org.dionysus.streamer.security.SecurityConfig;
import org.dionysus.streamer.user.model.User;
import org.dionysus.streamer.user.model.UserCredentials;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class UserControllerTest {
    private static Logger logger = LoggerFactory.getLogger(UserControllerTest.class);

    private UserController controller;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JWTBuilder jwtBuilder;

    private SecurityConfig securityConfig;

    @Before
    public void testSetup() {
        MockitoAnnotations.initMocks(this);
        securityConfig = new SecurityConfig();
        controller = new UserController(userRepository, jwtBuilder, securityConfig, null);
    }

    @Test
    public void testThatGetUserReturnsUserFromRepo() {
        String testId = "testId";
        User user = new User(testId, new UserCredentials("user", "password"));
        user.getCredentials().setUsername("testUser");
        Mockito.when(userRepository.findById(testId)).thenReturn(Mono.just(user));
        User resultUser = controller.getUser(testId).block();
        Assert.assertThat("User matches repository response", resultUser, CoreMatchers.equalTo(user));
    }

    @Test(expected = NotFoundException.class)
    public void testThatGetUserThrowsForMissing() {
        Mockito.when(userRepository.findById(Mockito.anyString())).thenReturn(Mono.empty());
        controller.getUser("testId").block();
    }

    @Test
    public void testThatRefreshGetsNewToken() {
        String username = "username";
        String newToken = "newToken";
        ServerWebExchange exchange = Mockito.mock(ServerWebExchange.class);
        ServerHttpResponse response = Mockito.mock(ServerHttpResponse.class);
        Mockito.when(exchange.getResponse()).thenReturn(response);
        Mockito.when(response.setComplete()).thenReturn(Mono.empty());
        HttpHeaders headers = new HttpHeaders();
        Mockito.when(response.getHeaders()).thenReturn(headers);
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(username);
        Mockito.when(jwtBuilder.buildJWT(username)).thenReturn(newToken);
        controller.refreshToken(exchange, authentication).block();
        Mockito.verify(response, Mockito.times(1)).setStatusCode(HttpStatus.OK);
        Mockito.verify(response, Mockito.times(1)).setComplete();
        Assert.assertThat("Header has new token from builder",
                headers.getFirst(securityConfig.getHeader()),
                CoreMatchers.equalTo(newToken));
    }
}
