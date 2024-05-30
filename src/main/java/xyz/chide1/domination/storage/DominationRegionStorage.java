package xyz.chide1.domination.storage;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.chide1.domination.Domination;
import xyz.chide1.domination.object.DominationRegion;
import xyz.chide1.domination.object.Group;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class DominationRegionStorage {

    @Getter
    private static final DominationRegionStorage instance = new DominationRegionStorage();

    @Getter
    private static final Map<String, DominationRegion> dominationRegionMap = new HashMap<>();

    private final File REGION_DIRECTORY = new File(Domination.getInstance().getDataFolder(), "region");

    private DominationRegionStorage() {}

    public void saveAll() {
        if (!REGION_DIRECTORY.exists()) REGION_DIRECTORY.mkdir();
        dominationRegionMap.keySet().forEach(this::save);
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
        ymlFile.set("owner", Optional.ofNullable(dominationRegion.getOwner())
                .map(Enum::name)
                .orElse(null));
        ymlFile.set("ownedTime", Optional.ofNullable(dominationRegion.getOwnedTime())
                .map(Time::toString)
                .orElse(null));

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
        Group group = Optional.ofNullable(ymlFile.getString("owner"))
                .map(Group::valueOf)
                .orElse(null);
        Time ownedTime = Optional.ofNullable(ymlFile.getString("ownedTime"))
                .map(Time::valueOf)
                .orElse(null);
        DominationRegion dominationRegion = new DominationRegion(name, center, group, ownedTime);
        dominationRegionMap.put(name, dominationRegion);
    }

}
