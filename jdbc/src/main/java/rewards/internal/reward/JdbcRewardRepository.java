package rewards.internal.reward;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import common.datetime.SimpleDate;
import rewards.AccountContribution;
import rewards.Dining;
import rewards.RewardConfirmation;

/**
 * JDBC implementation of a reward repository that records the result of a reward transaction by inserting a reward
 * confirmation record.
 */


//	TODO-03: Add a field of type JdbcTemplate.  Refactor the constructor to instantiate it.
//	Refactor the nextConfirmationNumber() and confirmReward(...) methods to use the template.
//	Save all work, run the JdbcRewardRepositoryTests.  It should pass.

public class JdbcRewardRepository implements RewardRepository {
	
	private JdbcTemplate jdbcTemplate;

	private DataSource dataSource;

	public JdbcRewardRepository(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public RewardConfirmation confirmReward(AccountContribution contribution, Dining dining) {
		String sql = "insert into T_REWARD (CONFIRMATION_NUMBER, REWARD_AMOUNT, REWARD_DATE, ACCOUNT_NUMBER, DINING_MERCHANT_NUMBER, DINING_DATE, DINING_AMOUNT) values (?, ?, ?, ?, ?, ?, ?)";
		String confirmationNumber = nextConfirmationNumber();
		jdbcTemplate.update(sql, confirmationNumber, contribution.getAmount().asBigDecimal(),
				SimpleDate.today().asDate(), contribution.getAccountNumber(), dining.getMerchantNumber(),
				dining.getDate().asDate(), dining.getAmount().asBigDecimal());
		return new RewardConfirmation(confirmationNumber, contribution);
	}

	private String nextConfirmationNumber() {
		String sql = "select next value for S_REWARD_CONFIRMATION_NUMBER from DUAL_REWARD_CONFIRMATION_NUMBER";
		return jdbcTemplate.queryForObject(sql, String.class);
	}
}
