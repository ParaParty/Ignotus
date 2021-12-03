# 一些基础支持库

## 功能
1. `ApplicationContextRegister.applicationContext`：Spring 上下文全局静态变量。
2. `Base64Utils`：在 `java.util.Base64` 的基础上添加了字符串到字符串的编码与解码方法。
3. `JSON`：使用 Jackson Mock 了一些 FastJson 的便捷工具。
4. `Miscellaneous`：一些杂项，主要是 `UUIDUtils` 和字符串乘数字的语法糖（如下图所示）。
    ```console
    $ python
    >>> "2" + "3" * 2
    '233'
    >>>
    ```

## 配置
### 自定义 JSON ObjectMapper
本库内的 `JSON` 工具使用了 Jackson ObjectMapper 来作为序列化与反序列化工具。
为尽可能满足各种需求，本库提供了一个自定义 ObjectMapper 的方法：
自己实现一个 `JSONMapperProvider` 接口然后将这个类放入 Spring 对象容器中。

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.tairitsu.ignotus.support.service.JSONMapperProvider;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class JSONMapperProviderConfig implements JSONMapperProvider {
    ObjectMapper objectMapper = JsonMapper.builder()
        .addModule(new ParameterNamesModule())
        .addModule(new Jdk8Module())
        .addModule(new JavaTimeModule())
        .addModule(new KotlinModule.Builder().build())
        .build();
    
    @NotNull
    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
```

