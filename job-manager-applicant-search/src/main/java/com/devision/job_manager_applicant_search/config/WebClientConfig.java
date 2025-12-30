package com.devision.job_manager_applicant_search.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${subscription.service.url:http://localhost:8085}")
    private String subscriptionServiceUrl;

    @Value("${applicant.service.url:http://localhost:8080}")
    private String applicantServiceUrl;

    @Bean
    public WebClient subscriptionWebClient() {
        return WebClient.builder()
                .baseUrl(subscriptionServiceUrl)
                .build();
    }

    @Bean
    public WebClient applicantWebClient() {
        return WebClient.builder()
                .baseUrl(applicantServiceUrl)
                .build();
    }
}
