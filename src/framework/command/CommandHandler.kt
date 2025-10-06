package com.github.frederikpietzko.framework.command

import kotlin.reflect.KClass

@FunctionalInterface
interface CommandHandler<TCommand : Command, TResult : Any?> {
    val command: KClass<TCommand>
    suspend fun Invoker.handle(command: TCommand): CommandResult<TResult>
}
