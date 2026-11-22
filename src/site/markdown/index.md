# Plexus Testing

Library to help testing plexus components: a JUnit 5 extension that builds a Plexus container for each
test and injects the test instance's `@Inject` fields from it. It needs Java 8 or later, JUnit 5, and
`javax.inject` on the test classpath for the components under test.

## Example usage

### Test class

<!-- MACRO{snippet|id=test-class|file=src/test/java/org/codehaus/plexus/testing/PlexusTestJavaxTest.java} -->

### Customization of the Plexus Container

Test class can customize the Plexus Container used for testing by implementing
the [`PlexusTestConfiguration`](apidocs/org/codehaus/plexus/testing/PlexusTestConfiguration.html).
All methods have default empty implementations so you can implement only the ones you need.

<!-- MACRO{snippet|id=test-customize-class|file=src/test/java/org/codehaus/plexus/testing/PlexusTestCustomizeTest.java} -->

### Used classes in test

<!-- MACRO{snippet|id=test-component|file=src/test/java/org/codehaus/plexus/testing/TestJavaxComponent.java} -->
<!-- MACRO{snippet|id=test-component2|file=src/test/java/org/codehaus/plexus/testing/TestJavaxComponent2.java} -->

### Container lifecycle and configuration

- A new container is created for each test method, and it is closed after the test. For a `@Nested`
  test class, both the nested and the enclosing instance are injected from that one container.
- The container scans the Sisu index (`META-INF/sisu/javax.inject.Named`), so components that a test
  looks up must be indexed, for example by `sisu-maven-plugin`.
- If a resource named after the test class exists (`org/foo/FunTest.xml` for `org.foo.FunTest`; the
  superclasses are tried too), it is used as the Plexus container configuration.

### Base directory

`PlexusExtension.getBasedir()`, `getTestFile(String)` and `getTestPath(String)` resolve paths against the
`basedir` system property, or the working directory when it is not set. The `basedir` and `plexus.home`
entries are available in the container context; `plexus.home` defaults to `target/plexus-home` under the
base directory.
