package com.ai.rag;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;


import java.util.List;

//相似度检索
@SpringBootTest
public class SimilaritySearchTest {

    @Autowired
    private DashScopeEmbeddingModel embeddingModel;

    private SimpleVectorStore simpleVectorStore;

    @Autowired
    private ChatModel chatModel;

    @TestConfiguration
    static class TestConfig{
        @Bean
        public VectorStore vectorStore(DashScopeEmbeddingModel embeddingModel) {
            return SimpleVectorStore.builder(embeddingModel).build();
        }
    }

    @Autowired
    private VectorStore vectorStore;

    //向量存储初始化
    @BeforeEach
    void setUp() {
        Document doc = Document.builder()
                .text("2025年夏季奥运会将于巴黎举⾏, 预计吸引全球数百万观众")
                .build();
        Document doc2 = Document.builder()
                .text("对⽐学习框架下多语⾔BERT模型的语义表⽰分析")
                .build();
        Document doc3 = Document.builder()
                .text("暮⾊中的⽼槐树在⻛中摇曳, 枯枝划破绯红的晚霞")
                .build();
        Document doc4 = Document.builder()
                .text("基于Transformer的预训练模型在机器翻译中的迁移学习研究")
                .build();
        List<Document> list = List.of(doc,doc2,doc3,doc4);
        vectorStore.add(list);
        System.out.println("向量数据库存储成功");
    }


    //检索全部信息
    @Test
    void similarSearch() {
        List<Document> search = vectorStore.similaritySearch("机器学习");
        search.forEach(System.out::println);
    }

    //添加限制条件检索
    @Test
    void similarSearchLimit() {
        SearchRequest request = SearchRequest.builder()
                .query("机器学习")  //查询文本
                .topK(5)           //最多返回5条
                .similarityThreshold(0.45) //相似度阈值0.45
                .build();
        List<Document> documentList = vectorStore.similaritySearch(request);
        documentList.forEach(System.out::println);
    }

    @Test
    void testRag() {
        ChatClient client = ChatClient
                .builder(chatModel)
                .build();
        QuestionAnswerAdvisor advisor = QuestionAnswerAdvisor
                .builder(vectorStore)
                .build();

        String message = "奥运会什么时候举行";
        String content = client.prompt()
                .advisors(advisor)
                .user(message)
                .call()
                .content();
        System.out.println(content);
    }


    @Test
    void testRag2(){
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        String message = "奥运会什么时间举行";
        QuestionAnswerAdvisor advisor = QuestionAnswerAdvisor
                .builder(vectorStore)
                .searchRequest(SearchRequest
                        .builder()
                        .query(message)
                        .similarityThreshold(0.4)
                        .topK(5)
                        .build())
                .build();

        String content = chatClient
                .prompt()
                .advisors(advisor)
                .user(message)
                .call()
                .content();
        System.out.println(content);
    }


    @Test
    void testRag3(){
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        String message = "中国的首都是哪里";
        PromptTemplate template = new PromptTemplate("""
			{query}
            下面是上下文信息
			---------------------
			{question_answer_context}
			---------------------
            根据给定的上下文信息进行回复, 并遵守以下规则:
            1. 如果上下文信息中有相关的知识, 使用上下文的信息进行回复
            2. 如果上下文信息中没有相关的知识, 直接回复"不知道"
            3. 在回复时, 尽量避免使用"根据提供的信息...", "根据上下文信息..."类似的话语
			""");
        QuestionAnswerAdvisor advisor = QuestionAnswerAdvisor
                .builder(vectorStore)
                .promptTemplate(template)
                .searchRequest(SearchRequest
                        .builder()
                        .query(message)
                        .similarityThreshold(0.4)
                        .topK(5)
                        .build())
                .build();

        String content = chatClient
                .prompt()
                .advisors(advisor)
                .user(message)
                .call()
                .content();
        System.out.println(content);
    }
}
