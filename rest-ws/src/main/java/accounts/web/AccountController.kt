package accounts.web

import accounts.AccountManager
import common.money.Percentage
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import rewards.internal.account.Account
import rewards.internal.account.Beneficiary
import java.util.*

/**
 * A controller handling requests for CRUD operations on Accounts and their
 * Beneficiaries.
 */
@RestController
class AccountController(@Autowired private val accountManager: AccountManager) {

    private val logger = Logger.getLogger(javaClass)

    /**
     * Provide a list of all accounts.
     */
    // TODO 02: Complete this method. Add annotations to:
    //   a. Respond to GET /accounts
    //   b. Return a List<Account> to be converted to the response body
    // Save your work and restart the application.
    // You should get JSON results in your browser when accessing http://localhost:8080/accounts
    @GetMapping(value = "/accounts")
    fun accountSummary(): List<Account> {
        return accountManager.allAccounts
    }

    /**
     * Provide the details of an account with the given id.
     */
    // TODO 04: Complete this method. Add annotations to:
    //   a. Respond to GET /accounts/{accountId}
    //   b. Return  an Account to be converted to the response body
    // Save your work and restart the application.
    // You should get JSON results in your browser when accessing http://localhost:8080/accounts/0
    @GetMapping(value = "/accounts/{id}")
    fun accountDetails(@PathVariable id: Int): Account {
        return retrieveAccount(id.toLong())
    }

    /**
     * Creates a new Account, setting its URL as the Location header on the
     * response.
     */
    // TODO 06: Complete this method. Add annotations to:
    //  a. Respond to POST /accounts requests
    //  b. Automatically get an unmarshaled Account from the request
    //  c. Indicate a "201 Created" status
    @PostMapping(value = "/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    fun createAccount(@RequestBody newAccount: Account): ResponseEntity<Void>? {
        // Saving the account also sets its entity Id
        val account = accountManager.save(newAccount)

        // Return a ResponseEntity - it will be used to build the
        // HttpServletResponse.
        return entityWithLocation(account.entityId)
    }

    /**
     * Return a response with the location of the new resource. It's URL is
     * assumed to be a child of the URL just received.
     *
     *
     * Suppose we have just received an incoming URL of, say,
     * `http://localhost:8080/accounts` and `resourceId`
     * is "1111". Then the URL of the new resource will be
     * `http://localhost:8080/accounts/1111`.

     * @param resourceId
     * *            Is of the new resource.
     * *
     * @return
     */
    private fun entityWithLocation(resourceId: Any): ResponseEntity<Void>? =
            ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequestUri()
                    .path("/{childId}")
                    .buildAndExpand(resourceId)
                    .toUri()).build()

    /**
     * Returns the Beneficiary with the given name for the Account with the
     * given id.
     */
    @GetMapping(value = "/accounts/{accountId}/beneficiaries/{beneficiaryName}")
    @ResponseBody
    fun getBeneficiary(@PathVariable("accountId") accountId: Int,
                       @PathVariable("beneficiaryName") beneficiaryName: String): Beneficiary {
        return retrieveAccount(accountId.toLong()).getBeneficiary(beneficiaryName)
    }

    /**
     * Adds a Beneficiary with the given name to the Account with the given id,
     * setting its URL as the Location header on the response.
     */
    // TODO 11: Complete this method. Add annotations to:
    //   a. Respond to a POST /accounts/{accountId}/beneficiaries
    //   b. Extract a beneficiary name from the incoming request
    //   c. Indicate a "201 Created" status
    fun addBeneficiary(accountId: Long, beneficiaryName: String): ResponseEntity<Void>? {
        accountManager.addBeneficiary(accountId, beneficiaryName)

        // TODO 12: Create a ResponseEntity containing the location of the newly
        // created beneficiary.
        //  a. Look at the mapping for getBeneficiary() above to see what the URL
        //     should be.  What are we using to identify the Beneficiary?
        //  b. Use the entityWithLocation method - like we did for createAccount().

        return null  // TODO 12: Modify this to return something
    }

    /**
     * Removes the Beneficiary with the given name from the Account with the
     * given id.
     */
    // TODO 13: Complete this method by adding the appropriate annotations to:
    //  a. Respond to a DELETE to /accounts/{accountId}/beneficiaries/{beneficiaryName}
    //  b. Indicate a "204 No Content" status
    fun removeBeneficiary(accountId: Long, beneficiaryName: String) {
        val account = accountManager.getAccount(accountId)
        val b = account.getBeneficiary(beneficiaryName)

        // We ought to reset the allocation percentages, but for now we won't
        // bother. If we are removing the only beneficiary or the beneficiary
        // has an allocation of zero we don't need to worry. Otherwise, throw an
        // exception.
        if (account.beneficiaries.size != 1 && b.allocationPercentage != Percentage.zero()) {
            // The solution has the missing logic, if you are interested.
            throw RuntimeException("Logic to rebalance Beneficiaries not defined.")
        }

        accountManager.removeBeneficiary(accountId, beneficiaryName, HashMap<String, Percentage>())
    }

    /**
     * Maps IllegalArgumentExceptions to a 404 Not Found HTTP status code.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleNotFound(ex: Exception) {
        logger.error("Exception is: ", ex)
        // just return empty 404
    }

    // TODO 18 (BONUS): Add a new exception-handling method that maps
    // DataIntegrityViolationExceptions to a 409 Conflict status code.
    // Use the handleNotFound method above for guidance and/or look at
    // the Advanced materials in the slides.

    /**
     * Finds the Account with the given id, throwing an IllegalArgumentException
     * if there is no such Account.
     */
    @Throws(IllegalArgumentException::class)
    private fun retrieveAccount(accountId: Long): Account {
        val account = accountManager.getAccount(accountId) ?: throw IllegalArgumentException("No such account with id " + accountId)
        return account
    }

}
