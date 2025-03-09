package dev.slne.surf.parkour.command.subcommand

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.parkour.command.argument.ParkourArgument
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.send
import dev.slne.surf.parkour.util.Permission
import org.bukkit.entity.Player

class ParkourStartCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_START)
        withOptionalArguments(ParkourArgument("parkour"))

        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val parkour =
                args.getUnchecked<Parkour?>("parkour") ?: DatabaseProvider.getParkours().first()

            if (parkour == null) {
                player.send { error("Es wurde kein Parkour gefunden.") }
                return@PlayerCommandExecutor
            }

            if (Parkour.isJumping(player)) {
                player.send { error("Du befindest dich bereits in einem Parkour.") }
                return@PlayerCommandExecutor
            }

            plugin.launch {
                parkour.startParkour(player)
                parkour.announceNewParkourStarted(player, parkour.name)
            }
        })
    }
}
