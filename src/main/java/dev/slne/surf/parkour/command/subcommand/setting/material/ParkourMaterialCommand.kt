package dev.slne.surf.parkour.command.subcommand.setting.material

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.parkour.command.subcommand.setting.material.settings.ParkourSettingMaterialAddCommand
import dev.slne.surf.parkour.command.subcommand.setting.material.settings.ParkourSettingMaterialListCommand
import dev.slne.surf.parkour.command.subcommand.setting.material.settings.ParkourSettingMaterialRemoveCommand
import dev.slne.surf.parkour.util.Permission

class ParkourMaterialCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_SETTING_MATERIAL)

        subcommand(ParkourSettingMaterialAddCommand("add"))
        subcommand(ParkourSettingMaterialRemoveCommand("remove"))
        subcommand(ParkourSettingMaterialListCommand("list"))
    }
}