package com.github.frederikpietzko.ui.components

import com.github.frederikpietzko.users.domain.ValidationError
import kotlinx.html.*

/**
 * Renders a Bootstrap form control with label, input field, and validation feedback.
 *
 * @param id The HTML id for the input element
 * @param name The name attribute for the input element
 * @param label The label text to display
 * @param type The input type (text, email, password, etc.)
 * @param value The current value of the input
 * @param required Whether the field is required
 * @param placeholder Placeholder text for the input
 * @param errors List of validation errors to check against
 * @param helpText Optional help text displayed below the input
 * @param minLength Optional minimum length constraint
 * @param pattern Optional regex pattern for validation
 * @param title Optional title attribute for validation message
 */
fun FlowContent.formControl(
    id: String,
    name: String,
    label: String,
    type: InputType = InputType.text,
    value: String = "",
    required: Boolean = false,
    placeholder: String = "",
    errors: List<ValidationError> = emptyList(),
    helpText: String? = null,
    minLength: String? = null,
    pattern: String? = null,
    title: String? = null
) {
    div("mb-3") {
        label("form-label") {
            htmlFor = id
            +label
            if (required) {
                span("text-danger") { +"*" }
            }
        }
        
        val hasError = errors.any { it.field == name }
        val inputClasses = "form-control ${if (hasError) "is-invalid" else ""}"
        
        input(type = type, classes = inputClasses) {
            this.id = id
            this.name = name
            this.value = value
            this.required = required
            if (placeholder.isNotEmpty()) {
                this.placeholder = placeholder
            }
            minLength?.let { this.minLength = it }
            pattern?.let { this.pattern = it }
            title?.let { attributes["title"] = it }
        }
        
        errors.find { it.field == name }?.let { error ->
            div("invalid-feedback") {
                +error.message
            }
        }
        
        helpText?.let {
            div("form-text") {
                +it
            }
        }
    }
}
