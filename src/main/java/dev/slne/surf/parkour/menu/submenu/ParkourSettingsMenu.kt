package dev.slne.surf.parkour.menu.submenu

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.player.PlayerData
import dev.slne.surf.parkour.send
import dev.slne.surf.parkour.util.gui.*
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent

class ParkourSettingsMenu(override val playerData: PlayerData) : ChestGui(
    5, ComponentHolder.of(
        buildText {
            primary("Einstellungen".toSmallCaps())
            decorate(TextDecoration.BOLD)
        }
    )
), PlayerDataHolderGui {
    init {
        val outlinePane = StaticPane(0, 0, 9, 5)
        val outlineItem = outlineItem()
        val menuButton = menuButton()

        repeat(9) { x ->
            outlinePane.addItem(outlineItem, x, 0)
            if (x == 4) {
                outlinePane.addItem(menuButton, x, 4)
            } else {
                outlinePane.addItem(outlineItem, x, 4)
            }
        }

        for (y in 1 until 4) {
            outlinePane.addItem(outlineItem, 0, y)
            outlinePane.addItem(outlineItem, 8, y)
        }

        val settingsPane = StaticPane(1, 1, 7, 3)
        val currentSoundToggleState = buildText {
            if (playerData.likesSound) success("aktiviert") else error("deaktiviert")
        }

        val soundSettingsItem = GuiItem(
            buildItem(Material.JUKEBOX) {
                displayName(text("Sound"))
                buildLore {
                    line {
                        info("Der Sound ist aktuell ")
                        append(currentSoundToggleState)
                        info(".")
                    }
                    line {
                        info("Klicke, um die Einstellung zu ändern!")
                    }
                }
            }
        ) { it.handleSoundSettings() }
        settingsPane.addItem(soundSettingsItem, 0, 0)

        addPane(outlinePane)
        addPane(settingsPane)

        cancelGlobalClick()
        cancelGlobalDrag()
    }

    private fun InventoryClickEvent.handleSoundSettings() {
        playerData.edit { likesSound = !likesSound }
        player.send {
            success("Die Parkour-Sounds sind nun ")
            if (playerData.likesSound) success("aktiviert") else error("deaktiviert")
            success(".")
        }

        ParkourSettingsMenu(playerData).show(whoClicked)
    }
}