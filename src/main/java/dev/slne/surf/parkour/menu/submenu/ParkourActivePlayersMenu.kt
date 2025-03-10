package dev.slne.surf.parkour.menu.submenu

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.player.PlayerData
import dev.slne.surf.parkour.util.gui.*
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
import org.bukkit.inventory.meta.SkullMeta

class ParkourActivePlayersMenu(override val playerData: PlayerData, parkour: Parkour) :
    ChestGui(5, ComponentHolder.of(buildText {
        primary("Aktive Spieler".toSmallCaps())
        decorate(TextDecoration.BOLD)
    })), PlayerDataHolderGui {

    private val outlinePane = StaticPane(0, 0, 9, 5)
    private val outlineItem = outlineItem()
    private val pages = PaginatedPane(1, 1, 7, 3)
    private val backButton = backButton(pages)
    private val continueButton = nextButton(pages)

    init {
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

        outlinePane.addItem(menuButton(), 4, 4)

        val playerList = mutableObjectListOf<GuiItem>(parkour.activePlayers.size)
        for (activePlayer in parkour.activePlayers) {
            val points = parkour.currentPoints.getInt(activePlayer)

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

        cancelGlobalClick()
        cancelGlobalDrag()

        addPane(outlinePane)
        addPane(pages)
    }

    override fun update() {
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

        super.update()
    }
}