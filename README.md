# Plexus Testing

[![Maven Central](https://img.shields.io/maven-central/v/org.codehaus.plexus/plexus-testing.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/org.codehaus.plexus/plexus-testing)
[![GitHub CI](https://github.com/codehaus-plexus/plexus-testing/actions/workflows/maven.yml/badge.svg)](https://github.com/codehaus-plexus/plexus-testing/actions)
[![Reproducible Builds](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/jvm-repo-rebuild/reproducible-central/master/content/org/codehaus/plexus/plexus-testing/badge.json)](https://github.com/jvm-repo-rebuild/reproducible-central/blob/master/content/org/codehaus/plexus/plexus-testing/README.md)

A JUnit 5 extension for testing JSR-330 components. Annotate a test class with `@PlexusTest` and your
`@Inject` fields are populated from a container built for that test.

## Status

Maintained. Small and stable — it exists so the other projects here can test their components without each
inventing the same fixture.

## Using it

```xml
<dependency>
  <groupId>org.codehaus.plexus</groupId>
  <artifactId>plexus-testing</artifactId>
  <version>2.1.0</version>
  <scope>test</scope>
</dependency>
```

Check the badge above for the current version.

```java
@PlexusTest
class MyComponentTest {

    @Inject
    MyComponent component;

    @Test
    void itWorks() {
        assertNotNull( component );
    }
}
```

A test class can customise the container by implementing
[`PlexusTestConfiguration`](https://codehaus-plexus.github.io/plexus-testing/apidocs/org/codehaus/plexus/testing/PlexusTestConfiguration.html);
every method has a default, so implement only the ones you need. The
[project site](https://codehaus-plexus.github.io/plexus-testing/) shows the full examples, compiled from
this repository's own tests.

## Requirements

Java 8 or later, and JUnit 5.

## Documentation

- [Project site](https://codehaus-plexus.github.io/plexus-testing/)
- [Javadoc](https://javadoc.io/doc/org.codehaus.plexus/plexus-testing)
- [Release notes](https://github.com/codehaus-plexus/plexus-testing/releases)

## Contributing

See [CONTRIBUTING.md](https://github.com/codehaus-plexus/.github/blob/master/CONTRIBUTING.md). In short:
`mvn verify` builds, and run `mvn spotless:apply` before pushing or CI will fail on formatting.

Please report security vulnerabilities privately — see
[SECURITY.md](https://github.com/codehaus-plexus/.github/blob/master/SECURITY.md), not a public issue.
