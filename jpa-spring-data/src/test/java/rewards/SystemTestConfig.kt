package rewards

import config.RewardsConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.Database
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import java.util.*
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
@Import(RewardsConfig::class)
open class SystemTestConfig {

    /**
     * Creates an in-memory "rewards" database populated
     * with test data for fast testing
     */
    @Bean
    open fun dataSource(): DataSource {
        return EmbeddedDatabaseBuilder()
                .addScript("classpath:rewards/testdb/schema.sql")
                .addScript("classpath:rewards/testdb/data.sql")
                .build()
    }


    //	TODO-07: Configure and return a LocalContainerEntityManagerFactoryBean.  Be sure
    //	set the dataSource, jpaVendorAdaptor and other properties appropriately.
    @Bean
    open fun entityManagerFactory(dataSource: DataSource): LocalContainerEntityManagerFactoryBean {
        // We've set these up for you ...
        val adapter = HibernateJpaVendorAdapter().apply {
            setShowSql(true)
            setGenerateDdl(true)
            setDatabase(Database.HSQL)
        }

        val props = Properties().apply {
            setProperty("hibernate.show_sql", "true")
            setProperty("hibernate.format_sql", "true")
        }

        // Your turn ... configure the emf like the example in the slides ...
        val entityManagerFactoryBean = LocalContainerEntityManagerFactoryBean().apply {
            setJpaProperties(props)
            setDataSource(dataSource)
            setPackagesToScan("rewards.internal")
            jpaVendorAdapter = adapter
        }
        return entityManagerFactoryBean
    }

    //	TODO-08: Define a JpaTransactionManager bean with the name transactionManager.
    //	The @Bean method should accept a parameter of type EntityManagerFactory.
    //	Use this parameter when instantiating the JpaTransactionManager.
    //	Run the RewardNetworkTests, it should pass.
    @Bean
    open fun transactionManager(entityManagerFactory: EntityManagerFactory): JpaTransactionManager
            = JpaTransactionManager(entityManagerFactory)
}
