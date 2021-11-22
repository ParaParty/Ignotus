package moe.bit.ignotusdemo.service;

import moe.bit.ignotusdemo.model.dto.UserLoginDto;
import moe.bit.ignotusdemo.model.dto.UserRegisterDto;
import moe.bit.ignotusdemo.model.vo.UserVo;

public interface UserService {
    UserVo register(UserRegisterDto body);

    UserVo login(UserLoginDto body);
}
