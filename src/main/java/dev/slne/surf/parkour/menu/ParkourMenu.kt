package dev.slne.surf.parkour.menu

import com.github.shynixn.mccoroutine.folia.launch
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.leaderboard.LeaderboardSortingType
import dev.slne.surf.parkour.menu.submenu.*
import dev.slne.surf.parkour.menu.type.RedirectType
import dev.slne.surf.surfapi.bukkit.api.builder.*
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.SkullMeta


class ParkourMenu(player: Player) : ChestGui(5, ComponentHolder.of(buildText {
    primary("Parkour".toSmallCaps())
    decorate(TextDecoration.BOLD)
})) {
    init {
        plugin.launch {
            val playerData = DatabaseProvider.getPlayerData(player.uniqueId)
            val jumps = playerData.points
            val tries = playerData.trys
            val highscore = playerData.highScore
            val name = playerData.name

            val outlinePane = StaticPane(0, 0, 9, 5)
            val outlineItem = GuiItem(buildItem(Material.GRAY_STAINED_GLASS_PANE) {
                displayName(text(" "))
            }) { it.isCancelled = true }

            val closeMenuItem = GuiItem(buildItem(Material.BARRIER) {
                displayName { primary("Schließen") }
                lore { info("Klicke, um das Hautmenü zu schließen!") }
            }) { it.whoClicked.closeInventory() }

            repeat(9) { x ->
                outlinePane.addItem(outlineItem, x, 0)
                if (x == 4) {
                    outlinePane.addItem(closeMenuItem, x, 4)
                } else {
                    outlinePane.addItem(outlineItem, x, 4)
                }
            }

            for (y in 1 until 4) {
                outlinePane.addItem(outlineItem, 0, y)
                outlinePane.addItem(outlineItem, 8, y)
            }

            val playerHeadPane = StaticPane(4, 1, 1, 1)
            val profileHead = buildItem(Material.PLAYER_HEAD) {
                displayName(text(name))
                buildLore {
                    line {
                        spacer(" - ")
                        variableKey("Sprünge: ".toSmallCaps())
                        variableValue(jumps.toString())
                    }
                    line {
                        spacer(" - ")
                        variableKey("Versuche: ".toSmallCaps())
                        variableValue(tries.toString())
                    }
                    line {
                        spacer(" - ")
                        variableKey("Highscore: ".toSmallCaps())
                        variableValue(highscore.toString())
                    }
                }

                meta<SkullMeta> { owningPlayer = player }
            }

            playerHeadPane.addItem(GuiItem(profileHead), 0, 0)

            val taskbarPane = StaticPane(0, 3, 9, 1)
            val statsItem = GuiItem(buildItem(Material.NETHER_STAR) {
                displayName { primary("Bestenliste") }
                lore { info("Klicke, um dir die Bestenliste anzusehen!") }
            }) { ParkourScoreboardMenu(player, LeaderboardSortingType.POINTS_HIGHEST) }

            val startItem = GuiItem(buildItem(Material.RECOVERY_COMPASS) {
                displayName { primary("Parkour starten") }
                lore { info("Klicke, um einen Parkour zu starten!") }
            }) {
                if (DatabaseProvider.getParkours().isEmpty()) {
                    ParkourGeneralFailureMenu(
                        player,
                        buildText { error("Es gibt keine verfügbaren Parkours!") }
                    )
                    return@GuiItem
                }

                if (DatabaseProvider.getParkours().size == 1) {
                    plugin.launch {
                        DatabaseProvider.getParkours().first().startParkour(player)
                    }
                    return@GuiItem
                }

                ParkourSelectMenu(RedirectType.START_PARKOUR).show(player)
            }

            val settingsItem = GuiItem(buildItem(Material.REPEATING_COMMAND_BLOCK) {
                displayName { primary("Einstellungen") }
                lore { info("Klicke, um zu den Einstellungen zu gelangen!") }
            }) { ParkourSettingsMenu(player) }

            val activePlayersItem = GuiItem(buildItem(Material.WRITABLE_BOOK) {
                displayName { primary("Aktive Spieler") }
                lore { info("Klicke, um dir die aktiven Spieler anzusehen!") }
            }) {
                if (DatabaseProvider.getParkours().isEmpty()) {
                    ParkourGeneralFailureMenu(
                        player,
                        buildText { error("Es gibt derzeit keine verfügbaren Parkours!") }
                    )
                    return@GuiItem
                }

                if (DatabaseProvider.getParkours().size == 1) {
                    val parkour = DatabaseProvider.getParkours().first()

                    if (parkour.activePlayers.isEmpty()) {
                        ParkourGeneralFailureMenu(
                            player,
                            buildText { error("Dieser Parkour ist leer!") }
                        )
                        return@GuiItem
                    }

                    ParkourActivePlayersMenu(player, parkour)
                    return@GuiItem
                }

                ParkourSelectMenu(RedirectType.PARKOUR_ACTIVES).show(player)
            }

            taskbarPane.addItem(statsItem, 1, 0)
            taskbarPane.addItem(startItem, 3, 0)
            taskbarPane.addItem(settingsItem, 5, 0)
            taskbarPane.addItem(activePlayersItem, 7, 0)

            setOnGlobalDrag { it.isCancelled = true }
            setOnGlobalClick { it.isCancelled = true }

            addPane(taskbarPane)
            addPane(outlinePane)
            addPane(playerHeadPane)

            show(player);
        }
    }
}
