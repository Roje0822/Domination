package xyz.chide1.domination.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import net.raidstone.wgevents.events.RegionEnteredEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.chide1.domination.Domination;
import xyz.chide1.domination.object.DominationRegion;
import xyz.chide1.domination.object.Group;
import xyz.chide1.domination.storage.DominationRegionStorage;
import xyz.chide1.domination.util.DominationRegionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StartDominationListener implements Listener {

    @Getter
    private static final Map<UUID, BukkitTask> bukkitTaskMap = new HashMap<>();

    @EventHandler
    public void onEnter(RegionEnteredEvent event) {
        String regionName = event.getRegionName();
        Map<String, DominationRegion> dominationRegionMap = DominationRegionStorage.getDominationRegionMap();
        Player player = event.getPlayer();

        if (!dominationRegionMap.containsKey(regionName)) return;
        if (player == null) return;
        if (player.isOp()) return;

        ProtectedRegion region = event.getRegion();
        DominationRegion dominationRegion = dominationRegionMap.get(regionName);
        Group playerGroup = player.hasPermission("group.아르카나") ? Group.ARCANA : Group.BELONICA;
        DominationRegionUtil util = DominationRegionUtil.getInstance();
        ProtectedRegion pvpRegion = util.getPVPRegion(regionName, BukkitAdapter.adapt(player.getWorld()));

        if (dominationRegion.getOwner() != playerGroup) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!pvpRegion.contains(BukkitAdapter.asBlockVector(onlinePlayer.getLocation()))) continue;
                onlinePlayer.sendMessage(player.getName() + "님이 점령을 시작합니다");
            }
        }

        if (util.getBossBarTimer(regionName) == 160 && playerGroup != dominationRegion.getOwner())
            util.setBossBarProgress(regionName, 0);

        BukkitTask bukkitTask = new BukkitRunnable() {
            int timer = 0;

            @Override
            public void run() {
                if (dominationRegion.getOwner() == playerGroup) return;
                if (util.getBossBarTimer(regionName) < timer)
                    util.setBossBarProgress(regionName, timer);
                if (timer >= 160) {
                    player.sendTitle("점령을 완료하였습니다", "", 10, 30, 10);
                    bukkitTaskMap.remove(player.getUniqueId());
                    util.dominateRegion(regionName, playerGroup);
                    this.cancel();
                    return;
                }

                boolean isOtherGroupInRegion = false;
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (!region.contains(BukkitAdapter.asBlockVector(onlinePlayer.getLocation()))) continue;
                    Group group = onlinePlayer.hasPermission("group.아르카나") ? Group.ARCANA : Group.BELONICA;
                    if (playerGroup != group) {
                        isOtherGroupInRegion = true;
                    }
                }
                if (!isOtherGroupInRegion) timer++;
            }
        }.runTaskTimer(Domination.getInstance(), 1L, 1L);
        bukkitTaskMap.put(player.getUniqueId(), bukkitTask);
    }
}
