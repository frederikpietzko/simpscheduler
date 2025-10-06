package com.github.frederikpietzko.users.handlers

import com.github.frederikpietzko.framework.command.CommandHandler
import com.github.frederikpietzko.framework.command.CommandResult
import com.github.frederikpietzko.framework.command.Invoker
import com.github.frederikpietzko.users.commands.CreateUserCommand
import com.github.frederikpietzko.users.domain.HashedPassword
import com.github.frederikpietzko.users.domain.User
import com.github.frederikpietzko.users.domain.UserId
import com.github.frederikpietzko.users.persistence.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.mindrot.jbcrypt.BCrypt
import kotlin.reflect.KClass

object CreateUserHandler : CommandHandler<CreateUserCommand, UserId> {
    private val logger = KotlinLogging.logger { }
    override val command: KClass<CreateUserCommand> = CreateUserCommand::class

    override suspend fun Invoker.handle(command: CreateUserCommand): CommandResult<UserId> {
        val user = command.toUser()
        if(UserRepository.findByUsernameOrNull(user.username) != null) {
            logger.info { "Username already taken: ${user.username.value}" }
            return CommandResult.failure("Username already taken")
        }
        logger.info { "Creating User(id=${user.id}, username=${user.username})" }
        return CommandResult.success(UserRepository.insert(user))
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
