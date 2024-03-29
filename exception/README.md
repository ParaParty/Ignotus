# JSON:API 格式的全局异常处理模块
[JSON:API 输出格式](https://jsonapi.org/format/#errors) 的全局异常处理模块

## 快速上手

### 抛出一个错误

代码：
```java
import com.tairitsu.ignotus.exception.business.UnexpectedException;

public class Test {
  public static void main(String[] args) {
    throw new UnexpectedException("测试异常");
  }
}
```

输出：
```json
{
  "errors": [
    {
      "status": "500",
      "code": "internal_server_error",
      "detail": "测试异常"
    }
  ]
}
```

### 抛出一堆错误

代码：
```java
import com.tairitsu.ignotus.exception.ApiExceptionBag;
import com.tairitsu.ignotus.exception.business.UnexpectedException;

public class Test {
  public static void main(String[] args) {
      ApiExceptionBag t = new ApiExceptionBag();
      t.add(new UnexpectedException("测试异常1"));
      t.add(new UnexpectedException("测试异常1"));
      throw t;
  }
}
```

输出：
```json
{
  "errors": [
    {
      "status": "500",
      "code": "internal_server_error",
      "detail": "测试异常1"
    },
    {
      "status": "500",
      "code": "internal_server_error",
      "detail": "测试异常1"
    }
  ]
}
```

## 定义一个错误

```java
import com.tairitsu.ignotus.exception.SingleApiException;

public class MyDemoException extends SingleApiException {
    public MyDemoException() {
        super(418, "i_am_a_teapot", "我是茶壶肥又矮呀，我是茶壶肥又矮。");
    }
}
```

如果需要输出更多的信息，可以在构造函数里给 `meta` 或其他属性进行赋值。

## 错误收集器

实现一个 `ApiExceptionReporter` 并加入 Spring 容器。

## 设置
```properties
# 调试模式，当调试模式开启时，会输出更多 UnexpectedException 的堆栈信息。
ignotus.exception.debug = true
```
