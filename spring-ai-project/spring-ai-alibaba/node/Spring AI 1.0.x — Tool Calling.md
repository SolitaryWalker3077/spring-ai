# Spring AI 1.0.x — Tool Calling 学习笔记

## 一、Tool Calling 概述

**Tool Calling（工具调用）** 是让大模型通过外部工具获取信息或执行具体操作的机制。

主要解决两类问题：

- **信息获取**：查询天气、数据库、文件、第三方 API 等。
- **业务操作**：发送消息、创建订单、执行任务等。

**核心原理：大模型只决定是否调用工具、调用哪个工具以及传递什么参数；真正执行工具的是 Spring AI 应用程序。**

### 1.1 整体执行流程

```text
用户发起请求
    ↓
ChatClient
    ↓
ChatModel
    ↓
AI Model
    │
    │ 判断需要调用工具
    │ 返回工具名称和 JSON 参数
    ↓
ToolCallingManager
    │
    │ 根据工具名称查找 ToolCallback
    ↓
ToolCallback
    │
    │ 执行具体 Java 方法
    ↓
获取工具执行结果
    ↓
判断 returnDirect
    │
    ├── true → 直接返回调用方
    │
    └── false → 将结果重新交给 AI Model
                       ↓
                  生成最终回答
```

这里的 `ChatModel` 负责组织模型交互及默认工具调用循环，`ToolCallingManager` 负责具体工具执行。

---

## 二、工具的两种主要定义方式

### 2.1 声明式定义：@Tool

使用 `@Tool` 注解把普通 Java 方法声明为大模型可以调用的工具。

```java
@Component
public class WeatherTools {

    @Tool(
        name = "getWeather",
        description = "根据城市名称查询天气",
        returnDirect = false
    )
    public String getWeather(
        @ToolParam(description = "城市名称")
        String city
    ) {
        // 这里可以替换为真实天气 API
        return city + "：晴，25°C";
    }
}
```

使用 `ChatClient` 注册：

```java
String result = ChatClient.create(chatModel)
    .prompt()
    .user("查询武汉的天气")
    .tools(weatherTools)
    .call()
    .content();
```

**@Tool 常用属性**

| 属性                | 作用              |
| ----------------- | --------------- |
| `name`            | 工具名称，默认使用方法名    |
| `description`     | 工具功能描述，帮助模型选择工具 |
| `returnDirect`    | 是否将工具结果直接返回     |
| `resultConverter` | 自定义工具结果转换器      |

`@ToolParam` 用于描述工具参数，常用属性为 `description` 和 `required`。

Spring AI 会根据方法参数自动生成 JSON Schema，提供给大模型。

注意：`@Tool` 不代表该方法每次都一定会执行，是否调用仍取决于模型返回的工具调用请求。

### 2.2 编程式定义：MethodToolCallback

除了注解，还可以通过代码手动创建 `ToolCallback`。

适用于动态注册工具、无法修改原有方法或者需要定制工具定义的场景。

假设存在普通 Java 类：

```java
public class WeatherTools {

    public String getWeather(String city) {
        return city + "：晴，25°C";
    }
}
```

通过反射获取方法，并构建工具对象：

```java
Method method = ReflectionUtils.findMethod(
    WeatherTools.class,
    "getWeather",
    String.class
);

ToolCallback callback = MethodToolCallback.builder()
    .toolDefinition(
        ToolDefinitions.builder(method)
            .name("getWeather")
            .description("查询指定城市的天气")
            .build()
    )
    .toolMethod(method)
    .toolObject(new WeatherTools())
    .toolMetadata(
        ToolMetadata.builder()
            .returnDirect(false)
            .build()
    )
    .build();
```

注册到 `ChatClient`：

```java
chatClient.prompt()
    .user("查询武汉天气")
    .toolCallbacks(callback)
    .call()
    .content();
```

**核心理解**

- `toolDefinition()`：提供工具说明。
- `toolMethod()`：指定执行哪个 Java 方法。
- `toolObject()`：指定方法所属对象。
- `toolMetadata()`：设置工具执行相关元数据。

`MethodToolCallback` 本质上是 Spring AI 对普通 Java 方法的封装，底层通过反射执行对应的方法。

