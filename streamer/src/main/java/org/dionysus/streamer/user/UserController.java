package org.dionysus.streamer.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@Singleton
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Inject
    public UserController(UserRepository userRepository,
                          BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping(path="/{id}", produces = "application/json")
    public CompletableFuture<User> getUser(@PathVariable String id) {
        CompletableFuture<User> future = new CompletableFuture<>();
        this.userRepository.findById(id).doOnSuccessOrError((user, error) -> {
            if(error != null) {
                future.completeExceptionally(error);
            } else {
                //redact password
                user.getCredentials().setPassword(null);
                future.complete(user);
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
                // redact passwords
                users.forEach(user -> user.getCredentials().setPassword(null));
                future.complete(users);
            }
        }).subscribe();
        return future;
    }

    @PostMapping(produces = "application/json", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public CompletableFuture<User> putUser(@RequestBody User user) {
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
