package com.ai.alibaba;


import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ChatModelTest {

    @Autowired
    private DashScopeChatModel chatModel;

    @Test
    public void chat() {
        String message = chatModel.call("你是谁");
        System.out.println(message);
    }
}