### 2.3 编程式定义：FunctionToolCallback

Spring AI 也可以把 Java 函数式接口封装成工具。

```java
record WeatherRequest(String city) {}

record WeatherResponse(String message) {}

Function<WeatherRequest, WeatherResponse> weatherFn =
    req -> new WeatherResponse(
        req.city() + "：晴，25°C"
    );

ToolCallback callback = FunctionToolCallback
    .builder("getWeather", weatherFn)
    .description("查询城市天气")
    .inputType(WeatherRequest.class)
    .build();
```

也可以使用 `@Bean` 注册函数式接口，再通过 `.toolNames("工具Bean名称")` 动态引用。

**三种方式的选择：**

| 方式                     | 使用场景              |
| ---------------------- | ----------------- |
| `@Tool`                | 日常开发，最推荐          |
| `MethodToolCallback`   | 动态封装普通 Java 方法    |
| `FunctionToolCallback` | 通过 Java 函数式接口定义工具 |

对于一般 Spring AI 应用项目，优先使用 `@Tool` 即可。

---

## 三、核心接口与组件

### 3.1 ToolCallback — 工具核心接口

`ToolCallback` 是 Spring AI 工具的核心抽象，负责提供工具定义和执行能力。

主要方法：

```java
public interface ToolCallback {

    ToolDefinition getToolDefinition();

    ToolMetadata getToolMetadata();

    String call(String toolInput);

    String call(
        String toolInput,
        ToolContext toolContext
    );
}
```

**方法解释**

| 方法                          | 作用             |
| --------------------------- | -------------- |
| `getToolDefinition()`       | 获取工具名称、描述和参数结构 |
| `getToolMetadata()`         | 获取工具执行相关配置     |
| `call(String)`              | 根据 JSON 参数执行工具 |
| `call(String, ToolContext)` | 使用参数和额外上下文执行工具 |

其中 `getToolMetadata()` 和带 `ToolContext` 的 `call()` 在接口中提供了默认实现。

**重点：ToolCallback = 工具定义 + 工具执行能力。**

### 3.2 MethodToolCallback — 方法工具实现类

`MethodToolCallback` 实现了 `ToolCallback` 接口。

主要负责：

1. 保存 Java 方法及其所属对象。
2. 将模型提供的 JSON 参数转换成 Java 方法参数。
3. 通过反射执行对应的方法。
4. 使用结果转换器，将 Java 返回值转换为字符串。

调用逻辑可以简化为：

```text
call(toolInput, toolContext)
        ↓
解析 JSON 参数
        ↓
构造 Java 方法参数
        ↓
反射调用目标方法
        ↓
处理方法返回值
        ↓
转换成 String
```

**它解决的问题：让大模型输出的工具调用请求能够对应到真正的 Java 方法执行。**

### 3.3 ToolDefinition — 工具定义

`ToolDefinition` 负责告诉大模型：有哪些工具、工具能做什么、调用时需要传入什么参数。

核心方法：

```java
public interface ToolDefinition {

    String name();

    String description();

    String inputSchema();
}
```

其中 `inputSchema()` 返回 JSON Schema 格式的参数说明。

例如：

```json
{
  "type": "object",
  "properties": {
    "city": {
      "type": "string",
      "description": "城市名称"
    }
  },
  "required": ["city"]
}
```

大模型根据这些信息产生类似的工具请求：

```json
{
  "name": "getWeather",
  "arguments": {
    "city": "武汉"
  }
}
```

**ToolDefinition 只是工具说明书，并不负责执行工具。**

### 3.4 ToolMetadata — 工具元数据

用于控制工具执行后的部分行为。

最常用的属性是 `returnDirect`。

```java
ToolMetadata metadata = ToolMetadata.builder()
    .returnDirect(true)
    .build();
```

声明式工具通过 `@Tool(returnDirect = true)` 实现类似配置。

### 3.5 ToolContext — 工具上下文

`ToolContext` 用于在工具执行时传递额外信息。

例如：

- 当前用户 ID
- 租户 ID
- 业务上下文
- 服务器侧的权限信息

示例：

```java
@Tool(description = "查询用户订单")
public String getOrders(ToolContext context) {

    Long userId = (Long)
        context.getContext().get("userId");

    return orderService.findByUserId(userId);
}
```

