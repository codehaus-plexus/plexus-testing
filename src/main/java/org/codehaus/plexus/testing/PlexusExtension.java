package org.codehaus.plexus.testing;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.util.Collections;
import java.util.Optional;

import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.DefaultContext;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * This is a slightly modified version of the original plexus class
 * available at https://raw.githubusercontent.com/codehaus-plexus/plexus-containers/master/plexus-container-default/
 *              src/main/java/org/codehaus/plexus/PlexusTestCase.java
 * in order to migrate the tests to JUnit 5.
 *
 * @author Jason van Zyl
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @author Guillaume Nodet
 */
public class PlexusExtension implements BeforeEachCallback, AfterEachCallback {

    private static final ExtensionContext.Namespace PLEXUS_EXTENSION =
            ExtensionContext.Namespace.create("PlexusExtension");

    public static final String BASEDIR_KEY = "basedir";

    private static final ThreadLocal<ExtensionContext> extensionContextThreadLocal = new ThreadLocal<>();

    static {
        if (System.getProperty("guice_custom_class_loading", "").trim().isEmpty()) {
            System.setProperty("guice_custom_class_loading", "CHILD");
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        extensionContextThreadLocal.set(context);
        setTestBasedir(getDefaultBasedir(), context);

        ((DefaultPlexusContainer) getContainer(context))
                .addPlexusInjector(
                        Collections.emptyList(), binder -> binder.requestInjection(context.getRequiredTestInstance()));
    }

    private PlexusContainer setupContainer(ExtensionContext context) {
        // ----------------------------------------------------------------------------
        // Context Setup
        // ----------------------------------------------------------------------------

        DefaultContext plexusContext = new DefaultContext();
        plexusContext.put("basedir", getTestBasedir(context));
        customizeContext(plexusContext);

        boolean hasPlexusHome = plexusContext.contains("plexus.home");

        if (!hasPlexusHome) {
            File f = new File(getTestBasedir(context), "target/plexus-home");

            if (!f.isDirectory()) {
                f.mkdir();
            }

            plexusContext.put("plexus.home", f.getAbsolutePath());
        }

        // ----------------------------------------------------------------------------
        // Configuration
        // ----------------------------------------------------------------------------

        String config = getCustomConfigurationName();

        ContainerConfiguration containerConfiguration =
                new DefaultContainerConfiguration().setName("test").setContext(plexusContext.getContextData());

        if (config != null) {
            containerConfiguration.setContainerConfiguration(config);
        } else {
            String resource = getConfigurationName(context);
            containerConfiguration.setContainerConfiguration(resource);
        }

        customizeContainerConfiguration(containerConfiguration);
        testInstanceCustomizeContainerConfiguration(containerConfiguration, context);

        PlexusContainer container;
        try {
            container = new DefaultPlexusContainer(containerConfiguration);
            container.addComponent(container, PlexusContainer.class.getName());
        } catch (PlexusContainerException e) {
            throw new IllegalArgumentException("Failed to create plexus container.", e);
        }
        testInstanceCustomizeContainer(container, context);
        context.getStore(PLEXUS_EXTENSION).put(PlexusContainer.class, container);

        return container;
    }

    /**
     * Allow custom test case implementations do augment the default container configuration before
     * executing tests.
     *
     * @param containerConfiguration {@link ContainerConfiguration}.
     */
    protected void customizeContainerConfiguration(ContainerConfiguration containerConfiguration) {
        containerConfiguration.setAutoWiring(true);
        containerConfiguration.setClassPathScanning(PlexusConstants.SCANNING_INDEX);
    }

    private void testInstanceCustomizeContainerConfiguration(
            ContainerConfiguration containerConfiguration, ExtensionContext context) {
        Object testInstance = context.getRequiredTestInstance();
        if (testInstance instanceof PlexusTestConfiguration) {
            ((PlexusTestConfiguration) testInstance).customizeConfiguration(containerConfiguration);
        }
    }

    private void testInstanceCustomizeContainer(PlexusContainer container, ExtensionContext context) {
        Object testInstance = context.getRequiredTestInstance();
        if (testInstance instanceof PlexusTestConfiguration) {
            ((PlexusTestConfiguration) testInstance).customizeContainer(container);
        }
    }

    protected void customizeContext(Context context) {}

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        PlexusContainer container =
                context.getStore(PLEXUS_EXTENSION).remove(PlexusContainer.class, PlexusContainer.class);
        if (container != null) {
            container.dispose();
        }
        context.getStore(PLEXUS_EXTENSION).remove("testBasedir", String.class);
        extensionContextThreadLocal.remove();
    }

