package rewards.internal

import common.money.MonetaryAmount
import config.RewardsConfig
import org.junit.Before
import org.junit.Test
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext
import rewards.Dining
import rewards.RewardNetwork
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Created by trijckaert on 13/12/16.
 */
class RewardNetworkTests {

    var rewardNetwork: RewardNetwork? = null

    @Before
    fun setUp() {
        val context: ApplicationContext = SpringApplication.run(RewardsConfig::class.java)
        rewardNetwork = context.getBean(RewardNetwork::class.java)
    }

    @Test
    fun testRewardForDining() {
        // create a new dining of 100.00 charged to credit card '1234123412341234' by merchant '123457890' as test input
        val dining = Dining.createDining("100.00", "1234123412341234", "1234567890")

        // call the 'rewardNetwork' to test its rewardAccountFor(Dining) method
        val confirmation = rewardNetwork?.rewardAccountFor(dining)

        // assert the expected reward confirmation results
        assertNotNull(confirmation)
        assertNotNull(confirmation?.confirmationNumber)

        // assert an account contribution was made
        val contribution = confirmation?.accountContribution
        assertNotNull(contribution)

        // the account number should be '123456789'
        assertEquals("123456789", contribution?.accountNumber)

        // the total contribution amount should be 8.00 (8% of 100.00)
        assertEquals(MonetaryAmount.valueOf("8.00"), contribution?.amount)

        // the total contribution amount should have been split into 2 distributions
        assertEquals(2, contribution?.distributions?.size?.toLong())

        // each distribution should be 4.00 (as both have a 50% allocation)
        assertEquals(MonetaryAmount.valueOf("4.00"), contribution?.getDistribution("Annabelle")?.amount)
        assertEquals(MonetaryAmount.valueOf("4.00"), contribution?.getDistribution("Corgan")?.amount)
    }
}