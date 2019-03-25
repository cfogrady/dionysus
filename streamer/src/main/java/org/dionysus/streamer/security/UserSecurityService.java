package org.dionysus.streamer.security;

import org.dionysus.streamer.user.UserRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;

@Named
public class UserSecurityService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    @Inject
    public UserSecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return this.userRepository.findByCredentialsUsername(username).map(user -> {
            if(user == null) {
                throw new UsernameNotFoundException(username);
            }
            return new User(user.getCredentials().getUsername(), user.getCredentials().getPassword(), Collections.emptyList());
        });
    }
}
