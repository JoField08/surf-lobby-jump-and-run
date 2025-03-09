package dev.slne.surf.parkour.menu.submenu

import com.github.shynixn.mccoroutine.folia.launch
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.leaderboard.LeaderboardSortingType
import dev.slne.surf.parkour.menu.ParkourMenu
import dev.slne.surf.parkour.util.HeadUtil
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.builder.lore
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player

class ParkourScoreboardMenu(player: Player, private var sorting: LeaderboardSortingType) :
    ChestGui(5, ComponentHolder.of(buildText {
        primary("Bestenliste".toSmallCaps())
        decorate(TextDecoration.BOLD)
    })) {

    init {
        plugin.launch {
            val items = mutableObjectListOf<GuiItem>()
            val outlinePane = StaticPane(0, 0, 9, 5)
            val pages = PaginatedPane(1, 1, 7, 3)

            val outlineItem = GuiItem(buildItem(Material.GRAY_STAINED_GLASS_PANE) {
                displayName(text(" "))
            })
            val backButton = GuiItem(buildItem(Material.ARROW) {
                displayName { primary("Vorherige Seite") }
                lore {
                    info("Klicke, um die Seite zu wechseln!")
                }
            }) {
                if (pages.page > 0) {
                    pages.page -= 1
                    update()
                }
            }

            val continueButton = GuiItem(buildItem(Material.ARROW) {
                displayName { primary("Nächste Seite") }
                lore {
                    info("Klicke, um die Seite zu wechseln!")
                }
            }) {
                if (pages.page < pages.pages - 1) {
                    pages.page += 1
                    update()
                }
            }

            val menuButton = GuiItem(buildItem(Material.BARRIER) {
                displayName { primary("Hautmenü") }
                lore {
                    info("Klicke, um zum Hautmenü zurückzukehren!")
                }
            }) { ParkourMenu(player) }

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
                ParkourScoreboardMenu(player, sorting)
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

            DatabaseProvider.getEveryPlayerData(sorting)
                .forEach { (uuid, name, highScore, points, trys) ->
                    items += GuiItem(HeadUtil.getPlayerHead(uuid).apply {
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

            pages.populateWithGuiItems(items)

            outlinePane.addItem(menuButton, 4, 4)
            outlinePane.addItem(cycleButton, 4, 0)

            setOnGlobalDrag { it.isCancelled = true }
            setOnGlobalClick { it.isCancelled = true }

            addPane(outlinePane)
            addPane(pages)
            show(player)
        }
    }
}