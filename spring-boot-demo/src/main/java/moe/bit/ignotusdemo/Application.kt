package moe.bit.ignotusdemo

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
open class Application {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java, *args)
        }
    }
}
