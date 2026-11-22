# Plexus Testing

[![Maven Central](https://img.shields.io/maven-central/v/org.codehaus.plexus/plexus-testing.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/org.codehaus.plexus/plexus-testing)
[![GitHub CI](https://github.com/codehaus-plexus/plexus-testing/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/codehaus-plexus/plexus-testing/actions/workflows/maven.yml?query=branch%3Amaster)
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
  <scope>test</scope>
</dependency>
```

Check the badge above for the current version.

```java
import javax.inject.Inject;

import org.codehaus.plexus.testing.PlexusTest;
import org.junit.jupiter.api.Test;

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
every method has a default, so implement only the ones you need.

How the container is built:

- A new container is created for each test method, and it is closed after the test.
- It scans the Sisu index (`META-INF/sisu/javax.inject.Named`) and wires `@Inject` fields of the test
  instance, including those of `@Nested` classes.
- If a resource named after the test class exists (`org/foo/FunTest.xml` for `org.foo.FunTest`, also
  looked up for its superclasses), it is used as the Plexus container configuration.
- `PlexusExtension.getBasedir()`, `getTestFile(path)` and `getTestPath(path)` resolve paths against the
  `basedir` system property, or the working directory when it is not set. The `plexus.home` context
  entry defaults to `target/plexus-home` under it.

The
[project site](https://codehaus-plexus.github.io/plexus-testing/) shows the full examples, compiled from
this repository's own tests.

## Requirements

Java 8 or later, JUnit 5, and `javax.inject` for the components under test (this artifact does not bring it). Building the project needs Maven 3.9.0 or later.

## Documentation

- [Project site](https://codehaus-plexus.github.io/plexus-testing/)
- [Javadoc](https://javadoc.io/doc/org.codehaus.plexus/plexus-testing)
- [Release notes](https://github.com/codehaus-plexus/plexus-testing/releases)

## Contributing

Conventions for contributors and agents are in [AGENTS.md](AGENTS.md); see also
[CONTRIBUTING.md](https://github.com/codehaus-plexus/.github/blob/master/CONTRIBUTING.md). In short:
`mvn verify` builds, and run `mvn spotless:apply` before pushing or CI will fail on formatting.

Please report security vulnerabilities privately — see
[SECURITY.md](https://github.com/codehaus-plexus/.github/blob/master/SECURITY.md), not a public issue.
