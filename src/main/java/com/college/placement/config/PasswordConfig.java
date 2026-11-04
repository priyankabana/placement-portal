package com.college.placement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * One shared BCrypt encoder. Only spring-security-crypto is on the classpath,
 * so this adds hashing without switching on the Spring Security filter chain -
 * the existing AuthInterceptor still handles the sign in check.
 */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
