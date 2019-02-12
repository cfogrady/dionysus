package org.dionysus.streamer.security;

import org.dionysus.streamer.user.User;
import org.dionysus.streamer.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.client.HttpClientErrorException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;

@Singleton
public class UserSecurityService implements UserDetailsService {
    private static Logger logger = LoggerFactory.getLogger(UserSecurityService.class);

    private final UserRepository userRepository;

    @Inject
    public UserSecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByCredentialsUsername(username).blockOptional().orElseThrow(() -> new UsernameNotFoundException(username));
        return new org.springframework.security.core.userdetails.User(user.getCredentials().getUsername(), user.getCredentials().getPassword(), Collections.emptyList());
    }
}
