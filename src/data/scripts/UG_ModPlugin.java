package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;

import data.scripts.world.UG_Gen;

public class UG_ModPlugin extends BaseModPlugin {
    @Override
    public void onNewGameAfterEconomyLoad() {
        new UG_Gen().generate(Global.getSector());
    }
}