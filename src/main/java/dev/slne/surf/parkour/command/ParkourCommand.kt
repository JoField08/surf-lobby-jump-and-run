package dev.slne.surf.parkour.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.parkour.command.subcommand.*
import dev.slne.surf.parkour.command.subcommand.setting.ParkourMaterialCommand
import dev.slne.surf.parkour.command.subcommand.setting.ParkourSettingCommand
import dev.slne.surf.parkour.menu.ParkourMenu
import dev.slne.surf.parkour.util.Permission

class ParkourCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR)

        withSubcommand(ParkourSettingCommand("setting"))
        withSubcommand(ParkourStartCommand("start"))
        withSubcommand(ParkourListCommand("list"))
        withSubcommand(ParkourStatsCommand("stats"))
        withSubcommand(ParkourToggleSoundCommand("toggleSound"))
        withSubcommand(ParkourMaterialCommand("material"))
        withSubcommand(ParkourCreateCommand("create"))
        withSubcommand(ParkourRemoveCommand("remove"))

        playerExecutor { player, _ ->
            ParkourMenu(player)
        }
    }
}

