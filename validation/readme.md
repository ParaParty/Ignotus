# Laravel 风格验证器

## 说明

本验证器主要模仿了 [Laravel Validation](https://laravel.com/docs/8.x/validation) 使用方式。

## 使用

- 通过 Spring 对象容器获取 `Validator` 对象，使用 `Validator.validate()` 开始您的验证。

Java 的例子我不想写了，自己脑补吧
```kt
    Validator().validate("""
        {
            "username": "Rick Roll",
            "gender": "未知",
            "description": 100,
            "email": "test@test.com",
            "age": 60
        }
    """.trimIndent(),
        mapOf(
            "username" to "required",                            // 传递了一个验证器名字，没有参数
            "gender" to ("in" to listOf("M", "F")),              // 传递了一个验证器名字，并且带了一个 list 参数
            "description" to "string|max:10",                    // 传递了两个验证器，一个验证器是只有名字，另一个验证器是带了参数
            "email" to listOf("required", "max:64", "email"),    // 传递了三个验证器
            "age" to object :AttributeValidatorInterface {       // 传递了一个匿名类作验证器
                override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
                    if (value !is Number) {
                        fail("年龄必须为数字")
                        return
                    }
                    if (value.toInt() < 0) {
                        fail("年龄必须为自然数")
                        return
                    }
                }
            }
        )
    )
```

## 扩展

- 扩展验证器
  1. 实现 `AttributeValidatorInterface` 接口，并将实现类加入到 Spring IOC 对象容器。  
     需注意：验证器类名以 `Validator` 开头，并将类名开头的 `Validator` 去除后剩余的部分第一个字母改为小写作为验证器名。  
     如：`ValidatorEmail` 类的验证器名为 `email`。 

- 扩展验证器注解
  1. 新建一个注解，并在注解上标记 `@ValidatorAnnotation`。
  2. 如果验证器拥有参数，可以在注解中添加 `value` 字段，验证器框架会将 `value` 的字段值传递给验证器。
  3. 验证器注解类名为对应的验证器类名去掉开头的 `Validator` 的剩余部分。  
     如：`ValidatorEmail` 的验证器注解为 `Email`  
     这次给个 Java 的例子吧
    ```java
    import com.tairitsu.ignotus.validation.ValidatorAnnotation;
    
    import java.lang.annotation.ElementType;
    import java.lang.annotation.Retention;
    import java.lang.annotation.RetentionPolicy;
    import java.lang.annotation.Target;
    
    @ValidatorAnnotation
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface EMail {
        String value() default "";
    }
    ```
