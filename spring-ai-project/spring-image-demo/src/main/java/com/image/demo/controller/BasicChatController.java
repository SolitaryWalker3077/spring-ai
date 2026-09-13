package com.image.demo.controller;

import ai.z.openapi.ZhipuAiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/chat")
@RestController
public class BasicChatController {

    @Autowired
    private  ZhipuAiClient client;

    public BasicChatController(ZhipuAiClient client) {
        this.client = client;
    }

    @RequestMapping("/basic")
    public String basicClient(String userInput) {
        return this.client.chat().
    }

}
