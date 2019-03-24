package org.dionysus.streamer.user;

import org.dionysus.streamer.security.JWTBuilder;
import org.dionysus.streamer.security.SecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTBuilder jwtBuilder;
    private final SecurityConfig securityConfig;

    @Inject
    public UserController(UserRepository userRepository,
                          BCryptPasswordEncoder bCryptPasswordEncoder,
                          JWTBuilder jwtBuilder,
                          SecurityConfig securityConfig) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtBuilder = jwtBuilder;
        this.securityConfig = securityConfig;
    }

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping(path="/refreshToken")
    public void refreshToken(HttpServletRequest request,
                             HttpServletResponse response,
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
    public CompletableFuture<User> getUser(@PathVariable String id) {
        CompletableFuture<User> future = new CompletableFuture<>();
        this.userRepository.findById(id).doOnSuccessOrError((user, error) -> {
            if(error != null) {
                future.completeExceptionally(error);
            } else {
                if(user == null) {
                    //TODO: Bad Request or Not Found
                } else {
                    future.complete(user);
                }
            }
        }).subscribe();
        return future;
    }

    @GetMapping(path="/all", produces = "application/json")
    public CompletableFuture<List<User>> getUsers() {
        CompletableFuture<List<User>> future = new CompletableFuture<>();
        this.userRepository.findAll().collect(Collectors.toList()).doOnSuccessOrError((users, error) -> {
            if(error != null) {
                future.completeExceptionally(error);
            } else {
                future.complete(users);
            }
        }).subscribe();
        return future;
    }

    @PostMapping(produces = "application/json", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public CompletableFuture<User> postUser(@RequestBody User user) {
        CompletableFuture<User> future = new CompletableFuture<>();
        this.userRepository.insert(user).doOnSuccessOrError((userResult, error) -> {
            if(error != null) {
                future.completeExceptionally(error);
            } else {
                future.complete(userResult);
            }
        }).subscribe();
        return future;
    }
}
