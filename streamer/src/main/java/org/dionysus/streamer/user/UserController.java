package org.dionysus.streamer.user;

import org.bson.types.ObjectId;
import org.dionysus.streamer.exception.NotFoundException;
import org.dionysus.streamer.security.JWTBuilder;
import org.dionysus.streamer.security.SecurityConfig;
import org.dionysus.streamer.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.inject.Inject;

@RestController
@RequestMapping("/user")
public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;
    private final JWTBuilder jwtBuilder;
    private final SecurityConfig securityConfig;
    private final BCryptPasswordEncoder passwordEncoder;

    @Inject
    public UserController(UserRepository userRepository,
                          JWTBuilder jwtBuilder,
                          SecurityConfig securityConfig,
                          BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtBuilder = jwtBuilder;
        this.securityConfig = securityConfig;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping(path="/refreshToken")
    public Mono<Void> refreshToken(ServerWebExchange exchange,
                             Authentication authentication) {
        String userName = (String) authentication.getPrincipal();
        String newToken = jwtBuilder.buildJWT(userName);
        exchange.getResponse().getHeaders().add(securityConfig.getHeader(), newToken);
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        return exchange.getResponse().setComplete();
    }

    @GetMapping(path="/{id}", produces = "application/json")
    public Mono<User> getUser(@PathVariable String id) {
        return this.userRepository.findById(id)
                .switchIfEmpty(Mono.error(() ->
                        new NotFoundException("No User exists with id " + id)));
    }

    @GetMapping(path="/all", produces = "application/json")
    public Flux<User> getUsers() {
        return this.userRepository.findAll();
    }

    @PostMapping(produces = "application/json", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<User> postUser(@RequestBody User user) {
        logger.info("New User: {}", user);
        user.setId(null);
        user.getCredentials().setPassword(this.passwordEncoder.encode(user.getCredentials().getPassword()));
        return this.userRepository.insert(user);
    }
}
