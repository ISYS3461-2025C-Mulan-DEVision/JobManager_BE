package com.devision.job_manager_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class JobManagerGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobManagerGatewayApplication.class, args);
	}

}
