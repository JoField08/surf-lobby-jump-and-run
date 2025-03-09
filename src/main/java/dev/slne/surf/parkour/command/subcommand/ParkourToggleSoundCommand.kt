package dev.slne.surf.parkour.command.subcommand

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.send
import dev.slne.surf.parkour.util.Permission

class ParkourToggleSoundCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_TOGGLE)

        playerExecutor { player, args ->
            plugin.launch {
                val playerData = DatabaseProvider.getPlayerData(player.uniqueId)

                playerData.edit {
                    likesSound = !likesSound
                }

                player.send {
                    info("Die ParkourSounds sind nun ")
                    if (playerData.likesSound) success("aktiviert") else error("deaktiviert")
                    info(".")
                }
            }
        }
    }
}
