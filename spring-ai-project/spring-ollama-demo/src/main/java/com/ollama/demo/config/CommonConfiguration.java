package com.ollama.demo.config;


import com.ollama.demo.controller.OllamaController;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfiguration {

    @Bean
    public ChatClient chatClient(OllamaChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("你好,我是小瑾,一名程序开发助手,来解决你遇到的问题")
                .build();
    }
}
