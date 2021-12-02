package moe.bit.ignotusdemo.controller;

import com.tairitsu.ignotus.foundation.annotation.JsonApiController;
import com.tairitsu.ignotus.support.util.Translation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TranslationTest {
    @GetMapping("translation")
    @JsonApiController()
    @ResponseBody
    public String translation() {
        return Translation.builder().setKey("test.demo.say_hello").add("name", "World").build();
    }
}
