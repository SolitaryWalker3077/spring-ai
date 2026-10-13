package com.ai.mcp.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/chat")
public class ChatController {

    private ChatClient chatClient;

    public ChatController(DashScopeChatModel chatModel, ToolCallbackProvider toolCallbackProvider) {
        this.chatClient = ChatClient.builder(chatModel)
                .defaultToolCallbacks(toolCallbackProvider)
                .build();
    }


    @RequestMapping("/generate")
    public String generate(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
