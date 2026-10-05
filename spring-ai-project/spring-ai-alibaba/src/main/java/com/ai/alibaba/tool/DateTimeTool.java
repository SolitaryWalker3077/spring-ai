package com.ai.alibaba.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;

public class DateTimeTool {


    //通过@Tool注解去创建工具
    @Tool(description = "Get the current date and time in the user's timezone")
    String getCurrentDatetime() {
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }
}
