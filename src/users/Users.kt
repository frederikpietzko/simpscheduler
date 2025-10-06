package com.github.frederikpietzko.users

import com.github.frederikpietzko.framework.command.CommandResult
import com.github.frederikpietzko.framework.command.addHandlers
import com.github.frederikpietzko.framework.command.invoke
import com.github.frederikpietzko.ui.layout.Page
import com.github.frederikpietzko.users.commands.CreateUserCommand
import com.github.frederikpietzko.users.commands.LoginUserCommand
import com.github.frederikpietzko.users.domain.*
import com.github.frederikpietzko.users.domain.Validation.validateEmail
import com.github.frederikpietzko.users.domain.Validation.validateName
import com.github.frederikpietzko.users.domain.Validation.validatePassword
import com.github.frederikpietzko.users.domain.Validation.validateUsername
import com.github.frederikpietzko.users.handlers.CreateUserHandler
import com.github.frederikpietzko.users.handlers.LoginUserHandler
import com.github.frederikpietzko.users.views.respondRegisterUserView
import com.github.frederikpietzko.users.views.respondLoginUserView
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.html.h1

fun Application.configureUsers() {
    addHandlers(CreateUserHandler, LoginUserHandler)
    routing {
        get("/") {
            call.respondHtmlTemplate(Page("SimpScheduler")) {
                body {
                    h1 { +"Hello World!" }
                }
            }
        }

        get("/login") {
            call.respondLoginUserView()
        }

        post("/login") {
            val parameters = call.receiveParameters()
            val username = parameters["username"]?.trim() ?: ""
            val password = parameters["password"] ?: ""

            // Validate basic input
            val errors = ValidationErrors()
            if (username.isBlank()) {
                errors.add(ValidationError("username", "Username is required"))
            }
            if (password.isBlank()) {
                errors.add(ValidationError("password", "Password is required"))
            }

            if (errors.isNotEmpty()) {
                call.respondLoginUserView(
                    errors = errors,
                    username = username
                )
            } else {
                // Login user via command
                val command = LoginUserCommand(
                    username = Username(username),
                    password = ClearPassword(password)
                )
                when (val result = invoke(command)) {
                    is CommandResult.Failure<*> -> call.respondLoginUserView(
                        errors = listOf(
                            ValidationError("general", result.message)
                        ),
                        username = username
                    )

                    is CommandResult.Success<*> -> {
                        // TODO: Set up session/authentication here
                        call.respondLoginUserView(
                            successMessage = "Login successful! Welcome back."
                        )
                    }
                }
            }
        }

        get("/register") {
            call.respondRegisterUserView()
        }

        post("/register") {
            val parameters = call.receiveParameters()
            val email = parameters["email"]?.trim() ?: ""
            val username = parameters["username"]?.trim() ?: ""
            val firstName = parameters["firstName"]?.trim() ?: ""
            val lastName = parameters["lastName"]?.trim() ?: ""
            val password = parameters["password"] ?: ""
            val confirmPassword = parameters["confirmPassword"] ?: ""

            // Validate all fields
            val errors = ValidationErrors()
            with(Validation) {
                errors.validateEmail(email)
                errors.validateUsername(username)
                errors.validateName(firstName, "firstName")
                errors.validateName(lastName, "lastName")
                errors.validatePassword(password)
            }

            if (password != confirmPassword) {
                errors.add(ValidationError("confirmPassword", "Passwords do not match"))
            }

            if (errors.isNotEmpty()) {
                call.respondRegisterUserView(
                    errors = errors,
                    email = email,
                    username = username,
                    firstName = firstName,
                    lastName = lastName
                )
            } else {
                // Create user via command
                val command = CreateUserCommand(
                    username = Username(username),
                    password = ClearPassword(password),
                    person = Person(
                        firstName = firstName,
                        lastName = lastName,
                        emailAddress = EmailAddress(email)
                    )
                )
                when (val result = invoke(command)) {
                    is CommandResult.Failure<*> -> call.respondRegisterUserView(
                        errors = listOf(
                            ValidationError(
                                "general",
                                "An error occurred during registration: ${result.message}"
                            )
                        ),
                        email = email,
                        username = username,
                        firstName = firstName,
                        lastName = lastName
                    )

                    is CommandResult.Success<*> -> call.respondRegisterUserView(
                        successMessage = "Registration successful! Your account has been created."
                    )
                }
            }
        }
    }
}