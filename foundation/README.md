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
