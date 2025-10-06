package com.github.frederikpietzko.users.views

import com.github.frederikpietzko.ui.components.*
import com.github.frederikpietzko.ui.layout.Page
import com.github.frederikpietzko.users.domain.ValidationError
import io.ktor.server.application.*
import io.ktor.server.html.*
import kotlinx.html.*

suspend fun ApplicationCall.respondLoginUserView(
    errors: List<ValidationError> = emptyList(),
    successMessage: String? = null,
    username: String = ""
) {
    respondHtmlTemplate(Page("Login - SimpScheduler")) {
        body {
            div("row justify-content-center mt-5") {
                div("col-md-6 col-lg-4") {
                    card(additionalClasses = "shadow") {
                        body {
                            div("p-4") {
                                cardTitle(classes = "text-center mb-4") {
                                    +"Sign In"
                                }

                                // Success message
                                successMessage?.let {
                                    successAlert(it)
                                }

                                // Error messages
                                if (errors.isNotEmpty()) {
                                    errorAlert(errors.map { it.message })
                                }

                                loginForm(username, errors)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun FlowContent.loginForm(
    username: String,
    errors: List<ValidationError>
) {
    form(action = "/login", method = FormMethod.post) {
        // Username field
        formControl(
            id = "username",
            name = "username",
            label = "Username",
            value = username,
            required = true,
            placeholder = "Enter your username",
            errors = errors
        )

        // Password field
        formControl(
            id = "password",
            name = "password",
            label = "Password",
            type = InputType.password,
            required = true,
            placeholder = "Enter your password",
            errors = errors
        )

        // Submit button
        submitButton("Sign In")

        // Additional links
        div("d-flex justify-content-between align-items-center mt-3") {
            small {
                a(href = "/forgot-password", classes = "text-decoration-none") {
                    +"Forgot password?"
                }
            }
        }

        // Registration link
        p("text-center text-muted mt-3 mb-0") {
            small {
                +"Don't have an account? "
                a(href = "/register") { +"Sign up" }
            }
        }
    }
}
