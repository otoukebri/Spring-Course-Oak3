package rewards

import config.RewardsConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import javax.sql.DataSource


@Configuration
@Import(RewardsConfig::class)
open class SystemTestConfig {


    /**
     * Creates an in-memory "rewards" database populated
     * with test data for fast testing
     */
    @Bean
    open fun dataSource() =
            EmbeddedDatabaseBuilder()
                    .addScript("classpath:rewards/testdb/schema.sql")
                    .addScript("classpath:rewards/testdb/data.sql")
                    .build()


    //	TODO-02: Define a bean named 'transactionManager' that configures a DataSourceTransactionManager
    // the name is important here since Spring will loop for it
    @Bean
    open fun transactionManager(dataSource: DataSource) =
            DataSourceTransactionManager(dataSource)
}
