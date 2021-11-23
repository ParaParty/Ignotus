# JSON:API 格式的数据序列化工具
在 Controller 后增加一个切面，框架自动解析 Controller 返回的数据并转换为 [JSON:API](https://jsonapi.org/format/#errors) 格式的数据。

## 快速上手

### 定义一个返回值对象
```java
import com.forestsay.authorization.model.entity.UserEntity;
import com.tairitsu.ignotus.serializer.vo.BaseResponse;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

public class UserVo extends BaseResponse {
    /**
     * JSON:API 规范中 `type` 字段
     */
    @NotNull
    @Override
    public String getModelType() {
        return "users";
    }

    String id;
    String username;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    /*
      ...
      ... 省略 id, username, created_at, updated_at 等字段的 getter 和 setter。
      ...
     */
}
```

### 在 Controller 中返回数据

如果 Controller 返回为 `BaseResponse` 或 `BaseResponse` 的集合，将会被框架序列化为 JSON:API 格式的数据。
```java
import java.util.ArrayList;

@RestController
public class UserController {
    @GetMapping("/list")
    public List<UserVo> list() {
        ArrayList<UserVo> ret = new ArrayList<>();
        ret.add(new UserVo());
        ret.add(new UserVo());
        ret.add(new UserVo());
        ret.add(new UserVo());
        return ret;
    }
    
    @GetMapping("/single")
    public UserVo single() {
        return new UserVo();
    }
}
```

