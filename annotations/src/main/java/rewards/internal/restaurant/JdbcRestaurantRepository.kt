package rewards.internal.restaurant

import common.money.Percentage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Repository
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.sql.DataSource

/**
 * Loads restaurants from a data source using the JDBC API.

 * This implementation should cache restaurants to improve performance. The cache should be populated on initialization
 * and cleared on destruction.
 */


/* TODO-06: Annotate the class with an appropriate stereotype annotation
 * to cause component-scan to detect and load this bean.
 * Configure Dependency Injection for dataSource.
 * Use constructor injection in this case
 * (note the logic in the constructor requires a dataSource).
 */

/* TODO-08: Experiment with setting the dataSource property using either setter or field injection.
 * Re-run the test. It should fail. Examine the stack trace and see if you can understand why.
 * (If not, refer to the detailed lab instructions). We will fix this error in the next step." */
@Repository
class JdbcRestaurantRepository : RestaurantRepository {

    private lateinit var dataSource: DataSource

    /**
     * The Restaurant object cache. Cached restaurants are indexed by their merchant numbers.
     */
    private var restaurantCache: MutableMap<String, Restaurant>? = null

    /**
     * The constructor sets the data source this repository will use to load restaurants.
     * When the instance of JdbcRestaurantRepository is created, a Restaurant cache is
     * populated for read only access

     * @param dataSource the data source
     */
    constructor(dataSource: DataSource) {
        this.dataSource = dataSource
    }

    override fun findByMerchantNumber(merchantNumber: String): Restaurant {
        return queryRestaurantCache(merchantNumber)
    }

    @Autowired
    fun setDataSource(dataSource: DataSource) {
        this.dataSource = dataSource
    }

    /**
     * Helper method that populates the [restaurant object cache][.restaurantCache] from rows in the T_RESTAURANT
     * table. Cached restaurants are indexed by their merchant numbers. This method should be called on initialization.
     */


    /* TODO-09: Mark this method with an annotation that will cause it to be executed by
	 * Spring after constructor / setter initialization has occurred.
	 * Re-run the RewardNetworkTests test. You should see the test succeed */
    @PostConstruct
    fun populateRestaurantCache() {
        restaurantCache = HashMap<String, Restaurant>()
        val sql = "select MERCHANT_NUMBER, NAME, BENEFIT_PERCENTAGE from T_RESTAURANT"
        var conn: Connection? = null
        var ps: PreparedStatement? = null
        var rs: ResultSet? = null
        try {
            conn = dataSource.connection
            ps = conn!!.prepareStatement(sql)
            rs = ps!!.executeQuery()
            while (rs!!.next()) {
                val restaurant = mapRestaurant(rs)
                // index the restaurant by its merchant number
                restaurantCache!!.put(restaurant.number, restaurant)
            }
        } catch (e: SQLException) {
            throw RuntimeException("SQL exception occurred finding by merchant number", e)
        } finally {
            if (rs != null) {
                try {
                    // Close to prevent database cursor exhaustion
                    rs.close()
                } catch (ex: SQLException) {
                }

            }
            if (ps != null) {
                try {
                    // Close to prevent database cursor exhaustion
                    ps.close()
                } catch (ex: SQLException) {
                }

            }
            if (conn != null) {
                try {
                    // Close to prevent database connection exhaustion
                    conn.close()
                } catch (ex: SQLException) {
                }

            }
        }
    }

    /**
     * Helper method that simply queries the cache of restaurants.

     * @param merchantNumber the restaurant's merchant number
     * *
     * @return the restaurant
     * *
     * @throws EmptyResultDataAccessException if no restaurant was found with that merchant number
     */
    private fun queryRestaurantCache(merchantNumber: String): Restaurant {
        val restaurant = restaurantCache!![merchantNumber] ?: throw EmptyResultDataAccessException(1)
        return restaurant
    }

    /**
     * Helper method that clears the cache of restaurants.  This method should be called on destruction
     */

    /* TODO-10: Add a breakpoint inside clearRestaurantCache(). Re-run RewardNetworkTests in debug mode.
	 * It seems that this method is never called. Use an annotation to register this method for a
	 * destruction lifecycle callback. Re-run the test and the breakpoint should now be reached.  */
    @PreDestroy
    fun clearRestaurantCache() {
        restaurantCache!!.clear()
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
        return restaurant
    }
}