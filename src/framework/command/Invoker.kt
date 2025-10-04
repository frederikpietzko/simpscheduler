package com.github.frederikpietzko.framework.command

import io.ktor.server.application.*

class Invoker private constructor(
    private val commandHandlers: MutableSet<CommandHandler<Command>> = mutableSetOf(),
) {
    companion object {
        private lateinit var _instance: Invoker

        fun initialize() {
            _instance = Invoker()
        }

        fun addHandlers(vararg handler: CommandHandler<Command>) {
            _instance.commandHandlers.addAll(handler)
        }

        val instance: Invoker get() = _instance

        suspend fun invoke(command: Command) {
            instance.invoke(command)
        }
    }

    suspend fun invoke(command: Command) = commandHandlers
        .firstOrNull { it.command == command::class }
        ?.apply { handle(command) }
        ?: throw IllegalArgumentException("No handler found for command ${command::class.java}")
}

fun Application.configureInvoker() {
    Invoker.initialize()
}

@Suppress("UNCHECKED_CAST")
fun <TCommandHandler : CommandHandler<*>> Application.addHandlers(vararg handler: TCommandHandler) =
    Invoker.addHandlers(*handler as Array<out CommandHandler<Command>>)

suspend fun invoke(command: Command) = Invoker.invoke(command)