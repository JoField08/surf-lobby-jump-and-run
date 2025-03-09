package dev.slne.surf.parkour.command.subcommand

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.parkour.command.argument.parkourArgument
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.send
import dev.slne.surf.parkour.util.Permission

class ParkourStartCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_START)

        parkourArgument("parkour", optional = true)

        playerExecutor { player, args ->
            val parkour =
                args.getOrDefaultUnchecked("parkour", DatabaseProvider.getParkours().firstOrNull())

            if (parkour == null) {
                player.send { error("Es wurde kein Parkour gefunden.") }
                return@playerExecutor
            }

            if (Parkour.isJumping(player)) {
                player.send { error("Du befindest dich bereits in einem Parkour.") }
                return@playerExecutor
            }

            plugin.launch {
                parkour.startParkour(player)
                parkour.announceNewParkourStarted(player, parkour.name)
            }
        }
    }
}
