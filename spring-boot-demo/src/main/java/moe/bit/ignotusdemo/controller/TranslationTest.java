package moe.bit.ignotusdemo.controller;

import com.tairitsu.ignotus.exception.business.UnexpectedException;
import com.tairitsu.ignotus.foundation.annotation.JsonApiController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TranslationTest {
    @GetMapping("api/exception")
    @JsonApiController()
    public void exception() {
        throw new IllegalArgumentException("test");
    }

    @GetMapping("api/exception_plain")
    @JsonApiController()
    public void exceptionPlain() {
        throw new UnexpectedException("test", null);
    }
}
