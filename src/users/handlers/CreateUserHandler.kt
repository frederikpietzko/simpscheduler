package com.github.frederikpietzko.users.handlers

import com.github.frederikpietzko.framework.command.CommandHandler
import com.github.frederikpietzko.framework.command.Invoker
import com.github.frederikpietzko.users.commands.CreateUserCommand
import com.github.frederikpietzko.users.domain.HashedPassword
import com.github.frederikpietzko.users.domain.User
import com.github.frederikpietzko.users.domain.UserId
import com.github.frederikpietzko.users.persistence.UserRepository
import kotlin.reflect.KClass

object CreateUserHandler : CommandHandler<CreateUserCommand> {
    override val command: KClass<CreateUserCommand> = CreateUserCommand::class

    override suspend fun Invoker.handle(command: CreateUserCommand) {
        val user = command.toUser()
        UserRepository.insert(user)
    }

    private fun CreateUserCommand.toUser() = User(
        id = UserId(),
        username = username,
        password = HashedPassword(password.value),
        person = person
    )
}
