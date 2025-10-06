package com.github.frederikpietzko.users.handlers

import com.github.frederikpietzko.framework.command.CommandHandler
import com.github.frederikpietzko.framework.command.Invoker
import com.github.frederikpietzko.framework.command.CommandResult
import com.github.frederikpietzko.users.commands.LoginUserCommand
import com.github.frederikpietzko.users.persistence.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.mindrot.jbcrypt.BCrypt
import kotlin.reflect.KClass

object LoginUserHandler : CommandHandler<LoginUserCommand, Unit> {
    private val logger = KotlinLogging.logger { }
    override val command: KClass<LoginUserCommand> = LoginUserCommand::class

    override suspend fun Invoker.handle(command: LoginUserCommand): CommandResult<Unit> {
        val user = UserRepository.findByUsernameWithPasswordOrNull(command.username)
        if (user == null) {
            logger.info { "Login failed: user not found for username ${command.username.value}" }
            return CommandResult.failure("Invalid username or password")
        }

        val hashedPassword = user.password?.value
        if (hashedPassword == null || !BCrypt.checkpw(command.password.value, hashedPassword)) {
            logger.info { "Login failed: password mismatch for username ${command.username.value}" }
            return CommandResult.failure("Invalid username or password")
        }

        logger.info { "Login succeeded for username ${command.username.value}" }
        return CommandResult.success(Unit)
    }
}
