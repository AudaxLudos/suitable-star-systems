package suitablestarsystems;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import lunalib.lunaSettings.LunaSettings;
import suitablestarsystems.campaign.InitialSystemAccess;
import suitablestarsystems.world.systems.System1;
import suitablestarsystems.world.systems.System2;
import suitablestarsystems.world.systems.System3;

public class ModPlugin extends BaseModPlugin {
    @Override
    public void onApplicationLoad() {
        Utils.random.setSeed(1);

        if (Utils.isLunaLibEnabled()) {
            Utils.loadSettings();
            LunaSettings.addSettingsListener(new SSSLunaSettingsListener());
        } else {
            Utils.loadSettings();
        }
    }

    @Override
    public void onNewGame() {
        Global.getSector().getMemoryWithoutUpdate().set("$sss_omegaPlanetCracked", false);
        Global.getSector().getMemoryWithoutUpdate().set("$sss_omegaPlanetFound", false);
        Global.getSector().getMemoryWithoutUpdate().set("$sss_remnantPlanetFound", false);
    }

    @Override
    public void onGameLoad(boolean newGame) {
        Global.getSector().getMemoryWithoutUpdate().set("$sss_omegaPlanetQuestOverride", Utils.OMEGA_PLANET_QUEST_OVERRIDE);
        Global.getSector().addTransientScript(new InitialSystemAccess());
    }

    @Override
    public void onNewGameAfterProcGen() {
        if (Utils.CAN_SPAWN_MAIN_SYSTEM) {
            new System1().generate(Global.getSector());
        }

        new System2().generate(Global.getSector());
        new System3().generate(Global.getSector());
    }
}