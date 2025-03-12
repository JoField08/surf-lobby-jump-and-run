package dev.slne.surf.parkour.menu.submenu

import com.github.shynixn.mccoroutine.folia.launch
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.menu.AbstractParkourGui
import dev.slne.surf.parkour.menu.ParkourMenu
import dev.slne.surf.parkour.menu.type.RedirectType
import dev.slne.surf.parkour.menu.util.*
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.player.PlayerData
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.builder.lore
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.util.intSetOf
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent

class ParkourSelectMenu(playerData: PlayerData, private val redirect: RedirectType) :
    AbstractParkourGui(5, buildText {
        primary("Parkour wählen".toSmallCaps())
        decorate(TextDecoration.BOLD)
    }, playerData) {

    private val outlineItem = outlineItem()
    private val outlinePane = StaticPane(0, 0, 9, 5).apply {
        fillTopRow(outlineItem)
        fillBottomRow(outlineItem, skipPositions = intSetOf(2, 4, 6))
        fillLeftRightColumns(outlineItem)
        addItem(menuButton(), 4, 4)
    }

    private val pages = PaginatedPane(1, 1, 7, 3)
    private val backButton = backButton(pages)
    private val continueButton = nextButton(pages)

    private val parkourItems = DatabaseProvider.getParkours().map { parkour ->
        GuiItem(buildItem(Material.COMPASS) {
            displayName { text(parkour.name) }
            lore { info("Klicke, um den Parkour auszuwählen.") }
        }) { it.handleParkourSelect(parkour) }
    }

    init {
        pages.populateWithGuiItems(parkourItems)

        addPane(outlinePane)
        addPane(pages)
    }

    override fun update() {
        updatePaginationButtons(outlinePane, pages, outlineItem, backButton, continueButton)
        super.update()
    }

    private fun InventoryClickEvent.handleParkourSelect(parkour: Parkour) {
        when (redirect) {
            RedirectType.MAIN -> ParkourMenu.lazyOpen(player)
            RedirectType.PARKOUR_ACTIVES -> {
                if (parkour.activePlayers.isEmpty()) {
                    ParkourGeneralFailureMenu(
                        playerData,
                        buildText { error("Es sind keine Spieler in diesem Parkour.") }
                    ).show(whoClicked)
                    return
                }
                ParkourActivePlayersMenu(playerData, parkour).show(whoClicked)
            }

            RedirectType.START_PARKOUR -> plugin.launch { parkour.startParkour(player) }
        }
    }
}
