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
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 *
 * @author samueladebowale
 */
// JUnit 5 example with Spring Boot >= 2.2.6
@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CreatePersonIT {

    @Container
    public static PostgreSQLContainer postgreSQLContainer
            = new PostgreSQLContainer("postgres:16.1").withPassword("inmemory").withUsername("inmemory");

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    public TestRestTemplate testRestTemplate;

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @Test
    public void testRestEndpointForAllPersons() {

        Person requestBody = new Person();
        requestBody.setName("rieckpil");

        assertEquals(0, personRepository.findAll().size());

        ResponseEntity<Person> result
                = testRestTemplate.postForEntity("/api/persons", requestBody, Person.class);

        assertNotNull(result);
        assertNotNull(result.getBody().getId());
        assertEquals("rieckpil", result.getBody().getName());
        assertEquals(1, personRepository.findAll().size());
        assertEquals("rieckpil", personRepository.findAll().get(0).getName());
    }
}
