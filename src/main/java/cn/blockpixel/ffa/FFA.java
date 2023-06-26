 package cn.blockpixel.ffa;

 import cn.blockpixel.ffa.arena.Arena;
 import cn.blockpixel.ffa.command.ffaCommand;
 import cn.blockpixel.ffa.form.formHelper;
 import cn.blockpixel.ffa.listener.ffaListener;
 import cn.nukkit.Player;
 import cn.nukkit.command.Command;
 import cn.nukkit.event.Listener;
 import cn.nukkit.level.Position;
 import cn.nukkit.plugin.Plugin;
 import cn.nukkit.plugin.PluginBase;
 import cn.nukkit.potion.Effect;
 import cn.nukkit.scheduler.Task;
 import com.smallaswater.npc.data.RsNpcConfig;
 import com.smallaswater.npc.variable.BaseVariableV2;
 import com.smallaswater.npc.variable.VariableManage;

 import java.io.File;
 import java.util.HashMap;
 import java.util.Map;

 public class FFA extends PluginBase {
   private final Map<String, Arena> arenas = new HashMap<>();
   private static FFA ffa;

   public void onEnable() {
     ffa = this;
     saveDefaultConfig();
     VariableManage.addVariableV2("FFA",RsnpcVariable.class);
     getServer().getPluginManager().registerEvents((Listener)new formHelper(), (Plugin)this);
     getServer().getPluginManager().registerEvents((Listener)new ffaListener(), (Plugin)this);
     getServer().getCommandMap().register("", (Command)new ffaCommand(getConfig().getString("setting.command.ffa.name"), getConfig().getString("setting.command.ffa.des")));
     for (String s : getConfig().getStringList("arena")) {
       this.arenas.put(s, new Arena(s, getConfig()));
       sendMsgToCon("Loaded arena -> " + s);
     }

     sendMsgToCon("Plugin is enabled!");
   }
   public static FFA getInstance() {
     return ffa;
   }
   public Map<String, Arena> getArenas() {
     return this.arenas;
   }
   public String getWorldPath() {
     return getServer().getFilePath() + File.separator + "worlds" + File.separator;
   }
   public Arena getArenaByName(Player player) {
     String name = player.getLevel().getName();
     return getArenas().getOrDefault(name, null);
   }
   public void sendMsgToCon(String s) {
     getServer().getLogger().info("[" + getPrefix() + "]" + s);
   }
   public String getPrefix() {
     return getConfig().getString("prefix");
   }
   public Position getDefaultSpawn() {
     return new Position(Integer.parseInt(getConfig().getString("defaultSpawn").split(":")[0]), Integer.parseInt(getConfig().getString("defaultSpawn").split(":")[1]), Integer.parseInt(getConfig().getString("defaultSpawn").split(":")[2]), getServer().getLevelByName(getConfig().getString("defaultSpawn").split(":")[3]));
   }
   public Effect strToEffect(String str) {
     int id = Integer.parseInt(str.split(":")[0]);
     int level = Integer.parseInt(str.split(":")[1]);
     int dur = Integer.parseInt(str.split(":")[2]);
     return Effect.getEffect(id).setAmplifier(level).setDuration(dur);
   }

   public boolean isPlayingInArenas(Player player){
       boolean a = false;
       for (Arena arena : getArenas().values()) {
           if(arena.getArenaPlayers().contains(player)) a = true;
       }
       return a;
   }
   public int getPlayingCount(){
       int count = 0;
       for (Arena value : getArenas().values()) {
           count = count + value.getArenaSize();
       }
       return count;
   }
 }

