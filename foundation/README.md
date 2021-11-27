# JSON:API 格式的 Controller 定义助手
在 Controller 前增加一个切面，框架自动解析 [JSON:API](https://jsonapi.org/format/#errors) 格式的参数，并处理为 Java STL 容器。 

## 快速上手

### 定义一个控制器
```java
import com.tairitsu.ignotus.support.util.ServletRequestExtension;

@RestController
public class UserController {
    @GetMapping("api/users")
    @JsonApiController()
    public List<UserVo> listUser(HttpServletRequest request) {
        // 可以获取到框架解析出
        // ServletRequestExtension.getExtractedInclude(request);
        // ServletRequestExtension.getExtractedSort(request);
        // ServletRequestExtension.getExtractedFilter(request);
        // ServletRequestExtension.getExtractedPagination(request);
        // TODO 自己看着办
    }
}
```

### 验证用户提交
完整例子见：[e5f4739](https://github.com/ParaParty/Ignotus/blob/e5f4739e53251b9e255cce70990a54cc4f64ae5e/spring-boot-demo/src/main/java/moe/bit/ignotusdemo/controller/UserController.java)
```java
import com.tairitsu.ignotus.support.util.ServletRequestExtension;

@RestController
public class UserController {
    @PostMapping("api/register")
    @JsonApiController()
    public UserVo register(@Valid @RequestBody RegisterRequest user) {
        // TODO 自己看着办
    }
}
```

## 设置
```properties
ignotus.foundation.pagination.page-based.enabled=true
ignotus.foundation.pagination.page-based.start-at=1

ignotus.foundation.pagination.offset-based.enabled=true

ignotus.foundation.pagination.min-limit=1
ignotus.foundation.pagination.default-limit=20
ignotus.foundation.pagination.max-limit=200
```
