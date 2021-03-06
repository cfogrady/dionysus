package org.dionysus.streamer.user;

import org.dionysus.streamer.user.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {
    String USER_COLLECTION = "user";

    Mono<User> findByCredentialsUsername(String username);
}
