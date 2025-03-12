package dev.slne.surf.parkour.command.subcommand

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.parkour.command.argument.parkourArgument
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.send
import dev.slne.surf.parkour.util.Permission
import dev.slne.surf.surfapi.core.api.messages.adventure.clickSuggestsCommand

class ParkourRemoveCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_REMOVE)

        parkourArgument("parkour")
        literalArgument("confirmed", "--confirm", optional = true)

        anyExecutor { sender, args ->
            val parkour: Parkour by args
            val confirmed = args.get("confirmed") != null

            if (!confirmed) {
                sender.send {
                    info("Bist du dir sicher, dass du den Parkour ")
                    variableValue(parkour.name)
                    info(" löschen möchtest?")
                    clickSuggestsCommand("/parkour remove ${parkour.name} --confirm")
                }

                return@anyExecutor
            }

            DatabaseProvider.getParkours().remove(parkour)

            sender.send {
                success("Der Parkour ")
                variableValue(parkour.name)
                success(" wurde erfolgreich gelöscht.")
            }
        }
    }
}