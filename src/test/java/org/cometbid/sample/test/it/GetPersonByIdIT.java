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

import org.cometbid.sample.test.person.Person;
import org.cometbid.sample.test.person.PersonRepository;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.boot.test.context.SpringBootTest.*;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 *
 * @author samueladebowale
 */
// JUnit 4.12 example
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = GetPersonByIdIT.Initializer.class)
public class GetPersonByIdIT {

    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer
            = new PostgreSQLContainer("postgres:16.1").withPassword("inmemory").withUsername("inmemory");

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    public TestRestTemplate testRestTemplate;

    public static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues values
                    = TestPropertyValues.of(
                            "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                            "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                            "spring.datasource.username=" + postgreSQLContainer.getUsername());
            values.applyTo(configurableApplicationContext);
        }
    }

    @Test
    public void testNotExistingPersonByIdShouldReturn404() {

        ResponseEntity<Person> result = testRestTemplate.getForEntity("/api/persons/42", Person.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody().getName());
        assertNull(result.getBody().getId());
    }

    @Test
    @Sql("/testdata/FILL_FOUR_PERSONS.sql")
    public void testExistingPersonById() {
        System.out.println(personRepository.findAll().size());

        ResponseEntity<Person> result = testRestTemplate.getForEntity("/api/persons/1", Person.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Phil", result.getBody().getName());
        assertEquals(1l, result.getBody().getId().longValue());
    }
}
