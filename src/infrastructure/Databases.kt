package com.github.frederikpietzko.infrastructure

import io.ktor.server.application.*
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase

suspend fun Application.configureDatabases() {
    val postgresConfig = environment.config.config("postgres")
    DbSettings.initialize(PostgresConfig(
        url = postgresConfig.property("url").getString(),
        user = postgresConfig.property("user").getString(),
        password = postgresConfig.property("password").getString(),
    ))
    applyMigrations()
}

data class PostgresConfig(
    val url: String,
    val user: String,
    val password: String,
)

object DbSettings {
    private lateinit var _db: R2dbcDatabase
    val db: R2dbcDatabase get() = _db

    fun initialize(config: PostgresConfig) {
        _db = R2dbcDatabase.connect(
            url = config.url,
            user =config.user,
            password = config.password,
        )
    }
}