package dev.slne.surf.parkour.util.gui

import com.github.stefvanschie.inventoryframework.gui.type.util.Gui
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

val InventoryClickEvent.player
    get() = whoClicked as? Player ?: error("No player has clicked on the inventory.")

fun Gui.previousPage(pages: PaginatedPane) {
    if (pages.page > 0) {
        pages.page -= 1
        update()
    }
}

fun Gui.nextPage(pages: PaginatedPane) {
    if (pages.page < pages.pages - 1) {
        pages.page += 1
        update()
    }
}

fun Gui.cancelGlobalClick() = setOnGlobalClick { it.isCancelled = true }
fun Gui.cancelGlobalDrag() = setOnGlobalDrag { it.isCancelled = true }