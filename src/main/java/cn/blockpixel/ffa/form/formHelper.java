/*    */ package cn.blockpixel.ffa.form;
/*    */ 
/*    */ import cn.blockpixel.ffa.FFA;
/*    */ import cn.blockpixel.ffa.arena.Arena;
/*    */ import cn.nukkit.Player;
/*    */ import cn.nukkit.event.EventHandler;
/*    */ import cn.nukkit.event.Listener;
/*    */ import cn.nukkit.event.player.PlayerFormRespondedEvent;
/*    */ import cn.nukkit.form.element.ElementButton;
/*    */ import cn.nukkit.form.response.FormResponseSimple;
/*    */ import cn.nukkit.form.window.FormWindow;
/*    */ import cn.nukkit.form.window.FormWindowSimple;
/*    */ import cn.nukkit.utils.Config;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class formHelper
/*    */   implements Listener
/*    */ {
/* 19 */   public static int FORM_ARENAS = 2023116;
/*    */   
/* 21 */   private static Config config = FFA.getInstance().getConfig();
/*    */   public static void sendArenaListForm(Player player) {
/* 23 */     FormWindowSimple form = new FormWindowSimple(config.getString("form.title"), config.getString("form.content"));
/* 24 */     for (Map.Entry<String, Arena> entry : (Iterable<Map.Entry<String, Arena>>)FFA.getInstance().getArenas().entrySet()) {
/* 25 */       String s = config.getString((String)entry.getKey() + ".form.button").replace("@online", String.valueOf(((Arena)entry.getValue()).getArenaSize()));
/* 26 */       form.addButton(new ElementButton(s));
/*    */     } 
/* 28 */     player.showFormWindow((FormWindow)form, FORM_ARENAS);
/*    */   }
/*    */   
/*    */   @EventHandler
/*    */   public void onForm(PlayerFormRespondedEvent e) {
/* 33 */     Player p = e.getPlayer();
/* 34 */     if (e.getResponse() instanceof FormResponseSimple) {
/* 35 */       FormResponseSimple response = (FormResponseSimple)e.getResponse();
/* 36 */       if (response != null && 
/* 37 */         e.getFormID() == FORM_ARENAS)
/* 38 */         for (Map.Entry<String, Arena> entry : (Iterable<Map.Entry<String, Arena>>)FFA.getInstance().getArenas().entrySet()) {
/* 39 */           String s = config.getString((String)entry.getKey() + ".form.button").replace("@online", String.valueOf(((Arena)entry.getValue()).getArenaSize()));
/* 40 */           if (response.getClickedButton().getText().equals(s))
/* 41 */             ((Arena)entry.getValue()).join(p); 
/*    */         }  
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\BPFFA.jar!\cn\blockpixel\ffa\form\formHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */