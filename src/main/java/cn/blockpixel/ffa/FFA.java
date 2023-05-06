/*    */ package cn.blockpixel.ffa;
/*    */ 
/*    */ import cn.blockpixel.ffa.arena.Arena;
/*    */ import cn.blockpixel.ffa.command.ffaCommand;
/*    */ import cn.blockpixel.ffa.form.formHelper;
/*    */ import cn.blockpixel.ffa.listener.ffaListener;
/*    */ import cn.nukkit.Player;
/*    */ import cn.nukkit.command.Command;
/*    */ import cn.nukkit.event.Listener;
/*    */ import cn.nukkit.level.Position;
/*    */ import cn.nukkit.plugin.Plugin;
/*    */ import cn.nukkit.plugin.PluginBase;
/*    */ import cn.nukkit.potion.Effect;
/*    */ import java.io.File;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class FFA extends PluginBase {
/* 19 */   private final Map<String, Arena> arenas = new HashMap<>();
/*    */   private static FFA ffa;
/*    */   
/*    */   public void onEnable() {
/* 23 */     ffa = this;
/* 24 */     saveDefaultConfig();
/* 25 */     getServer().getPluginManager().registerEvents((Listener)new formHelper(), (Plugin)this);
/* 26 */     getServer().getPluginManager().registerEvents((Listener)new ffaListener(), (Plugin)this);
/* 27 */     getServer().getCommandMap().register("", (Command)new ffaCommand(getConfig().getString("setting.command.ffa.name"), getConfig().getString("setting.command.ffa.des")));
/* 28 */     for (String s : getConfig().getStringList("arena")) {
/* 29 */       this.arenas.put(s, new Arena(s, getConfig()));
/* 30 */       sendMsgToCon("Loaded arena -> " + s);
/*    */     } 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 38 */     sendMsgToCon("Plugin is enabled!");
/*    */   }
/*    */   public static FFA getInstance() {
/* 41 */     return ffa;
/*    */   }
/*    */   public Map<String, Arena> getArenas() {
/* 44 */     return this.arenas;
/*    */   }
/*    */   public String getWorldPath() {
/* 47 */     return getServer().getFilePath() + File.separator + "worlds" + File.separator;
/*    */   }
/*    */   public Arena getArenaByName(Player player) {
/* 50 */     String name = player.getLevel().getName();
/* 51 */     return getArenas().getOrDefault(name, null);
/*    */   }
/*    */   public void sendMsgToCon(String s) {
/* 54 */     getServer().getLogger().info("[" + getPrefix() + "]" + s);
/*    */   }
/*    */   public String getPrefix() {
/* 57 */     return getConfig().getString("prefix");
/*    */   }
/*    */   public Position getDefaultSpawn() {
/* 60 */     return new Position(Integer.parseInt(getConfig().getString("defaultSpawn").split(":")[0]), Integer.parseInt(getConfig().getString("defaultSpawn").split(":")[1]), Integer.parseInt(getConfig().getString("defaultSpawn").split(":")[2]), getServer().getLevelByName(getConfig().getString("defaultSpawn").split(":")[3]));
/*    */   }
/*    */   public Effect strToEffect(String str) {
/* 63 */     int id = Integer.parseInt(str.split(":")[0]);
/* 64 */     int level = Integer.parseInt(str.split(":")[1]);
/* 65 */     int dur = Integer.parseInt(str.split(":")[2]);
/* 66 */     return Effect.getEffect(id).setAmplifier(level).setDuration(dur);
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\BPFFA.jar!\cn\blockpixel\ffa\FFA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */