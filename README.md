# SampleCucumber

Cucumber BDD test automation for the [SauceLabs demo app](https://www.saucedemo.com/), built on the [Sentinel](https://github.com/plmlkrz/Sentinel) WebDriver/Cucumber abstraction framework.

## Prerequisites

- **JDK 21+** (project targets Java 21; JDK 26 is fine as the installed runtime)
- **Maven**
- **Chrome** browser
- **Sentinel** built and installed locally — this project depends on `io.github.sentinel:sentinel:1.0.14-SNAPSHOT`, which is not published to a public repo. Clone [Sentinel](https://github.com/plmlkrz/Sentinel), then run `mvn install` in that repo before building this project.

## Setup

1. Install Sentinel locally (see above).
2. Copy the Sentinel config template and fill in your local values:
   ```bash
   cp conf/example.sentinel.yml conf/sentinel.yml
   ```
   Key fields in `conf/sentinel.yml`:
   - `browser` — e.g. `chrome`
   - `headless` — `true`/`false`
   - `timeout` — element wait timeout (seconds)
   - `pageObjectPackages` — must include `com.saucedemo`
   - `chromeBrowserBinary` — path to a specific Chrome binary (optional)
   - `chromedriverPath` — path to a manually downloaded ChromeDriver binary, used if WebDriverManager can't auto-resolve a driver for your installed Chrome version (a matching driver is bundled under `drivers/chromedriver-win64/`)
3. Build the project (also compiles/verifies against Sentinel):
   ```bash
   mvn compile
   ```

## Running Tests

Run the full suite:
```bash
mvn test
```

Run a single scenario by tag:
```bash
mvn test -Dcucumber.filter.tags=@47A
```

Run all scenarios in a feature:
```bash
mvn test -Dcucumber.filter.tags=@48
```

Tests are run via `TestRunner.java`, a JUnit 5 Platform `@Suite` that discovers Cucumber automatically through Surefire — there is no `-Dtest=TestRunner` equivalent.

## Reports

After a run, reports are written to:
- `target/cucumber.json` — raw Cucumber JSON
- `reports/extent-cucumber-report.html` — HTML report
- `reports/ExtentPdf.pdf` — PDF report

## Project Structure

```
conf/example.sentinel.yml             # Sentinel config template — copy to conf/sentinel.yml
drivers/chromedriver-win64/           # Bundled ChromeDriver (Windows)
src/main/java/com/saucedemo/          # YAML page objects (URLs, accounts, element locators)
src/test/resources/features/          # Cucumber feature files (numbered, e.g. "47 Swag Labs Login.feature")
src/test/java/steps/                  # Custom step definitions (delegate to Sentinel)
src/test/java/hooks/                  # @Before/@After/@BeforeAll/@AfterAll hooks
src/test/java/tests/TestRunner.java   # JUnit 5 @Suite runner
src/test/resources/extent.properties  # Report output configuration
```

## How Page Objects Work

Page objects are YAML files, not Java classes, and are wired into feature/step files by name convention:

- `SauceDemoLoginPage.yml` (filename) → `"Sauce Demo Login Page"` in steps (CamelCase split on capitals)
- `login_button` (YAML key) → `"Login Button"` in steps (snake_case → Title Case)
- Account names like `StandardUser` are used verbatim in both the YAML and the feature files
- Pages can compose other pages via `include:` (e.g. `SauceDemoMainPage` includes `SauceDemoMenuPage`)

Most step definitions come from Sentinel itself (`io.github.sentinel.steps`); project-specific composite steps live in `src/test/java/steps/SwagLabsSteps.java`. Glue is discovered from `io.github.sentinel.steps`, `steps`, and `hooks`.

## Scenario Tagging

Scenarios are tagged with numbered identifiers matching their feature file prefix (e.g. `@47A`, `@47B`, `@48A`), used to run individual scenarios via `-Dcucumber.filter.tags`.

## More Detail

See `CLAUDE.md` in the repo root for a deeper architecture overview and framework/dependency notes.
