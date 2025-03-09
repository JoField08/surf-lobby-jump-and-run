package dev.slne.surf.parkour.util

import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickRunsCommand
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import kotlin.math.ceil
import kotlin.math.min

@DslMarker
annotation class PageableMessageBuilderDsl

private const val LINES_PER_PAGE = 10

@PageableMessageBuilderDsl
class PageableMessageBuilder private constructor() {

    private val lines = mutableObjectListOf<Component>()
    var pageCommand = "An error occurred while trying to display the page."
    var title: Component = Component.empty()

    companion object {
        operator fun invoke(block: PageableMessageBuilder.() -> Unit): PageableMessageBuilder {
            return PageableMessageBuilder().apply(block)
        }
    }

    fun line(block: SurfComponentBuilder.() -> Unit) {
        lines.add(SurfComponentBuilder(block))
    }

    fun title(block: SurfComponentBuilder.() -> Unit) {
        title = SurfComponentBuilder(block)
    }

    fun send(sender: Audience, page: Int) {
        val totalPages = ceil(lines.size.toDouble() / LINES_PER_PAGE).toInt()
        val start = (page - 1) * LINES_PER_PAGE
        val end = min(start + LINES_PER_PAGE, lines.size)

        if (page < 1 || page > totalPages) {
            sender.sendText {
                error("Seite ")
                variableValue(page.toString())
                error(" existiert nicht.")
            }
            return
        }

        sender.sendText {
            appendNewline()
            append {
                decorate(TextDecoration.ITALIC)
                darkSpacer("Seite ")
                variableValue(page.toString())
                darkSpacer(" von ")
                variableValue(totalPages.toString())
            }
            appendNewline()

            for (i in start..<end) {
                append {
                    append(lines[i])
                    appendNewline()
                    decoration(TextDecoration.BOLD, false)
                }
            }

            getComponent(page, totalPages)?.let { append(it) }
        }
    }

    private fun getComponent(page: Int, totalPages: Int): Component? {
        if (page < 1 || page > totalPages) return null

        return buildText {
            if (page > 1) {
                append {
                    success("[<< Zurück] ")
                    clickRunsCommand(pageCommand.replace("%page%", (page - 1).toString()))
                }
            }

            if (page < totalPages) {
                append {
                    success("[Weiter >>]")
                    clickRunsCommand(pageCommand.replace("%page%", (page + 1).toString()))
                }
            }
        }
    }
}
