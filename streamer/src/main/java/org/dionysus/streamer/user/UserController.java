package org.dionysus.streamer.user;

import org.dionysus.streamer.security.JWTBuilder;
import org.dionysus.streamer.security.SecurityConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UncheckedIOException;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;
    private final JWTBuilder jwtBuilder;
    private final SecurityConfig securityConfig;

    @Inject
    public UserController(UserRepository userRepository,
                          JWTBuilder jwtBuilder,
                          SecurityConfig securityConfig) {
        this.userRepository = userRepository;
        this.jwtBuilder = jwtBuilder;
        this.securityConfig = securityConfig;
    }

    @GetMapping(path="/refreshToken")
    public void refreshToken(HttpServletResponse response,
                             Authentication authentication) {
        String userName = (String) authentication.getPrincipal();
        String newToken = jwtBuilder.buildJWT(userName);
        response.addHeader(securityConfig.getHeader(), newToken);
        response.setContentLength(0);
        response.setStatus(HttpStatus.OK.value());
        try {
            response.flushBuffer();
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    @GetMapping(path="/{id}", produces = "application/json")
    public Mono<User> getUser(@PathVariable String id) {
        return this.userRepository.findById(id);
    }

    @GetMapping(path="/all", produces = "application/json")
    public Flux<User> getUsers() {
        return this.userRepository.findAll();
    }

    @PostMapping(produces = "application/json", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<User> postUser(@RequestBody User user) {
        return this.userRepository.insert(user);
    }
}
