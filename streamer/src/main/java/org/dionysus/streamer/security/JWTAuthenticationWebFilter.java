package org.dionysus.streamer.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;

@Named
public class JWTAuthenticationWebFilter implements WebFilter {

    private static Logger logger = LoggerFactory.getLogger(JWTAuthenticationWebFilter.class);

    private final SecurityConfig securityConfig;

    @Inject
    public JWTAuthenticationWebFilter(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        return webFilterChain.filter(serverWebExchange)
                .subscriberContext(context -> {
                    Mono<SecurityContext> secContextPublisher = context.<Mono<SecurityContext>>get(SecurityContext.class);
                    secContextPublisher = secContextPublisher
                            .defaultIfEmpty(new SecurityContextImpl())
                            .doOnNext(securityContext -> {
                                if(securityContext.getAuthentication() == null) {
                                    securityContext.setAuthentication(getJWTAuthentication(serverWebExchange));
                                }
                            });
                    return ReactiveSecurityContextHolder
                            .withSecurityContext(secContextPublisher);
                });
    }

    private Authentication getJWTAuthentication(ServerWebExchange serverWebExchange) {
        logger.info("jwt auth");
        String header = serverWebExchange.getRequest().getHeaders().getFirst(securityConfig.getHeader());
        boolean tokenInHeader = !StringUtils.isBlank(header);
        //if the token isn't in the header try to get it of the query params
        String token = tokenInHeader ? header : serverWebExchange.getRequest().getQueryParams().getFirst(securityConfig.getHeader());
        if(!StringUtils.isBlank(token)) {
            return getAuthentication(token);
        }
        return null;
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String header) {
        String username = JWT.require(Algorithm.HMAC512(securityConfig.getSecretBytes()))
                .build()
                .verify(header)
                .getSubject();
        if(username != null) {
            return new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
        }
        return null;
    }
}
