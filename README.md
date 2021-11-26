# ParaParty Ignotus, Spring-Boot 常用组件

- 支持了一点 [JSON:API](https://jsonapi.org/)
- 实现了一些 [Laravel](https://laravel.com/) 风格的 api

## 使用

### Maven
在 `pom.xml` 中添加依赖：
```xml
<dependency>
  <groupId>com.tairitsu</groupId>
  <artifactId>ignotus-exception</artifactId> <!-- 以全局异常处理模块为例 -->
  <version>1.0.4</version>
</dependency>
```

### Gradle
```groovy
// JSON:API 输出格式的全局异常处理模块
implementation 'com.tairitsu:ignotus-exception:1.0.4'

// JSON:API 格式的 Controller 定义助手
implementation 'com.tairitsu:ignotus-foundation:1.0.4'

// JSON:API 格式的数据序列化工具
implementation 'com.tairitsu:ignotus-serializer:1.0.4'

// 基本辅助类
implementation 'com.tairitsu:ignotus-support:1.0.4'

// Laravel 风格的数据验证器
implementation 'com.tairitsu:ignotus-validation:1.0.4'

// Laravel 风格的键值对数据库封装
implementation 'com.tairitsu:ignotus-cache:1.0.4'

// 参考网友实现的 JetBrains-Exposed 自动时间戳封装
implementation 'com.tairitsu:ignotus-database-exposed:1.0.4'
```

## 使用介绍

具体每个模块的使用介绍见对应模块内的 `readme.md` 文件。

## 构建
```bash
./gradlew :cache:build :database-exposed:build :exception:build :foundation:build :serializer:build :support:build :translation:build :validation:build :spring-boot-demo:build
```

```
gradlew :cache:build :database-exposed:build :exception:build :foundation:build :serializer:build :support:build :translation:build :validation:build :spring-boot-demo:build
```

## 发布
```bash
./gradlew :cache:publish :database-exposed:publish :exception:publish :foundation:publish :serializer:publish :support:publish :validation:publish
```

```
gradlew :cache:publish :database-exposed:publish :exception:publish :foundation:publish :serializer:publish :support:publish :validation:publish
```
