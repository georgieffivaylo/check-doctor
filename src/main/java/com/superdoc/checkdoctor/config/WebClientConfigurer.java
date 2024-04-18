package com.superdoc.checkdoctor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfigurer {

    @Value("${superdoc.uri}")
    private String superDocUri;

    @Bean
    public WebClient superDocWebClient() {
        return  WebClient.builder()
                .baseUrl(superDocUri)
                .build();
    }
}
