package dev.slne.surf.parkour.command.subcommand.setting.material.settings

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.getValue
import dev.slne.surf.parkour.command.argument.parkourArgument
import dev.slne.surf.parkour.command.argument.solidMaterialArgument
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.send
import dev.slne.surf.parkour.util.Permission
import org.bukkit.Material

class ParkourSettingMaterialRemoveCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(Permission.COMMAND_PARKOUR_SETTING_MATERIAL_REMOVE)

        solidMaterialArgument("material")
        parkourArgument("parkour")

        anyExecutor { sender, args ->
            val material: Material by args
            val parkour: Parkour by args

            parkour.edit {
                availableMaterials.remove(material)
            }

            sender.send {
                variableValue(material.name)
                success(" wurde von der Liste der Materialien von ")
                variableValue(parkour.name)
                success(" entfernt.")
            }
        }
    }
}
