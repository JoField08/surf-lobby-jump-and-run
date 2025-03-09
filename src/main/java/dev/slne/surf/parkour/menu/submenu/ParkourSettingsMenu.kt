package dev.slne.surf.parkour.menu.submenu

import com.github.shynixn.mccoroutine.folia.launch
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.menu.ParkourMenu
import dev.slne.surf.parkour.send
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.builder.lore
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player

class ParkourSettingsMenu(player: Player) : ChestGui(
    5,
    ComponentHolder.of(
        buildText {
            primary("Einstellungen".toSmallCaps())
            decorate(TextDecoration.BOLD)
        }
    )
) {
    init {
        plugin.launch {
            val playerData = DatabaseProvider.getPlayerData(player.uniqueId)
            val outlinePane = StaticPane(0, 0, 9, 5)
            val outlineItem = GuiItem(buildItem(Material.GRAY_STAINED_GLASS_PANE) {
                displayName(text(" "))
            })

            val menuButton = GuiItem(buildItem(Material.BARRIER) {
                displayName { primary("Hauptmenü") }
                lore { info("Klicke, um zum Hautmenü zurückzukehren!") }
            }) { ParkourMenu(player) }

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
            ) {
                playerData.edit { likesSound = !likesSound }
                player.send {
                    success("Die Parkour-Sounds sind nun ")
                    if (playerData.likesSound) success("aktiviert") else error("deaktiviert")
                    success(".")
                }

                ParkourSettingsMenu(player)
            }
            settingsPane.addItem(soundSettingsItem, 0, 0)


            addPane(outlinePane)
            addPane(settingsPane)

            setOnGlobalClick { it.isCancelled = true }
            setOnGlobalDrag { it.isCancelled = true }

            show(player)
        }
    }
}