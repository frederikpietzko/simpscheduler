package com.github.frederikpietzko.ui.components

import io.ktor.server.html.*
import kotlinx.html.*

/**
 * Bootstrap Card Template that supports the full card structure:
 * - Optional image at the top
 * - Card body with flexible content
 *
 * Usage:
 * ```
 * card(width = "18rem") {
 *     image {
 *         src = "..."
 *         alt = "..."
 *     }
 *     body {
 *         cardTitle { +"Card title" }
 *         cardText { +"Some quick example text..." }
 *         a(href = "#", classes = "btn btn-primary") { +"Go somewhere" }
 *     }
 * }
 * ```
 */
class CardTemplate(
    val width: String? = null,
    val additionalClasses: String = ""
) : Template<FlowContent> {
    val image = Placeholder<IMG>()
    val body = Placeholder<FlowContent>()

    override fun FlowContent.apply() {
        val cardClasses = buildString {
            append("card")
            if (additionalClasses.isNotEmpty()) {
                append(" $additionalClasses")
            }
        }

        div(classes = cardClasses) {
            width?.let {
                style = "width: $it;"
            }

            // Optional card image at top
            img(classes = "card-img-top") {
                insert(image)
            }

            // Card body
            div(classes = "card-body") {
                insert(body)
            }
        }
    }
}

/**
 * Renders a Bootstrap card using the CardTemplate.
 *
 * @param width Optional width (e.g., "18rem", "100%")
 * @param additionalClasses Additional CSS classes (e.g., "shadow", "mb-3")
 * @param block Template builder block
 */
fun FlowContent.card(
    width: String? = null,
    additionalClasses: String = "",
    block: CardTemplate.() -> Unit
) {
    insert(CardTemplate(width, additionalClasses), block)
}

/**
 * Renders a card title (h5 with card-title class).
 *
 * @param classes Additional CSS classes
 * @param block Content builder
 */
fun FlowContent.cardTitle(
    classes: String = "",
    block: H5.() -> Unit
) {
    val titleClasses = buildString {
        append("card-title")
        if (classes.isNotEmpty()) {
            append(" $classes")
        }
    }
    h5(classes = titleClasses, block = block)
}

/**
 * Renders card text (p with card-text class).
 *
 * @param classes Additional CSS classes
 * @param block Content builder
 */
fun FlowContent.cardText(
    classes: String = "",
    block: P.() -> Unit
) {
    val textClasses = buildString {
        append("card-text")
        if (classes.isNotEmpty()) {
            append(" $classes")
        }
    }
    p(classes = textClasses, block = block)
}

