package dev.slne.surf.parkour.command.argument

import dev.jorel.commandapi.arguments.BlockStateArgument
import dev.jorel.commandapi.arguments.CustomArgument
import dev.slne.surf.parkour.util.failWithBuilder
import org.bukkit.Material
import org.bukkit.block.data.BlockData

class SolidMaterialArgument(nodeName: String) : CustomArgument<Material, BlockData>(
    BlockStateArgument(nodeName),
    CustomArgumentInfoParser { info ->
        val blockData = info.currentInput()
        val material = blockData.material

        if (!material.isSolid) {
            failWithBuilder {
                appendArgInput()
                append(" is not solid.")
            }
        }

        material
    })