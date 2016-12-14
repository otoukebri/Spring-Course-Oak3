package rewards.internal.restaurant

import common.money.Percentage
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import rewards.Dining
import rewards.internal.account.Account
import java.sql.ResultSet
import java.sql.SQLException
import javax.sql.DataSource

/**
 * Loads restaurants from a data source using the JDBC API.
 */
//	TODO-04: Add a field of type JdbcTemplate.  Refactor the constructor to instantiate it.
//	Refactor findByMerchantNumber(..) to use the JdbcTemplate and a RowMapper called RestaurantRowMapper.
//	Note that the mapRestaurant() method contains logic which the RowMapper may wish to use.
//  (If you prefer, use a lambda insead of the RestaurantRowMapper)
//	When complete, save all changes and run JdbcRestaurantRepositoryTests.  It should pass.

class JdbcRestaurantRepository(private val dataSource: DataSource) : RestaurantRepository {

    private val jdbcTemplate: JdbcTemplate

    init {
        this.jdbcTemplate = JdbcTemplate(dataSource)
    }

    override fun findByMerchantNumber(merchantNumber: String): Restaurant {
        val sql = "select MERCHANT_NUMBER, NAME, BENEFIT_PERCENTAGE, BENEFIT_AVAILABILITY_POLICY from T_RESTAURANT where MERCHANT_NUMBER = ?"
        return jdbcTemplate.queryForObject(sql, RestaurantRowMapper(), merchantNumber)
    }

    /**
     * Maps a row returned from a query of T_RESTAURANT to a Restaurant object.
     * @param rs the result set with its cursor positioned at the current row
     */
    @Throws(SQLException::class)
    private fun mapRestaurant(rs: ResultSet): Restaurant {
        // get the row column data
        val name = rs.getString("NAME")
        val number = rs.getString("MERCHANT_NUMBER")
        val benefitPercentage = Percentage.valueOf(rs.getString("BENEFIT_PERCENTAGE"))
        // map to the object
        val restaurant = Restaurant(number, name)
        restaurant.benefitPercentage = benefitPercentage
        restaurant.benefitAvailabilityPolicy = mapBenefitAvailabilityPolicy(rs)
        return restaurant
    }

    /**
     * Advances a ResultSet to the next row and throws an exception if there are no rows.
     * @param rs the ResultSet to advance
     * *
     * @throws EmptyResultDataAccessException if there is no next row
     * *
     * @throws SQLException
     */
    @Throws(EmptyResultDataAccessException::class, SQLException::class)
    private fun advanceToNextRow(rs: ResultSet) {
        if (!rs.next()) {
            throw EmptyResultDataAccessException(1)
        }
    }

    /**
     * Helper method that maps benefit availability policy data in the ResultSet to a fully-configured
     * [BenefitAvailabilityPolicy] object. The key column is 'BENEFIT_AVAILABILITY_POLICY', which is a
     * discriminator column containing a string code that identifies the type of policy. Currently supported types are:
     * 'A' for 'always available' and 'N' for 'never available'.

     *
     *
     * More types could be added easily by enhancing this method. For example, 'W' for 'Weekdays only' or 'M' for 'Max
     * Rewards per Month'. Some of these types might require additional database column values to be configured, for
     * example a 'MAX_REWARDS_PER_MONTH' data column.

     * @param rs the result set used to map the policy object from database column values
     * *
     * @return the matching benefit availability policy
     * *
     * @throws IllegalArgumentException if the mapping could not be performed
     */
    @Throws(SQLException::class)
    private fun mapBenefitAvailabilityPolicy(rs: ResultSet): BenefitAvailabilityPolicy {
        val policyCode = rs.getString("BENEFIT_AVAILABILITY_POLICY")
        if ("A" == policyCode) {
            return AlwaysAvailable.INSTANCE
        } else if ("N" == policyCode) {
            return NeverAvailable.INSTANCE
        } else {
            throw IllegalArgumentException("Not a supported policy code " + policyCode)
        }
    }

    /**
     * Returns true indicating benefit is always available.
     */
    internal class AlwaysAvailable : BenefitAvailabilityPolicy {

        override fun isBenefitAvailableFor(account: Account, dining: Dining): Boolean {
            return true
        }

        override fun toString(): String {
            return "alwaysAvailable"
        }

        companion object {
            val INSTANCE: BenefitAvailabilityPolicy = AlwaysAvailable()
        }
    }

    /**
     * Returns false indicating benefit is never available.
     */
    internal class NeverAvailable : BenefitAvailabilityPolicy {

        override fun isBenefitAvailableFor(account: Account, dining: Dining): Boolean {
            return false
        }

        override fun toString(): String {
            return "neverAvailable"
        }

        companion object {
            val INSTANCE: BenefitAvailabilityPolicy = NeverAvailable()
        }
    }

    private inner class RestaurantRowMapper : RowMapper<Restaurant> {

        @Throws(SQLException::class)
        override fun mapRow(rs: ResultSet, rowNum: Int): Restaurant {
            return mapRestaurant(rs)
        }
    }
}