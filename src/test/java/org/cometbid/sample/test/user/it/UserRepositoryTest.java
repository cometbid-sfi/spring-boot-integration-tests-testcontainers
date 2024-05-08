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

import org.assertj.core.api.Assertions;
import org.cometbid.sample.test.user.User;
import org.cometbid.sample.test.user.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.Assert;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 *
 * @author samueladebowale
 */
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // deactivate the default behaviour
@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRepositoryTest {

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
        // repository.deleteAll();
    }

    @Test
    @Order(1)
    public void should_save_user_and_equal_given_name_and_saved_user_name() {
        User user = generateUser();
        var savedUser = repository.save(user);

        // org.assertj.core.api
        Assertions.assertThat(savedUser.getName()).isEqualTo(user.getName());
        Assert.notNull(savedUser.getId(), "Id should not be null!");
    }

    @Test
    @Order(2)
    public void should_get_user_by_name_and_equal_given_name_and_saved_user_name() {
        User user = generateUser();
        var savedUser = repository.save(user);

        var users = repository.getUserByName(savedUser.getName());

        // org.assertj.core.api
        Assertions.assertThat(users.size()).isEqualTo(1);
        Assertions.assertThat(users.get(0).getName()).isEqualTo(user.getName());
    }

    private User generateUser() {
        var name = "Mert";
        return User.builder().name(name).build();
    }
}
