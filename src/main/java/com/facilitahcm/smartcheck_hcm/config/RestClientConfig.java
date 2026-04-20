package com.facilitahcm.smartcheck_hcm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("User-Agent", "Smartcheck-App");
            request.getHeaders().add("Accept", "application/json");
            return execution.execute(request, body);
        });

        return restTemplate;
    }
}
