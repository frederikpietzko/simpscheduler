package com.github.frederikpietzko.users.domain

import java.time.Instant
import java.util.*

@JvmInline
value class UserId(val value: UUID = UUID.randomUUID())

@JvmInline
value class Username(val value: String)

@JvmInline
value class HashedPassword(val value: String)

@JvmInline
value class ClearPassword(val value: String)

@JvmInline
value class EmailAddress(val value: String)

data class Person(
    val firstName: String,
    val lastName: String,
    val emailAddress: EmailAddress,
)

data class User(
    val id: UserId,
    val username: Username,
    val person: Person,
    val password: HashedPassword? = null,
    val createdAt: Instant? = null,
)

