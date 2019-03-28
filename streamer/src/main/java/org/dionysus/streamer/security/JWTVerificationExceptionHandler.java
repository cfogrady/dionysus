package org.dionysus.streamer.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.UncheckedIOException;

@Named
@Order(-2)
public class JWTVerificationExceptionHandler implements WebExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(JWTVerificationExceptionHandler.class);

    private final ObjectMapper objectMapper;

    @Inject
    public JWTVerificationExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // TODO: Add unit test
    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {
        logger.debug("JWTVerificationException");
        if(throwable instanceof JWTVerificationException) {
            if(throwable instanceof TokenExpiredException) {
                //419 which is what we want
                serverWebExchange.getResponse().setStatusCode(HttpStatus.INSUFFICIENT_SPACE_ON_RESOURCE);
                serverWebExchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
                ErrorResponse response = new ErrorResponse();
                response.setStatus(419);
                response.setError("Authentication Timeout");
                response.setMessage("Authentication token has expired");
                try {
                    byte[] bytes = objectMapper.writeValueAsBytes(response);
                    DataBuffer dataBufer = serverWebExchange.getResponse().bufferFactory().wrap(bytes);
                    return serverWebExchange.getResponse().writeWith(Flux.just(dataBufer));
                } catch (JsonProcessingException e) {
                    throw new UncheckedIOException(e);
                }
            }
            serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return serverWebExchange.getResponse().setComplete();
        }
        return Mono.error(throwable);
    }

    public static class ErrorResponse {
        private int status;
        private String error;
        private String message;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
