package com.github.frederikpietzko.users

import com.github.frederikpietzko.framework.command.addHandlers
import com.github.frederikpietzko.ui.layout.Page
import com.github.frederikpietzko.users.handlers.CreateUserHandler
import io.ktor.server.application.*
import io.ktor.server.html.respondHtmlTemplate
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.html.h1

fun Application.configureUsers() {
    addHandlers(CreateUserHandler)
    routing {
        get("/") {
            call.respondHtmlTemplate(Page("SimpScheduler")) {
                body {
                    h1 { +"Hello World!" }
                }
            }
        }
    }
}