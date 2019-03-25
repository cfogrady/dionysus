package org.dionysus.streamer.security;



import com.fasterxml.jackson.databind.ObjectMapper;
import org.dionysus.streamer.user.model.UserCredentials;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.UncheckedIOException;

@Named
public class JsonAuthenticationConverter implements ServerAuthenticationConverter {

    private final ObjectMapper objectMapper;

    @Inject
    public JsonAuthenticationConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange serverWebExchange) {
        return serverWebExchange.getRequest().getBody().next().map(body -> {
            try {
                UserCredentials credentials = objectMapper.readValue(body.asInputStream(), UserCredentials.class);
                return new UsernamePasswordAuthenticationToken(
                        credentials.getUsername(),
                        credentials.getPassword()
                );
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        });
    }
}
