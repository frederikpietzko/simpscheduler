package com.github.frederikpietzko.ui.components

import kotlinx.html.*

/**
 * Alert types following Bootstrap conventions
 */
enum class AlertType(val cssClass: String) {
    SUCCESS("alert-success"),
    DANGER("alert-danger"),
    WARNING("alert-warning"),
    INFO("alert-info")
}

/**
 * Renders a Bootstrap alert with optional dismiss button.
 *
 * @param type The alert type (success, danger, warning, info)
 * @param dismissible Whether the alert can be dismissed
 * @param content The content builder for the alert body
 */
fun FlowContent.alert(
    type: AlertType,
    dismissible: Boolean = false,
    content: DIV.() -> Unit
) {
    val classes = buildString {
        append("alert ${type.cssClass}")
        if (dismissible) {
            append(" alert-dismissible fade show")
        }
    }
    
    div(classes = classes) {
        role = "alert"
        content()
        
        if (dismissible) {
            button(classes = "btn-close") {
                this.type = ButtonType.button
                attributes["data-bs-dismiss"] = "alert"
                attributes["aria-label"] = "Close"
            }
        }
    }
}

/**
 * Convenience function for success alerts
 */
fun FlowContent.successAlert(
    message: String,
    dismissible: Boolean = true
) {
    alert(AlertType.SUCCESS, dismissible) {
        +message
    }
}

/**
 * Convenience function for danger/error alerts with multiple messages
 */
fun FlowContent.errorAlert(
    messages: List<String>,
    heading: String = "Please correct the following errors:",
    dismissible: Boolean = false
) {
    alert(AlertType.DANGER, dismissible) {
        if (heading.isNotEmpty()) {
            h5("alert-heading") { +heading }
        }
        ul("mb-0") {
            messages.forEach { message ->
                li { +message }
            }
        }
    }
}
