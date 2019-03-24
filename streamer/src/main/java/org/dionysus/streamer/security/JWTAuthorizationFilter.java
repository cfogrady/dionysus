package org.dionysus.streamer.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    private static Logger logger = LoggerFactory.getLogger(JWTAuthorizationFilter.class);

    private final SecurityConfig securityConfig;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager,
                                  SecurityConfig securityConfig) {
        super(authenticationManager);
        this.securityConfig = securityConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(securityConfig.getHeader());
        boolean tokenInHeader = !StringUtils.isBlank(header);
        //if the token isn't in the header try to get it of the query params
        String token = tokenInHeader ? header : req.getParameter(securityConfig.getHeader());
        if(!StringUtils.isBlank(token)) {
            UsernamePasswordAuthenticationToken authentication = getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(req, res);

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
