package org.dionysus.streamer.security;


import javax.inject.Named;
import java.time.Instant;

/**
 * Provider class. Acts against outside state. In this case time.
 */
@Named
public class DateTimeProvider {

    public Instant getNow() {
        return Instant.now();
    }
}
