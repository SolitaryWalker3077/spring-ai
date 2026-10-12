package com.ai.alibaba.controller;


import com.ai.alibaba.tool.DateTimeTool;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/chatByModel")
public class ChatModelController {

    @Autowired
    private ChatModel chatModel;

    //使用ChatModel添加工具
    public ChatModelController(DashScopeChatModel chatModel) {
        this.chatModel = chatModel;
    }


    @RequestMapping("/callByTool")
    public String callByTools(String message) {
        ToolCallback[] dateTimeTools = ToolCallbacks.from(new DateTimeTool());

        ChatOptions chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(dateTimeTools)
                .build();

        Prompt prompt = new Prompt(message,chatOptions);
        return chatModel.call(prompt)
                .getResult()
                .getOutput()
                .getText();
    }
}
