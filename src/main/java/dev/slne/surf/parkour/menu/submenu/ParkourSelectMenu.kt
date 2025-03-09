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
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.builder.lore
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player

class ParkourSelectMenu(private val redirect: RedirectType) :
    ChestGui(5, ComponentHolder.of(buildText {
        primary("Parkour wählen".toSmallCaps())
        decorate(TextDecoration.BOLD)
    })) {

    private val outlinePane = StaticPane(0, 0, 9, 5)
    private val pages = PaginatedPane(1, 1, 7, 3)
    private val items = mutableObjectListOf<GuiItem>()

    private val outlineItem = GuiItem(buildItem(Material.GRAY_STAINED_GLASS_PANE) {
        displayName(text(" "))
    })
    private val menuButton = GuiItem(buildItem(Material.BARRIER) {
        displayName { primary("Hautmenü") }
        lore { info("Klicke, um zum Hautmenü zurückzukehren!") }
    }) { ParkourMenu(it.whoClicked as Player) }
    private val backButton = GuiItem(buildItem(Material.ARROW) {
        displayName { primary("Vorherige Seite") }
        lore { info("Klicke, um die Seite zu wechseln!") }
    }) {
        if (pages.page > 0) {
            pages.page -= 1
            update()
        }
    }

    private val continueButton = GuiItem(buildItem(Material.ARROW) {
        displayName { primary("Nächste Seite") }
        lore { info("Klicke, um die Seite zu wechseln!") }
    }) {
        if (pages.page < pages.pages - 1) {
            pages.page += 1
            update()
        }
    }

    init {
        for (parkour in DatabaseProvider.getParkours()) {
            items += GuiItem(buildItem(Material.COMPASS) {
                displayName { text(parkour.name) }
                lore { info("Klicke, um den Parkour auszuwählen.") }
            }) {
                val player = it.whoClicked as Player
                when (redirect) {
                    RedirectType.MAIN -> ParkourMenu(player)
                    RedirectType.PARKOUR_ACTIVES -> {
                        if (parkour.activePlayers.isEmpty()) {
                            ParkourGeneralFailureMenu(
                                player,
                                buildText { error("Es sind keine Spieler in diesem Parkour.") }
                            )
                            return@GuiItem
                        }
                        ParkourActivePlayersMenu(player, parkour)
                    }

                    RedirectType.START_PARKOUR -> plugin.launch { parkour.startParkour(player) }
                }
            }
        }
        pages.populateWithGuiItems(items)

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


        setOnGlobalDrag { it.isCancelled = true }
        setOnGlobalClick { it.isCancelled = true }
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
