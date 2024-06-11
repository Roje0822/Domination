package xyz.chide1.domination.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.chide1.domination.Domination;
import xyz.chide1.domination.object.DominationRegion;
import xyz.chide1.domination.object.Group;
import xyz.chide1.domination.storage.DominationRegionStorage;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DominationRegionUtil {
    
    @Getter
    private static final DominationRegionUtil instance = new DominationRegionUtil();

    private final Map<String, DominationRegion> dominationRegionMap;

    @Getter
    private static final Map<String, BukkitTask> regionBukkitTaskMap = new HashMap<>();

    @Getter
    private static final Map<String, BossBar> tempBossBarMap = new HashMap<>();
    
    private DominationRegionUtil() {
        dominationRegionMap = DominationRegionStorage.getDominationRegionMap();
    }
    
    public void createDominationRegion(String name, Location center) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(center.getWorld()));

        Location min = center.clone().add(-2, 0, -2);
        Location max = center.clone().add(2, 0, 2);
        min.setY(-64);
        max.setY(256);

        ProtectedCuboidRegion cuboidRegion = new ProtectedCuboidRegion(name, BukkitAdapter.asBlockVector(min), BukkitAdapter.asBlockVector(max));
        regionManager.addRegion(cuboidRegion);

        min = center.clone().add(-75, 0, -75);
        max = center.clone().add(75, 0, 75);
        min.setY(-64);
        max.setY(256);

        cuboidRegion = new ProtectedCuboidRegion(name + "pvp", BukkitAdapter.asBlockVector(min), BukkitAdapter.asBlockVector(max));
        cuboidRegion.setFlag(Flags.PVP, StateFlag.State.ALLOW);
        cuboidRegion.setFlag(Flags.GREET_TITLE, "점령 지역에 입장하였습니다.");
        regionManager.addRegion(cuboidRegion);
        BossBar bossBar = getBossBar(name, null);

        DominationRegion region = new DominationRegion(name, center, null, null, bossBar, null);
        dominationRegionMap.put(name, region);
    }
    
    public void deleteDominationRegion(String name) {
        if (!dominationRegionMap.containsKey(name)) return;

        DominationRegion dominationRegion = dominationRegionMap.get(name);
        dominationRegionMap.remove(name);

        dominationRegion.getBossBar().removeAll();

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(dominationRegion.getCenter().getWorld()));
        regionManager.removeRegion(name);
        regionManager.removeRegion(name + "pvp");
    }

    public void dominateRegion(String name, Group owner) {
        if (regionBukkitTaskMap.containsKey(name)) {
            regionBukkitTaskMap.get(name).cancel();
        }

        DominationRegion dominationRegion = dominationRegionMap.get(name);
        dominationRegion.getBossBar().setTitle(owner.getGroupName());
        dominationRegion.getBossBar().setColor(owner.getColor());

        dominationRegion.setOwner(owner);
        dominationRegion.setOwnedTime(LocalDateTime.now());
        dominationRegionMap.put(name, dominationRegion);

        BukkitTask bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                System.out.println("asdf");
                dominationRegion.setOwner(null);
                dominationRegion.setOwnedTime(null);
                BossBar bossBar = dominationRegion.getBossBar();
                bossBar.setTitle(name);
                bossBar.setColor(BarColor.WHITE);
                regionBukkitTaskMap.remove(name);
            }
        }.runTaskLater(Domination.getInstance(),20 * 60 * 60 + 10);

        new BukkitRunnable() {
            int timer = 0;
            @Override
            public void run() {
                if (!dominationRegionMap.containsKey(name))
                    this.cancel();
                if (dominationRegionMap.get(name).getOwner() != owner)
                    this.cancel();
                BossBar bossBar1 = dominationRegionMap.get(name).getBossBar();
                if (bossBar1.getColor().equals(BarColor.WHITE))
                    this.cancel();
                if (timer >= 3600)
                    this.cancel();
                bossBar1.setTitle(owner.getGroupName() + " " + timer++ + "초");
            }
        }.runTaskTimer(Domination.getInstance(), 0,  20);

        regionBukkitTaskMap.put(name, bukkitTask);
    }
    
    public DominationRegion getRegion(String name) {
        return dominationRegionMap.get(name);
    }

    public ProtectedRegion getPVPRegion(String name, World world) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(world);
        return regionManager.getRegion(name + "pvp");
    }

    public BossBar getBossBar(String name, Group group) {
        BossBar bossBar = Bukkit.createBossBar(
                Optional.ofNullable(group).map(Group::getGroupName).orElse(name),
                Optional.ofNullable(group).map(Group::getColor).orElse(BarColor.WHITE),
                BarStyle.SOLID);
        bossBar.setProgress(0);
        return bossBar;
    }

    public void setBossBarProgress(String name, int tick) {
        BossBar bossBar = dominationRegionMap.get(name).getBossBar();
        bossBar.setProgress((double) tick / 160);

        tempBossBarMap.put(name, bossBar);
    }

    public int getBossBarTimer(String name) {
        BossBar bossBar = dominationRegionMap.get(name).getBossBar();
        return (int) (bossBar.getProgress() * 160);
    }
}
