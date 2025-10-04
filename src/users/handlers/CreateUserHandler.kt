package com.github.frederikpietzko.users.handlers

import com.github.frederikpietzko.framework.command.CommandHandler
import com.github.frederikpietzko.framework.command.Invoker
import com.github.frederikpietzko.users.commands.CreateUserCommand
import com.github.frederikpietzko.users.domain.HashedPassword
import com.github.frederikpietzko.users.domain.User
import com.github.frederikpietzko.users.domain.UserId
import com.github.frederikpietzko.users.persistence.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.mindrot.jbcrypt.BCrypt
import kotlin.reflect.KClass

object CreateUserHandler : CommandHandler<CreateUserCommand> {
    private val logger = KotlinLogging.logger { }
    override val command: KClass<CreateUserCommand> = CreateUserCommand::class

    override suspend fun Invoker.handle(command: CreateUserCommand) {
        val user = command.toUser()
        logger.info { "Hashing password for user: ${command.username.value}" }
        UserRepository.insert(user)
    }

    private fun CreateUserCommand.toUser(): User {
        val hashed = BCrypt.hashpw(password.value, BCrypt.gensalt())
        logger.debug { "Password hashed for user: ${username.value}" }
        return User(
            id = UserId(),
            username = username,
            password = HashedPassword(hashed),
            person = person
        )
    }
}
