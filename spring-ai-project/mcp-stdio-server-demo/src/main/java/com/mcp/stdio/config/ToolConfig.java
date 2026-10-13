package com.mcp.stdio.config;

import com.mcp.stdio.service.UserService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

@Configuration
public class ToolConfig {


    @Bean
    public ToolCallbackProvider getUserInfo(UserService userService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(userService)
                .build();
    }

}
