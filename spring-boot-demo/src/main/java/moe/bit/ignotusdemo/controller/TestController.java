package moe.bit.ignotusdemo.controller;

import com.tairitsu.ignotus.foundation.annotation.JsonApiController;
import com.tairitsu.ignotus.support.util.Translation;
import moe.bit.ignotusdemo.model.vo.TestVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("testVo")
    @JsonApiController()
    @ResponseBody
    public TestVo translation() {
        return new TestVo();
    }
}
