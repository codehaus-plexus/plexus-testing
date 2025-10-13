# Plexus Testing

Library to help testing plexus components

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
