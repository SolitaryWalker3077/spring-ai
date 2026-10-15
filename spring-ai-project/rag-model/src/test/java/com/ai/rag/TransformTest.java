package com.ai.rag;

import org.springframework.ai.document.Document;
import org.junit.jupiter.api.Test;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.util.List;

@SpringBootTest
public class TransformTest {


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
}