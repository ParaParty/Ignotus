import com.tairitsu.ignotus.translation.provider.resource.dsl.languageSet
import java.util.*

languageSet {
    "demo" by languageSet {
        "say_hello" by "Hello, <name>! 本服务的启动时间是 ${Date()}。"
    }
}
