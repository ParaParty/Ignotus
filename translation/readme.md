# Laravel 风格的文本模板模块

## 说明

本文本模板模块主要模仿了 [Laravel Localization](https://laravel.com/docs/8.x/localization) 的使用方式。

## 使用

### 编写语言文件

本库会扫描资源文件中所有满足 `classpath*:/ignotus/lang/**`
路径形式的的文件（如 [Validation](https://github.com/ParaParty/Ignotus/tree/master/validation/src/main/resources/ignotus/lang)
中展示的例子）并最终扫描到的所有文件将组成如下的树状结构

```
/resources
    /lang
        /zh_cn
            messages.json
            /other
                messages.json
        /zh_hk
            messages.json
            /other
                messages.json
        /zh_tw
            messages.json
            /other
                messages.json
```

其中 `zh_cn`、`zh_hk`、`zh_tw` 等为显示语言区域名字。

假设 `/zh_cn/messages.json` 的文件内容为：

```json
{
    "hello-world": "你好，世界。",
    "greeting": {
        "morning": "早上好",
        "afternoon": "下午好",
        "night": "晚上好"
    }
}
```

再假设 `/zh_cn/other/messages.json` 的文件内容为：

```json
{
    "another-key": "我是一个普通的文本",
    "hello-world": "你好，<name>！"
}
```

则本库将会将文本名和它对应的文本组织为以下的映射关系：

|文本名| 文本内容|
| --- | --- |
|messages.hello-world| 你好，世界。 |
|messages.greeting.morning| 早上好 |
|messages.greeting.afternoon| 下午好 |
|messages.greeting.night| 晚上好 |
|other.messages.another-key| 我是一个普通的文本 |
|other.messages.hello-world| 你好，\<name\>！ |

可以见得，文本名的构成由 `{文件路径}`、`{文件名不包含扩展名}` 和 `{文本键名}`三个元素组成。将这三个元素使用英文句号 `.` 连接在一起，就可以得到文本名。

如：`你好，世界。` 所在的文件路径是 `/`，文件名为 `messages.json`，文本键名为 `hello-world`。则文本名为 `messages.hello-world`。

`我是一个普通的文本` 所在的文件路径是 `/other/`，文件名为 `messages.json`，文本键名为 `another-key`。则文本名为 `other.messages.another-key`。

我们注意到，其中 `other.messages.hello-world` 的文本内容拥有 `<name>`
占位符。这个占位符会被 [Antlr StringTemplate4](https://www.stringtemplate.org/) 解析，并将代码中传递的内容替换进去。

### 使用语言文件

#### 函数风格

```java
import com.tairitsu.ignotus.support.util.Translation;

public class Test {
    public static void main(String[] args) {
        // 获取一个普通的文本
        String helloWorld = Translation.lang("message.hello-world");

        // 获取一个带变量的文本
        HashMap<String, Object> args = new HashMap<String, Object>();
        args.put("name", "世界");
        String betterHelloWorld = Translation.lang("other.message.hello-world", args);
    }
}
```

### 建造者风格

```java
import com.tairitsu.ignotus.support.util.Translation;

public class Test {
    public static void main(String[] args) {
        String helloWorld = Translation.builder().setKey("other.message.hello-world").add("name", "World").build();
    }
}
```

## 格式支持

由于 JSON 在编写这种东西的时候可能会比较麻烦，本库还添加了 yaml、properties 和 kotlin dsl 的使用方式。具体的配置方法在这部分介绍。

### JSON

本功能不需要额外添加依赖，为 Spring Boot 的依赖项 Jackson 默认提供的功能。

在扫描资源文件时，扩展名为 `json` 和 `jsonc` 的文件将会被识别并处理。

### YAML

本功能需要添加 Jackson Dataformat YAML `com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.5` 依赖。

在扫描资源文件时，扩展名为 `yaml` 和 `yml` 的文件将会被识别并处理。

```yaml
hello-world: 你好，世界。
greeting:
    morning: 早上好
    afternoon: 下午好
    night: 晚上好
```

### YAML

本功能需要添加 Jackson Dataformat Properties `com.fasterxml.jackson.dataformat:jackson-dataformat-properties:2.12.5` 依赖。

在扫描资源文件时，扩展名为 `properties` 的文件将会被识别并处理。

```properties
hello-world=你好，世界。
greeting.morning=早上好
greeting.afternoon=下午好
greeting.night=晚上好
```

### Kotlin DSL

本功能需要添加的依赖比较多，他们为：

```groove
    implementation 'org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.0'
    runtimeOnly 'org.jetbrains.kotlin:kotlin-reflect:1.6.0'
    implementation 'org.jetbrains.kotlin:kotlin-script-runtime:1.6.0'
    implementation 'org.jetbrains.kotlin:kotlin-script-util:1.6.0'
    runtimeOnly 'org.jetbrains.kotlin:kotlin-compiler-embeddable:1.6.0'
    runtimeOnly 'org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:1.6.0'
```

在扫描资源文件时，扩展名为 `kts` 的文件将会被识别并处理。

```kotlin
import com.tairitsu.ignotus.translation.provider.resource.dsl.languageSet
import java.util.*

val random = Random()

languageSet {
    // DSL 的构造形式我们依旧提供了传统的键值对方式，文本字段为一个字符串
    "greeting" by languageSet {
        "morning" by "早上好"
        "afternoon" by "下午好"
        "night" by "晚上好"
    }
    
    // 但是在 DSL 的方式里，我们还提供了回调函数的形式，文本字段可以为一个函数，并且可以融入你的逻辑。
    // 这个函数在每次获取这个文本字段时都会被调用一次。
    "hello-world" by { _, _, _ -> if (random.nextBoolean()) "你好，世界。" else "你好，世界！" }
}
```

## 参考资料

[GitHub/s1monw1/KtsRunner](https://github.com/s1monw1/KtsRunner) ：在程序中运行 Kts 的参考代码。
