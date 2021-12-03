import com.tairitsu.ignotus.translation.provider.resource.dsl.languageSet
import java.util.*

val random = Random()
val startTime = Date()

languageSet {
    "demo" by languageSet {
        "say_hello" by {_, _, _ -> "${if (random.nextBoolean()) "你好" else "妮嚎"}，<name>！现在时间是 ${Date()} 本服务的启动时间是 ${startTime}。"}
    }
}
