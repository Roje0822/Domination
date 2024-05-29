package xyz.chide1.domination.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import lombok.Getter;
import org.bukkit.Location;
import xyz.chide1.domination.object.DominationRegion;
import xyz.chide1.domination.storage.DominationRegionStorage;

import java.util.Map;

public class DominationRegionUtil {
    
    @Getter
    private static final DominationRegionUtil instance = new DominationRegionUtil();
    private final Map<String, DominationRegion> dominationRegionMap;
    
    private DominationRegionUtil() {
        dominationRegionMap = DominationRegionStorage.getDominationRegionMap();
    }
    
    public void createDominationRegion(String name, Location center) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(center.getWorld()));

        Location min = center.clone().add(-2, -1, -2);
        Location max = center.clone().add(2, 4, 2);
        ProtectedCuboidRegion cuboidRegion = new ProtectedCuboidRegion(name, BukkitAdapter.asBlockVector(min), BukkitAdapter.asBlockVector(max));
        regionManager.addRegion(cuboidRegion);

        min = center.clone().add(-150, -10, -150);
        max = center.clone().add(150, 100, 150);
        cuboidRegion = new ProtectedCuboidRegion(name + "PVP", BukkitAdapter.asBlockVector(min), BukkitAdapter.asBlockVector(max));
        cuboidRegion.setFlag(Flags.PVP, StateFlag.State.ALLOW);
        cuboidRegion.setFlag(Flags.GREET_TITLE, "점령 지역에 입장하였습니다.");
        regionManager.addRegion(cuboidRegion);

        DominationRegion region = new DominationRegion(name, center, null, null);
        dominationRegionMap.put(name, region);
    }
    
    public void deleteDominationRegion(String name) {
        DominationRegion dominationRegion = dominationRegionMap.get(name);
        dominationRegionMap.remove(name);

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(dominationRegion.getCenter().getWorld()));
        regionManager.removeRegion(name);

    }
    
    public DominationRegion getRegion(String name) {
        return dominationRegionMap.get(name);
    }
}
