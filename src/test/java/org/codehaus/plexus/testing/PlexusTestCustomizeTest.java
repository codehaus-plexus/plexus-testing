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
// START SNIPPET: test-customize-class
import javax.inject.Inject;

import org.codehaus.plexus.PlexusContainer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

// MockitoExtension must be first
@ExtendWith(MockitoExtension.class)
@PlexusTest
class PlexusTestCustomizeTest implements PlexusTestConfiguration {

    @Mock
    private TestJavaxComponent2 mockComponent2;

    /*
    Customize the container configuration before it is used to create the container.

    Method has default empty implementation so not need to implement if not used.

    @Override
    public void customizeConfiguration(ContainerConfiguration containerConfiguration) {

    }
    */

    @Override
    public void customizeContainer(PlexusContainer container) {
        container.addComponent(mockComponent2, TestJavaxComponent2.class.getName());
    }

    @Inject
    private TestJavaxComponent testJavaxComponent;

    @Test
    void dependencyShouldBeInjected() {
        assertNotNull(testJavaxComponent);
        assertSame(testJavaxComponent.getTestComponent2(), mockComponent2);
    }

    @Nested
    class NestedTest {
        @Test
        void nestedDependencyShouldAlsoBeInjected() {
            assertNotNull(testJavaxComponent);
            assertSame(testJavaxComponent.getTestComponent2(), mockComponent2);
        }
    }
}
// END SNIPPET: test-customize-class
