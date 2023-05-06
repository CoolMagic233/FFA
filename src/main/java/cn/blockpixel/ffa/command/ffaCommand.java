package cn.blockpixel.ffa.command;

import cn.blockpixel.ffa.FFA;
import cn.blockpixel.ffa.arena.Arena;
import cn.blockpixel.ffa.form.formHelper;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class ffaCommand extends Command {
  public ffaCommand(String name, String des) {
    super(name, des);
  }


  public boolean execute(CommandSender commandSender, String s, String[] strings) {
    if (commandSender instanceof Player) {
      Arena arena = FFA.getInstance().getArenaByName((Player)commandSender);
      if (strings.length < 1) {
        formHelper.sendArenaListForm((Player)commandSender);
      }
      if (strings.length == 1) {
        if (strings[0].equals(FFA.getInstance().getConfig().getString("setting.command.quit.name"))) {
          if (arena == null) {
            commandSender.sendMessage("[" + FFA.getInstance().getPrefix() + "] §cYou not in arena!");
            return true;
          }
          if (arena.isPlaying((Player)commandSender)) {
            arena.quit((Player)commandSender);
          } else {
            commandSender.sendMessage("[" + FFA.getInstance().getPrefix() + "] §cYou not in arena!");
          }
        }
        return true;
      }
    }
    return false;
  }
}
