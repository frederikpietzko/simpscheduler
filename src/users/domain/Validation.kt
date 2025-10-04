package com.github.frederikpietzko.users.domain

data class ValidationError(val field: String, val message: String)

object Validation {
    fun validatePassword(password: String): ValidationError? {
        if (password.length < 8) {
            return ValidationError("password", "Password must be at least 8 characters long")
        }
        if (!password.any { it.isDigit() }) {
            return ValidationError("password", "Password must contain at least one number")
        }
        if (!password.any { it.isUpperCase() }) {
            return ValidationError("password", "Password must contain at least one uppercase letter")
        }
        if (!password.any { !it.isLetterOrDigit() }) {
            return ValidationError("password", "Password must contain at least one symbol")
        }
        return null
    }

    fun validateEmail(email: String): ValidationError? {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
        if (!email.matches(emailRegex)) {
            return ValidationError("email", "Invalid email address format")
        }
        return null
    }

    fun validateUsername(username: String): ValidationError? {
        if (username.isBlank()) {
            return ValidationError("username", "Username cannot be empty")
        }
        if (username.length < 3) {
            return ValidationError("username", "Username must be at least 3 characters long")
        }
        return null
    }

    fun validateName(name: String, fieldName: String): ValidationError? {
        if (name.isBlank()) {
            return ValidationError(fieldName, "${fieldName.replaceFirstChar { it.uppercase() }} cannot be empty")
        }
        return null
    }
}
