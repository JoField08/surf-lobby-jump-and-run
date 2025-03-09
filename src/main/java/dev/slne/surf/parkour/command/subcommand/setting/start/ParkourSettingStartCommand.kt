package dev.slne.surf.parkour.command.subcommand.setting.start

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.locationArgument
import dev.slne.surf.parkour.command.argument.parkourArgument
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.send
import dev.slne.surf.parkour.util.Permission
import org.bukkit.Location

class ParkourSettingStartCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_SETTING_START)

        locationArgument("pos")
        parkourArgument("parkour")

        anyExecutor { sender, args ->
            val pos: Location by args
            val parkour: Parkour by args

            parkour.edit {
                start = pos.toVector().normalize()
            }

            sender.send {
                success("Der Start von ")
                variableValue(parkour.name)
                success(" wurde neu definiert.")
            }
        }
    }
}