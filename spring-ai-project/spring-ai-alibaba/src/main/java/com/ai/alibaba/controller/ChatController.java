package com.ai.alibaba.controller;


import com.ai.alibaba.tool.DateTimeTool;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private ChatClient chatClient;

    public ChatController(DashScopeChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    //不使用工具
    @RequestMapping("/call")
    public String call(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    //使用工具
    @RequestMapping("/callTool")
    public String callAndTool(String message) {
        return chatClient.prompt()
                .user(message)
                .tools(new DateTimeTool()) //应用工具
                .call()
                .content();
    }
}
