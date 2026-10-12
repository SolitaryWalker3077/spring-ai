package com.ai.alibaba.config;

import com.ai.alibaba.tool.WeatherTools;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.ai.tool.support.ToolDefinitions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

@Configuration
public class ToolConfig {

    @Bean
    public ToolCallback weatherTool() {
        //先用java代码实现该工具的实现方法,然后通过反射得到该方法
        Method method = ReflectionUtils.findMethod(WeatherTools.class,"getCurrentWeatherByCityName",String.class);

        ToolCallback weatherTools = MethodToolCallback.builder()
                .toolDefinition(ToolDefinitions.builder(method)  //ToolDefinition.builder构建自定义ToolDefinition实例实例
                        .description("根据给定的城市名称, 获取城市当前的天⽓")
                        .build())
                .toolMethod(method)
                .toolObject(new WeatherTools())
                .build();
        return weatherTools;
    }
}
