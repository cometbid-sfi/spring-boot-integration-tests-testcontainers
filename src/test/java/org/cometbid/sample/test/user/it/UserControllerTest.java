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
package org.cometbid.sample.test.user.it;

import org.cometbid.sample.test.user.User;
import org.cometbid.sample.test.user.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 * @author samueladebowale
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest {

    @Container
    static PostgreSQLContainer postgresqlContainer = new PostgreSQLContainer("postgres:15")
            .withDatabaseName("test")
            .withUsername("root")
            .withPassword("root");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
    }

    @Autowired
    private UserRepository repository;

    @Autowired
    private TestRestTemplate restTemplate;

    private String id;

    @BeforeAll
    static void beforeAll() {
        postgresqlContainer.start();

    }

    @AfterAll
    static void afterAll() {
        postgresqlContainer.stop();
    }

    @BeforeEach
    void setUp() {
//        repository.deleteAll();
//
//        var savedUser = repository.save(User.builder().name("mert").build());
//        this.id = savedUser.getId();
    }

    @Test
    @Order(1)
    public void should_save_user_and_return_created_status_code() {

        var user = User.builder().name("mert").build();

        ResponseEntity<User> createResponse = restTemplate.postForEntity("/api/v1/users", user, User.class);
        User savedUser = createResponse.getBody();

        // org.junit.jupiter.api.Assertions
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertEquals(user.getName(), savedUser.getName());
        assertNotNull(savedUser);
    }

    @Test
    @Order(2)
    public void should_find_user_by_id_and_return_ok_status_code() {
        var user = User.builder().name("mert").build();
        repository.save(user);

        var url = String.format("/api/v1/users/%s", user.getId());

        ResponseEntity<User> findUserByIdResponse = restTemplate.getForEntity(url, User.class);
        User existUser = findUserByIdResponse.getBody();

        // org.junit.jupiter.api.Assertions
        assertEquals(HttpStatus.OK, findUserByIdResponse.getStatusCode());
        assertNotNull(existUser);

    }
}
