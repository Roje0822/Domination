package xyz.chide1.domination;

import lombok.Getter;
import org.bukkit.boss.BossBar;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.chide1.domination.command.DominationRegionCommand;
import xyz.chide1.domination.listener.*;
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
        getServer().getPluginManager().registerEvents(new AddBossBarListener(), this);
        getServer().getPluginManager().registerEvents(new RemoveBossBarListener(), this);
        getServer().getPluginManager().registerEvents(new StartDominationListener(), this);
        getServer().getPluginManager().registerEvents(new CancelDominationListener(), this);
        getServer().getPluginManager().registerEvents(new AdditionalExperienceListener(), this);

        // Config
        saveDefaultConfig();

        // Data
        DominationRegionStorage.getInstance().loadAll();
    }

    @Override
    public void onDisable() {
        DominationRegionStorage.getInstance().saveAll();
        getServer().getBossBars().forEachRemaining(BossBar::removeAll);
    }
}
