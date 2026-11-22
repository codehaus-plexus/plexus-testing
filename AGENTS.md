# Agent guide for plexus-testing

`org.codehaus.plexus:plexus-testing` is a JUnit 5 extension for testing JSR-330 components: annotate a
test with `@PlexusTest` and `@Inject` fields are populated from a container built for that test. It
exists so other Codehaus Plexus projects can test components without each building a harness. The
public classes are `PlexusTest`, `PlexusExtension` and `PlexusTestConfiguration`. `junit-jupiter-api`
and `org.eclipse.sisu.plexus` are compile dependencies; Mockito and `javax.inject` are test-only, used
by this repository's own tests.

## Build and test

- CI calls the shared `codehaus-plexus/.github` `maven.yml@master` workflow, which runs
  `mvn verify javadoc:javadoc` on JDK 8, 21 and 25 and on Linux, Windows and macOS. This
  repository's `.github/workflows/maven.yml` has no deploy job, so master does not publish
  snapshots from CI.
- Main code must use the Java 8 API (`javaVersion` 8 from the `plexus` parent).
- Unit tests use JUnit 5, with Mockito through `mockito-junit-jupiter`; the code snippets on the
  site are taken from `src/test/java` by `MACRO{snippet}`, so keep the `START SNIPPET` and
  `END SNIPPET` markers in `PlexusTestJavaxTest`, `PlexusTestCustomizeTest`, `TestJavaxComponent`
  and `TestJavaxComponent2`.
- The JAR sets `Automatic-Module-Name: org.codehaus.plexus.testing`; keep that name stable.
- Run `mvn spotless:apply` on JDK 17 to 21 before pushing; it also formats Markdown, except
  `src/site/markdown`.

## Documentation updates

A change that users or contributors can notice ships with the documentation it affects, in the same
pull request. A refactor with no visible effect needs none.

- Behaviour of `@PlexusTest`, `PlexusExtension` or `PlexusTestConfiguration` (container setup,
  base directory, configuration lookup): their Javadoc, the matching section of
  `src/site/markdown/index.md`, and the example in `README.md`.
- `src/site/markdown/index.md` is the only page, listed as Introduction in `src/site/site.xml`
  together with the Javadoc; add a menu item there when adding a page.
- The Java requirement, or how to build and release: `README.md`.
- A new convention, build gate or test fixture: this file.

