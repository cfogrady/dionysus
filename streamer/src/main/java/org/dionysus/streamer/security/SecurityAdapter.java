package org.dionysus.streamer.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.inject.Inject;
import javax.inject.Named;

@Named
@EnableWebSecurity
public class SecurityAdapter extends WebSecurityConfigurerAdapter {
    private static Logger logger = LoggerFactory.getLogger(SecurityAdapter.class);

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserSecurityService userSecurityService;
    private final ObjectMapper objectMapper;
    private final SecurityConfig securityConfig;

    @Inject
    public SecurityAdapter(UserSecurityService userSecurityService,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           ObjectMapper objectMapper,
                           SecurityConfig securityConfig) {
        this.userSecurityService = userSecurityService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.objectMapper = objectMapper;
        this.securityConfig = securityConfig;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), objectMapper, securityConfig))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), securityConfig))
                // this disables session creation on Spring Security
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userSecurityService).passwordEncoder(bCryptPasswordEncoder);
    }
}
