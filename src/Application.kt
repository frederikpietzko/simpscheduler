package com.github.frederikpietzko

import com.github.frederikpietzko.framework.command.configureInvoker
import com.github.frederikpietzko.infrastructure.*
import com.github.frederikpietzko.users.configureUsers
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain.main(args)
}

suspend fun Application.module() {
    configureHTTP()
    configureSecurity()
    configureSerialization()
    configureMonitoring()
    configureDatabases()
    configureRouting()
    configureInvoker()
    configureUsers()
}
