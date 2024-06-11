package xyz.chide1.domination.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.boss.BarColor;

@AllArgsConstructor
@Getter
public enum Group {
    ARCANA(BarColor.YELLOW, "아르카나"),
    BELONICA(BarColor.PURPLE, "벨로니카");

    private final BarColor color;
    private final String groupName;
}
