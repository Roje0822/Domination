package xyz.chide1.domination.listener;

import net.raidstone.wgevents.events.RegionEnteredEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EnterDominationRegionListener implements Listener {

    @EventHandler
    public void onEnter(RegionEnteredEvent event) {
        System.out.println(event.getRegionName() + "entered");
    }
}
