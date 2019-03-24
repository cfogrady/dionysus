package org.dionysus.streamer.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dionysus.streamer.user.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UncheckedIOException;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;
    private final SecurityConfig securityConfig;
    private final JWTBuilder jwtBuilder;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager,
                                   ObjectMapper objectMapper,
                                   SecurityConfig securityConfig,
                                   JWTBuilder jwtBuilder) {
        this.authenticationManager = authenticationManager;
        this.objectMapper = objectMapper;
        this.securityConfig = securityConfig;
        this.jwtBuilder = jwtBuilder;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            UserCredentials credentials = objectMapper.readValue(req.getInputStream(), UserCredentials.class);
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(credentials.getUsername(),
                            credentials.getPassword()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        String token = jwtBuilder.buildJWT(((User)auth.getPrincipal()).getUsername());

        res.addHeader(securityConfig.getHeader(), token);
    }
}
