package org.dionysus.streamer.user;

import org.dionysus.streamer.security.JWTBuilder;
import org.dionysus.streamer.security.SecurityConfig;
import org.dionysus.streamer.user.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.inject.Inject;

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
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "No User exists with id " + id)));
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
