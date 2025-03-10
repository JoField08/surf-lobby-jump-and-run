package dev.slne.surf.parkour.menu.submenu

import com.github.shynixn.mccoroutine.folia.launch
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.menu.ParkourMenu
import dev.slne.surf.parkour.menu.type.RedirectType
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.player.PlayerData
import dev.slne.surf.parkour.util.gui.*
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.builder.lore
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent

class ParkourSelectMenu(override val playerData: PlayerData, private val redirect: RedirectType) :
    ChestGui(5, ComponentHolder.of(buildText {
        primary("Parkour wählen".toSmallCaps())
        decorate(TextDecoration.BOLD)
    })), PlayerDataHolderGui {

    private val outlinePane = StaticPane(0, 0, 9, 5)
    private val pages = PaginatedPane(1, 1, 7, 3)

    private val outlineItem = outlineItem()
    private val menuButton = menuButton()
    private val backButton = backButton(pages)
    private val continueButton = nextButton(pages)

    init {
        val parkourItems = DatabaseProvider.getParkours().map { parkour ->
            GuiItem(buildItem(Material.COMPASS) {
                displayName { text(parkour.name) }
                lore { info("Klicke, um den Parkour auszuwählen.") }
            }) { it.handleParkourSelect(parkour) }
        }
        pages.populateWithGuiItems(parkourItems)

        for (x in 0 until 9) {
            outlinePane.addItem(outlineItem, x, 0)
            if (x == 2 || x == 4 || x == 6) {
                continue
            }
            outlinePane.addItem(outlineItem, x, 4)
        }

        for (y in 1 until 4) {
            outlinePane.addItem(outlineItem, 0, y)
            outlinePane.addItem(outlineItem, 8, y)
        }

        outlinePane.addItem(menuButton, 4, 4)

        addPane(outlinePane)
        addPane(pages)

        cancelGlobalDrag()
        cancelGlobalClick()
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

    private fun InventoryClickEvent.handleParkourSelect(parkour: Parkour) {
        when (redirect) {
            RedirectType.MAIN -> ParkourMenu.lazyOpen(player)
            RedirectType.PARKOUR_ACTIVES -> {
                if (parkour.activePlayers.isEmpty()) {
                    ParkourGeneralFailureMenu(
                        playerData,
                        buildText { error("Es sind keine Spieler in diesem Parkour.") }
                    ).show(player)
                    return
                }
                ParkourActivePlayersMenu(playerData, parkour).show(player)
            }

            RedirectType.START_PARKOUR -> plugin.launch { parkour.startParkour(player) }
        }
    }
}
