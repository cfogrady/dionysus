package org.dionysus.streamer.security;

import org.dionysus.streamer.user.User;
import org.dionysus.streamer.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;

@Named
public class UserSecurityService implements UserDetailsService {

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
