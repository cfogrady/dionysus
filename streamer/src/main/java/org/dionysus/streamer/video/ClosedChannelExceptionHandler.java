package org.dionysus.streamer.video;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import javax.inject.Named;
import java.nio.channels.ClosedChannelException;

@Named
@Order(-2)
public class ClosedChannelExceptionHandler implements WebExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(ClosedChannelExceptionHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {
        logger.debug("JWTVerificationException");
        if (throwable instanceof ClosedChannelException) {
            return serverWebExchange.getResponse().setComplete();
        }
        return Mono.error(throwable);
    }
}
