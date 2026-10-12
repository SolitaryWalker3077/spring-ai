package com.ai.alibaba.controller;


import com.ai.alibaba.tool.DateTimeTool;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/chat")
public class ChatController {

    private ChatClient chatClient;

    //为ChatClient添加默认工具
    public ChatController(DashScopeChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel)
                .defaultTools(new DateTimeTool())
                .build();
    }


    @Autowired
    public ChatController(DashScopeChatModel chatModel,ToolCallback weatherTool) {
        this.chatClient = ChatClient.builder(chatModel)
                .defaultTools(new DateTimeTool())
                .defaultToolCallbacks(weatherTool)
                .build();
    }

    @RequestMapping("/callTool")
    public String callAndTool(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    @RequestMapping("/callTool2")
    public String callAndToo2(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }



    //使用工具
    //运行时工具
//    @RequestMapping("/callTool")
//    public String callAndTool(String message) {
//        return chatClient.prompt()
//                .user(message)
//                .tools(new DateTimeTool()) //应用工具
//                .call()
//                .content();
//    }

    @RequestMapping("/call2")
    public String call2(String message,ToolCallback weatherTool) {

        return chatClient.prompt()
                .toolCallbacks(weatherTool) //工具的定义
                .user(message)
                .call()
                .content();
    }


}
