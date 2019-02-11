package org.dionysus.streamer.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
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

    @Inject
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping("/ping")
    public String index() {
        logger.info("Current Thread: {}", Thread.currentThread().getName());
        return "Greetings from Spring Boot!";
    }

    @GetMapping(path="/{id}", produces = "application/json")
    public CompletableFuture<User> getUser(@PathVariable String id) {
        CompletableFuture<User> future = new CompletableFuture<>();
        this.userRepository.findById(id).doOnSuccessOrError((user, error) -> {
            if(error != null) {
                future.completeExceptionally(error);
            } else {
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
                future.complete(users);
            }
        }).subscribe();
        return future;
    }

    @PostMapping(produces = "application/json", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public CompletableFuture<User> putUser(@RequestBody User user) {
        CompletableFuture<User> future = new CompletableFuture<>();
        logger.info("About to insert {}", Thread.currentThread().getName());
        this.userRepository.insert(user).doOnSuccessOrError((userResult, error) -> {
            logger.info("Inserted {}", Thread.currentThread().getName());
            if(error != null) {
                future.completeExceptionally(error);
            } else {
                future.complete(userResult);
            }
        }).subscribe();
        logger.info("Return future {}", Thread.currentThread().getName());
        return future;
    }
}
