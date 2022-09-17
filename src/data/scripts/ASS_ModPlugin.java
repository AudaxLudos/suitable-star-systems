package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;

import data.scripts.world.ASS_Gen;

public class ASS_ModPlugin extends BaseModPlugin {
    @Override
    public void onNewGameAfterEconomyLoad() {
        new ASS_Gen().generate(Global.getSector());
    }
}