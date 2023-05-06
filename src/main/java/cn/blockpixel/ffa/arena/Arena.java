/*     */ package cn.blockpixel.ffa.arena;
/*     */ 
/*     */ import cn.blockpixel.ffa.FFA;
/*     */ import cn.blockpixel.ffa.listener.ffaListener;
/*     */ import cn.nukkit.Player;
/*     */ import cn.nukkit.Server;
/*     */ import cn.nukkit.item.Item;
/*     */ import cn.nukkit.item.enchantment.Enchantment;
/*     */ import cn.nukkit.level.Level;
/*     */ import cn.nukkit.level.Position;
/*     */ import cn.nukkit.utils.Config;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class Arena
/*     */ {
/*  19 */   private List<Player> players = new ArrayList<>();
/*  20 */   private Map<Player, HashMap<Integer, Item>> invSave = new HashMap<>();
/*     */   
/*     */   private Config config;
/*     */   private String arena;
/*     */   
/*     */   public Arena(String arena, Config config) {
/*  26 */     this.arena = arena;
/*  27 */     String levelName = config.getString(arena + ".world");
/*  28 */     this.level = Server.getInstance().getLevelByName(levelName);
/*  29 */     this.config = config;
/*  30 */     this.position = new Position(Integer.parseInt(config.getString(arena + ".spawn").split(":")[0]), Integer.parseInt(config.getString(arena + ".spawn").split(":")[1]), Integer.parseInt(config.getString(arena + ".spawn").split(":")[2]), this.level);
/*     */   }
/*     */   private Level level; private Position position;
/*     */   public void join(Player player) {
/*  34 */     if (!this.players.contains(player)) {
/*  35 */       this.players.add(player);
/*     */     }
/*  37 */     for (Player player1 : this.players) {
/*  38 */       player1.sendMessage(this.config.getString(this.arena + ".message.join").replace("@player", player.getName()));
/*     */     }
/*  40 */     player.teleport(this.position);
/*  41 */     player.getLevel().setRaining(false);
/*  42 */     player.getLevel().setTime(0);
/*  43 */     player.setGamemode(this.config.getInt(this.arena + ".gameMode"));
/*  44 */     player.setHealth(this.config.getInt(this.arena + ".initialHP"));
/*  45 */     HashMap<Integer, Item> abc = new HashMap<>();
/*  46 */     for (int i = 0; i < 36; i++) {
/*  47 */       abc.put(Integer.valueOf(i), player.getInventory().getItem(i));
/*     */     }
/*  49 */     this.invSave.put(player, abc);
/*  50 */     player.getInventory().clearAll();
/*  51 */     for (String s : this.config.getStringList(this.arena + ".item")) {
/*  52 */       Item item = Item.get(this.config.getInt(this.arena + "." + s + ".id"), Integer.valueOf(this.config.getInt(this.arena + "." + s + ".meta")), this.config.getInt(this.arena + "." + s + ".count"));
/*  53 */       for (String e : this.config.getStringList(this.arena + "." + s + ".enchantment")) {
/*  54 */         String[] enchantment = e.split(":");
/*  55 */         item.addEnchantment(new Enchantment[] { Enchantment.get(Integer.parseInt(enchantment[0])).setLevel(Integer.parseInt(enchantment[1])) });
/*     */       } 
/*  57 */       player.getInventory().addItem(new Item[] { item });
/*     */     } 
/*  59 */     for (String s : this.config.getStringList(this.arena + ".effect")) {
/*  60 */       player.addEffect(FFA.getInstance().strToEffect(s));
/*     */     }
/*  62 */     player.getInventory().setArmorItem(0, Item.get(this.config.getInt(this.arena + ".armor.head")));
/*  63 */     player.getInventory().setArmorItem(1, Item.get(this.config.getInt(this.arena + ".armor.breastplate")));
/*  64 */     player.getInventory().setArmorItem(2, Item.get(this.config.getInt(this.arena + ".armor.leg")));
/*  65 */     player.getInventory().setArmorItem(3, Item.get(this.config.getInt(this.arena + ".armor.boots")));
/*     */   }
/*     */   public void quit(Player player) {
/*  68 */     this.players.remove(player);
/*  69 */     player.getInventory().clearAll();
/*  70 */     for (Player player1 : this.players) {
/*  71 */       player1.sendMessage(this.config.getString(this.arena + ".message.quit").replace("@player", player.getName()));
/*     */     }
/*  73 */     for (int i = 0; i < 36; i++) {
/*  74 */       player.getInventory().setItem(i, (Item)((HashMap)this.invSave.get(player)).get(Integer.valueOf(i)));
/*     */     }
/*  76 */     this.invSave.remove(player);
/*  77 */     ffaListener.removeLastKiller(player);
/*  78 */     player.teleport(FFA.getInstance().getDefaultSpawn());
/*  79 */     player.setHealth(player.getMaxHealth());
/*  80 */     player.getFoodData().setLevel(player.getFoodData().getMaxLevel());
/*  81 */     player.removeAllEffects();
/*     */   }
/*     */   public int getArenaSize() {
/*  84 */     return this.players.size();
/*     */   }
/*     */   public boolean isPlaying(Player player) {
/*  87 */     if (this.players.contains(player)) {
/*  88 */       return true;
/*     */     }
/*  90 */     return false;
/*     */   }
/*     */   public void sendMsgToArena(String s) {
/*  93 */     for (Player player : this.players) {
/*  94 */       player.sendMessage("[" + FFA.getInstance().getPrefix() + "]" + s);
/*     */     }
/*     */   }
/*     */   
/*     */   public List<Player> getArenaPlayers() {
/*  99 */     return this.players;
/*     */   }
/*     */   public Config getConfig() {
/* 102 */     return this.config;
/*     */   }
/*     */   
/*     */   public String getArenaName() {
/* 106 */     return this.arena;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\BPFFA.jar!\cn\blockpixel\ffa\arena\Arena.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */