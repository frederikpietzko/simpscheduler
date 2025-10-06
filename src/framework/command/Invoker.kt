package com.github.frederikpietzko.framework.command

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.*

class Invoker private constructor(
    private val commandHandlers: MutableSet<CommandHandler<Command, *>> = mutableSetOf(),
) {
    companion object {
        private val log = KotlinLogging.logger {}
        private lateinit var _instance: Invoker

        fun initialize() {
            _instance = Invoker()
        }

        fun addHandlers(vararg handler: CommandHandler<Command, *>) {
            _instance.commandHandlers.addAll(handler)
        }

        val instance: Invoker get() = _instance

        suspend fun invoke(command: Command) = instance.invoke(command)
    }

    suspend fun invoke(command: Command): CommandResult<*> =
        requireNotNull(value = commandHandlers.firstOrNull { it.command == command::class }) { "No handler found for command ${command::class.java.simpleName}" }
            .also { log.info { "Handling Command $command with handler ${it::class.simpleName}." } }
            .run { handle(command) }
}

fun Application.configureInvoker() {
    Invoker.initialize()
}

@Suppress("UNCHECKED_CAST", "CAST_NEVER_SUCCEEDS")
fun <TCommandHandler : CommandHandler<*, *>> addHandlers(vararg handler: TCommandHandler) =
    Invoker.addHandlers(*handler as Array<out CommandHandler<Command, *>>)

suspend fun invoke(command: Command) = Invoker.invoke(command)