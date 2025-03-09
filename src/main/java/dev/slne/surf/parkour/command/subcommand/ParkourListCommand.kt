package dev.slne.surf.parkour.command.subcommand

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.parkour.command.argument.ParkourArgument
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.send
import dev.slne.surf.parkour.util.PageableMessageBuilder
import dev.slne.surf.parkour.util.Permission
import dev.slne.surf.parkour.util.playerName
import org.bukkit.entity.Player

class ParkourListCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_LIST)
        withArguments(ParkourArgument("parkour"))
        withOptionalArguments(IntegerArgument("page"))
        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val parkour = args.getUnchecked<Parkour>("parkour") ?: return@PlayerCommandExecutor
            val page = args.getOrDefaultUnchecked("page", 1)

            if (parkour.activePlayers.isEmpty()) {
                player.send {
                    error("Es sind keine Spieler in ")
                    info(parkour.name)
                    error(" aktiv.")
                }
                return@PlayerCommandExecutor
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
            }.send(player, page)
        })
    }
}
