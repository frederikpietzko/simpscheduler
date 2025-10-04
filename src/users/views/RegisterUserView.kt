package com.github.frederikpietzko.users.views

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
                    div("card shadow") {
                        div("card-body p-4") {
                            h2("card-title text-center mb-4") {
                                +"Create Your Account"
                            }
                            
                            // Success message
                            if (successMessage != null) {
                                div("alert alert-success alert-dismissible fade show") {
                                    role = "alert"
                                    +successMessage
                                    button(classes = "btn-close") {
                                        type = ButtonType.button
                                        attributes["data-bs-dismiss"] = "alert"
                                        attributes["aria-label"] = "Close"
                                    }
                                }
                            }
                            
                            // Error messages
                            if (errors.isNotEmpty()) {
                                div("alert alert-danger") {
                                    role = "alert"
                                    h5("alert-heading") { +"Please correct the following errors:" }
                                    ul("mb-0") {
                                        errors.forEach { error ->
                                            li { +error.message }
                                        }
                                    }
                                }
                            }
                            
                            form(action = "/register", method = FormMethod.post) {
                                // Email field
                                div("mb-3") {
                                    label("form-label") {
                                        htmlFor = "email"
                                        +"Email Address"
                                        span("text-danger") { +"*" }
                                    }
                                    input(type = InputType.email, classes = "form-control ${if (errors.any { it.field == "email" }) "is-invalid" else ""}") {
                                        id = "email"
                                        name = "email"
                                        value = email
                                        required = true
                                        placeholder = "you@example.com"
                                        pattern = "[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"
                                        attributes["title"] = "Enter a valid email address"
                                    }
                                    errors.find { it.field == "email" }?.let { error ->
                                        div("invalid-feedback") {
                                            +error.message
                                        }
                                    }
                                }
                                
                                // Username field
                                div("mb-3") {
                                    label("form-label") {
                                        htmlFor = "username"
                                        +"Username"
                                        span("text-danger") { +"*" }
                                    }
                                    input(type = InputType.text, classes = "form-control ${if (errors.any { it.field == "username" }) "is-invalid" else ""}") {
                                        id = "username"
                                        name = "username"
                                        value = username
                                        required = true
                                        minLength = "3"
                                        placeholder = "johndoe"
                                        attributes["title"] = "Username must be at least 3 characters"
                                    }
                                    errors.find { it.field == "username" }?.let { error ->
                                        div("invalid-feedback") {
                                            +error.message
                                        }
                                    }
                                }
                                
                                // First Name field
                                div("mb-3") {
                                    label("form-label") {
                                        htmlFor = "firstName"
                                        +"First Name"
                                        span("text-danger") { +"*" }
                                    }
                                    input(type = InputType.text, classes = "form-control ${if (errors.any { it.field == "firstName" }) "is-invalid" else ""}") {
                                        id = "firstName"
                                        name = "firstName"
                                        value = firstName
                                        required = true
                                        placeholder = "John"
                                    }
                                    errors.find { it.field == "firstName" }?.let { error ->
                                        div("invalid-feedback") {
                                            +error.message
                                        }
                                    }
                                }
                                
                                // Last Name field
                                div("mb-3") {
                                    label("form-label") {
                                        htmlFor = "lastName"
                                        +"Last Name"
                                        span("text-danger") { +"*" }
                                    }
                                    input(type = InputType.text, classes = "form-control ${if (errors.any { it.field == "lastName" }) "is-invalid" else ""}") {
                                        id = "lastName"
                                        name = "lastName"
                                        value = lastName
                                        required = true
                                        placeholder = "Doe"
                                    }
                                    errors.find { it.field == "lastName" }?.let { error ->
                                        div("invalid-feedback") {
                                            +error.message
                                        }
                                    }
                                }
                                
                                // Password field
                                div("mb-3") {
                                    label("form-label") {
                                        htmlFor = "password"
                                        +"Password"
                                        span("text-danger") { +"*" }
                                    }
                                    input(type = InputType.password, classes = "form-control ${if (errors.any { it.field == "password" }) "is-invalid" else ""}") {
                                        id = "password"
                                        name = "password"
                                        required = true
                                        minLength = "8"
                                        // Pattern: At least 8 chars, 1 number, 1 uppercase, 1 symbol
                                        pattern = "(?=.*\\d)(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,}"
                                        placeholder = "••••••••"
                                        attributes["title"] = "Password must be at least 8 characters with 1 number, 1 uppercase letter, and 1 symbol"
                                    }
                                    errors.find { it.field == "password" }?.let { error ->
                                        div("invalid-feedback") {
                                            +error.message
                                        }
                                    }
                                    div("form-text") {
                                        +"Must be at least 8 characters with 1 number, 1 uppercase letter, and 1 symbol"
                                    }
                                }
                                
                                // Confirm Password field
                                div("mb-3") {
                                    label("form-label") {
                                        htmlFor = "confirmPassword"
                                        +"Confirm Password"
                                        span("text-danger") { +"*" }
                                    }
                                    input(type = InputType.password, classes = "form-control ${if (errors.any { it.field == "confirmPassword" }) "is-invalid" else ""}") {
                                        id = "confirmPassword"
                                        name = "confirmPassword"
                                        required = true
                                        placeholder = "••••••••"
                                        attributes["title"] = "Please confirm your password"
                                    }
                                    errors.find { it.field == "confirmPassword" }?.let { error ->
                                        div("invalid-feedback") {
                                            +error.message
                                        }
                                    }
                                }
                                
                                // Submit button
                                div("d-grid gap-2 mt-4") {
                                    button(type = ButtonType.submit, classes = "btn btn-primary btn-lg") {
                                        +"Register"
                                    }
                                }
                                
                                // Additional text
                                p("text-center text-muted mt-3 mb-0") {
                                    small {
                                        +"Already have an account? "
                                        a(href = "/login") { +"Sign in" }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
