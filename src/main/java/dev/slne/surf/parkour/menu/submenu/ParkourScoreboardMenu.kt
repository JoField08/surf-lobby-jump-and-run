package dev.slne.surf.parkour.menu.submenu

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.ticks
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.leaderboard.LeaderboardSortingType
import dev.slne.surf.parkour.player.PlayerData
import dev.slne.surf.parkour.util.HeadUtil
import dev.slne.surf.parkour.util.gui.*
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material

class ParkourScoreboardMenu(
    override val playerData: PlayerData,
    private var sorting: LeaderboardSortingType
) : ChestGui(5, ComponentHolder.of(buildText {
    primary("Bestenliste".toSmallCaps())
    decorate(TextDecoration.BOLD)
})), PlayerDataHolderGui {

    private val outlinePane = StaticPane(0, 0, 9, 5)
    private val pages = PaginatedPane(1, 1, 7, 3)

    private val outlineItem = outlineItem()
    private val backButton = backButton(pages)
    private val continueButton = nextButton(pages)

    init {
        val cycleButton = GuiItem(buildItem(Material.COMPASS) {
            displayName { primary("Sortieren nach: ${sorting.displayName}") }
            buildLore {
                line { info("Klicke, um die Sortierung zu ändern!") }
                LeaderboardSortingType.entries.forEach { type ->
                    line {
                        darkSpacer(if (type == sorting) "> " else "  ")
                        info(type.displayName)
                    }
                }
            }
        }) {
            sorting = sorting.next()
            ParkourScoreboardMenu(playerData, sorting).show(it.whoClicked)
        }

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
        outlinePane.addItem(cycleButton, 4, 0)

        lazilyAddStatisticsItems()

        cancelGlobalDrag()
        cancelGlobalClick()

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

    private fun lazilyAddStatisticsItems() {
        plugin.launch {
            val guiItemsDeferred = DatabaseProvider.getEveryPlayerData(sorting)
                .map { async { it.asStatisticsItem() } }
                .toMutableList()

            while (guiItemsDeferred.isNotEmpty()) {
                val completedItems = guiItemsDeferred.filter { it.isCompleted }
                guiItemsDeferred -= completedItems

                val guiItems = completedItems.awaitAll()
                if (guiItems.isNotEmpty()) {
                    withContext(plugin.globalRegionDispatcher) {
                        pages.populateWithGuiItems(guiItems)
                        update()
                    }
                }

                delay(10.ticks)
            }
        }
    }

    private suspend fun PlayerData.asStatisticsItem() = GuiItem(HeadUtil.getPlayerHead(uuid).apply {
        displayName(text(name))
        buildLore {
            line { }
            line { info("Statistiken:") }
            line {
                spacer("  - ")
                variableKey("Sprünge: ".toSmallCaps())
                variableValue(points.toString())
            }
            line {
                spacer("  - ")
                variableKey("Versuche: ".toSmallCaps())
                variableValue(trys.toString())
            }
            line {
                spacer("  - ")
                variableKey("Highscore: ".toSmallCaps())
                variableValue(highScore.toString())
            }
        }
    })
}