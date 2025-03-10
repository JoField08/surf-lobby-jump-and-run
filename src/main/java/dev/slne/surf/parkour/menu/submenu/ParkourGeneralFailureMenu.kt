package dev.slne.surf.parkour.menu.submenu

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.menu.ParkourMenu
import dev.slne.surf.parkour.player.PlayerData
import dev.slne.surf.parkour.util.gui.PlayerDataHolderGui
import dev.slne.surf.parkour.util.gui.cancelGlobalClick
import dev.slne.surf.parkour.util.gui.cancelGlobalDrag
import dev.slne.surf.parkour.util.gui.outlineItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.builder.lore
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material

class ParkourGeneralFailureMenu(override val playerData: PlayerData, title: Component) : ChestGui(
    5,
    ComponentHolder.of(buildText {
        error("Ups...".toSmallCaps())
        decorate(TextDecoration.BOLD)
    })
), PlayerDataHolderGui {
    init {
        val outlinePane = StaticPane(0, 0, 9, 5)
        val outlineItem = outlineItem()

        val failurePane = StaticPane(4, 2, 1, 1)
        val failureItem = GuiItem(buildItem(Material.BARRIER) {
            displayName(title)
            lore { info("Klicke, um zum Hautmenü zurückzukehren!") }
        }) { ParkourMenu(playerData).show(it.whoClicked) }

        repeat(9) { x ->
            outlinePane.addItem(outlineItem, x, 0)
            outlinePane.addItem(outlineItem, x, 4)
        }

        for (y in 1 until 4) {
            outlinePane.addItem(outlineItem, 0, y)
            outlinePane.addItem(outlineItem, 8, y)
        }

        failurePane.addItem(failureItem, 0, 0)

        addPane(outlinePane)
        addPane(failurePane)

        cancelGlobalClick()
        cancelGlobalDrag()
    }
}