调用工具时：

```java
chatClient.prompt()
    .user("查询我的订单")
    .tools(orderTools)
    .toolContext(Map.of("userId", currentUserId))
    .call()
    .content();
```

这里 `currentUserId` 应由服务器根据已认证的用户信息取得，不能直接信任大模型生成的用户 ID。

**ToolContext 与普通工具参数的区别：**

| 对比      | 普通参数             | ToolContext   |
| ------- | ---------------- | ------------- |
| 参数来源    | 大模型生成            | 应用程序提供        |
| 是否发送给模型 | 参数 Schema 会提供给模型 | 上下文数据默认不发送给模型 |
| 主要用途    | 工具业务输入           | 服务端上下文、安全信息   |

注意：`ToolContext` 本身不能替代权限校验。工具执行敏感操作时，仍然需要服务端进行认证和授权。

### 3.6 ToolCallbackProvider 与 ToolCallbackResolver

这两个接口作为补充了解：

- `ToolCallbackProvider`：统一提供一组 `ToolCallback`，常用于批量注册或集成外部工具。
- `ToolCallbackResolver`：根据工具名称解析相应的 `ToolCallback`，常用于动态工具注册。

日常开发只需要理解职责，不必实现这两个接口。

---

## 四、returnDirect — 工具结果返回策略

`returnDirect` 是工具的一个配置属性，而不是独立接口。

### 4.1 returnDirect=false（默认）

```text
用户问题
   ↓
大模型请求调用工具
   ↓
Spring AI 执行工具
   ↓
返回工具结果
   ↓
再次调用大模型
   ↓
大模型组织答案
   ↓
返回调用方
```

适合天气分析、数据库结果解释、多工具协作以及需要模型继续推理的场景。

### 4.2 returnDirect=true

```text
用户问题
   ↓
大模型请求调用工具
   ↓
Spring AI 执行工具
   ↓
直接返回工具结果
```

适合工具本身已经产生完整答案、不需要模型再次加工的场景。

可以减少后续模型调用的开销。

### 4.3 重要细节

1. `returnDirect=true` 不会跳过第一次模型调用。
2. `returnDirect=false` 是默认值。
3. 同一轮调用多个工具时，必须所有工具都配置 `returnDirect=true`，结果才会直接返回。
4. 该属性控制的是工具执行后的处理方式，而不是工具是否执行。

---

## 五、ToolCallingManager — 工具执行管理器

`ToolCallingManager` 是 Spring AI 1.0.x 中管理工具执行生命周期的核心接口。

主要有两个方法：

```java
public interface ToolCallingManager {

    List<ToolDefinition> resolveToolDefinitions(
        ToolCallingChatOptions chatOptions
    );

    ToolExecutionResult executeToolCalls(
        Prompt prompt,
        ChatResponse chatResponse
    );
}
```

### 5.1 resolveToolDefinitions()

负责解析当前请求配置的工具定义。

主要流程：

1. 从 `ToolCallingChatOptions` 获取已注册的 `ToolCallback`。
2. 对仅通过名称注册的工具，使用 `ToolCallbackResolver` 查找。
3. 提取工具的 `ToolDefinition`。
4. 将定义提供给模型，让模型知道可以使用哪些工具。

### 5.2 executeToolCalls()

负责执行大模型提出的工具调用请求。

主要流程：

1. 从 `ChatResponse` 中提取模型的 ToolCall。
2. 解析工具名称和 JSON 参数。
3. 根据工具名称查找对应 `ToolCallback`。
4. 调用 `ToolCallback.call()` 执行工具。
5. 收集执行结果并创建 `ToolResponseMessage`。
6. 根据工具元数据计算 `returnDirect`。
7. 返回 `ToolExecutionResult`。

`ToolExecutionResult` 包含工具调用后的消息历史，以及是否直接返回的标志。

Spring AI 默认使用 `DefaultToolCallingManager` 实现该接口。

---

## 六、Tool Calling 源码阅读

以 Spring AI 1.0.x 为准，只需要理解以下核心调用链。

### 6.1 工具注册阶段

