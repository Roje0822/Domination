package xyz.chide1.domination.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.chide1.domination.object.DominationRegion;
import xyz.chide1.domination.util.DominationRegionUtil;

import java.util.Random;

public class DominationRegionCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c 버킷에서는 명령어를 사용 할 수 없습니다.");
            return false;
        }
        if (args.length == 0) {
            // TODO: 2024-05-27 설명
        }

        DominationRegionUtil util = DominationRegionUtil.getInstance();

        switch (args[0]) {
            case "설정" -> {
                if (args.length != 2) {
                    player.sendMessage("§c 잘못된 명령어 사용 입니다");
                    return false;
                }
                String name = args[1];
                util.createDominationRegion(name, player.getLocation());
                player.sendMessage("§a" + name + "§f 점령 지역을 설정하였습니다");
                return true;
            }
            case "제거" -> {
                if (args.length != 2) {
                    player.sendMessage("§c 잘못된 명령어 사용 입니다");
                    return false;
                }
                String name = args[1];
                util.deleteDominationRegion(name);
                player.sendMessage("§c" + name + "§f 점령 지역을 제거하였습니다");
                return true;
            }
            case "정보" -> {
                if (args.length != 2) {
                    player.sendMessage("§c 잘못된 명령어 사용 입니다");
                    return false;
                }
                String name = args[1];
                DominationRegion region = util.getRegion(name);
                player.sendMessage(
                        "§f 점령지 정보",
                        "§f이름 : " + region.getName(),
                        "§f소유 그룹 : " + region.getOwner(),
                        "§f점령 시각 : " + region.getOwnedTime(),
                        "§f좌표 : " + region.getCenter().toString()
                        );
                return true;
            }
            case "랜덤티피" -> {
                if (args.length != 2) {
                    player.sendMessage("§c 잘못된 명령어 사용 입니다");
                    return false;
                }
                String name = args[1];
                DominationRegion region = util.getRegion(name);
                Random random = new Random();
                Location center = region.getCenter();
                Location location = new Location(
                        center.getWorld(),
                        center.getBlockX() + random.nextInt(100) - 50,
                        center.getBlockY(),
                        center.getBlockZ() + random.nextInt(100) - 50
                );

                player.teleport(location);
                player.sendMessage("§a" + name + "§f 점령 지역으로 랜덤 티피 하였습니다");
                return true;
            }
        }


        return false;
    }
}
