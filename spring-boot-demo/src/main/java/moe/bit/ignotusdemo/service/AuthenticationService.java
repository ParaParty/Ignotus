package moe.bit.ignotusdemo.service;

import moe.bit.ignotusdemo.model.entity.UserEntity;
import moe.bit.ignotusdemo.model.vo.Credentials;
import moe.bit.ignotusdemo.model.vo.FAuthInfo;
import kotlin.Pair;
import moe.bit.ignotusdemo.model.vo.FAuthInfo;
import org.jetbrains.annotations.Nullable;

public interface AuthenticationService {
    @Nullable
    FAuthInfo authenticateWithHeader(String token);

    Pair<FAuthInfo, Credentials> login(UserEntity user);
}
