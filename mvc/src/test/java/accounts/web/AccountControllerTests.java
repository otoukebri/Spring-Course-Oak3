package accounts.web;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;

import java.util.List;

import accounts.internal.StubAccountManager;
import rewards.internal.account.Account;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * A JUnit test case testing the AccountController. The AccountController has
 * two handler methods, therefore, two tests.
 */
public class AccountControllerTests {

	private AccountController controller;

	@Before
	public void setUp() throws Exception {
		controller = new AccountController(new StubAccountManager());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testHandleListRequest() {
		ExtendedModelMap model = new ExtendedModelMap();
		String viewName = controller.accountList(model);

		List<Account> accounts = (List<Account>) model.get("accounts");
		assertNotNull(accounts);
		assertEquals(1, accounts.size());
		assertEquals(Long.valueOf(0), accounts.get(0).getEntityId());

		// TODO-04: Change this to expect a logical view name
		// Re-run the test and make sure it passes
		//
		assertEquals("accountList", viewName);
	}

	// TODO-05: Restart the server. You should still be able to see the
	// list of accounts on the home page.

	// TODO-07: Add a test for the accountDetails() method of AccountController.
	// Use the test method above for hints. Supply 0 for the ID value to
	// retrieve.
	// Create assertions for model contents and view name.
	// When complete run the test. It should pass.

	@Test
	public void testHandleAccountList() throws Exception {
		ExtendedModelMap model = new ExtendedModelMap();
		String viewName = controller.accountDetails(model, 0);

		final Account account = (Account) model.get("account");
		assertNotNull(account);
		assertEquals(Long.valueOf(0), account.getEntityId());
	}


	// TODO-08: Restart the server. You should now be able to click
	// any of the account links and reach their details page.

}
