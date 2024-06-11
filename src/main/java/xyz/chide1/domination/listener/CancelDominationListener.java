package xyz.chide1.domination.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.raidstone.wgevents.events.RegionLeftEvent;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import xyz.chide1.domination.object.DominationRegion;
import xyz.chide1.domination.object.Group;
import xyz.chide1.domination.storage.DominationRegionStorage;
import xyz.chide1.domination.util.DominationRegionUtil;

import java.util.Map;
import java.util.UUID;

public class CancelDominationListener implements Listener {
    @EventHandler
    public void onLeft(RegionLeftEvent event) {
        Map<UUID, BukkitTask> bukkitTaskMap = StartDominationListener.getBukkitTaskMap();
        UUID uuid = event.getUUID();
        Player player = event.getPlayer();

        if (player == null) return;
        if (!bukkitTaskMap.containsKey(uuid)) return;

        String regionName = event.getRegionName();
        Group playerGroup = player.hasPermission("group.아르카나") ? Group.ARCANA : Group.BELONICA;
        ProtectedRegion pvpRegion = DominationRegionUtil.getInstance().getPVPRegion(regionName, BukkitAdapter.adapt(player.getWorld()));

        bukkitTaskMap.get(uuid).cancel();
        bukkitTaskMap.remove(uuid);

        DominationRegion dominationRegion = DominationRegionStorage.getDominationRegionMap().get(regionName);
        BossBar bossBar = dominationRegion.getBossBar();

        if (dominationRegion.getOwner() != playerGroup) {
            bossBar.setProgress(0);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!pvpRegion.contains(BukkitAdapter.asBlockVector(onlinePlayer.getLocation()))) continue;
                onlinePlayer.sendMessage(player.getName() + "님이 점령을 취소했습니다");
            }
        }
    }
}