package com.github.frederikpietzko.users.domain

data class ValidationError(val field: String, val message: String)

class ValidationErrors : ArrayList<ValidationError>()

object Validation {
    fun ValidationErrors.validatePassword(password: String) {
        if (password.length < 8) {
            add(ValidationError("password", "Password must be at least 8 characters long"))
        }
        if (!password.any { it.isDigit() }) {
            add(ValidationError("password", "Password must contain at least one number"))
        }
        if (!password.any { it.isUpperCase() }) {
            add(ValidationError("password", "Password must contain at least one uppercase letter"))
        }
        if (!password.any { !it.isLetterOrDigit() }) {
            add(ValidationError("password", "Password must contain at least one symbol"))
        }
    }

    fun ValidationErrors.validateEmail(email: String) {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
        if (!email.matches(emailRegex)) {
            add(ValidationError("email", "Invalid email address format"))
        }
    }

    fun ValidationErrors.validateUsername(username: String) {
        if (username.isBlank()) {
            add(ValidationError("username", "Username cannot be empty"))
        }
        if (username.length < 3) {
            add(ValidationError("username", "Username must be at least 3 characters long"))
        }
    }

    fun ValidationErrors.validateName(name: String, fieldName: String) {
        if (name.isBlank()) {
            add(ValidationError(fieldName, "${fieldName.replaceFirstChar { it.uppercase() }} cannot be empty"))
        }
    }
}
