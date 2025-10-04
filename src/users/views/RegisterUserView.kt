package com.github.frederikpietzko.users.views

import com.github.frederikpietzko.ui.components.*
import com.github.frederikpietzko.ui.layout.Page
import com.github.frederikpietzko.users.domain.ValidationError
import io.ktor.server.application.*
import io.ktor.server.html.*
import kotlinx.html.*

suspend fun ApplicationCall.respondRegisterUserView(
    errors: List<ValidationError> = emptyList(),
    successMessage: String? = null,
    email: String = "",
    username: String = "",
    firstName: String = "",
    lastName: String = ""
) {
    respondHtmlTemplate(Page("Register - SimpScheduler")) {
        body {
            div("row justify-content-center mt-5") {
                div("col-md-6 col-lg-5") {
                    card(additionalClasses = "shadow") {
                        body {
                            div("p-4") {
                                cardTitle(classes = "text-center mb-4") {
                                    +"Create Your Account"
                                }

                                // Success message
                                successMessage?.let {
                                    successAlert(it)
                                }

                                // Error messages
                                if (errors.isNotEmpty()) {
                                    errorAlert(errors.map { it.message })
                                }
                                createUserForm(email, errors, username, firstName, lastName)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun FlowContent.createUserForm(
    email: String,
    errors: List<ValidationError>,
    username: String,
    firstName: String,
    lastName: String
) {
    form(action = "/register", method = FormMethod.post) {
        // Email field
        formControl(
            id = "email",
            name = "email",
            label = "Email Address",
            type = InputType.email,
            value = email,
            required = true,
            placeholder = "you@example.com",
            pattern = "[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}",
            title = "Enter a valid email address",
            errors = errors
        )

        // Username field
        formControl(
            id = "username",
            name = "username",
            label = "Username",
            value = username,
            required = true,
            minLength = "3",
            placeholder = "johndoe",
            title = "Username must be at least 3 characters",
            errors = errors
        )

        // First Name field
        formControl(
            id = "firstName",
            name = "firstName",
            label = "First Name",
            value = firstName,
            required = true,
            placeholder = "John",
            errors = errors
        )

        // Last Name field
        formControl(
            id = "lastName",
            name = "lastName",
            label = "Last Name",
            value = lastName,
            required = true,
            placeholder = "Doe",
            errors = errors
        )

        // Password field
        formControl(
            id = "password",
            name = "password",
            label = "Password",
            type = InputType.password,
            required = true,
            minLength = "8",
            pattern = "(?=.*\\d)(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,}",
            placeholder = "••••••••",
            title = "Password must be at least 8 characters with 1 number, 1 uppercase letter, and 1 symbol",
            helpText = "Must be at least 8 characters with 1 number, 1 uppercase letter, and 1 symbol",
            errors = errors
        )

        // Confirm Password field
        formControl(
            id = "confirmPassword",
            name = "confirmPassword",
            label = "Confirm Password",
            type = InputType.password,
            required = true,
            placeholder = "••••••••",
            title = "Please confirm your password",
            errors = errors
        )

        // Submit button
        submitButton("Register")

        // Additional text
        p("text-center text-muted mt-3 mb-0") {
            small {
                +"Already have an account? "
                a(href = "/login") { +"Sign in" }
            }
        }
    }
}
