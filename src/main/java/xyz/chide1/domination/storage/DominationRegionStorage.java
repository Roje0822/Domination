package xyz.chide1.domination.storage;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.chide1.domination.Domination;
import xyz.chide1.domination.object.DominationRegion;
import xyz.chide1.domination.util.DominationRegionUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DominationRegionStorage {

    @Getter
    private static final DominationRegionStorage instance = new DominationRegionStorage();

    @Getter
    private static final Map<String, DominationRegion> dominationRegionMap = new HashMap<>();

    private final File REGION_DIRECTORY = new File(Domination.getInstance().getDataFolder(), "region");

    private DominationRegionStorage() {}

    public void saveAll() {
        if (!REGION_DIRECTORY.exists()) REGION_DIRECTORY.mkdir();
        if (dominationRegionMap == null) return;
        dominationRegionMap.keySet().forEach(this::save);
        for (File file : REGION_DIRECTORY.listFiles()) {
            if (!dominationRegionMap.containsKey(file.getName().replace(".yml", ""))) file.delete();
        }
    }

    public void loadAll() {
        if (!REGION_DIRECTORY.exists()) return;
        for (File file : Objects.requireNonNull(REGION_DIRECTORY.listFiles())) {
            load(file);
        }
    }

    private void save(String name) {
        File regionFile = new File(REGION_DIRECTORY, name + ".yml");
        if (!regionFile.exists()) {
            try {
                regionFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        DominationRegion dominationRegion = dominationRegionMap.get(name);
        YamlConfiguration ymlFile = YamlConfiguration.loadConfiguration(regionFile);
        ymlFile.set("name", dominationRegion.getName());
        ymlFile.set("center", dominationRegion.getCenter());
        ymlFile.set("monsters", dominationRegion.getMonsterNames());

        try {
            ymlFile.save(regionFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load(File file) {
        YamlConfiguration ymlFile = YamlConfiguration.loadConfiguration(file);
        String name = ymlFile.getString("name");
        Location center = ymlFile.getLocation("center");
        BossBar bossBar = DominationRegionUtil.getInstance().getBossBar(name, null);
        List<String> monsterNames = ymlFile.getStringList("monsters");

        DominationRegion dominationRegion = new DominationRegion(name, center, null, null, bossBar, monsterNames);
        dominationRegionMap.put(name, dominationRegion);
    }

}
