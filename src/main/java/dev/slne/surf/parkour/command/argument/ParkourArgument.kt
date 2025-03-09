package dev.slne.surf.parkour.command.argument

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.util.failWithBuilder

class ParkourArgument(nodeName: String) : CustomArgument<Parkour, String>(
    StringArgument(nodeName),
    CustomArgumentInfoParser { info ->
        Parkour.getByName(info.currentInput()) ?: failWithBuilder {
            append("The parkour ")
            appendArgInput()
            append(" does not exist.")
        }
    }) {
    init {
        replaceSuggestions(ArgumentSuggestions.stringCollection {
            DatabaseProvider.getParkours().map { it.name }
        })
    }
}

inline fun CommandAPICommand.parkourArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand = withArguments(ParkourArgument(nodeName).setOptional(optional).apply(block))