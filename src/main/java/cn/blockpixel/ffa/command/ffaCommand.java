/*    */ package cn.blockpixel.ffa.command;
/*    */ 
/*    */ import cn.blockpixel.ffa.FFA;
/*    */ import cn.blockpixel.ffa.arena.Arena;
/*    */ import cn.blockpixel.ffa.form.formHelper;
/*    */ import cn.nukkit.Player;
/*    */ import cn.nukkit.command.Command;
/*    */ import cn.nukkit.command.CommandSender;
/*    */ 
/*    */ public class ffaCommand extends Command {
/*    */   public ffaCommand(String name, String des) {
/* 12 */     super(name, des);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean execute(CommandSender commandSender, String s, String[] strings) {
/* 17 */     if (commandSender instanceof Player) {
/* 18 */       Arena arena = FFA.getInstance().getArenaByName((Player)commandSender);
/* 19 */       if (strings.length < 1) {
/* 20 */         formHelper.sendArenaListForm((Player)commandSender);
/*    */       }
/* 22 */       if (strings.length == 1) {
/* 23 */         if (strings[0].equals(FFA.getInstance().getConfig().getString("setting.command.quit.name"))) {
/* 24 */           if (arena == null) {
/* 25 */             commandSender.sendMessage("[" + FFA.getInstance().getPrefix() + "] §cYou not in arena!");
/* 26 */             return true;
/*    */           } 
/* 28 */           if (arena.isPlaying((Player)commandSender)) {
/* 29 */             arena.quit((Player)commandSender);
/*    */           } else {
/* 31 */             commandSender.sendMessage("[" + FFA.getInstance().getPrefix() + "] §cYou not in arena!");
/*    */           } 
/*    */         } 
/* 34 */         return true;
/*    */       } 
/*    */     } 
/* 37 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\BPFFA.jar!\cn\blockpixel\ffa\command\ffaCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */