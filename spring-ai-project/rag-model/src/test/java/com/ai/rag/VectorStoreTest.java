package com.ai.rag;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class VectorStoreTest {

    @Autowired
    private DashScopeEmbeddingModel embeddingModel;

    private SimpleVectorStore simpleVectorStore;


    @BeforeEach
    void setUp() {
         simpleVectorStore = SimpleVectorStore.builder(embeddingModel).build();
    }

    /**
     *向量的存储
     * */
    @Test
    void save() {
        Document doc = Document.builder()
                .text("2025年夏季奥运会将于巴黎举⾏, 预计吸引全球数百万观众")
                .build();
        Document doc2 = Document.builder()
                .text("对⽐学习框架下多语⾔BERT模型的语义表⽰分析")
                .build();
        Document doc3 = Document.builder()
                .text("暮⾊中的⽼槐树在⻛中摇曳, 枯枝划破绯红的晚霞")
                .build();
        List<Document> list = List.of(doc,doc2,doc3);
        simpleVectorStore.add(list);
        System.out.println("向量数据库存储成功");
    }

}
