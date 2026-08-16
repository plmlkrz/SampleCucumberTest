package steps;

import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;
import io.github.sentinel.steps.AccountSteps;
import io.github.sentinel.steps.BaseSteps;
import io.github.sentinel.steps.WindowAndTabSteps;
import io.github.sentinel.strings.SentinelStringUtils;

import io.cucumber.java.en.Given;

public class SwagLabsSteps {

    @Given("I login to the Sauce Demo Login Page as {}")
    public void i_login_to_the_page_as_user(String account) throws Throwable {
        ExtentCucumberAdapter.addTestStepLog("Given I navigate to the Sauce Demo Login Page");
        BaseSteps.navigateToPage("Sauce Demo Login Page");

        ExtentCucumberAdapter.addTestStepLog(SentinelStringUtils.format(
                "And I fill the account information for account {} into the Username field and the Password field",
                account));
        AccountSteps.fillAccountInfoIntoUsernameAndPasswordFields(account, "Username field", "Password field");

        ExtentCucumberAdapter.addTestStepLog("And I click the Login Button");
        BaseSteps.click("Login button");

        ExtentCucumberAdapter.addTestStepLog("And I am redirected to the Sauce Demo Main Page");
        WindowAndTabSteps.switchTo("Sauce Demo Main Page");
    }
}
