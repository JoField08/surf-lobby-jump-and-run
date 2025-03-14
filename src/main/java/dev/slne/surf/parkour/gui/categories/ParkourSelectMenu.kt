package dev.slne.surf.parkour.gui.categories

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.gui.ParkourMenu
import dev.slne.surf.parkour.gui.RedirectType
import dev.slne.surf.parkour.instance
import dev.slne.surf.parkour.util.ItemBuilder
import dev.slne.surf.parkour.util.MessageBuilder
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player

class ParkourSelectMenu(player: Player, redirect: RedirectType) : ChestGui(5, ComponentHolder.of(MessageBuilder().primary("ᴘᴀʀᴋᴏᴜʀ ᴡÄʜʟᴇɴ").build().decorate(TextDecoration.BOLD))) {
    init {
        val outlinePane = StaticPane(0, 0, 9, 5)
        val pages = PaginatedPane(1, 1, 7, 3)
        val items = ObjectArrayList<GuiItem>()
        val outlineItem = GuiItem(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(Component.text(" ")).build())
        val menuButton = GuiItem(ItemBuilder(Material.BARRIER).setName(MessageBuilder().primary("Hautmenü").build()).addLoreLine(MessageBuilder().info("Klicke, um zum Hautmenü zurückzukehren!").build()).build()) {
            ParkourMenu(player)
        }
        val backButton = GuiItem(ItemBuilder(Material.ARROW).setName(MessageBuilder().error("Vorherige Seite").build()).addLoreLine(MessageBuilder().info("Klicke, um die Seite zu wechseln!").build()).build()) {
            if (pages.page > 0) {
                pages.page -= 1
                update()
            }
        }

        val continueButton = GuiItem(ItemBuilder(Material.ARROW).setName(MessageBuilder().success("Nächste Seite").build()).addLoreLine(MessageBuilder().info("Klicke, um die Seite zu wechseln!").build()).build()) {
            if (pages.page < pages.pages - 1) {
                pages.page += 1
                update()
            }
        }


        for (x in 0 until 9) {
            outlinePane.addItem(outlineItem, x, 0)
            outlinePane.addItem(outlineItem, x, 4)
        }

        for (y in 1 until 4) {
            outlinePane.addItem(outlineItem, 0, y)
            outlinePane.addItem(outlineItem, 8, y)
        }

        for (parkour in DatabaseProvider.getParkours()) {
            items.add(GuiItem(ItemBuilder(Material.COMPASS)
                .setName(MessageBuilder(parkour.name).build())
                .addLoreLine(MessageBuilder().info("Klicke, um den Parkour auszuwählen.").build())
                .build()) {

                when(redirect) {
                    RedirectType.MAIN -> {
                        ParkourMenu(player)
                    }
                    RedirectType.PARKOUR_ACTIVES -> {
                        if(parkour.activePlayers.isEmpty()) {
                            ParkourGeneralFailureMenu(player, MessageBuilder().error("Es sind keine Spieler in diesem Parkour."))
                            return@GuiItem
                        }

                        ParkourActivePlayersMenu(player, parkour)
                    }

                    RedirectType.START_PARKOUR -> {
                        instance.launch {
                            parkour.startParkour(player)
                        }
                    }
                }
            })
        }

        pages.populateWithGuiItems(items)

        outlinePane.addItem(menuButton, 4, 4)

        if(pages.page > 0) {
            outlinePane.addItem(backButton, 2, 4)
        } else {
            outlinePane.addItem(outlineItem, 2, 4)
        }

        if(pages.page + 1 < pages.pages) {
            outlinePane.addItem(continueButton, 6, 4)
        } else {
            outlinePane.addItem(outlineItem, 6, 4)
        }

        addPane(outlinePane)
        addPane(pages)

        setOnGlobalClick { it.isCancelled = true }
        setOnGlobalDrag { it.isCancelled = true }

        show(player)
    }
}