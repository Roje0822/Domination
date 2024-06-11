package xyz.chide1.domination.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.raidstone.wgevents.events.RegionEnteredEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.chide1.domination.object.DominationRegion;
import xyz.chide1.domination.storage.DominationRegionStorage;

import java.util.Map;

public class AddBossBarListener implements Listener {

    @EventHandler
    public void onEnter(RegionEnteredEvent event) {
        String regionName = event.getRegionName();
        Map<String, DominationRegion> dominationRegionMap = DominationRegionStorage.getDominationRegionMap();
        Player player = event.getPlayer();

        if (player == null) return;
        if (!regionName.contains("pvp")) return;
        if (!dominationRegionMap.containsKey(regionName.replace("pvp", ""))) return;

        DominationRegion dominationRegion = dominationRegionMap.get(regionName.replace("pvp", ""));
        dominationRegion.getBossBar().addPlayer(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(
                BukkitAdapter.adapt(event.getPlayer().getWorld()));
        Map<String, DominationRegion> dominationRegionMap = DominationRegionStorage.getDominationRegionMap();

        regionManager.getRegions().forEach((regionName, protectedRegion) -> {
            if (!regionName.contains("pvp")) return;
            if (!dominationRegionMap.containsKey(regionName.replace("pvp", ""))) return;

            DominationRegion dominationRegion = dominationRegionMap.get(regionName.replace("pvp", ""));
            dominationRegion.getBossBar().addPlayer(event.getPlayer());
        });
    }
}
