package dev.slne.surf.parkour.menu.submenu

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.menu.AbstractParkourGui
import dev.slne.surf.parkour.menu.util.*
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.player.PlayerData
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

class ParkourActivePlayersMenu(playerData: PlayerData, parkour: Parkour) :
    AbstractParkourGui(5, buildText {
        primary("Aktive Spieler".toSmallCaps())
        decorate(TextDecoration.BOLD)
    }, playerData) {

    private val outlineItem = outlineItem()
    private val outlinePane = StaticPane(0, 0, 9, 5).apply {
        fillActivePlayersBorder(outlineItem)
        addItem(menuButton(), 4, 4)
    }

    private val pages = PaginatedPane(1, 1, 7, 3)
    private val backButton = backButton(pages)
    private val continueButton = nextButton(pages)

    init {
        val activePlayers = parkour.activePlayers
        val playerList = activePlayers.mapTo(mutableObjectListOf(activePlayers.size)) {
            GuiItem(buildItem(Material.PLAYER_HEAD) {
                displayName(text(it.playerName()))
                lore {
                    spacer("  - ")
                    variableKey("Aktuelle Sprünge: ".toSmallCaps())
                    variableValue(parkour.currentPoints(it).toString())
                }
                meta<SkullMeta> { owningPlayer = Bukkit.getOfflinePlayer(it) }
            })
        }

        pages.populateWithGuiItems(playerList)

        addPane(outlinePane)
        addPane(pages)
    }

    override fun update() {
        updatePaginationButtons(outlinePane, pages, outlineItem, backButton, continueButton)
        super.update()
    }
}