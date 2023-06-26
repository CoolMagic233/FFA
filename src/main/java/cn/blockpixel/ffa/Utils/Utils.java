package cn.blockpixel.ffa.Utils;

import cn.blockpixel.ffa.arena.Arena;
import cn.nukkit.Player;

import java.util.List;

public class Utils {
    public static List<Cooldown> addPlayerToCoolDowns(Player player, List<Cooldown> list, Arena arena){
        Cooldown cooldown1 = new Cooldown(player,arena.getConfig().getInt("setting.cooldown.ender_pearl.cooldown_time"));
        list.add(cooldown1);
        return list;
    }
    public static Cooldown getCoolDowns(Player player, List<Cooldown> list){
        for (Cooldown cooldown : list) {
            if(cooldown.getPlayer().getName().equals(player.getName())){
                return cooldown;
            }
        }
        return null;
    }
}
