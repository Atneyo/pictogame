package chloe.pictogame.integration;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.mysql.MySQLContainer;

/**
 * Base class for tests that need a real MySQL. The container is started once and shared by
 * every subclass (Testcontainers removes it automatically when the JVM exits).
 */
public abstract class MySqlIntegrationTest {

    private static final MySQLContainer MYSQL =
            new MySQLContainer("mysql:8.4").withUrlParam("allowPublicKeyRetrieval", "true");

    static {
        MYSQL.start();
    }

    // The container port is random, so the datasource is configured at runtime
    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        // RequiredConfigCheck insists on these three keys to exist, even though the
        // datasource properties above already override the values from application.yml
        registry.add("DB_NAME", MYSQL::getDatabaseName);
        registry.add("DB_USER", MYSQL::getUsername);
        registry.add("DB_PASSWORD", MYSQL::getPassword);
    }
}