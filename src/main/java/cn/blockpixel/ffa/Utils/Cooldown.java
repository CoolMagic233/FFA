package cn.blockpixel.ffa.Utils;

import cn.nukkit.Player;

public class Cooldown {
    private Player player;
    private int cooldown;

    public Cooldown(Player player, int cooldown) {
        this.player = player;
        this.cooldown = cooldown;
    }

    public boolean update(){
        if(cooldown >= 1){
            cooldown--;
            return false;
        }
        return true;
    }

    public Player getPlayer() {
        return player;
    }

    public int getCooldown() {
        return cooldown;
    }
}
