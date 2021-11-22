package moe.bit.ignotusdemo.service.implement;

import moe.bit.ignotusdemo.exception.UserNotFoundException;
import moe.bit.ignotusdemo.exception.UsernameOrPasswordIsWrong;
import moe.bit.ignotusdemo.model.dto.UserLoginDto;
import moe.bit.ignotusdemo.model.dto.UserRegisterDto;
import moe.bit.ignotusdemo.model.entity.UserEntity;
import moe.bit.ignotusdemo.model.table.UserTable;
import moe.bit.ignotusdemo.model.vo.UserVo;
import moe.bit.ignotusdemo.service.AuthenticationService;
import moe.bit.ignotusdemo.service.UserService;
import com.tairitsu.ignotus.database.annotation.Transaction;
import kotlin.collections.CollectionsKt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserServiceImpl implements UserService {
    PasswordEncoder bcrypt = new BCryptPasswordEncoder();

    @Autowired
    AuthenticationService authService;

    @Override
    @Transaction
    public UserVo register(UserRegisterDto body) {
        // 创建实体例子
        var userEntity = UserEntity.create(t -> {
            t.setUsername(body.getUsername());
            t.setPassword(bcrypt.encode(body.getPassword()));
        });
        userEntity.flush(null);
        return new UserVo(userEntity);
    }

    @Override
    @Transaction
    public UserVo login(UserLoginDto body) {
        // 查询实体例子
        var userEntities = UserEntity.Companion.find(s -> s.eq(UserTable.INSTANCE.getUsername(), body.getUsername()));
        if (userEntities.count() != 1) {
            throw new UserNotFoundException();
        }

        var userEntity = CollectionsKt.first(userEntities);
        if (!bcrypt.matches(body.getPassword(), userEntity.getPassword())) {
            throw new UsernameOrPasswordIsWrong();
        }

        // 更新实体例子
        userEntity.setLastLoginAt(LocalDateTime.now());
        userEntity.flush(null);

        var credentials = authService.login(userEntity);

        var ret = new UserVo(userEntity);
        ret.setRelationship("credentials", credentials.getSecond());
        return ret;
    }
}
