package moe.bit.ignotusdemo.service.implement;

import moe.bit.ignotusdemo.model.entity.UserEntity;
import moe.bit.ignotusdemo.model.vo.Credentials;
import moe.bit.ignotusdemo.service.AuthenticationService;
import moe.bit.ignotusdemo.model.vo.FAuthInfo;
import com.tairitsu.ignotus.cache.CacheService;
import kotlin.Pair;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;

@Component
public class AuthenticationServiceImpl implements AuthenticationService {
    private final CacheService cache;

    public AuthenticationServiceImpl(CacheService cache) {
        this.cache = cache;
    }

    @Override
    @Nullable
    public FAuthInfo authenticateWithHeader(String token) {
        String[] tokenArr = token.split(" ");
        if (tokenArr.length != 2 || !tokenArr[0].equalsIgnoreCase("bearer")) {
            return null;
        }
        return cache.get("user:token:" + tokenArr[1], FAuthInfo.class, null);
    }

    public Pair<FAuthInfo, Credentials> login(UserEntity user) {
        FAuthInfo authInfo = new FAuthInfo();
        authInfo.setId(user.getId().toString());
        authInfo.setUsername(user.getUsername());
        authInfo.setCreatedAt(user.getCreatedAt());
        //noinspection ConstantConditions
        authInfo.setUpdatedAt(user.getUpdatedAt());

        ArrayList<String> roles = new ArrayList<String>();
        // TODO 先随便整一个用户组
        roles.add("USER");
        authInfo.setRoles(roles);

        String token = generateToken();
        Duration validDuration = Duration.ofHours(8);
        cache.put("user:token:" + token, authInfo, validDuration);

        Credentials credentials = new Credentials();
        credentials.setAccessToken(token);
        credentials.setExpiresIn((int) validDuration.getSeconds());
        credentials.setStatus(0);

        return new Pair<FAuthInfo, Credentials>(authInfo, credentials);
    }

    public String generateToken() {
        return RandomStringUtils.randomAlphanumeric(128);
    }
}
