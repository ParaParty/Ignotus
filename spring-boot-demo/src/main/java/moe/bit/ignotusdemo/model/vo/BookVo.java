package moe.bit.ignotusdemo.model.vo;

import com.tairitsu.ignotus.serializer.vo.BaseResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

public class BookVo extends BaseResponse {

    private final String name;

    public BookVo(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    @Override
    public String getModelType() {
        return "books";
    }

    @NotNull
    @Override
    public String getId() {
        // 测试一下我的代码对这种 Prop 处理的能力
        return RandomStringUtils.randomAlphanumeric(5);
    }

    public String getName() {
        return name;
    }
}

