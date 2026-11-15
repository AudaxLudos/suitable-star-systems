package suitablestarsystems;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import suitablestarsystems.campaign.InitialSystemAccess;
import suitablestarsystems.world.systems.System1;
import suitablestarsystems.world.systems.System2;
import suitablestarsystems.world.systems.System3;

public class ModPlugin extends BaseModPlugin {
    @Override
    public void onApplicationLoad() {
        Settings.load();
    }

    @Override
    public void onNewGame() {
        Global.getSector().getMemoryWithoutUpdate().set("$sss_omegaPlanetCracked", false);
        Global.getSector().getMemoryWithoutUpdate().set("$sss_omegaPlanetFound", false);
        Global.getSector().getMemoryWithoutUpdate().set("$sss_remnantPlanetFound", false);

        if (Settings.SPAWN_MAIN_SYSTEM && Settings.OVERRIDE_MAIN_SYSTEM_LOC) {
            new System1().generate(Global.getSector());
        }
    }

    @Override
    public void onGameLoad(boolean newGame) {
        Global.getSector().getMemoryWithoutUpdate().set("$sss_omegaPlanetQuestOverride", Settings.SKIP_OMEGA_PLANET_INTERACTION);
        Global.getSector().addTransientScript(new InitialSystemAccess());
    }

    @Override
    public void onNewGameAfterProcGen() {
        if (Settings.SPAWN_MAIN_SYSTEM && !Settings.OVERRIDE_MAIN_SYSTEM_LOC) {
            new System1().generate(Global.getSector());
        }

        if (Settings.SPAWN_REMNANT_SYSTEM) {
            new System2().generate(Global.getSector());
        }

        if (Settings.SPAWN_OMEGA_SYSTEM) {
            new System3().generate(Global.getSector());
        }
    }
}