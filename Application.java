package com.example.bfh;

import com.example.bfh.workflow.BfhWorkflow;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // keep the WebClient bean here if you want
    @Bean
    WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }

    // run after context is ready, without making Application depend on BfhWorkflow
    @Bean
    org.springframework.boot.CommandLineRunner run(BfhWorkflow workflow) {
        return args -> workflow.execute();
    }
}
