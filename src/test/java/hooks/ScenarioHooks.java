package hooks;

import com.dougnoel.sentinel.webdrivers.WebDriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScenarioHooks {

    private static final Logger log = LogManager.getLogger(ScenarioHooks.class);

    /**
     * Clears browser cookies and local storage between scenarios to prevent
     * session state from leaking between tests.
     */
    @After
    public void clearBrowserState() {
        if (WebDriverFactory.exists()) {
            try {
                WebDriverFactory.getWebDriver().manage().deleteAllCookies();
            } catch (Exception e) {
                log.warn("Could not clear browser cookies between scenarios: {}", e.getMessage());
            }
        }
    }
}
