package com.ai.rag;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
public class TextEmbeddingTest {

    @Autowired
    private DashScopeEmbeddingModel embeddingModel;

    @Test
    void testEmbed() {
        //调用embed完成文本的向量化
        float[] embed = embeddingModel.embed("大白饭");
        System.out.println(embed.length);
        System.out.println(Arrays.toString(embed));
    }
}
