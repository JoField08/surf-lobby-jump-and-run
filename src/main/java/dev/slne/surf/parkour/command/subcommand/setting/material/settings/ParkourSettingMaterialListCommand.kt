package dev.slne.surf.parkour.command.subcommand.setting.material.settings

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.slne.surf.parkour.command.argument.parkourArgument
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.send
import dev.slne.surf.parkour.util.PageableMessageBuilder
import dev.slne.surf.parkour.util.Permission

class ParkourSettingMaterialListCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_SETTING_MATERIAL_LIST)

        parkourArgument("parkour")
        integerArgument("page", min = 1, optional = true)

        anyExecutor { sender, args ->
            val parkour: Parkour by args
            val page = args.getOrDefaultUnchecked("page", 1)

            if (parkour.availableMaterials.isEmpty()) {
                sender.send {
                    error("Es sind keine Material-Typen in ")
                    info(parkour.name)
                    error(" eingestellt.")
                }
                return@anyExecutor
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
            }.send(sender, page)
        }
    }
}
