package com.github.frederikpietzko.framework.command

import kotlin.reflect.KClass

@FunctionalInterface
interface CommandHandler<TCommand : Command> {
    val command: KClass<TCommand>
    suspend fun Invoker.handle(command: TCommand)
}
