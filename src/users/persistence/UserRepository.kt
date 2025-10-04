package com.github.frederikpietzko.users.persistence

import com.github.frederikpietzko.users.domain.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.neq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update

/**
 * Repository object for user data management in the database.
 * Supports finding, inserting, updating, and deleting users.
 */
object UserRepository {
    /**
     * Finds a user by their unique identifier or returns null if no user is found.
     *
     * @param id The unique identifier of the user to retrieve.
     * @return The user associated with the given identifier, or null if no user is found.
     */
    suspend fun findByIdOrNull(id: UserId) = suspendTransaction {
        UserTable.selectAll().where { UserTable.id eq id.value }.map { it.toUser() }.singleOrNull()
    }

    /**
     * Retrieves a user by their unique identifier.
     * Throws an exception if no user is found for the specified identifier.
     *
     * @param id The unique identifier of the user to retrieve.
     * @throws IllegalArgumentException If no user is found for the given identifier.
     */
    suspend fun getById(id: UserId) = requireNotNull(findByIdOrNull(id)) { "User with id $id not found!" }

    /**
     * Inserts a new user into the database.
     * The user's password must not be null when creating a new user.
     *
     * @param user The user to insert, containing the user's unique identifier, username, password, and personal information.
     * @throws IllegalArgumentException If the password of the user is null.
     */
    suspend fun insert(user: User) = suspendTransaction {
        requireNotNull(user.password) { "Password Cannot be null when creating user!" }
        UserTable.insertAndGetId {
            it[id] = user.id.value
            it[password] = user.password.value
            it[username] = user.username.value
            it[firstName] = user.person.firstName
            it[lastName] = user.person.lastName
            it[emailAddress] = user.person.emailAddress.value
        }.value.let(::UserId)
    }


    /**
     * Updates the user information in the database based on the provided user object.
     * If the user's password is not null, it will also update the password.
     *
     * @param user The user object containing updated information, including the user's
     * unique identifier, username, password (if present), and personal details
     * such as first name and last name.
     * @return The unique identifier of the updated user.
     */
    suspend fun update(user: User) = suspendTransaction {
        UserTable.update({ UserTable.id eq user.id.value }) {
            if (user.password != null) {
                it[password] = user.password.value
            }
            it[username] = user.username.value
            it[firstName] = user.person.firstName
            it[lastName] = user.person.lastName
            it[emailAddress] = user.person.emailAddress.value
        }
        user.id
    }

    /**
     * Deletes a user from the database based on their unique identifier.
     *
     * @param id The unique identifier of the user to delete.
     */
    suspend fun delete(id: UserId) = suspendTransaction {
        UserTable.deleteWhere { UserTable.id eq id.value }
    }

    /**
     * Deletes all users from the database.
     * This method is primarily intended for testing purposes.
     */
    suspend fun deleteAll() = suspendTransaction {
        UserTable.deleteWhere { UserTable.id neq null }
    }

    /**
     * Finds a user by their username or returns null if no user is found.
     *
     * @param username The username of the user to retrieve.
     * @return The user associated with the given username, or null if no user is found.
     */
    suspend fun findByUsernameOrNull(username: Username) = suspendTransaction {
        UserTable.selectAll().where { UserTable.username eq username.value }.map { it.toUser() }.singleOrNull()
    }

    /**
     * Finds a user by their username or returns null if no user is found.
     * This version includes the password hash for testing purposes.
     *
     * @param username The username of the user to retrieve.
     * @return The user associated with the given username with password included, or null if no user is found.
     */
    suspend fun findByUsernameWithPasswordOrNull(username: Username) = suspendTransaction {
        UserTable.selectAll().where { UserTable.username eq username.value }.map { it.toUserWithPassword() }.singleOrNull()
    }

    private fun ResultRow.toUser() =
        User(
            id = UserId(this[UserTable.id].value),
            username = Username(this[UserTable.username]),
            password = null,
            person =
                Person(
                    firstName = this[UserTable.firstName],
                    lastName = this[UserTable.lastName],
                    emailAddress = EmailAddress(this[UserTable.emailAddress]),
                ),
            createdAt = this[UserTable.createdAt],
        )

    private fun ResultRow.toUserWithPassword() =
        User(
            id = UserId(this[UserTable.id].value),
            username = Username(this[UserTable.username]),
            password = HashedPassword(this[UserTable.password]),
            person =
                Person(
                    firstName = this[UserTable.firstName],
                    lastName = this[UserTable.lastName],
                    emailAddress = EmailAddress(this[UserTable.emailAddress]),
                ),
            createdAt = this[UserTable.createdAt],
        )
}
