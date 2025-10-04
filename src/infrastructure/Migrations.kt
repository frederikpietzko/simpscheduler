package com.github.frederikpietzko.infrastructure

import io.ktor.server.application.*
import io.ktor.server.config.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.javatime.timestamp
import org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update
import java.time.Instant

suspend fun Application.applyMigrations() = suspendTransaction {
    val migrationsTableExists = SchemaUtils.listTables().any { it == MigrationTable.tableName }
    if (!migrationsTableExists) {
        SchemaUtils.create(MigrationTable)
    }
    val migrations = loadMigrations()
    val executedMigrations = MigrationTable
        .selectAll()
        .orderBy(MigrationTable.name, SortOrder.ASC)
        .map {
            Migration(
                name = it[MigrationTable.name],
                sql = "",
                hashCode = it[MigrationTable.hash],
                successful = it[MigrationTable.successful],
                appliedAt = it[MigrationTable.appliedAt],
            )
        }
        .toList()

    check(executedMigrations.size <= migrations.size) { "Executed more migrations than available" }
    migrations.zip(executedMigrations).forEach { (migration, executedMigration) ->
        check(migration.name == executedMigration.name) { "Migration name mismatch: ${migration.name} != ${executedMigration.name}" }
        check(migration.hashCode == executedMigration.hashCode) { "Migration hash mismatch: ${migration.hashCode} != ${executedMigration.hashCode}" }
        if (executedMigration.successful == false) {
            log.info("Retrying migration ${migration.name}")
            try {
                exec(migration.sql)
                MigrationTable.update({ MigrationTable.name eq migration.name }) {
                    it[successful] = true
                    it[appliedAt] = Instant.now()
                }
            } catch (e: Exception) {
                log.error("Failed to apply migration ${migration.name}", e)
            }
        }
    }

    migrations.slice(executedMigrations.size .. migrations.lastIndex).forEach { migration ->
        var success = false
        try {
            exec(migration.sql)
            success = true
        } catch (e: Exception) {
            log.error("Failed to apply migration ${migration.name}", e)
            success = false
        } finally {
            MigrationTable.insert {
                it[name] = migration.name
                it[successful] = success
                it[appliedAt] = Instant.now()
                it[hash] = migration.hashCode
            }
        }
    }
}

private object MigrationTable : Table("migrations") {
    val name = varchar("name", 255)
    val hash = integer("hash")
    val appliedAt = timestamp("applied_at").clientDefault { Instant.now() }
    val successful = bool("successful")

    override val primaryKey = PrimaryKey(name)
}

private data class Migration(
    val name: String,
    val sql: String,
    val hashCode: Int,
    val successful: Boolean? = null,
    val appliedAt: Instant? = null,
)

private val classLoader by lazy { object {}.javaClass.classLoader }

private fun Application.loadMigrations() =
    environment.config.tryGetStringList("migrations")
        ?.asSequence()
        ?.map { name ->
            val sql = classLoader.getResource("migrations/$name").readText(charset = Charsets.UTF_8)
            Migration(
                name = name,
                sql = sql,
                hashCode = sql.hashCode(),
            )
        }
        ?.sortedBy { it.name }
        ?.toList()
        ?: emptyList()
