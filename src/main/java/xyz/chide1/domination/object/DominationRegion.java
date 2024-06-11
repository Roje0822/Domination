package xyz.chide1.domination.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.boss.BossBar;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class DominationRegion {
    private String name;
    private Location center;
    private Group owner;
    private LocalDateTime ownedTime;
    private BossBar bossBar;
    private List<String> monsterNames;
}
