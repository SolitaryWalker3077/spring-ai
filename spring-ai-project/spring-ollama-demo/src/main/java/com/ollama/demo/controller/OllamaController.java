package com.ollama.demo.controller;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ollama")
public class OllamaController {

    @Autowired
    private OllamaChatModel ollamaChatModel;

    @RequestMapping("/chat")
    public String chat(String message) {
        return ollamaChatModel.call(message);
    }

    /*
    *   流式响应
    * */
    @RequestMapping(value = "/stream",produces = "text/html;charset=utf-8")
    public Flux<String> stream(String message) {
        return ollamaChatModel.stream(message);
    }
}
