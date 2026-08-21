package com.chat.bot.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfiguration {


    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder().build();
    }

//    @Bean
//    public ChatClient ollamaChatClient(OllamaChatModel model,ChatMemory chatMemory) {
//       return ChatClient.builder(model)
//               .defaultSystem("我是小瑾,主要负责java方向学习")
//               .defaultAdvisors(new SimpleLoggerAdvisor(),MessageChatMemoryAdvisor.builder(chatMemory).build())
//               .build();
//    }
    @Bean
    public ChatClient deepsekChatClient(ChatClient.Builder chatClientBuilder,ChatMemory chatMemory) {
        return chatClientBuilder
                .defaultSystem("""
                        你叫小瑾，是你的专属智能助手，精通一切,
                        不会的可以学，
                        """)
                .defaultAdvisors(new SimpleLoggerAdvisor(),MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}
