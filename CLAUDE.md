# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Test Commands

```bash
mvn test                                  # Run all tests
mvn test -Dcucumber.filter.tags=@47A      # Run a single scenario by tag
mvn test -Dcucumber.filter.tags=@48       # Run all scenarios in feature 48
```

## Architecture Overview

This is a Cucumber BDD test automation project for the [SauceLabs demo app](https://www.saucedemo.com/), built on the **Sentinel framework** — a WebDriver/Cucumber abstraction layer.

### Key Design Patterns

**YAML-based Page Objects** — Page objects live in `src/main/java/com/saucedemo/` as `.yml` files (not Java classes). Each defines:
- A base URL
- Named test accounts (username/password pairs)
- UI element locators (supporting multiple strategies: id, xpath, css, name, text)
- Page composition via `include:` (e.g., `SauceDemoMainPage` includes `SauceDemoMenuPage`)

**Sentinel-provided step definitions** — Most Cucumber steps come from the Sentinel framework itself (`com.dougnoel.sentinel.steps`). The glue paths in `TestRunner.java` are: `com.dougnoel.sentinel.steps`, `steps`, `hooks`.

**Custom steps delegate to Sentinel** — `src/test/java/steps/SwagLabsSteps.java` contains composite steps that call Sentinel's `BaseSteps` and `AccountSteps` methods directly.

### Project Layout

```
conf/example.sentinel.yml             # Sentinel config template (browser, timeout, page packages)
src/main/java/com/saucedemo/          # YAML page objects
src/test/resources/features/          # Cucumber feature files (numbered, e.g. "47 Swag Labs Login.feature")
src/test/java/steps/                  # Custom step definitions
src/test/java/hooks/ScenarioHooks.java # @After hook: clears cookies between scenarios
src/test/java/hooks/SuiteHooks.java   # @BeforeAll/@AfterAll: recording and driver teardown
src/test/java/tests/TestRunner.java   # JUnit 5 @Suite runner (cucumber-junit-platform-engine)
src/test/resources/extent.properties  # Report output paths
```

### Runner

`TestRunner.java` uses the JUnit 5 Platform Suite engine (`@Suite` + `@IncludeEngines("cucumber")`). Tag filtering is passed as a system property: `-Dcucumber.filter.tags=@tagName`. There is no `-Dtest=TestRunner` equivalent — the suite is discovered automatically by Surefire via the JUnit Platform.

### Sentinel Configuration

Copy `conf/example.sentinel.yml` to `conf/sentinel.yml` and configure:
- `browser`: chrome, firefox, etc.
- `headless`: true/false
- `timeout`: element wait timeout in seconds
- `pageObjectPackages`: must include `com.saucedemo`
- `chromedriverPath`: path to manually downloaded ChromeDriver binary (used when WebDriverManager cannot auto-resolve the current Chrome version)

### Test Reports

- JSON: `target/cucumber.json`
- HTML: `reports/extent-cucumber-report.html`
- PDF: `reports/ExtentPdf.pdf`

### Scenario Tagging Convention

Scenarios are tagged with numbered identifiers matching their feature file prefix (e.g., `@47A`, `@47B`, `@48A`). Use these tags to run individual scenarios.

### Java & Dependencies

- **JDK**: 26 (installed); compiler targets Java 21 (LTS) via `<release>21</release>`
- **Cucumber**: 7.8.1 (transitive from Sentinel)
- **JUnit Platform**: 1.9.2 (`junit-platform-suite`, `junit-platform-launcher`)
- `<proc>none</proc>` disables annotation processing — required because Sentinel's transitive Lombok 1.18.24 crashes on Java 17+
