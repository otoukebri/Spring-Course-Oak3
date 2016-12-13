package config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import rewards.internal.RewardNetworkImpl
import rewards.internal.account.JdbcAccountRepository
import rewards.internal.restaurant.JdbcRestaurantRepository
import rewards.internal.reward.JdbcRewardRepository
import javax.sql.DataSource

/**
 * Created by trijckaert on 13/12/16.
 */
@Configuration
@Import(TestInfrastructureConfig::class)
open class RewardsConfig {

    @Autowired lateinit var dataSource: DataSource

    @Bean
    open fun rewardNetworkImpl(accountRepository: JdbcAccountRepository,
                          restaurantRepository: JdbcRestaurantRepository,
                          rewardRepository: JdbcRewardRepository) =
            RewardNetworkImpl(
                    accountRepository,
                    restaurantRepository,
                    rewardRepository
            )

    @Bean
    open fun accountRepository() = JdbcAccountRepository().apply { setDataSource(dataSource) }

    @Bean
    open fun restaurantRepository() = JdbcRestaurantRepository().apply { setDataSource(dataSource) }

    @Bean
    open fun rewardRepository() = JdbcRewardRepository().apply { setDataSource(dataSource) }
}