package accounts

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

/**
 * Created by trijckaert on 16/12/16.
 */
@Controller
class HelloWorldController {

    @RequestMapping("/hello")
    fun sayHello(): String = "hello"
}