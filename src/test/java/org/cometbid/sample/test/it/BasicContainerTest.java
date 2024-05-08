/*
 * The MIT License
 *
 * Copyright 2024 samueladebowale.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.cometbid.sample.test.it;

import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 *
 * @author samueladebowale
 */
@Disabled("Only for demonstration purposes")
@Testcontainers
class BasicContainerTest {

    @Container
    static GenericContainer<?> keycloak
            = new GenericContainer<>(DockerImageName.parse("jboss/keycloak:16.1.1"))
                    .waitingFor(Wait.forHttp("/auth").forStatusCode(200))
                    .withExposedPorts(8080)
                    .withClasspathResourceMapping("/config/test.txt", "/tmp/test.txt", BindMode.READ_WRITE)
                    .withEnv(
                            Map.of(
                                    "KEYCLOAK_USER", "testcontainers",
                                    "KEYCLOAK_PASSWORD", "testcontainers",
                                    "DB_VENDOR", "h2"));

    @Test
    void testWithKeycloak() throws IOException, InterruptedException {
        org.testcontainers.containers.Container.ExecResult execResult
                = keycloak.execInContainer("/bin/sh", "-c", "echo \"Admin user is $KEYCLOAK_USER\"");
        System.out.println("Result: " + execResult.getStdout());
        System.out.println("Keycloak is running on port: " + keycloak.getMappedPort(8080));
    }
}
