package accounts.web

import accounts.AccountManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class AccountController(@Autowired private val accountManager: AccountManager) {

    @RequestMapping("/accountDetails")
    fun accountDetails(@RequestParam("entityId") id: Long, model: Model): String {
        model.addAttribute("account", accountManager.getAccount(id))
        return "accountDetails"
    }

    @RequestMapping("/accountList")
    fun accountList(model: Model): String {
        model.addAttribute("accounts", accountManager.allAccounts)
        return "accountList"
    }
}