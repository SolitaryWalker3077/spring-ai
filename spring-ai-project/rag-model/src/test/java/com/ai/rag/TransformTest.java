package com.ai.rag;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.junit.jupiter.api.Test;

import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.ai.model.transformer.SummaryMetadataEnricher;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.util.List;

@SpringBootTest
public class TransformTest {


    @Autowired
    private ChatModel chatModel;



    /**
     * 测试 TokenTextSplitter 文档拆分功能
     * 先通过 TextReader 加载 classpath 下的 rule.txt 文本文件，
     * 再使用 TokenTextSplitter 按 Token 边界将长文档拆分为多个子文档，
     * 最后遍历输出每个子文档的文本内容和元数据信息
     */
    @Test
    void testSplitter(@Value("classpath:/file/rule.txt") Resource resource) {
        // 创建 TextReader 加载文本文件资源
        TextReader reader = new TextReader(resource);

        // 读取文件，将文本内容转换为 Document 列表
        List<Document> documents = reader.get();

        // 创建基于 Token 的文本拆分器
        TokenTextSplitter splitter = new TokenTextSplitter();
        // 对文档列表执行拆分，返回拆分后的子文档列表
        List<Document> apply = splitter.apply(documents);
        // 输出拆分后的子文档数量
        System.out.println("文本拆分 size :" + apply.size());
        // 遍历每个子文档，输出其文本内容和元数据
        apply.forEach(document -> {
            System.out.println(document.getText());
            System.out.println(document.getMetadata());
        });
    }

    /**
     * 测试 KeywordMetadataEnricher 关键词元数据增强功能
     * 完整的 RAG ETL 流程演示：
     * 1. 使用 TextReader 读取文本文件
     * 2. 使用 TokenTextSplitter 将长文档拆分为子文档
     * 3. 使用 KeywordMetadataEnricher 通过 LLM 为每个子文档提取关键词元数据
     * 4. 输出每个子文档的关键词和文本内容
     */
    @Test
    public void testKeywordMetadataEnricher(@Value("classpath:file/rule.txt") Resource resource) {
        // Step 1: 文本读取 - 从 classpath 加载 rule.txt 文件
        TextReader textReader = new TextReader(resource);
        List<Document> documents = textReader.read();

        // Step 2: 文件分割 - 按 Token 边界将长文档拆分为多个子文档
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> apply = splitter.apply(documents);

        // Step 3: 关键词提取 - 使用 Builder 模式构建 KeywordMetadataEnricher
//        KeywordMetadataEnricher enricher = KeywordMetadataEnricher.builder(chatModel)
//                .keywordCount(3)
//                .build();
        // 指定自定义的关键词提取模板，限制只返回预定义范围内的关键词，每个文档提取 3 个
        KeywordMetadataEnricher enricher = KeywordMetadataEnricher.builder(chatModel)
                .keywordsTemplate(new PromptTemplate("""
                        根据给定的文本: {context_str}, 生成关键字, 只允许以下关键字
                        [会员宗旨, 会员类型, 会员注册, 积分制度, 会员权益, 会员行为, 会员服务, 公告, 隐私保护]
                        只返回关键字, 其他信息不返回
                        """))
                .keywordCount(3)
                .build();

        // 对拆分后的子文档执行关键词提取，将关键词写入每个 Document 的 metadata 中
        apply = enricher.apply(apply);

        // Step 4: 遍历输出每个子文档的关键词元数据和文本内容
        apply.forEach(document -> {
            System.out.println(document.getMetadata().get("excerpt_keywords"));
            System.out.println(document.getText());
        });
    }

    @Test
    public void testSummaryEnricher(@Value("classpath:file/rule.txt") Resource resource) {

        TextReader textReader = new TextReader(resource);
        List<Document> documents = textReader.read();

        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> splitterDoc = splitter.apply(documents);

//        //摘要生成
//        SummaryMetadataEnricher summaryMetadataEnricher = new SummaryMetadataEnricher(chatModel,
//                List.of(SummaryMetadataEnricher.SummaryType.PREVIOUS, SummaryMetadataEnricher.SummaryType.CURRENT, SummaryMetadataEnricher.SummaryType.NEXT));

        String summaryTemplate = """
                根据给定的文本: 
                {context_str} 
                生成摘要信息,限制50字以内,只返回摘要信息,其他信息不返回
                """;
        SummaryMetadataEnricher summaryMetadataEnricher = new SummaryMetadataEnricher(chatModel,
                List.of(SummaryMetadataEnricher.SummaryType.PREVIOUS, SummaryMetadataEnricher.SummaryType.CURRENT, SummaryMetadataEnricher.SummaryType.NEXT),
                summaryTemplate, MetadataMode.NONE);


        List<Document> enricherDoc = summaryMetadataEnricher.apply(splitterDoc);
        enricherDoc.forEach(document -> {
            System.out.println(document.getMetadata().get("excerpt_keywords"));
            System.out.println(document.getText());
        });
    }

}