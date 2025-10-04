package com.github.frederikpietzko.users

import com.github.frederikpietzko.framework.command.addHandlers
import com.github.frederikpietzko.framework.command.invoke
import com.github.frederikpietzko.ui.layout.Page
import com.github.frederikpietzko.users.commands.CreateUserCommand
import com.github.frederikpietzko.users.domain.*
import com.github.frederikpietzko.users.handlers.CreateUserHandler
import com.github.frederikpietzko.users.views.respondRegisterUserView
import io.ktor.server.application.*
import io.ktor.server.html.respondHtmlTemplate
import io.ktor.server.request.*
import io.ktor.server.routing.get
import io.ktor.server.routing.post
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
            val errors = mutableListOf<ValidationError>()
            
            Validation.validateEmail(email)?.let { errors.add(it) }
            Validation.validateUsername(username)?.let { errors.add(it) }
            Validation.validateName(firstName, "firstName")?.let { errors.add(it) }
            Validation.validateName(lastName, "lastName")?.let { errors.add(it) }
            Validation.validatePassword(password)?.let { errors.add(it) }
            
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
                try {
                    val command = CreateUserCommand(
                        username = Username(username),
                        password = ClearPassword(password),
                        person = Person(
                            firstName = firstName,
                            lastName = lastName,
                            emailAddress = EmailAddress(email)
                        )
                    )
                    invoke(command)
                    
                    call.respondRegisterUserView(
                        successMessage = "Registration successful! Your account has been created."
                    )
                } catch (e: Exception) {
                    call.respondRegisterUserView(
                        errors = listOf(ValidationError("general", "An error occurred during registration: ${e.message}")),
                        email = email,
                        username = username,
                        firstName = firstName,
                        lastName = lastName
                    )
                }
            }
        }
    }
}