```text
@Tool 注解
    ↓
ToolCallbacks.from(...)
    ↓
MethodToolCallback
    ↓
ToolDefinition + ToolMetadata
    ↓
注册到 ChatClient / ChatModel
```

`@Tool` 标注的方法最终可以转换为 `ToolCallback`，这就是声明式定义和编程式定义能够使用同一套执行机制的原因。

### 6.2 工具执行阶段

```text
ChatModel
    ↓
AI Model 返回 ToolCall
    ↓
ChatModel 判断是否需要执行工具
    ↓
DefaultToolCallingManager.executeToolCalls()
    ↓
根据名称匹配 ToolCallback
    ↓
ToolCallback.call(arguments, toolContext)
    ↓
MethodToolCallback 执行 Java 方法
    ↓
ToolResponseMessage
    ↓
ToolExecutionResult
    ↓
ChatModel 判断 returnDirect
```

实际源码中，`DefaultToolCallingManager` 首先在当前请求的工具列表里匹配名称，如果没有找到，就尝试通过 `ToolCallbackResolver` 解析。

执行工具的关键调用是：

`toolCallback.call(finalToolInputArguments, toolContext)`

并通过 `ToolExecutionExceptionProcessor` 处理工具执行异常。

对于同一轮多个工具的 `returnDirect`，源码使用逻辑与（AND）合并，因此只有全部为 `true`，最终才为 `true`。

### 6.3 建议阅读的源码顺序

1. `ToolCallback` — 先看工具的统一抽象。
2. `ToolDefinition` — 了解模型看到的工具描述。
3. `MethodToolCallback` — 了解普通 Java 方法如何被执行。
4. `ToolCallingManager` — 了解工具执行的管理接口。
5. `DefaultToolCallingManager` — 重点看 `executeToolCalls()` 和 `executeToolCall()`。

对于当前阶段，源码阅读到能够解释以上调用链即可，不需要继续追踪模型 SDK、序列化底层或反射实现细节。

---

## 七、开发时的注意事项

- 工具名称在同一次请求中应保持唯一。
- `description` 要写清楚工具的用途、适用条件和参数要求。
- `@ToolParam` 应明确描述参数，避免模型传入错误数据。
- 不能完全信任模型生成的工具参数，需要在 Java 业务层进行校验。
- 对于删除数据、支付、发送消息等有副作用的操作，必须进行服务端权限检查，必要时增加用户确认。
- 工具执行异常可能由 Spring AI 转换为模型可理解的错误信息，也可能抛给调用方，应根据业务设计异常处理策略。

---

## 八、面试与复习重点

**Q1：Tool Calling 的底层原理是什么？**

大模型根据工具定义生成工具名称和参数，Spring AI 通过 `ToolCallingManager` 找到对应的 `ToolCallback` 并执行，随后根据配置将结果返回给模型或调用方。

**Q2：ToolCallback 和 MethodToolCallback 的区别？**

`ToolCallback` 是工具的统一接口；`MethodToolCallback` 是它的实现类，用于将普通 Java 方法封装成可执行工具。

**Q3：ToolDefinition 的作用是什么？**

描述工具名称、功能和参数 JSON Schema，帮助大模型决定什么时候调用工具以及如何生成参数。

**Q4：ToolContext 有什么作用？**

向工具传递应用程序提供的额外上下文，例如用户身份、租户信息和业务数据，这些数据默认不会作为工具参数发送给模型。

**Q5：声明式与编程式工具定义的区别？**

声明式使用 `@Tool` 注解，简洁方便；编程式手动构建 `ToolCallback`，更加灵活，适合动态工具和特殊扩展场景。

**Q6：returnDirect 的作用是什么？**

控制工具执行后是将结果直接返回，还是重新交给大模型处理。

### 最终总结

**Tool Calling 的六个核心概念：**

- `ToolDefinition`：工具说明书。
- `ToolCallback`：工具的统一抽象。
- `MethodToolCallback`：Java 方法工具实现。
- `ToolContext`：工具运行时上下文。
- `ToolCallingManager`：工具执行管理器。
- `returnDirect`：工具结果返回策略。

掌握工具定义、注册、执行以及结果返回的完整链路，就具备了 Spring AI 1.0.x Tool Calling 的主要应用开发能力。
