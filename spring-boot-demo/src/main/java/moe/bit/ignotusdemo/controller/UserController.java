package moe.bit.ignotusdemo.controller;

import moe.bit.ignotusdemo.model.dto.UserLoginDto;
import moe.bit.ignotusdemo.model.dto.UserRegisterDto;
import moe.bit.ignotusdemo.model.vo.UserVo;
import moe.bit.ignotusdemo.service.UserService;
import com.tairitsu.ignotus.foundation.annotation.JsonApiController;
import com.tairitsu.ignotus.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("api/register")
    @JsonApiController()
    public UserVo register(@RequestBody @Valid UserRegisterDto body) {
        return userService.register(body);
    }

    @PostMapping("api/login")
    @JsonApiController()
    public UserVo login(@RequestBody @Valid UserLoginDto body) {
        return userService.login(body);
    }
}
