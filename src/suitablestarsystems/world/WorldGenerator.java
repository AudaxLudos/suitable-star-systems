package suitablestarsystems.world;

import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.procgen.Constellation;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import org.lwjgl.util.vector.Vector2f;
import suitablestarsystems.Utils;
import suitablestarsystems.world.systems.System1;
import suitablestarsystems.world.systems.System2;
import suitablestarsystems.world.systems.System3;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class WorldGenerator implements SectorGeneratorPlugin {
    @Override
    public void generate(SectorAPI sector) {
        Random random = StarSystemGenerator.random;
        random.setSeed(1);
        Set<Constellation> constellations = new HashSet<Constellation>();
        for (StarSystemAPI system : sector.getStarSystems()) {
            if (!system.isInConstellation() || !system.isProcgen())
                continue;
            Constellation c = system.getConstellation();
            if (c != null)
                constellations.add(c);
        }

        // Put system 1 in a random constellation
        StarSystemAPI system1 = sector.createStarSystem("system_SSS_1");
        Constellation constellation1 = Utils.getNearestConstellation(new Vector2f(-6000f, -6000f), constellations);
        Vector2f location1 = findLocationInConstellation(constellation1, random);
        constellations.remove(constellation1);
        constellation1.getSystems().add(system1);
        system1.setConstellation(constellation1);
        system1.getLocation().set(location1);
        new System1().generate(sector);

        // Put system 2 in a random constellation
        StarSystemAPI system2 = sector.createStarSystem("system_SSS_2");
        Constellation constellation2 = Utils.getNearestConstellation(constellation1.getLocation(), constellations);
        Vector2f location2 = findLocationInConstellation(constellation2, random);
        constellations.remove(constellation2);
        constellation2.getSystems().add(system2);
        system2.setConstellation(constellation2);
        system2.getLocation().set(location2);
        new System2().generate(sector);

        // Put system 3 in a random constellation
        StarSystemAPI system3 = sector.createStarSystem("system_SSS_3");
        Constellation constellation3 = Utils.getNearestConstellation(constellation1.getLocation(), constellations);
        Vector2f location3 = findLocationInConstellation(constellation3, random);
        constellations.remove(constellation3);
        constellation3.getSystems().add(system3);
        system3.setConstellation(constellation3);
        system3.getLocation().set(location3);
        new System3().generate(sector);
    }

    public Vector2f findLocationInConstellation(Constellation constellation, Random random) {
        Vector2f result = null;
        Vector2f centroid = Utils.getCentroid(constellation.getSystems());
        while (result == null) {
            float x0 = centroid.x + random.nextFloat() * 4000f;
            float y0 = centroid.y + random.nextFloat() * 4000f;
            float r0 = 1200f;
            boolean isIntersect = false;
            for (StarSystemAPI system : constellation.getSystems()) {
                float x1 = system.getHyperspaceAnchor().getLocationInHyperspace().getX();
                float y1 = system.getHyperspaceAnchor().getLocationInHyperspace().getY();
                float r1 = system.getMaxRadiusInHyperspace();
                float distanceSq = (x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1);
                if (distanceSq < (r0 + r1) * (r0 + r1)) {
                    isIntersect = true;
                    break;
                }
            }
            if (!isIntersect)
                result = new Vector2f(x0, y0);
        }
        return result;
    }
}
