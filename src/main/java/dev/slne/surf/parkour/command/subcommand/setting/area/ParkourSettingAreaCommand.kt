package dev.slne.surf.parkour.command.subcommand.setting.area

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.locationArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.worldArgument
import dev.slne.surf.parkour.command.argument.parkourArgument
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.send
import dev.slne.surf.parkour.util.Area
import dev.slne.surf.parkour.util.Permission
import org.bukkit.Location

class ParkourSettingAreaCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_SETTING_AREA)

        parkourArgument("parkour")
        locationArgument("pos1")
        locationArgument("pos2")
        worldArgument("world", optional = true)

        playerExecutor { player, args ->
            val parkour: Parkour by args
            val pos1: Location by args
            val pos2: Location by args
            val world = args.getOrDefaultUnchecked("world", player.world)

            val max = pos1.toVector()
            val min = pos2.toVector()

            parkour.edit {
                this.area = Area(max, min)
                this.world = world
            }

            player.send {
                success("Die Arena von ")
                variableValue(parkour.name)
                success(" wurde neu definiert.")
            }
        }
    }
}