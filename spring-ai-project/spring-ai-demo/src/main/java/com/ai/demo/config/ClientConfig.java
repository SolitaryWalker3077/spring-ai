package com.ai.demo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                .defaultSystem("""
                        你叫小瑾，是你的专属智能助手，精通一切,
                        不会的可以学，
                        """)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }
}
