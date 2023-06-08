package cn.blockpixel.ffa;

import cn.nukkit.Player;
import com.smallaswater.npc.data.RsNpcConfig;
import com.smallaswater.npc.variable.BaseVariableV2;

public class RsnpcVariable extends BaseVariableV2 {

    @Override
    public void onUpdate(Player player, RsNpcConfig rsNpcConfig) {
        addVariable("{ffaOnline}", String.valueOf(FFA.getInstance().getPlayingCount()));
    }
}
