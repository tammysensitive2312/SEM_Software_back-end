package org.example.sem_backend.integrationtest;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import org.example.sem_backend.main_service.middleware.auth.jwt.JwtUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class BaseIntegrationTest {
    @LocalServerPort
    private Integer port;

    @Autowired
    private JwtUtils jwtUtils;

    @Container
    public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    protected static final String TEST_EMAIL = "testemail@gmail.com";
    protected static final String TEST_USERNAME = "testuser";
    protected static final Long TEST_USER_ID = 1L;

    @BeforeAll
    static void startContainer() {
        mySQLContainer.start();
    }

    @AfterAll
    static void stopContainer() {
        mySQLContainer.stop();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @BeforeEach
    protected void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;

        // Generate JWT token
//        String jwtToken = jwtUtils.generateTokenFromUsernameAndEmail(TEST_EMAIL, TEST_USERNAME, TEST_USER_ID);
//
//        // Add default RequestSpecification with JWT cookie
//        RestAssured.requestSpecification = new RequestSpecBuilder()
//                .addHeader("Authorization", "Bearer " + jwtToken)
//                .setContentType(ContentType.JSON)
//                .build();
    }

    protected void givenAuthorized() {
        String jwtToken = jwtUtils.generateTokenFromUsernameAndEmail(TEST_EMAIL, TEST_USERNAME, TEST_USER_ID);
        RestAssured.given()
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(ContentType.JSON);
    }

}
