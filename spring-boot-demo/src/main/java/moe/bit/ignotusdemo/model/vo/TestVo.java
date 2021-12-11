package moe.bit.ignotusdemo.model.vo;

import com.tairitsu.ignotus.serializer.vo.BaseResponse;
import com.tairitsu.ignotus.support.util.UUIDUtils;
import org.jetbrains.annotations.NotNull;

public class TestVo extends BaseResponse {

    private String id = UUIDUtils.createNoLeadingDigitId();
    private String hidden = "hidden";
    private String visible = "visible";


    @NotNull
    @Override
    public String getModelType() {
        return "test";
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }

    public String getVisible() {
        return visible;
    }
}
