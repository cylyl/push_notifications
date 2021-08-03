package com.github.push;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppContext {

    @Bean
    public PushManager pushManager() {
        return new PushManager();
    }
}
