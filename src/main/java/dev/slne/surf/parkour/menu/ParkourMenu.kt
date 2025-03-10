package dev.slne.surf.parkour.menu

import com.github.shynixn.mccoroutine.folia.launch
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.database.DatabaseProvider.Users.points
import dev.slne.surf.parkour.leaderboard.LeaderboardSortingType
import dev.slne.surf.parkour.menu.submenu.*
import dev.slne.surf.parkour.menu.type.RedirectType
import dev.slne.surf.parkour.player.PlayerData
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.util.gui.*
import dev.slne.surf.surfapi.bukkit.api.builder.*
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.meta.SkullMeta

class ParkourMenu(override val playerData: PlayerData) : ChestGui(5, ComponentHolder.of(buildText {
    primary("Parkour".toSmallCaps())
    decorate(TextDecoration.BOLD)
})), PlayerDataHolderGui {
    init {
        val (uuid, name, highscore, jumps, tries) = playerData

        val outlinePane = StaticPane(0, 0, 9, 5)
        val outlineItem = outlineItem()

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
            displayName(text(points))
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

            meta<SkullMeta> { owningPlayer = Bukkit.getOfflinePlayer(uuid) }
        }

        playerHeadPane.addItem(GuiItem(profileHead), 0, 0)

        val taskbarPane = StaticPane(0, 3, 9, 1)
        val statsItem = GuiItem(buildItem(Material.NETHER_STAR) {
            displayName { primary("Bestenliste") }
            lore { info("Klicke, um dir die Bestenliste anzusehen!") }
        }) { it.handleStatsItem() }

        val startItem = GuiItem(buildItem(Material.RECOVERY_COMPASS) {
            displayName { primary("Parkour starten") }
            lore { info("Klicke, um einen Parkour zu starten!") }
        }) { it.handleStart() }

        val settingsItem = GuiItem(buildItem(Material.REPEATING_COMMAND_BLOCK) {
            displayName { primary("Einstellungen") }
            lore { info("Klicke, um zu den Einstellungen zu gelangen!") }
        }) { ParkourSettingsMenu(playerData).show(it.whoClicked) }

        val activePlayersItem = GuiItem(buildItem(Material.WRITABLE_BOOK) {
            displayName { primary("Aktive Spieler") }
            lore { info("Klicke, um dir die aktiven Spieler anzusehen!") }
        }) { it.handleActivePlayers() }

        taskbarPane.addItem(statsItem, 1, 0)
        taskbarPane.addItem(startItem, 3, 0)
        taskbarPane.addItem(settingsItem, 5, 0)
        taskbarPane.addItem(activePlayersItem, 7, 0)

        cancelGlobalDrag()
        cancelGlobalClick()

        addPane(taskbarPane)
        addPane(outlinePane)
        addPane(playerHeadPane)
    }

    private fun InventoryClickEvent.handleStart() {
        val player = player
        val parkours = DatabaseProvider.getParkours()

        when {
            parkours.isEmpty() -> ParkourGeneralFailureMenu(
                playerData,
                buildText { error("Es gibt keine verfügbaren Parkours!") }
            ).show(player)

            parkours.size == 1 -> plugin.launch { parkours.first().startParkour(player) }
            else -> ParkourSelectMenu(playerData, RedirectType.START_PARKOUR).show(player)
        }
    }

    private fun InventoryClickEvent.handleActivePlayers() {
        val player = player

        if (DatabaseProvider.getParkours().isEmpty()) {
            ParkourGeneralFailureMenu(
                playerData,
                buildText { error("Es gibt derzeit keine verfügbaren Parkours!") }
            ).show(player)
            return
        }

        if (DatabaseProvider.getParkours().size == 1) {
            val parkour = DatabaseProvider.getParkours().first()

            if (parkour.activePlayers.isEmpty()) {
                ParkourGeneralFailureMenu(
                    playerData,
                    buildText { error("Dieser Parkour ist leer!") }
                ).show(player)
                return
            }

            ParkourActivePlayersMenu(playerData, parkour).show(player)
            return
        }

        ParkourSelectMenu(playerData, RedirectType.PARKOUR_ACTIVES).show(player)
    }

    private fun InventoryClickEvent.handleStatsItem() {
        ParkourScoreboardMenu(playerData, LeaderboardSortingType.POINTS_HIGHEST).show(whoClicked)
    }


    companion object {
        suspend operator fun invoke(player: Player): ParkourMenu {
            val data = DatabaseProvider.getPlayerData(player.uniqueId)
            return ParkourMenu(data)
        }

        fun lazyOpen(player: Player) {
            plugin.launch { invoke(player).show(player) }
        }
    }
}
