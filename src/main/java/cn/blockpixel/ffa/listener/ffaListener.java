package cn.blockpixel.ffa.listener;

import cn.blockpixel.ffa.FFA;
import cn.blockpixel.ffa.Utils.Cooldown;
import cn.blockpixel.ffa.Utils.Utils;
import cn.blockpixel.ffa.arena.Arena;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemEnderPearl;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.scheduler.Task;

import java.util.*;

public class ffaListener implements Listener {
  private static String lastDamageName;
  private static Map<Player, Player> lastKiller = new HashMap<>();
  public static void removeLastKiller(Player p) {
    lastKiller.remove(p);
  }
  private List<Cooldown> cooldown = new ArrayList<>();

  public ffaListener() {
     FFA.getInstance().getServer().getScheduler().scheduleRepeatingTask(new Task() {
      @Override
      public void onRun(int i) {
        Iterator<Cooldown> iterator = cooldown.iterator();
        while (iterator.hasNext()){
          Cooldown cooldown1 = iterator.next();
          cooldown1.update();
          if(cooldown1.getCooldown() <= 0){
            iterator.remove();
          }
        }
      }
    },20);
  }

  @EventHandler
  public void onDamage(EntityDamageEvent e) {
    if (e.getEntity() instanceof Player) {
      Arena entry = FFA.getInstance().getArenaByName((Player)e.getEntity());
      if (entry == null) return;
      Player player = (Player)e.getEntity();
      if (entry.isPlaying(player)) {
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
          e.setCancelled(FFA.getInstance().getConfig().getBoolean(entry.getArenaName() + ".events.fall"));
        }
        if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
          if (lastKiller.containsKey(e.getEntity())) {
            Player killer = lastKiller.get(e.getEntity());
            for (String s : entry.getConfig().getStringList(entry.getArenaName() + ".increase.item")) {
              Item item = Item.get(Integer.parseInt(s.split(":")[0]), Integer.parseInt(s.split(":")[1]), Integer.parseInt(s.split(":")[2]));
              killer.getInventory().addItem(item);
            }
            for (String s : entry.getConfig().getStringList(entry.getArenaName() + ".increase.effect")) {
              killer.addEffect(FFA.getInstance().strToEffect(s));
            }
            for (String s : entry.getConfig().getStringList(entry.getArenaName() + ".increase.command")) {
              String[] cmd = s.split("&");
              if (cmd.length > 1 && "con".equals(cmd[1])) {
                FFA.getInstance().getServer().dispatchCommand((CommandSender)FFA.getInstance().getServer().getConsoleSender(), cmd[0].replace("@p", killer.getName())); continue;
              }
              FFA.getInstance().getServer().dispatchCommand((CommandSender)player, cmd[0].replace("@p", killer.getName()));
            }
            entry.sendMsgToArena(entry.getConfig().getString(entry.getArenaName() + ".message.death").replace("@d", killer.getName()).replace("@player", player.getName()).replace("@hp", String.valueOf(player.getHealth())));
          }
          e.setCancelled();
          entry.quit(player);
        }
      }
    }
  }
  @EventHandler
  public void onReHealth(EntityRegainHealthEvent e) {
    if (e.getEntity() instanceof Player) {
      Arena entry = FFA.getInstance().getArenaByName((Player)e.getEntity());
      if (entry == null)
        return;  Player player = (Player)e.getEntity();
      if (entry.isPlaying(player)){
        if(e.getRegainReason() != EntityRegainHealthEvent.CAUSE_MAGIC){
          e.setCancelled(FFA.getInstance().getConfig().getBoolean(entry.getArenaName() + ".events.reHealth"));
        }
      }
    }
  }

  @EventHandler
  public void onUse(PlayerInteractEvent e){
    Arena arena = FFA.getInstance().getArenaByName(e.getPlayer());
    if (arena == null) return;
    if(arena.getConfig().getBoolean("setting.cooldown.ender_pearl.enable")){
      if(e.getItem().getId() == new ItemEnderPearl().getId()){
         if(arena.getConfig().getStringList("setting.cooldown.ender_pearl.enable_world").contains(arena.getArenaName())){
            if(Utils.getCoolDowns(e.getPlayer(),cooldown) == null){
              cooldown.add(new Cooldown(e.getPlayer(),arena.getConfig().getInt("setting.cooldown.ender_pearl.cooldown_time")));
              return;
            }
            if(Utils.getCoolDowns(e.getPlayer(),cooldown).getCooldown() >= 1){
              e.getPlayer().sendMessage(arena.getConfig().getString(arena.getArenaName() + ".message.cooldown_ender_pearl"," "));
              e.setCancelled();
            }
         }
      }
    }
  }

  @EventHandler
  public void onDtoD(EntityDamageByEntityEvent e) {
    if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
      Player player = (Player)e.getDamager();
      lastKiller.put((Player)e.getEntity(), player);
      lastDamageName = ((Player)lastKiller.get(e.getEntity())).getName();
      Arena entry = FFA.getInstance().getArenaByName((Player)e.getEntity());
      if(entry == null) return;
      if (FFA.getInstance().getConfig().getBoolean(entry.getArenaName() + ".events.hurt")) {
        e.setDamage(0.0F);
      }
      if (e.getFinalDamage() + 1.0F >= e.getEntity().getHealth()) {
          if (entry.isPlaying(player)) {
            //flush inventory
            player.getInventory().clearAll();
            for (String s : entry.getConfig().getStringList(entry.getArenaName() + ".item")) {
              Item item = Item.get(entry.getConfig().getInt(entry.getArenaName() + "." + s + ".id"), entry.getConfig().getInt(entry.getArenaName() + "." + s + ".meta"), entry.getConfig().getInt(entry.getArenaName() + "." + s + ".count"));
              for (String en : entry.getConfig().getStringList(entry.getArenaName() + "." + s + ".enchantment")) {
                String[] enchantment = en.split(":");
                item.addEnchantment(Enchantment.get(Integer.parseInt(enchantment[0])).setLevel(Integer.parseInt(enchantment[1])));
              }
              player.getInventory().addItem(item);
            }
            //flush armor
            player.getInventory().setArmorItem(0, Item.get(entry.getConfig().getInt(entry.getArenaName() + ".armor.head")));
            player.getInventory().setArmorItem(1, Item.get(entry.getConfig().getInt(entry.getArenaName()+ ".armor.breastplate")));
            player.getInventory().setArmorItem(2, Item.get(entry.getConfig().getInt(entry.getArenaName() + ".armor.leg")));
            player.getInventory().setArmorItem(3, Item.get(entry.getConfig().getInt(entry.getArenaName() + ".armor.boots")));
            //player increase
          for (String s : entry.getConfig().getStringList(entry.getArenaName() + ".increase.item")) {
            Item item = Item.get(Integer.parseInt(s.split(":")[0]), Integer.parseInt(s.split(":")[1]), Integer.parseInt(s.split(":")[2]));
            player.getInventory().addItem(item);
          }
          for (String s : entry.getConfig().getStringList(entry.getArenaName() + ".increase.effect")) {
            player.addEffect(FFA.getInstance().strToEffect(s));
          }
          // kill and death command
          for (String s : entry.getConfig().getStringList(entry.getArenaName() + ".increase.command")) {
            String[] cmd = s.split("&");
            if (cmd.length > 1 && "con".equals(cmd[1])) {
              FFA.getInstance().getServer().dispatchCommand((CommandSender)FFA.getInstance().getServer().getConsoleSender(), cmd[0].replace("@p", lastDamageName)); continue;
            }
            FFA.getInstance().getServer().dispatchCommand((CommandSender)player, cmd[0].replace("@p", lastDamageName));
          }
          for (String s : entry.getConfig().getStringList(entry.getArenaName() + ".death.command")) {
            String[] cmd = s.split("&");
            if (cmd.length > 1 && "con".equals(cmd[1])) {
              FFA.getInstance().getServer().dispatchCommand((CommandSender)FFA.getInstance().getServer().getConsoleSender(), cmd[0].replace("@p", e.getEntity().getName())); continue;
            }
            FFA.getInstance().getServer().dispatchCommand((CommandSender)player, cmd[0].replace("@p", e.getEntity().getName()));
          }

          String die = null;
          Player diePlayer = null;
          if (e.getEntity() instanceof Player) {
            die = e.getEntity().getName();
            diePlayer = (Player)e.getEntity();
          }
          assert die != null;
          e.setCancelled();
          entry.sendMsgToArena(entry.getConfig().getString(entry.getArenaName() + ".message.death").replace("@d", lastDamageName).replace("@player", die).replace("@hp", String.valueOf(e.getDamager().getHealth())));
          entry.quit(diePlayer);
        }
      }
    }
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    Player player = e.getPlayer();
    for (Map.Entry<String, Arena> entry : FFA.getInstance().getArenas().entrySet()) {
      if (entry.getValue().isPlaying(player)) {
        entry.getValue().getArenaPlayers().remove(player);
      }
    }
  }

  @EventHandler
  public void FoodChange(PlayerFoodLevelChangeEvent e) {
    Arena entry = FFA.getInstance().getArenaByName(e.getPlayer());
    if (entry == null)
      return;  if (entry.isPlaying(e.getPlayer()))
      e.setCancelled(FFA.getInstance().getConfig().getBoolean(entry.getArenaName() + ".events.food"));
  }

  @EventHandler
  public void onChat(PlayerChatEvent e) {
    Arena entry = FFA.getInstance().getArenaByName(e.getPlayer());
    if (entry == null) return;
    String message = e.getMessage();
    entry.sendMsgToArena("<"+e.getPlayer().getName()+"> "+message);
    e.setCancelled();
  }
  @EventHandler
  public void onRespawn(PlayerRespawnEvent e){
    Arena entry = FFA.getInstance().getArenaByName(e.getPlayer());
    if (entry == null) return;
    if(FFA.getInstance().isPlayingInArenas(e.getPlayer())){
      entry.quit(e.getPlayer());
    }
  }
}
