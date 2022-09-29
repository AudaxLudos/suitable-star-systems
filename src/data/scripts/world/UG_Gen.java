package data.scripts.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.procgen.Constellation;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.util.Misc;

import data.scripts.world.systems.System1;
import data.scripts.world.systems.System2;
import data.scripts.world.systems.System3;
import data.scripts.world.systems.System4;

public class UG_Gen implements SectorGeneratorPlugin {
    @Override
    public void generate(SectorAPI sector) {
        Random random = StarSystemGenerator.random;
        List<StarSystemAPI> systems = sector.getStarSystems();
        List<Constellation> constellations = new ArrayList<Constellation>();
        for (StarSystemAPI system : systems) {
            if (system.isProcgen() && !constellations.contains(system.getConstellation()))
                constellations.add(system.getConstellation());
        }

        // Randomly put system 1 into a random constellation
        StarSystemAPI system1 = sector.createStarSystem("system1");
        Constellation constellation1 = getNearestConstellation(new Vector2f(-6000f, -6000f), constellations);
        Vector2f location1 = findLocationInConstellation(constellation1, random);
        constellations.remove(constellation1);
        constellation1.getSystems().add(system1);
        system1.setConstellation(constellation1);
        system1.getLocation().set(location1);
        new System1().generate(sector);

        // Randomly put system 2 into a random constellation
        StarSystemAPI system2 = sector.createStarSystem("system2");
        Constellation constellation2 = getNearestConstellation(constellation1.getLocation(), constellations);
        Vector2f location2 = findLocationInConstellation(constellation2, random);
        constellations.remove(constellation2);
        constellation2.getSystems().add(system2);
        system2.setConstellation(constellation2);
        system2.getLocation().set(location2);
        new System2().generate(sector);

        // Randomly put system 3 into a random constellation
        StarSystemAPI system3 = sector.createStarSystem("system3");
        Constellation constellation3 = getNearestConstellation(constellation1.getLocation(), constellations);
        Vector2f location3 = findLocationInConstellation(constellation3, random);
        constellations.remove(constellation3);
        constellation3.getSystems().add(system3);
        system3.setConstellation(constellation3);
        system3.getLocation().set(location3);
        new System3().generate(sector);

        // Randomly put system 4 into a random constellation
        StarSystemAPI system4 = sector.createStarSystem("system4");
        Constellation constellation4 = getNearestConstellation(constellation1.getLocation(), constellations);
        Vector2f location4 = findLocationInConstellation(constellation4, random);
        constellations.remove(constellation4);
        constellation4.getSystems().add(system4);
        system4.setConstellation(constellation4);
        system4.getLocation().set(location4);
        new System4().generate(sector);
    }

    public Vector2f findLocationInConstellation(Constellation constellation, Random random) {
        Vector2f result = null;
        Vector2f centroid = getCentroid(constellation.getSystems());
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

    public Vector2f getCentroid(List<StarSystemAPI> systems) {
        float centroidX = 0, centroidY = 0;
        for (StarSystemAPI system : systems) {
            centroidX += system.getHyperspaceAnchor().getLocationInHyperspace().getX();
            centroidY += system.getHyperspaceAnchor().getLocationInHyperspace().getY();
        }
        return new Vector2f(centroidX / systems.size(), centroidY / systems.size());
    }

    public Constellation getNearestConstellation(Vector2f origin, List<Constellation> constellations) {
        float minDist = Float.MAX_VALUE;
        Constellation closest = null;
        for (Constellation constellation : constellations) {
            float dist = Misc.getDistance(origin, constellation.getLocation());
            if (dist < minDist) {
                minDist = dist;
                closest = constellation;
            }
        }
        return closest;
    }
}
