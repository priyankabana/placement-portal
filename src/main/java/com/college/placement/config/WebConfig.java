package com.college.placement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registers the login check for every URL, except the ones that have to stay
 * open - the login page itself, the stylesheet, the script and the H2 console.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/css/**", "/js/**", "/h2-console/**", "/error",
                        "/favicon.ico");
    }
}
