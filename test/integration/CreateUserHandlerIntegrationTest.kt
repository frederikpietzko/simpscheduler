package com.github.frederikpietzko.integration

import com.github.frederikpietzko.framework.command.Invoker
import com.github.frederikpietzko.framework.command.addHandlers
import com.github.frederikpietzko.infrastructure.DbSettings
import com.github.frederikpietzko.infrastructure.PostgresConfig
import com.github.frederikpietzko.users.commands.CreateUserCommand
import com.github.frederikpietzko.users.domain.*
import com.github.frederikpietzko.users.handlers.CreateUserHandler
import com.github.frederikpietzko.users.persistence.UserRepository
import com.github.frederikpietzko.users.persistence.UserTable
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mindrot.jbcrypt.BCrypt
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class CreateUserHandlerIntegrationTest {

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

            // Initialize the database settings
            DbSettings.initialize(
                PostgresConfig(
                    url = "r2dbc:postgresql://${postgres.host}:${postgres.getMappedPort(5432)}/${postgres.databaseName}",
                    user = postgres.username,
                    password = postgres.password
                )
            )

            // Create database schema for testing
            suspendTransaction {
                SchemaUtils.create(UserTable)
            }

            // Initialize the command invoker and register handlers
            Invoker.initialize()
            addHandlers(CreateUserHandler)
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            postgres.stop()
        }
    }

    @BeforeEach
    fun cleanDatabase(): Unit = runBlocking {
        // Clean up any existing test data before each test
        suspendTransaction {
            UserRepository.deleteAll()
        }
    }

    @Test
    fun `should create user successfully when given valid command`(): Unit = runBlocking {
        // Given
        val username = Username("testuser123")
        val password = ClearPassword("securePassword123")
        val person = Person(
            firstName = "John",
            lastName = "Doe",
            emailAddress = EmailAddress("john.doe@example.com")
        )
        val command = CreateUserCommand(
            username = username,
            password = password,
            person = person
        )

        // When
        Invoker.invoke(command)

        // Then
        // Verify that the user was actually created in the database
        val users = UserRepository.findByUsernameWithPasswordOrNull(username)
        users shouldNotBe null
        users!!.username shouldBe username
        users.person.firstName shouldBe "John"
        users.person.lastName shouldBe "Doe"
        users.person.emailAddress shouldBe EmailAddress("john.doe@example.com")
        users.createdAt shouldNotBe null

        // Verify password was hashed correctly
        users.password shouldNotBe null
        BCrypt.checkpw(password.value, users.password!!.value) shouldBe true
    }

    @Test
    fun `should create users with unique IDs when processing multiple commands`(): Unit = runBlocking {
        // Given
        val command1 = CreateUserCommand(
            username = Username("user1"),
            password = ClearPassword("password1"),
            person = Person("First", "User", EmailAddress("user1@example.com"))
        )
        val command2 = CreateUserCommand(
            username = Username("user2"),
            password = ClearPassword("password2"),
            person = Person("Second", "User", EmailAddress("user2@example.com"))
        )

        // When
        Invoker.invoke(command1)
        Invoker.invoke(command2)

        // Then
        val user1 = UserRepository.findByUsernameOrNull(Username("user1"))
        val user2 = UserRepository.findByUsernameOrNull(Username("user2"))

        user1 shouldNotBe null
        user2 shouldNotBe null
        user1!!.id shouldNotBe user2!!.id
        user1.username shouldBe Username("user1")
        user2.username shouldBe Username("user2")
    }

    @Test
    fun `should hash different passwords differently`(): Unit = runBlocking {
        // Given
        val command1 = CreateUserCommand(
            username = Username("userA"),
            password = ClearPassword("passwordA"),
            person = Person("User", "A", EmailAddress("userA@example.com"))
        )
        val command2 = CreateUserCommand(
            username = Username("userB"),
            password = ClearPassword("passwordB"),
            person = Person("User", "B", EmailAddress("userB@example.com"))
        )

        // When
        Invoker.invoke(command1)
        Invoker.invoke(command2)

        // Then
        val userA = UserRepository.findByUsernameWithPasswordOrNull(Username("userA"))
        val userB = UserRepository.findByUsernameWithPasswordOrNull(Username("userB"))

        userA shouldNotBe null
        userB shouldNotBe null
        userA!!.password shouldNotBe userB!!.password

        // Verify both passwords are correctly hashed
        BCrypt.checkpw("passwordA", userA.password!!.value) shouldBe true
        BCrypt.checkpw("passwordB", userB.password!!.value) shouldBe true
        BCrypt.checkpw("passwordA", userB.password!!.value) shouldBe false
        BCrypt.checkpw("passwordB", userA.password!!.value) shouldBe false
    }
}
