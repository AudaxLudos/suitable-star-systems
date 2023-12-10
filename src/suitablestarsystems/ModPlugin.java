package suitablestarsystems;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import org.json.JSONException;
import org.json.JSONObject;
import suitablestarsystems.world.WorldGenerator;

import java.io.IOException;

public class ModPlugin extends BaseModPlugin {
    @Override
    public void onNewGame() {
        Global.getSector().getMemoryWithoutUpdate().set("$sss_omegaPlanetCracked", false);
    }

    @Override
    public void onGameLoad(boolean newGame) {
        try {
            JSONObject settings = Global.getSettings().loadJSON("settings.json");
            Global.getSector().getMemoryWithoutUpdate().set("$sss_omegaPlanetQuestOverride", settings.getBoolean("omegaPlanetQuestOverride"));
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onNewGameAfterEconomyLoad() {
        new WorldGenerator().generate(Global.getSector());
    }
}