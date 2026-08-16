# CLAUDE.md

<!-- Last audited: 2026-08-15 -->

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Prerequisites

Sentinel is **not published to any remote repository**. Build it into the local
Maven repo before running these tests, and again whenever Sentinel changes:

```bash
cd ../Sentinel && mvn install -DskipTests
```

Then copy `conf/example.sentinel.yml` to `conf/sentinel.yml` (gitignored).

## Build and Test Commands

```bash
mvn test                        # Run all tests
mvn test -Dtest=TestRunner      # Run via specific runner
mvn test -Dcucumber.filter.tags=@47A   # Run a single scenario by tag
```

## Architecture Overview

This is a Cucumber BDD test automation project for the [SauceLabs demo app](https://www.saucedemo.com/), built on the **Sentinel framework** — a WebDriver/Cucumber abstraction layer.

### Key Design Patterns

**YAML-based Page Objects** — Page objects live in `src/main/java/com/saucedemo/` as `.yml` files (not Java classes). Each defines:
- A base URL
- Named test accounts (username/password pairs)
- UI element locators (supporting multiple strategies: id, xpath, css, name, text)
- Page composition via `include:` (e.g., `SauceDemoMainPage` includes `SauceDemoMenuPage`)

Sentinel types elements as `Map<String,Map<String,String>>`, so **each locator
value must be a single string, never a YAML list**. A list makes the entire page
object fail to deserialize, which surfaces as every scenario using that page
erroring out at once — not as a single bad locator.

**Sentinel-provided step definitions** — Most Cucumber steps come from the Sentinel framework itself (`io.github.sentinel.steps`). The glue paths in `TestRunner.java` are: `io.github.sentinel.steps`, `steps`, `hooks`.

**Custom steps delegate to Sentinel** — `src/test/java/steps/SwagLabsSteps.java` contains composite steps that call Sentinel's `BaseSteps` and `AccountSteps` methods directly.

**Per-scenario lifecycle lives in hooks** — `src/test/java/hooks/SentinelHooks.java` quits the driver and resets Sentinel's managers after each scenario, and attaches a screenshot on failure. Without it, browser state leaks between scenarios: scenarios pass in isolation but fail when run in sequence. Keep the lifecycle here rather than in `TestRunner`, so it is not run twice.

### Project Layout

```
conf/example.sentinel.yml          # Sentinel config template (browser, timeout, page packages)
src/main/java/com/saucedemo/       # YAML page objects
src/test/java/features/            # Cucumber feature files (numbered, e.g. "47 Swag Labs Login.feature")
src/test/java/steps/               # Custom step definitions
src/test/java/hooks/               # Per-scenario setup/teardown (required by glue path)
src/test/java/tests/TestRunner.java # Cucumber runner with Extent Reports plugins
src/test/resources/extent.properties # Report output paths
```

### Sentinel Configuration

Copy `conf/example.sentinel.yml` to `conf/sentinel.yml` (gitignored) and configure:
- `browser`: chrome, firefox, etc.
- `headless`: true/false
- `timeout`: element wait timeout in seconds
- `pageObjectPackages`: must include `com.saucedemo`

### Test Reports

- JSON: `target/cucumber.json`
- HTML: `reports/extent-cucumber-report.html`
- PDF: `reports/ExtentPdf.pdf`
- Screenshots: `reports/`

### Scenario Tagging Convention

Scenarios are tagged with numbered identifiers matching their feature file prefix (e.g., `@47A`, `@47B`, `@48A`). Use these tags to run individual scenarios.
