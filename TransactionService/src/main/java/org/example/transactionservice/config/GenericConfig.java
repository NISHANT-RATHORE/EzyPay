package org.example.transactionservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GenericConfig {
    @Bean
    ObjectMapper objectMapper(){
        return new ObjectMapper();
    }
}
