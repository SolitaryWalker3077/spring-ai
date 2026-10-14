# Spring AI RAG快速入门笔记

## 1. 什么是RAG

RAG（Retrieval Augmented
Generation，检索增强生成）是一种结合信息检索和文本生成的AI应用架构。

核心思想：

> 在大模型回答问题之前，从外部知识库检索相关资料，将资料作为上下文增强Prompt，再交给大模型生成答案。

------------------------------------------------------------------------

# 2. RAG三阶段流程

    用户问题
      |
      ↓
    Retrieval（检索）
      |
      ↓
    Augmentation（增强）
      |
      ↓
    Generation（生成）
      |
      ↓
    LLM回答

------------------------------------------------------------------------

# 3. Retrieval（检索）

目标：

根据用户问题，从知识库中找到相关资料。

流程：

    用户Query
     ↓
    Embedding模型
     ↓
    文本向量
     ↓
    VectorStore
     ↓
    相似度搜索
     ↓
    Top-K文档

## Embedding

Embedding负责将文本转换为高维向量。

例如：

    Spring AI是什么？
    ↓
    [0.23,0.54,0.12...]

作用：

让机器可以通过向量计算文本之间的语义相似度。

------------------------------------------------------------------------

## VectorStore

VectorStore负责：

-   保存向量
-   根据向量查询相似内容

核心方法：

``` java
vectorStore.add(documents);
```

保存文档。

``` java
vectorStore.similaritySearch(query);
```

相似度查询。

------------------------------------------------------------------------

# 4. Augmentation（增强）

将检索到的Document加入Prompt。

增强前：

    问题：
    奥运会什么时候举行？

增强后：

    Context:
    2025年夏季奥运会将在巴黎举行

    Question:
    奥运会什么时候举行？

------------------------------------------------------------------------

# 5. Generation（生成）

流程：

    增强后的Prompt
     ↓
    LLM
     ↓
    最终答案

模型根据检索到的知识生成回答。

------------------------------------------------------------------------

# 6. Spring AI RAG架构

    文档
     ↓
    DocumentReader
     ↓
    TextSplitter
     ↓
    EmbeddingModel
     ↓
    VectorStore


    用户问题
     ↓
    问题向量化
     ↓
    VectorStore检索
     ↓
    Top-K文档
     ↓
    Prompt增强
     ↓
    ChatModel
     ↓
    答案

------------------------------------------------------------------------

# 7. Spring AI核心API

## Document

表示知识库中的文本片段。

``` java
Document doc =
Document.builder()
.text("内容")
.build();
```

------------------------------------------------------------------------

## EmbeddingModel

文本转换为向量：

    Text
     ↓
    EmbeddingModel
     ↓
    Vector

------------------------------------------------------------------------

## VectorStore

负责向量存储和搜索。

主要方法：

``` java
add()
```

保存向量。

``` java
similaritySearch()
```

相似度查询。

------------------------------------------------------------------------

# 8. Advisor

Advisor类似Spring中的Interceptor。

执行位置：

    ChatClient
     ↓
    Advisor
     ↓
    ChatModel

作用：

在调用大模型之前增强请求。

例如：

-   修改Prompt
-   添加上下文
-   执行额外逻辑

------------------------------------------------------------------------

# 9. QuestionAnswerAdvisor

QuestionAnswerAdvisor是Spring AI提供的快速RAG实现。

自动完成：

    用户问题
     ↓
    查询VectorStore
     ↓
    获取Document
     ↓
    拼接Context
     ↓
    增强Prompt
     ↓
    调用LLM
     ↓
    返回答案

代码：

``` java
QuestionAnswerAdvisor advisor =
    QuestionAnswerAdvisor
    .builder(vectorStore)
    .build();

chatClient.prompt()
    .advisors(advisor)
    .user(message)
    .call();
```

------------------------------------------------------------------------

# 10. Advisor执行流程

    ChatClient

    ↓

    QuestionAnswerAdvisor

    ↓

    vectorStore.similaritySearch()

    ↓

    Document列表

    ↓

    PromptTemplate

    ↓

    增强Prompt

    ↓

    ChatModel

------------------------------------------------------------------------

# 11. 自定义Prompt模板

可以通过PromptTemplate控制回答方式。

例如：

``` java
PromptTemplate template;
```

可以控制：

-   回答格式
-   幻觉限制
-   不知道时如何回复

------------------------------------------------------------------------

# 12. 手写RAG VS Advisor RAG

## 手写RAG

流程：

    问题
     ↓
    Embedding
     ↓
    VectorStore查询
     ↓
    Document
     ↓
    拼接Prompt
     ↓
    LLM

优点：

理解底层流程。

缺点：

开发复杂。

## Advisor RAG

流程：

    问题
     ↓
    QuestionAnswerAdvisor
     ↓
    自动完成RAG
     ↓
    LLM回答

优点：

开发效率高。

------------------------------------------------------------------------

# 13. 面试回答

Spring AI中可以通过VectorStore和Advisor实现RAG。

首先使用EmbeddingModel将文档转换成向量并保存到VectorStore。

用户请求时，将问题进行向量化，在VectorStore中进行相似度检索。

然后通过QuestionAnswerAdvisor将检索到的Document作为Context增强Prompt。

最后调用ChatModel生成基于知识库的回答。

------------------------------------------------------------------------

# 14. 当前学习重点

需要掌握：

-   RAG整体流程
-   Embedding作用
-   VectorStore作用
-   similaritySearch
-   QuestionAnswerAdvisor使用
-   Prompt增强

暂时不用深入：

-   Embedding数学原理
-   向量数据库底层实现
-   高级RAG优化算法

后续：

    Spring AI
    +
    Tool Calling
    +
    RAG
    +
    Memory

    =
    完整AI应用
