package org.dionysus.streamer.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

@Configuration
@EnableWebFluxSecurity
public class SecuritySpringConfig {
    private static Logger logger = LoggerFactory.getLogger(SecuritySpringConfig.class);

    public static final String JSON_AUTH_FILTER = "jsonAuthFilter";

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Inject
    public SecurityWebFilterChain securitygWebFilterChain(
            ServerHttpSecurity http,
            @Named(JSON_AUTH_FILTER) AuthenticationWebFilter jsonAuthenticationFilter,
            JWTAuthenticationWebFilter jwtAuthenticationWebFilter) {
        return http.cors().and().csrf().disable().authorizeExchange()
                .anyExchange().authenticated()
                .and()
                .addFilterAt(jsonAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
        // this disables session creation on Spring Security
        //.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).build();
    }

    @Bean
    @Inject
    public ReactiveAuthenticationManager authenticationManager(BCryptPasswordEncoder bCryptPasswordEncoder,
                                                                UserSecurityService userSecurityService) {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userSecurityService);
        authenticationManager.setPasswordEncoder(bCryptPasswordEncoder);

        return authenticationManager;
    }

    @Bean
    @Named(JSON_AUTH_FILTER)
    @Inject
    public AuthenticationWebFilter jsonAuthenticationFilter(ReactiveAuthenticationManager authenticationManager,
                                                            JsonAuthenticationConverter authenticationConverter,
                                                            SecurityConfig securityConfig,
                                                            JWTBuilder jwtBuilder) {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(authenticationManager);
        filter.setServerAuthenticationConverter(authenticationConverter);
        filter.setRequiresAuthenticationMatcher(
                ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/login")
        );
        filter.setAuthenticationSuccessHandler((filterExchange, authentication) -> {
            logger.info("json auth success");
            filterExchange.getExchange().getResponse().getHeaders().add(securityConfig.getHeader(), jwtBuilder.buildJWT(authentication.getName()));
            filterExchange.getExchange().getResponse().setStatusCode(HttpStatus.OK);
            filterExchange.getExchange().getResponse().setComplete();
            return Mono.empty();
        });
        return filter;
    }

}
