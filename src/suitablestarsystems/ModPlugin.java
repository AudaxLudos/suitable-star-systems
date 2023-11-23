package suitablestarsystems;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import suitablestarsystems.world.WorldGenerator;

public class ModPlugin extends BaseModPlugin {
    @Override
    public void onNewGameAfterEconomyLoad() {
        new WorldGenerator().generate(Global.getSector());
    }
}