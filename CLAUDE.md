# CLAUDE.md

<!-- Last audited: 2026-08-07 -->

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

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

**Sentinel-provided step definitions** — Most Cucumber steps come from the Sentinel framework itself (`com.dougnoel.sentinel.steps`). The glue paths in `TestRunner.java` are: `com.dougnoel.sentinel.steps`, `steps`, `hooks`.

**Custom steps delegate to Sentinel** — `src/test/java/steps/SwagLabsSteps.java` contains composite steps that call Sentinel's `BaseSteps` and `AccountSteps` methods directly.

### Project Layout

```
conf/example.sentinel.yml          # Sentinel config template (browser, timeout, page packages)
src/main/java/com/saucedemo/       # YAML page objects
src/test/java/features/            # Cucumber feature files (numbered, e.g. "47 Swag Labs Login.feature")
src/test/java/steps/               # Custom step definitions
src/test/java/tests/TestRunner.java # Cucumber runner with Extent Reports plugins
src/test/resources/extent.properties # Report output paths
```

### Sentinel Configuration

Copy `conf/example.sentinel.yml` to `conf/sentinel.yml` (or the path Sentinel expects) and configure:
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
