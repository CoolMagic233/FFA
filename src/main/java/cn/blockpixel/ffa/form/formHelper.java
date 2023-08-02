package cn.blockpixel.ffa.form;

import cn.blockpixel.ffa.FFA;
import cn.blockpixel.ffa.arena.Arena;
import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.Config;
import java.util.Map;

public class formHelper
  implements Listener
{
  public static int FORM_ARENAS = 2023116;

  private static Config config = FFA.getInstance().getConfig();
  public static void sendArenaListForm(Player player) {
    FormWindowSimple form = new FormWindowSimple(config.getString("form.title"), config.getString("form.content"));
    for (Map.Entry<String, Arena> entry : (Iterable<Map.Entry<String, Arena>>)FFA.getInstance().getArenas().entrySet()) {
      String s = config.getString((String)entry.getKey() + ".form.button").replace("@online", String.valueOf(((Arena)entry.getValue()).getArenaSize()));
      String imagePath = config.getString(entry.getKey() + ".form.image");
      if (FFA.getInstance().buttonImage){
        form.addButton(new ElementButton(s,new ElementButtonImageData("path",imagePath)));
      }else {
        form.addButton(new ElementButton(s));
      }
    }
    player.showFormWindow((FormWindow)form, FORM_ARENAS);
  }

  @EventHandler
  public void onForm(PlayerFormRespondedEvent e) {
    Player p = e.getPlayer();
    if (e.getResponse() instanceof FormResponseSimple) {
      FormResponseSimple response = (FormResponseSimple)e.getResponse();
      if (response != null &&
        e.getFormID() == FORM_ARENAS)
        for (Map.Entry<String, Arena> entry : FFA.getInstance().getArenas().entrySet()) {
          String s = config.getString(entry.getKey() + ".form.button").replace("@online", String.valueOf(((Arena)entry.getValue()).getArenaSize()));
          if (response.getClickedButton().getText().equals(s))
            entry.getValue().join(p);
        }
    }
  }
}
