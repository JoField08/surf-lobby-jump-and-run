package dev.slne.surf.parkour.command.subcommand

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.slne.surf.parkour.command.argument.parkourArgument
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.send
import dev.slne.surf.parkour.util.PageableMessageBuilder
import dev.slne.surf.parkour.util.Permission
import dev.slne.surf.parkour.util.playerName

class ParkourListCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_LIST)

        parkourArgument("parkour")
        integerArgument("page", min = 1, optional = true)

        anyExecutor { sender, args ->
            val parkour: Parkour by args
            val page = args.getOrDefaultUnchecked("page", 1)

            if (parkour.activePlayers.isEmpty()) {
                sender.send {
                    error("Es sind keine Spieler in ")
                    info(parkour.name)
                    error(" aktiv.")
                }
                return@anyExecutor
            }

            PageableMessageBuilder {
                pageCommand = "/parkour list ${parkour.name} %page%"
                title {
                    primary("Spieler in ")
                    variableValue(parkour.name)
                }

                parkour.activePlayers.forEach {
                    line {
                        spacer("- ")
                        variableValue(it.playerName())
                        info(" (${parkour.currentPoints.getInt(it)})")
                    }
                }
            }.send(sender, page)
        }
    }
}
