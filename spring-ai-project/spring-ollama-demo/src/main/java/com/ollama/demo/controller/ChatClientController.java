package com.ollama.demo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatClientController {

    @Autowired
    private ChatClient chatClient;
    public ChatClientController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @RequestMapping("/role")
    public String role(String prompt) {
        return chatClient.prompt().user(prompt).call().content();
    }
}
