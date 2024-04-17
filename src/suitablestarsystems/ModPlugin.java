package suitablestarsystems;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import suitablestarsystems.campaign.InitialSystemAccess;
import suitablestarsystems.world.WorldGenerator;

import java.util.Objects;

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

        // mark remnant and omega system spawned by this mod as not random mission target for existing saves
        for (StarSystemAPI system : Global.getSector().getStarSystems()) {
            if (system.isProcgen()) continue;
            if (system.getStar() == null) continue;
            if (!Objects.equals(system.getStar().getTypeId(), StarTypes.BLACK_HOLE) || !Objects.equals(system.getStar().getTypeId(), StarTypes.WHITE_DWARF))
                continue;
            for (PlanetAPI planet : system.getPlanets()) {
                if (!Objects.equals(planet.getId(), "planet_sss_remnant") || !Objects.equals(planet.getId(), "planet_sss_omega"))
                    continue;
                if (planet.hasTag(Tags.NOT_RANDOM_MISSION_TARGET)) continue;
                planet.addTag(Tags.NOT_RANDOM_MISSION_TARGET);
            }
        }
    }

    @Override
    public void onNewGameAfterEconomyLoad() {
        new WorldGenerator().generate(Global.getSector());
    }
}