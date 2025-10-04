package com.github.frederikpietzko.ui.components

import kotlinx.html.*

/**
 * Button variants following Bootstrap conventions
 */
enum class ButtonVariant(val cssClass: String) {
    PRIMARY("btn-primary"),
    SECONDARY("btn-secondary"),
    SUCCESS("btn-success"),
    DANGER("btn-danger"),
    WARNING("btn-warning"),
    INFO("btn-info"),
    LIGHT("btn-light"),
    DARK("btn-dark"),
    LINK("btn-link"),
    OUTLINE_PRIMARY("btn-outline-primary"),
    OUTLINE_SECONDARY("btn-outline-secondary")
}

/**
 * Button sizes following Bootstrap conventions
 */
enum class ButtonSize(val cssClass: String) {
    SMALL("btn-sm"),
    NORMAL(""),
    LARGE("btn-lg")
}

/**
 * Renders a Bootstrap button with customizable variant and size.
 *
 * @param text The button text
 * @param type The button type (submit, button, reset)
 * @param variant The button variant/style
 * @param size The button size
 * @param block Whether the button should be full-width
 * @param disabled Whether the button is disabled
 * @param additionalClasses Additional CSS classes to add
 * @param onClick Optional onclick handler
 */
fun FlowContent.button(
    text: String,
    type: ButtonType = ButtonType.button,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    size: ButtonSize = ButtonSize.NORMAL,
    block: Boolean = false,
    disabled: Boolean = false,
    additionalClasses: String = "",
    onClick: String? = null
) {
    val classes = buildString {
        append("btn ${variant.cssClass}")
        if (size.cssClass.isNotEmpty()) {
            append(" ${size.cssClass}")
        }
        if (block) {
            append(" w-100")
        }
        if (additionalClasses.isNotEmpty()) {
            append(" $additionalClasses")
        }
    }
    
    button(type = type, classes = classes) {
        this.disabled = disabled
        onClick?.let { attributes["onclick"] = it }
        +text
    }
}

/**
 * Renders a submit button in a full-width grid container (common pattern for forms).
 *
 * @param text The button text
 * @param variant The button variant
 * @param size The button size
 */
fun FlowContent.submitButton(
    text: String,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    size: ButtonSize = ButtonSize.LARGE
) {
    div("d-grid gap-2 mt-4") {
        button(
            text = text,
            type = ButtonType.submit,
            variant = variant,
            size = size
        )
    }
}
