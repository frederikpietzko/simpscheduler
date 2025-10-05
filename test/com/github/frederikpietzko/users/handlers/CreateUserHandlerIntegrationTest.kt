package com.github.frederikpietzko.users.handlers

import com.github.frederikpietzko.framework.command.Invoker
import com.github.frederikpietzko.framework.command.addHandlers
import com.github.frederikpietzko.testutil.DatabaseIntegrationTestBase
import com.github.frederikpietzko.users.commands.CreateUserCommand
import com.github.frederikpietzko.users.domain.ClearPassword
import com.github.frederikpietzko.users.domain.EmailAddress
import com.github.frederikpietzko.users.domain.Person
import com.github.frederikpietzko.users.domain.Username
import com.github.frederikpietzko.users.persistence.UserRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mindrot.jbcrypt.BCrypt

class CreateUserHandlerIntegrationTest : DatabaseIntegrationTestBase() {

    @BeforeEach
    fun cleanDatabase(): Unit = runBlocking {
        suspendTransaction {
            UserRepository.deleteAll()
        }
        Invoker.initialize()
        addHandlers(CreateUserHandler)
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
        val users = UserRepository.findByUsernameWithPasswordOrNull(username)
        users shouldNotBe null
        users!!.username shouldBe username
        users.person.firstName shouldBe "John"
        users.person.lastName shouldBe "Doe"
        users.person.emailAddress shouldBe EmailAddress("john.doe@example.com")
        users.createdAt shouldNotBe null
        users.password shouldNotBe null
        BCrypt.checkpw(password.value, users.password!!.value) shouldBe true
    }

    @Test
    fun `should create users with unique IDs when processing multiple commands`(): Unit = runBlocking {
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
        Invoker.invoke(command1)
        Invoker.invoke(command2)
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
        Invoker.invoke(command1)
        Invoker.invoke(command2)
        val userA = UserRepository.findByUsernameWithPasswordOrNull(Username("userA"))
        val userB = UserRepository.findByUsernameWithPasswordOrNull(Username("userB"))
        userA shouldNotBe null
        userB shouldNotBe null
        userA!!.password shouldNotBe userB!!.password
        BCrypt.checkpw("passwordA", userA.password!!.value) shouldBe true
        BCrypt.checkpw("passwordB", userB.password!!.value) shouldBe true
        BCrypt.checkpw("passwordA", userB.password.value) shouldBe false
        BCrypt.checkpw("passwordB", userA.password.value) shouldBe false
    }
}
