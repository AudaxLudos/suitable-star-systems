package data.scripts.world;

import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;

import data.scripts.world.systems.System1;
import data.scripts.world.systems.System2;
import data.scripts.world.systems.System3;

public class ASS_Gen implements SectorGeneratorPlugin {
    @Override
    public void generate(SectorAPI sector) {
        new System1().generate(sector);
        new System2().generate(sector);
        new System3().generate(sector);
    }
}
