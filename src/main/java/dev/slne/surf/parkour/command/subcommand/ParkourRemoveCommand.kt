package dev.slne.surf.parkour.command.subcommand

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.getValue
import dev.slne.surf.parkour.command.argument.parkourArgument
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.send
import dev.slne.surf.parkour.util.Permission

class ParkourRemoveCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_REMOVE)

        parkourArgument("parkour")

        anyExecutor { sender, args ->
            val parkour: Parkour by args

            DatabaseProvider.getParkours().remove(parkour)

            sender.send {
                success("Der Parkour ")
                variableValue(parkour.name)
                success(" wurde erfolgreich gelöscht.")
            }
        }
    }
}