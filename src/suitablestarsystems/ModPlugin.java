package suitablestarsystems;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import suitablestarsystems.campaign.InitialSystemAccess;
import suitablestarsystems.world.WorldGenerator;

public class ModPlugin extends BaseModPlugin {
    @Override
    public void onNewGame() {
        Global.getSector().getMemoryWithoutUpdate().set("$sss_omegaPlanetCracked", false);
        Global.getSector().getMemoryWithoutUpdate().set("$sss_omegaPlanetFound", false);
        Global.getSector().getMemoryWithoutUpdate().set("$sss_remnantPlanetFound", false);
    }

    @Override
    public void onGameLoad(boolean newGame) {
        Global.getSector().getMemoryWithoutUpdate().set("$sss_omegaPlanetQuestOverride", Global.getSettings().getBoolean("omegaPlanetQuestOverride"));
        Global.getSector().addTransientScript(new InitialSystemAccess());
    }

    @Override
    public void onNewGameAfterEconomyLoad() {
        new WorldGenerator().generate(Global.getSector());
    }
}