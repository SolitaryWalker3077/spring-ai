package com.ai.demo.controller;



import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ds")
public class DeepSeekChatController {

    @Autowired
    private final OpenAiChatModel chatModel;

    public DeepSeekChatController(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/chat")
    public String generate(@RequestParam String message) {
        return chatModel.call(message);
    }
}