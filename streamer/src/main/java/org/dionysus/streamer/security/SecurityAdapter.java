package org.dionysus.streamer.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import javax.inject.Named;

//@Named
public class SecurityAdapter {
    private static Logger logger = LoggerFactory.getLogger(SecurityAdapter.class);

    /*private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserSecurityService userSecurityService;
    private final SecurityConfig securityConfig;
    private final JsonAuthenticationConverter authenticationConverter;
    private final JWTAuthenticationWebFilter jwtAuthenticationWebFilter;
    private final JWTBuilder jwtBuilder;

    @Inject
    public SecurityAdapter(UserSecurityService userSecurityService,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           SecurityConfig securityConfig,
                           JsonAuthenticationConverter authenticationConverter,
                           JWTAuthenticationWebFilter jwtAuthenticationWebFilter,
                           JWTBuilder jwtBuilder) {
        this.userSecurityService = userSecurityService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.securityConfig = securityConfig;
        this.jwtBuilder = jwtBuilder;
        this.authenticationConverter = authenticationConverter;
        this.jwtAuthenticationWebFilter = jwtAuthenticationWebFilter;
    }*/

}
