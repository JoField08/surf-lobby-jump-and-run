package dev.slne.surf.parkour.command.argument

import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ParkourArgument(nodeName: String): CustomArgument<Parkour, String> (
    StringArgument(nodeName),
    CustomArgumentInfoParser { info: CustomArgumentInfo<String> ->
        return@CustomArgumentInfoParser Parkour.getByName(info.input())
            ?: throw CustomArgumentException.fromAdventureComponent(
                buildText {
                    error("Der Parkour ")
                    variableValue(info.input())
                    error(" existiert nicht.")
                }
            )
    }) {
    init {
        this.replaceSuggestions(ArgumentSuggestions.stringCollection {
            DatabaseProvider.getParkours().map { it.name }
        })
    }
}