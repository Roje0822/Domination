package xyz.chide1.domination;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.chide1.domination.command.DominationRegionCommand;
import xyz.chide1.domination.listener.EnterDominationRegionListener;
import xyz.chide1.domination.storage.DominationRegionStorage;

public class Domination extends JavaPlugin {

    @Getter
    private static Domination instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {

        // Command
        getCommand("점령지역").setExecutor(new DominationRegionCommand());

        // Listener
        getServer().getPluginManager().registerEvents(new EnterDominationRegionListener(), this);

        // Config
        saveDefaultConfig();

        // Data
        DominationRegionStorage.getInstance().loadAll();
    }

    @Override
    public void onDisable() {
        DominationRegionStorage.getInstance().saveAll();
    }
}
