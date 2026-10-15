package com.ai.rag;


import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.jsoup.JsoupDocumentReader;
import org.springframework.ai.reader.jsoup.config.JsoupDocumentReaderConfig;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.util.List;

@SpringBootTest
public class ReadTest {


    @Test
    void testJsonReader(@Value("classpath:/file/web.json") Resource resource) {
        JsonReader reader = new JsonReader(resource);
        List<Document> documentList = reader.get();
        System.out.printf("document size : %d \n", documentList.size());
        for (Document document : documentList) {
            System.out.println(document.getText());
        }
    }

    @Test
    void testJsonReader2(@Value("classpath:/file/web.json") Resource resource) {
        JsonReader reader = new JsonReader(resource, "sites");
        List<Document> documentList = reader.get();
        System.out.printf("document size : %d \n", documentList.size());
        for (Document document : documentList) {
            System.out.println(document.getText());
        }
    }

    @Test
    void testTextReader(@Value("classpath:/file/text.txt") Resource resource) {
        TextReader textReader = new TextReader(resource);
        List<Document> documents = textReader.get();
        System.out.printf("documents: %d\n", documents.size());
        for (Document document : documents) {
            System.out.println(document.getText());
        }
    }


    @Test
    void testTextReader2(@Value("classpath:/file/text.txt") Resource resource) {
        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("aaa", "bbb");
        List<Document> documents = textReader.get();
        System.out.printf("documents: %d\n", documents.size());
        for (Document document : documents) {
            System.out.println(document.getText());
        }
    }


    @Test
    void testJSOUPReader(@Value("classpath:/file/my-page.html") Resource resource) {
        JsoupDocumentReaderConfig config = JsoupDocumentReaderConfig.builder()
                .selector("article p") // 指定从中提取⽂本的元素 (默认为"body")
                .includeLinkUrls(true) // Include link URLs in metadata
                .metadataTags(List.of("author", "date")) // Extract author and date meta tags
                .additionalMetadata("source", "my-page.html") // Add custom metadata
                .build();
        JsoupDocumentReader reader = new JsoupDocumentReader(resource, config);
        List<Document> documents = reader.get();
        System.out.printf("documents: %d\n", documents.size());
        for (Document document : documents) {
            System.out.println(document.getText());
        }
    }


    @Test
    void testReadMarkdown(@Value("classpath:/file/code.md") Resource resource) {
        MarkdownDocumentReaderConfig config =
                MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        .withAdditionalMetadata("filename", "code.md")
                        .build();
        MarkdownDocumentReader reader = new MarkdownDocumentReader(resource,
                config);
        List<Document> documents = reader.get();
        System.out.printf("documents: %d\n", documents.size());
        for (Document document : documents) {
            System.out.println(document.getText());
        }
    }

    //分页读取
    @Test
    void testReadPDFpage(){
        PagePdfDocumentReader pdfReader = new
                PagePdfDocumentReader("classpath:/file/sample1.pdf",
                PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                .withNumberOfTopTextLinesToDelete(0)
                                .build())
                        .withPagesPerDocument(1)
                        .build());
        List<Document> documents = pdfReader.get();
        System.out.printf("documents: %d\n", documents.size());
        for (Document document : documents) {
            System.out.println(document.getText());
        }
    }
    //分段读取
    @Test
    void testReadPDFParagrap(){
        ParagraphPdfDocumentReader pdfReader = new
                ParagraphPdfDocumentReader("classpath:/file/sample1.pdf",
                PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0).withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                .withNumberOfTopTextLinesToDelete(0)
                                .build())
                        .withPagesPerDocument(1)
                        .build());
        List<Document> documents = pdfReader.get();
        System.out.printf("documents: %d\n", documents.size());
        for (Document document : documents) {
            System.out.println(document.getText());
            System.out.println("------------------------------------------------- ");
        }
    }


    @Test
    void testReadTika(@Value("classpath:/file/tika.docx") Resource resource){
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
        List<Document> documents = tikaDocumentReader.get();
        System.out.printf("documents: %d\n", documents.size());
        for (Document document : documents) {
            System.out.println(document.getText());
        }
    }
    @Test
    void testReadPPT(@Value("classpath:/file/ppt-sample.pptx") Resource resource){
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
        List<Document> documents = tikaDocumentReader.get();
        System.out.printf("documents: %d\n", documents.size());
        for (Document document : documents) {
            System.out.println(document.getText());
        }
    }
}
