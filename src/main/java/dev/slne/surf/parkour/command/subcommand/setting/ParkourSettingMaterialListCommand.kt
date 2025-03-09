package dev.slne.surf.parkour.command.subcommand.setting

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.parkour.command.argument.ParkourArgument
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.send
import dev.slne.surf.parkour.util.PageableMessageBuilder
import dev.slne.surf.parkour.util.Permission
import org.bukkit.entity.Player

class ParkourSettingMaterialListCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_SETTING_MATERIAL_LIST)
        withArguments(ParkourArgument("parkour"))
        withOptionalArguments(IntegerArgument("page"))
        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val parkour = args.getUnchecked<Parkour>("parkour") ?: return@PlayerCommandExecutor
            val page = args.getOrDefaultUnchecked("page", 1)

            if (parkour.availableMaterials.isEmpty()) {
                player.send {
                    error("Es sind keine Material-Typen in ")
                    info(parkour.name)
                    error(" eingestellt.")
                }
                return@PlayerCommandExecutor
            }

            PageableMessageBuilder {
                pageCommand = "/parkour material list ${parkour.name} %page%"
                title {
                    primary("Materialien von ")
                    info(parkour.name)
                }

                parkour.availableMaterials.forEach {
                    line {
                        darkSpacer("- ")
                        variableValue(it.name)
                    }
                }
            }.send(player, page)
        })
    }
}
