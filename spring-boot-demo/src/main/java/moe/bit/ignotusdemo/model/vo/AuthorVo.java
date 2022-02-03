package moe.bit.ignotusdemo.model.vo;

import com.tairitsu.ignotus.serializer.vo.BaseResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

public class AuthorVo extends BaseResponse {
    private final String id;
    private final String name;

    public AuthorVo(@NotNull String name) {
        this.id = RandomStringUtils.randomAlphanumeric(5);
        this.name = name;
    }

    @NotNull
    @Override
    public String getModelType() {
        return "authors";
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }
}
