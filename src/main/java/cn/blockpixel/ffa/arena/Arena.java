package cn.blockpixel.ffa.arena;

 import cn.blockpixel.ffa.FFA;
 import cn.blockpixel.ffa.listener.ffaListener;
 import cn.nukkit.Player;
 import cn.nukkit.Server;
 import cn.nukkit.item.Item;
 import cn.nukkit.item.enchantment.Enchantment;
 import cn.nukkit.level.Level;
 import cn.nukkit.level.Position;
 import cn.nukkit.utils.Config;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;

 public class Arena
 {
   private List<Player> players = new ArrayList<>();
   private Map<Player, HashMap<Integer, Item>> invSave = new HashMap<>();

   private Config config;
   private String arena;

   public Arena(String arena, Config config) {
     this.arena = arena;
     String levelName = config.getString(arena + ".world");
     this.level = Server.getInstance().getLevelByName(levelName);
     this.config = config;
     this.position = new Position(Integer.parseInt(config.getString(arena + ".spawn").split(":")[0]), Integer.parseInt(config.getString(arena + ".spawn").split(":")[1]), Integer.parseInt(config.getString(arena + ".spawn").split(":")[2]), this.level);
   }
   private Level level; private Position position;
   public void join(Player player) {
     if (!(FFA.getInstance().isPlayingInArenas(player))) {
       this.players.add(player);
     }else {
       player.sendMessage(this.config.getString(this.arena + ".message.already" , "You already in arena ,if you want to join others that need to quit the this arena.").replace("@player", player.getName()));
       return;
     }
     for (Player player1 : this.players) {
       player1.sendMessage(this.config.getString(this.arena + ".message.join").replace("@player", player.getName()));
     }
     player.teleport(this.position);
     player.getLevel().setRaining(false);
     player.getLevel().setTime(0);
     player.setGamemode(this.config.getInt(this.arena + ".gameMode"));
     player.setHealth(this.config.getInt(this.arena + ".initialHP"));
     HashMap<Integer, Item> abc = new HashMap<>();
     for (int i = 0; i < 36; i++) {
       abc.put(i, player.getInventory().getItem(i));
     }
     this.invSave.put(player, abc);
     player.getInventory().clearAll();
     for (String s : this.config.getStringList(this.arena + ".item")) {
       Item item = Item.get(this.config.getInt(this.arena + "." + s + ".id"), this.config.getInt(this.arena + "." + s + ".meta"), this.config.getInt(this.arena + "." + s + ".count"));
       for (String e : this.config.getStringList(this.arena + "." + s + ".enchantment")) {
         String[] enchantment = e.split(":");
         item.addEnchantment(Enchantment.get(Integer.parseInt(enchantment[0])).setLevel(Integer.parseInt(enchantment[1])));
       }
       player.getInventory().addItem(item);
     }
     for (String s : this.config.getStringList(this.arena + ".effect")) {
       player.addEffect(FFA.getInstance().strToEffect(s));
     }
     player.getInventory().setArmorItem(0, Item.get(this.config.getInt(this.arena + ".armor.head")));
     player.getInventory().setArmorItem(1, Item.get(this.config.getInt(this.arena + ".armor.breastplate")));
     player.getInventory().setArmorItem(2, Item.get(this.config.getInt(this.arena + ".armor.leg")));
     player.getInventory().setArmorItem(3, Item.get(this.config.getInt(this.arena + ".armor.boots")));
   }
   public void quit(Player player) {
     this.players.remove(player);
     player.getInventory().clearAll();
     for (Player player1 : this.players) {
       player1.sendMessage(this.config.getString(this.arena + ".message.quit").replace("@player", player.getName()));
     }
     for (int i = 0; i < 36; i++) {
       player.getInventory().setItem(i, (Item)((HashMap<?, ?>)this.invSave.get(player)).get(i));
     }
     this.invSave.remove(player);
     ffaListener.removeLastKiller(player);
     player.teleport(FFA.getInstance().getDefaultSpawn());
     player.setHealth(player.getMaxHealth());
     player.getFoodData().setLevel(player.getFoodData().getMaxLevel());
     player.removeAllEffects();
   }
   public int getArenaSize() {
     return this.players.size();
   }
   public boolean isPlaying(Player player) {
     if (this.players.contains(player)) {
       return true;
     }
     return false;
   }
   public void sendMsgToArena(String s) {
     for (Player player : this.players) {
       player.sendMessage("[" + FFA.getInstance().getPrefix() + "]" + s);
     }
   }

   public List<Player> getArenaPlayers() {
     return this.players;
   }
   public Config getConfig() {
     return this.config;
   }

   public String getArenaName() {
     return this.arena;
   }
 }

