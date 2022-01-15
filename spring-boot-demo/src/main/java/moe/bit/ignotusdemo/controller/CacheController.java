package moe.bit.ignotusdemo.controller;

import com.tairitsu.ignotus.cache.CacheService;
import com.tairitsu.ignotus.foundation.annotation.JsonApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CacheController {
    @Autowired
    CacheService cache;

    @PostMapping("api/cache/{key}")
    @JsonApiController()
    public void put(@RequestBody String value, @PathVariable("key") String key) {
        cache.put(key, value);
    }

    @GetMapping("api/cache/{key}")
    @JsonApiController()
    public String get(@PathVariable("key") String key) {
        return cache.get(key, String.class, null);
    }

    @PostMapping("api/cache-remember/{key}")
    @JsonApiController()
    public String remember(@RequestBody String value, @PathVariable("key") String key) {
        return cache.remember(key, String.class, () -> value);
    }
}
