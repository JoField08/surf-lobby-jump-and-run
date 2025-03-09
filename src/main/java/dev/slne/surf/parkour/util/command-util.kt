package dev.slne.surf.parkour.util

import dev.jorel.commandapi.arguments.CustomArgument

inline fun failWithBuilder(block: CustomArgument.MessageBuilder.() -> Unit): Nothing {
    throw CustomArgument.CustomArgumentException.fromMessageBuilder(
        CustomArgument.MessageBuilder().apply(block)
    )
}