package com.ai.demo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatClientController {
    @Autowired
    private ChatClient chatClient;


    @GetMapping("/client")
    String generation(String userInput) {
        return this.chatClient.prompt()
                .user(userInput)
                .call()
                .content();
    }

    //结构化输出
    record Recipe(String dish, List<String> ingredients) {}
    @GetMapping("/entity")
    public String entity(String userInput) {
        Recipe recipe = this.chatClient.prompt()
                .user(String.format("请帮我⽣成%s的⻝谱", userInput))
                .call()
                .entity(Recipe.class);
        return recipe.toString();
    }


    //流式输出
    @GetMapping(value = "/stream",produces = "text/html;charset=utf-8")
    public Flux<String> stream(String userInput) {
        return this.chatClient.prompt()
                .user(userInput)
                .stream()
                .content();
    }

}
