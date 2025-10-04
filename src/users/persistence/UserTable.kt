package com.github.frederikpietzko.users.persistence

import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.javatime.timestamp
import java.time.Instant

object UserTable : UUIDTable("users") {
    val username = text("username").uniqueIndex()
    val password = text("password")
    val firstName = text("first_name")
    val lastName = text("last_name")
    val emailAddress = text("email_address")
    val createdAt = timestamp("created_at").clientDefault { Instant.now() }
}
