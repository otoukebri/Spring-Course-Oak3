package accounts

import accounts.internal.JpaAccountManager
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EntityScan("rewards")
open class BootLabApplication {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(BootLabApplication::class.java, *args)
        }
    }

    @Bean open fun accountManager() = JpaAccountManager()
}