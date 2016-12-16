package accounts

import config.AppConfig
import config.DbConfig
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(AppConfig::class, DbConfig::class)
open class RestWsApplication {

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(RestWsApplication::
            class.java, *args)
        }
    }

    //	TODO 01: Run this Spring Boot application
    //	(first make sure that you are not still running an application from a prior lab)
    //	Verify you can reach http://localhost:8080 from a browser.

}
