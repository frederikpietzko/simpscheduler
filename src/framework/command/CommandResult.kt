package com.github.frederikpietzko.framework.command

sealed interface CommandResult<T : Any?> {
    class Success<T : Any?>(
        val value: T
    ) : CommandResult<T>

    class Failure<T: Any?>(
        val message: String,
        val reason: Throwable? = null
    ) : CommandResult<T>


    companion object {
        fun <T : Any?> success(value: T): CommandResult<T> = Success(value)
        @Suppress("CAST_NEVER_SUCCEEDS")
        fun <T : Any?> failure(message: String, reason: Throwable? = null): CommandResult<T> = this as Failure<T>
    }
}

@Suppress("UNCHECKED_CAST")
fun <T: Any?, R: Any?> CommandResult<T>.map(transform: (T) -> R) = when(this) {
    is CommandResult.Success -> CommandResult.success( transform(value))
    is CommandResult.Failure -> this as CommandResult.Failure<R>
}

fun <T: Any?> CommandResult<T>.getOrThrow(): T = when(this) {
    is CommandResult.Success -> value
    is CommandResult.Failure -> throw IllegalStateException(message, reason)
}

fun <T: Any?> CommandResult<T>.getOrElse(defaultValue: T): T = when(this) {
    is CommandResult.Success -> value
    is CommandResult.Failure -> defaultValue
}

fun <T: Any?> CommandResult<T>.getOrElse(defaultValue: () -> T): T = when(this) {
    is CommandResult.Success -> value
    is CommandResult.Failure -> defaultValue()
}

fun <T: Any?> CommandResult<T>.getOrNull(): T? = when(this) {
    is CommandResult.Success -> value
    is CommandResult.Failure -> null
}