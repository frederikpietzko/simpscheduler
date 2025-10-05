package com.github.frederikpietzko.testutil

import com.github.frederikpietzko.infrastructure.DbSettings
import com.github.frederikpietzko.infrastructure.PostgresConfig
import com.github.frederikpietzko.users.persistence.UserTable
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
abstract class DatabaseIntegrationTestBase {
    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass")

        @JvmStatic
        @BeforeAll
        fun setUpDatabase() = runBlocking {
            postgres.start()
            DbSettings.initialize(
                PostgresConfig(
                    url = "r2dbc:postgresql://${postgres.host}:${postgres.getMappedPort(5432)}/${postgres.databaseName}",
                    user = postgres.username,
                    password = postgres.password
                )
            )
            suspendTransaction {
                SchemaUtils.create(UserTable)
            }
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            postgres.stop()
        }
    }
}

