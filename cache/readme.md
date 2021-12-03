# Laravel 风格键值对数据库操作

## 说明

本键值对数据库操作封装主要模仿了 [Laravel Cache](https://laravel.com/docs/8.x/cache) 的使用方式。

## 安装

1. 同时在项目中引入 `spring-boot-starter-data-redis` 或 `spring-boot-starter-data-mongodb` 和本库。
2. 使用 Spring 依赖注入 `com.tairitsu.ignotus.cache.CacheService` 接口。

## 使用

见
1. [接口方法注释](https://github.com/ParaParty/Ignotus/blob/master/cache/src/main/java/com/tairitsu/ignotus/cache/CacheService.kt)
3. [Laravel Cache 文档](https://laravel.com/docs/8.x/cache)
4. [Laravel 接口方法注释](https://github.com/laravel/framework/blob/8.x/src/Illuminate/Contracts/Cache/Repository.php)
