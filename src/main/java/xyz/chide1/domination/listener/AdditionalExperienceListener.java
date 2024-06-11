package xyz.chide1.domination.listener;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.EXPSource;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import xyz.chide1.domination.object.DominationRegion;
import xyz.chide1.domination.object.Group;
import xyz.chide1.domination.storage.DominationRegionStorage;

import java.util.Map;

public class AdditionalExperienceListener implements Listener {

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer == null) return;
        Group playerGroup = killer.hasPermission("group.아르카나") ? Group.ARCANA : Group.BELONICA;
        String entityName = ChatColor.stripColor(entity.getName());
        Map<String, DominationRegion> dominationRegionMap = DominationRegionStorage.getDominationRegionMap();

        if (!MythicBukkit.inst().getMobManager().isMythicMob(entity)) return;

        for (String regionName : dominationRegionMap.keySet()) {
            DominationRegion dominationRegion = dominationRegionMap.get(regionName);
            if (dominationRegion.getMonsterNames() == null) continue;
            if (dominationRegion.getMonsterNames().contains(entityName) && playerGroup == dominationRegion.getOwner()) {
                System.out.println(event.getDroppedExp());
                double additionalExp = event.getDroppedExp() * 0.3;
                PlayerData playerData = PlayerData.get(killer);
                playerData.giveExperience(additionalExp, EXPSource.SOURCE);
            }
        }
    }
}
