package tests;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * Scenario lifecycle (recording, screenshots, driver teardown, state reset) is
 * owned by hooks.SentinelHooks, which runs per scenario rather than per class.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    monochrome = true,
    features = "src/test/java/features",
    glue = { "io.github.sentinel.steps", "steps", "hooks" },
    plugin = {
        "json:target/cucumber.json",
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
    }
)
public class TestRunner {
}
