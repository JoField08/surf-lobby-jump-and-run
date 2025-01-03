package dev.slne.surf.lobby.jar;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import dev.slne.surf.lobby.jar.command.ParkourCommand;
import dev.slne.surf.lobby.jar.command.subcommand.ParkourStatsCommand;
import dev.slne.surf.lobby.jar.config.PluginConfig;
import dev.slne.surf.lobby.jar.listener.ParkourListener;
import dev.slne.surf.lobby.jar.listener.PlayerKickListener;
import dev.slne.surf.lobby.jar.mysql.Database;
import dev.slne.surf.lobby.jar.papi.ParkourPlaceholderExtension;
import dev.slne.surf.lobby.jar.util.PluginColor;

import lombok.Getter;
import lombok.experimental.Accessors;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Accessors(fluent = true)
public class PluginInstance extends JavaPlugin {
  private JumpAndRunProvider jumpAndRunProvider;
  private WorldEditPlugin worldEditInstance;
  private boolean worldedit;


  @Getter
  private static final Component prefix = Component.text(">> ", NamedTextColor.GRAY)
      .append(Component.text("Parkour", PluginColor.BLUE_LIGHT))
      .append(Component.text(" | ", NamedTextColor.DARK_GRAY));

  @Override
  public void onEnable() {
    this.jumpAndRunProvider = new JumpAndRunProvider();
    this.jumpAndRunProvider.startActionbar();

    this.handlePlaceholderAPI();
    this.handeWorldEdit();

    new ParkourCommand("parkour").register();
    new ParkourStatsCommand("stats").register();

    Bukkit.getPluginManager().registerEvents(new ParkourListener(), this);
    Bukkit.getPluginManager().registerEvents(new PlayerKickListener(), this);
    Database.createConnection();
  }

  @Override
  public void onDisable() {
    this.jumpAndRunProvider.stopActionbar();
    this.jumpAndRunProvider.saveAll().join();

    Database.closeConnection();
    PluginConfig.save(jumpAndRunProvider.jumpAndRun());
  }

  public static PluginInstance instance() {
    return getPlugin(PluginInstance.class);
  }

  private void handlePlaceholderAPI(){
    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      new ParkourPlaceholderExtension().register();
    }
  }

  private void handeWorldEdit(){
    this.worldedit = Bukkit.getPluginManager().isPluginEnabled("WorldEdit");
    this.worldEditInstance = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
  }
}