    /**
     * The base directory for the test instance. By default, this is the same as the basedir.
     *
     * @param context  the test execution context
     *
     * @return the testBasedir
     * @since 1.7.0
     */
    protected String getTestBasedir(ExtensionContext context) {
        String testBasedir = context.getStore(PLEXUS_EXTENSION).get(BASEDIR_KEY, String.class);
        if (testBasedir == null) {
            testBasedir = getDefaultBasedir();
            context.getStore(PLEXUS_EXTENSION).put(BASEDIR_KEY, testBasedir);
        }
        return testBasedir;
    }

    /**
     * Set the base directory for the test instance. By default, this is the same as the basedir.
     *
     * @param testBasedir the testBasedir for the test instance
     * @param context  the test execution context
     * @since 1.7.0
     */
    protected void setTestBasedir(String testBasedir, ExtensionContext context) {
        context.getStore(PLEXUS_EXTENSION).put(BASEDIR_KEY, testBasedir);
    }

    public PlexusContainer getContainer(ExtensionContext context) {
        PlexusContainer container =
                context.getStore(PLEXUS_EXTENSION).get(PlexusContainer.class, PlexusContainer.class);
        if (container == null) {
            return setupContainer(context);
        }
        return container;
    }

    protected String getCustomConfigurationName() {
        return null;
    }

    /**
     * Allow the retrieval of a container configuration that is based on the name
     * of the test class being run. So if you have a test class called org.foo.FunTest, then
     * this will produce a resource name of org/foo/FunTest.xml which would be used to
     * configure the Plexus container before running your test.
     *
     * @return A configruation name
     */
    protected String getConfigurationName(ExtensionContext context) {
        Class<?> testClass = context.getRequiredTestClass();
        for (Class<?> clazz = testClass; clazz != null; clazz = clazz.getSuperclass()) {
            String name = clazz.getName().replace('.', '/') + ".xml";
            if (testClass.getClassLoader().getResource(name) != null) {
                return name;
            }
        }
        return null;
    }

    // ----------------------------------------------------------------------
    // Helper methods for sub classes
    // ----------------------------------------------------------------------

    public static File getTestFile(String path) {
        return new File(getBasedir(), path);
    }

    public static File getTestFile(String basedir, String path) {
        File basedirFile = new File(basedir);

        if (!basedirFile.isAbsolute()) {
            basedirFile = getTestFile(basedir);
        }

        return new File(basedirFile, path);
    }

    public static String getTestPath(String path) {
        return getTestFile(path).getAbsolutePath();
    }

    public static String getTestPath(String basedir, String path) {
        return getTestFile(basedir, path).getAbsolutePath();
    }

    private static String getDefaultBasedir() {
        String basedir = System.getProperty("basedir");

        if (basedir == null) {
            basedir = new File("").getAbsolutePath();
        }

        return basedir;
    }

    public static String getBasedir() {
        return Optional.ofNullable(extensionContextThreadLocal.get())
                .map(ec -> ec.getStore(PLEXUS_EXTENSION).get(BASEDIR_KEY, String.class))
                .orElseGet(PlexusExtension::getDefaultBasedir);
    }

    public static String getTestConfiguration(Class<?> clazz) {
        String s = clazz.getName().replace('.', '/');

        return s.substring(0, s.indexOf("$")) + ".xml";
    }
}
