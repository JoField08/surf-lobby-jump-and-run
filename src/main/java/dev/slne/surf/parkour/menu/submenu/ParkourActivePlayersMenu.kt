package dev.slne.surf.parkour.menu.submenu

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.menu.ParkourMenu
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.util.playerName
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.builder.lore
import dev.slne.surf.surfapi.bukkit.api.builder.meta
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.SkullMeta

class ParkourActivePlayersMenu(player: Player, parkour: Parkour) :
    ChestGui(5, ComponentHolder.of(buildText {
        primary("Aktive Spieler".toSmallCaps())
        decorate(TextDecoration.BOLD)
    })) {
    init {
        val outlinePane = StaticPane(0, 0, 9, 5)
        val pages = PaginatedPane(1, 1, 7, 3)
        val outlineItem = GuiItem(buildItem(Material.GRAY_STAINED_GLASS_PANE) {
            displayName(text(" "))
        })
        val backButton = GuiItem(buildItem(Material.ARROW) {
            displayName { primary("Vorherige Seite") }
            lore { info("Klicke, um die Seite zu wechseln!") }
        }) {
            if (pages.page > 0) {
                pages.page -= 1
                update()
            }
        }

        val continueButton = GuiItem(buildItem(Material.ARROW) {
            displayName { primary("Nächste Seite") }
            lore { info("Klicke, um die Seite zu wechseln!") }
        }) {
            if (pages.page < pages.pages - 1) {
                pages.page += 1
                update()
            }
        }

        val menuButton = GuiItem(buildItem(Material.BARRIER) {
            displayName { primary("Hautmenü") }
            lore { info("Klicke, um zum Hautmenü zurückzukehren!") }
        }) { ParkourMenu(player) }

        repeat(5) { y ->
            repeat(9) { x ->
                if (y == 4) {
                    outlinePane.addItem(outlineItem, 1, y)
                    outlinePane.addItem(outlineItem, 3, y)
                    outlinePane.addItem(outlineItem, 5, y)
                    outlinePane.addItem(outlineItem, 7, y)
                }
                if (y == 0) {
                    outlinePane.addItem(outlineItem, x, y)
                } else {
                    if (x == 0 || x == 8) {
                        outlinePane.addItem(outlineItem, x, y)
                    }
                }
            }
        }

        if (pages.page > 0) {
            outlinePane.addItem(backButton, 2, 4)
        } else {
            outlinePane.addItem(outlineItem, 2, 4)
        }

        if (pages.page < pages.pages - 1) {
            outlinePane.addItem(continueButton, 6, 4)
        } else {
            outlinePane.addItem(outlineItem, 6, 4)
        }

        outlinePane.addItem(menuButton, 4, 4)


        val playerList = mutableObjectListOf<GuiItem>(parkour.activePlayers.size)
        for (activePlayer in parkour.activePlayers) {
            val points = parkour.currentPoints.getInt(player.uniqueId)

            playerList += GuiItem(buildItem(Material.PLAYER_HEAD) {
                displayName(text(activePlayer.playerName()))
                lore {
                    spacer("  - ")
                    variableKey("Aktuelle Sprünge: ".toSmallCaps())
                    variableValue(points.toString())
                }
                meta<SkullMeta> { owningPlayer = Bukkit.getOfflinePlayer(activePlayer) }
            })
        }


        pages.populateWithGuiItems(playerList)

        setOnGlobalClick { it.isCancelled = true }
        setOnGlobalDrag { it.isCancelled = true }

        addPane(outlinePane)
        addPane(pages)

        show(player)
    }
}