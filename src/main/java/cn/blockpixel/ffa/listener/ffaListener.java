package cn.blockpixel.ffa.listener;

import cn.blockpixel.ffa.FFA;
import cn.blockpixel.ffa.arena.Arena;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerFoodLevelChangeEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.item.Item;
import java.util.HashMap;
import java.util.Map;

public class ffaListener implements Listener {
  private static String lastDamageName;
  private static Map<Player, Player> lastKiller = new HashMap<>();
  public static void removeLastKiller(Player p) {
    lastKiller.remove(p);
  }
  @EventHandler
  public void onDamage(EntityDamageEvent e) {
    if (e.getEntity() instanceof Player) {
      Arena entry = FFA.getInstance().getArenaByName((Player)e.getEntity());
      if (entry == null)
        return;  Player player = (Player)e.getEntity();
      if (entry.isPlaying(player)) {
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
          e.setCancelled(FFA.getInstance().getConfig().getBoolean(entry.getArenaName() + ".events.fall"));
        }
        if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
          if (lastKiller.containsKey(e.getEntity())) {
            Player killer = lastKiller.get(e.getEntity());
            for (String s : entry.getConfig().getStringList(entry.getArenaName() + ".increase.item")) {
              Item item = Item.get(Integer.parseInt(s.split(":")[0]), Integer.valueOf(Integer.parseInt(s.split(":")[1])), Integer.parseInt(s.split(":")[2]));
              killer.getInventory().addItem(new Item[] { item });
            }
            for (String s : entry.getConfig().getStringList(entry.getArenaName() + ".increase.effect")) {
              killer.addEffect(FFA.getInstance().strToEffect(s));
            }
            for (String s : entry.getConfig().getStringList(entry.getArenaName() + ".increase.command")) {
              if (player.isOp()) player.sendMessage("aaa");
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
      if (entry.isPlaying(player))
        e.setCancelled(FFA.getInstance().getConfig().getBoolean(entry.getArenaName() + ".events.reHealth"));
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
          for (String s : entry.getConfig().getStringList(entry.getArenaName() + ".increase.item")) {
            Item item = Item.get(Integer.parseInt(s.split(":")[0]), Integer.parseInt(s.split(":")[1]), Integer.parseInt(s.split(":")[2]));
            player.getInventory().addItem(item);
          }
          for (String s : entry.getConfig().getStringList(entry.getArenaName() + ".increase.effect")) {
            player.addEffect(FFA.getInstance().strToEffect(s));
          }
          for (String s : entry.getConfig().getStringList(entry.getArenaName() + ".increase.command")) {
            if (player.isOp()) player.sendMessage("aaa");
            String[] cmd = s.split("&");
            if (cmd.length > 1 && "con".equals(cmd[1])) {
              FFA.getInstance().getServer().dispatchCommand((CommandSender)FFA.getInstance().getServer().getConsoleSender(), cmd[0].replace("@p", lastDamageName)); continue;
            }
            FFA.getInstance().getServer().dispatchCommand((CommandSender)player, cmd[0].replace("@p", lastDamageName));
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
    for (Map.Entry<String, Arena> entry : (Iterable<Map.Entry<String, Arena>>)FFA.getInstance().getArenas().entrySet()) {
      if (((Arena)entry.getValue()).isPlaying(player)) {
        ((Arena)entry.getValue()).getArenaPlayers().remove(player);
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
    if (entry == null)
      return;  if (entry.isPlaying(e.getPlayer()) &&
      e.getMessage().equals("hub")) {
      e.setCancelled();
      entry.quit(e.getPlayer());
    }
  }
}
