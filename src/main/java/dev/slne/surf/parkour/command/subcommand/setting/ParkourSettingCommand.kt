package dev.slne.surf.parkour.command.subcommand.setting

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.parkour.command.subcommand.setting.area.ParkourSettingAreaCommand
import dev.slne.surf.parkour.command.subcommand.setting.spawn.ParkourSettingSpawnCommand
import dev.slne.surf.parkour.command.subcommand.setting.start.ParkourSettingStartCommand
import dev.slne.surf.parkour.util.Permission

class ParkourSettingCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_SETTING)

        subcommand(ParkourSettingAreaCommand("setArea"))
        subcommand(ParkourSettingSpawnCommand("setRespawn"))
        subcommand(ParkourSettingStartCommand("setStart"))
    }
}
