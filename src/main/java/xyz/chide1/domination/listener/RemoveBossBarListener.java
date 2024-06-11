package xyz.chide1.domination.listener;

import net.raidstone.wgevents.events.RegionLeftEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.chide1.domination.object.DominationRegion;
import xyz.chide1.domination.storage.DominationRegionStorage;

import java.util.Map;

public class RemoveBossBarListener implements Listener {

    @EventHandler
    public void onQuit(RegionLeftEvent event) {
        String regionName = event.getRegionName();
        Map<String, DominationRegion> dominationRegionMap = DominationRegionStorage.getDominationRegionMap();
        Player player = event.getPlayer();

        if (!regionName.contains("pvp")) return;
        if (!dominationRegionMap.containsKey(regionName.replace("pvp", ""))) return;
        if (player == null) return;

        player.sendTitle("점령지역을 벗어납니다", "", 10, 30, 10);
        DominationRegion dominationRegion = dominationRegionMap.get(regionName.replace("pvp", ""));
        dominationRegion.getBossBar().removePlayer(player);
    }
}
