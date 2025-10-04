package com.github.frederikpietzko.infrastructure

import io.ktor.server.application.*
import io.ktor.server.plugins.csrf.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable

fun Application.configureSecurity() {
    install(CSRF) {
        allowOrigin("http://localhost:8080")
        originMatchesHost()
    }
    install(Sessions) {
        cookie<MySession>("SIMP_SCHED_SESSION") {
            cookie.extensions["SameSite"] = "strict"
        }
    }
}

@Serializable
class MySession(val count: Int = 0)