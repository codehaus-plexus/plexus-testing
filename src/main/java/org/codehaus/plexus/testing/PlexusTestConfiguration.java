package org.codehaus.plexus.testing;

import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.PlexusContainer;

/**
 * Allow to customize the Plexus container by test class.
 *
 * @since 1.7.0
 */
public interface PlexusTestConfiguration {

    /**
     * Customize the container configuration before the container is created.
     *
     * @param containerConfiguration the container configuration to customize
     * @since 1.7.0
     */
    default void customizeConfiguration(ContainerConfiguration containerConfiguration) {}

    /**
     * Customize the container after it has been created.
     *
     * @param container the container to customize
     * @since 1.7.0
     */
    default void customizeContainer(PlexusContainer container) {}
}
