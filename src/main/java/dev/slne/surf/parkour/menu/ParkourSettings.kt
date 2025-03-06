package dev.slne.surf.parkour.menu

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.SurfParkour
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.instance
import dev.slne.surf.parkour.util.ItemBuilder
import dev.slne.surf.parkour.util.MessageBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player

class ParkourSettings(player: Player) : ChestGui(
    5,
    ComponentHolder.of(MessageBuilder().primary("ᴇɪɴsᴛᴇʟʟᴜɴɢᴇɴ").build().decorate(TextDecoration.BOLD))
) {
    init {
        instance.launch {
            val playerData = DatabaseProvider.getPlayerData(player.uniqueId)
            val outlinePane = StaticPane(0, 0, 9, 5)
            val outlineItem = GuiItem(
                ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(Component.text(" ")).build()
            )

            for (x in 0 until 9) {
                outlinePane.addItem(outlineItem, x, 0)
                outlinePane.addItem(outlineItem, x, 4)
            }

            for (y in 1 until 4) {
                outlinePane.addItem(outlineItem, 0, y)
                outlinePane.addItem(outlineItem, 8, y)
            }

            val settingsPane = StaticPane(1, 1, 7, 3)
            val currentSoundToggleState = if (playerData.likesSound) {
                MessageBuilder().success("aktiviert").build()
            } else {
                MessageBuilder().error("deaktiviert").build()
            }

            val soundSettingsItem = GuiItem(
                ItemBuilder(Material.JUKEBOX)
                    .setName(MessageBuilder("Sound").build())
                    .addLoreLine(
                        MessageBuilder()
                            .info("Der Sound ist aktuell ")
                            .build()
                            .append(currentSoundToggleState)
                            .append(MessageBuilder().info(".").build())
                    )
                    .addLoreLine(MessageBuilder().info("Klicke, um die Einstellung zu ändern!").build())
                    .build()
            ) {
                playerData.edit { likesSound = !likesSound }
                if (playerData.likesSound) {
                    SurfParkour.send(
                        player,
                        MessageBuilder().primary("Die ParkourSounds sind nun ").success("aktiviert").primary(".")
                    )
                } else {
                    SurfParkour.send(
                        player,
                        MessageBuilder().primary("Die ParkourSounds sind nun ").error("deaktiviert").primary(".")
                    )
                }

                ParkourSettings(player) // update GUI because of currentSoundToggleState which is displayed
            }
            settingsPane.addItem(soundSettingsItem, 0, 0)


            addPane(outlinePane)
            addPane(settingsPane)

            setOnGlobalClick { it.isCancelled = true }
            setOnGlobalDrag { it.isCancelled = true }

            show(player)
        }
    }
}