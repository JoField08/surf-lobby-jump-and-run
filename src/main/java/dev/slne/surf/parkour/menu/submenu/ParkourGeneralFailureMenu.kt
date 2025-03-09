package dev.slne.surf.parkour.menu.submenu

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.menu.ParkourMenu
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.builder.lore
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player

class ParkourGeneralFailureMenu(player: Player, title: Component) : ChestGui(
    5,
    ComponentHolder.of(buildText {
        error("Ups...".toSmallCaps())
        decorate(TextDecoration.BOLD)
    })
) {
    init {
        val outlinePane = StaticPane(0, 0, 9, 5)
        val outlineItem = GuiItem(buildItem(Material.GRAY_STAINED_GLASS_PANE) {
            displayName(text(" "))
        })

        val failurePane = StaticPane(4, 2, 1, 1)
        val failureItem = GuiItem(buildItem(Material.BARRIER) {
            displayName(title)
            lore { info("Klicke, um zum Hautmenü zurückzukehren!") }
        }) { ParkourMenu(player) }

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

        setOnGlobalClick { it.isCancelled = true }
        setOnGlobalDrag { it.isCancelled = true }

        show(player)
    }
}