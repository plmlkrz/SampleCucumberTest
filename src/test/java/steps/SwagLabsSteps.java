package steps;

import com.dougnoel.sentinel.steps.AccountSteps;
import com.dougnoel.sentinel.steps.BaseSteps;
import com.dougnoel.sentinel.steps.WindowAndTabSteps;
import io.cucumber.java.en.Given;

public class SwagLabsSteps {

    @Given("I login to the Sauce Demo Login Page as {}")
    public void i_login_to_the_page_as_user(String account) throws InterruptedException {
        BaseSteps.navigateToPage("Sauce Demo Login Page");
        AccountSteps.fillAccountInfoIntoUsernameAndPasswordFields(
            account, "Username field", "Password field");
        BaseSteps.click("Login button");
        WindowAndTabSteps.switchTo("Sauce Demo Main Page");
    }
}
