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

**Naming conventions between YAML and feature files:**
- YAML filename `SauceDemoLoginPage.yml` → referenced in steps as `"Sauce Demo Login Page"` (CamelCase split on caps, spaces inserted)
- YAML element key `login_button` → referenced in steps as `"Login Button"` (snake_case → Title Case)
- Account names (e.g., `StandardUser`) are used verbatim in both YAML and steps

**Sentinel-provided step definitions** — Most Cucumber steps come from the Sentinel framework itself (`io.github.sentinel.steps`). The glue paths in `TestRunner.java` are: `io.github.sentinel.steps`, `steps`, `hooks`.

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
- **Sentinel**: `io.github.sentinel:sentinel:1.0.14-SNAPSHOT` installed from `https://github.com/plmlkrz/Sentinel` (main branch) — must be built and `mvn install`-ed locally before this project can compile
- **Cucumber**: 7.8.1 (transitive from Sentinel)
- **JUnit Platform**: 1.9.2 (`junit-platform-suite`, `junit-platform-launcher`)
- `<proc>none</proc>` disables annotation processing — required because Sentinel's transitive Lombok 1.18.24 crashes on Java 17+
- Surefire passes `--add-opens java.base/java.io=ALL-UNNAMED` to allow Sentinel's `ConfigurationData` (extends `File`) to be deserialized by Jackson on Java 17+

## Task Delegation

Spawn subagents to isolate context, parallelize independent work, or offload bulk mechanical tasks. Don't spawn when the parent needs the reasoning, when synthesis requires holding things together, or when spawn overhead dominates.

Pick the cheapest model that can do the subtask well:
- Haiku: bulk mechanical work, no judgment
- Sonnet: scoped research, code exploration, in-scope synthesis
- Opus: subtasks needing real planning or tradeoffs

If a subagent realizes it needs a higher tier than itself, return to the parent. Parent owns final output and cross-spawn synthesis. User instructions override.

## Preferred Tools

### Data Fetching

1. **WebFetch** — free, text-only, works on public pages that don't block bots.
2. **agent-browser CLI** — free, local Rust CLI + Chrome via CDP. For dynamic pages or auth walls that WebFetch can't handle. Returns the accessibility tree with element refs (@e1, @e2) — ~82% fewer tokens than screenshot-based tools. Install: `npm i -g agent-browser && agent-browser install`. Use `snapshot` for AI-friendly DOM state, element refs for interaction.
3. **Notice recurring fetch patterns and propose wrapping them as dedicated tools.** When the same fetch/parse logic comes up more than once, suggest wrapping it as a named tool (e.g. a skill file or a .py script that calls `agent-browser` with the snapshot and extraction steps baked in for that source). Add the entry to `## Dedicated Tools` below and reference it by name on future calls.

### PDF Files

Use 'pdftotext', not the 'Read' tool. Use 'Read' only when the user directly asks to analyze images or charts inside the document.
