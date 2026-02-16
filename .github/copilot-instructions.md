# Spoon Coding Agent Onboarding

## Project Summary

Spoon is an open-source Java library for analyzing, rewriting, transforming, and transpiling Java source code. It builds an AST from Java sources and provides a powerful API for code analysis and transformation. Spoon supports Java versions up to 20 and is used in both academic and industrial contexts.

## Tech Stack

- **Primary Language:** Java (JDK 11+ required)
- **Build System:** Maven (multi-module)
- **Testing:** JUnit 5
- **Linting:** Checkstyle (`checkstyle.xml`, `checkstyle-test.xml`), Qodana (`qodana.yaml`)
- **Formatting:** Spotless Maven plugin
- **CI/CD:** GitHub Actions
- **Other:** Nix/Flake for reproducible builds and dev environments (`flake.nix`)
- **Documentation:** Markdown in `/doc`, Javadoc in `/spoon-javadoc`

## Project Structure

- `spoon-core/` — Main library code and tests
- `spoon-pom/` — Parent Maven POM for all modules
- `spoon-control-flow/`, `spoon-dataflow/`, `spoon-decompiler/`, `spoon-javadoc/`, `spoon-smpl/`, `spoon-visualisation/` — Submodules for experimental or specialized features
- `chore/` — Scripts for quality checks, release, reproducibility, contributor list, etc.
- `doc/` — Documentation (Markdown, site config)
- `src/` — Main, test, and site sources
- `target/` — Build outputs

## Coding Guidelines

- **Pull Requests:** Must be atomic (single feature/bug), well-tested, and documented. Use prefixes in commit messages: `fix:`, `feat:`, `test:`, `doc:`, `perf:`, `chore:`, `refactor:`, `checkstyle:`.
- **Testing:** New tests must use JUnit 5.
- **Formatting:** Use tabs for indentation (enforced by Checkstyle and Spotless).
- **Linting:** All code must pass Checkstyle and Qodana checks (see `checkstyle.xml`, `checkstyle-test.xml`, `qodana.yaml`)
- **Documentation:** Update `/doc` for new features.
- **Public API:** Only classes/methods not annotated with `@Internal` or in `internal` packages are considered public API. `@Experimental` is unstable.

## Build & Environment Setup

- **Java:** Use JDK 11 or newer.
- **Maven:** Build with `mvn clean package -DskipTests`.
- **Tests:** Run with `mvn test`.
- **Nix/Flake:** For reproducible builds and dev shells, use `nix develop` (see `flake.nix`).
- **Locale:** Ensure `LANG=en_US.UTF-8` for tests (see shellHook in `flake.nix`).

