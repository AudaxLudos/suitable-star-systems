package suitablestarsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.AccretionDiskGenPlugin.TexAndIndex;
import com.fs.starfarer.api.impl.campaign.procgen.*;
import com.fs.starfarer.api.impl.campaign.procgen.ProcgenUsedNames.NamePick;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantOfficerGeneratorPlugin;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantThemeGenerator;
import com.fs.starfarer.api.impl.campaign.terrain.AsteroidFieldTerrainPlugin.AsteroidFieldParams;
import com.fs.starfarer.api.impl.campaign.terrain.*;
import com.fs.starfarer.api.impl.campaign.terrain.BaseRingTerrain.RingParams;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Utils {
    public static final float STARTING_RADIUS_STAR_BASE = 750f;
    //    public static final float STARTING_RADIUS_STAR_RANGE = 500f;
    public static Random random = StarSystemGenerator.random;
    public static boolean CAN_SPAWN_MAIN_SYSTEM = true;
    public static boolean CAN_OVERRIDE_MAIN_SYSTEM_LOC = false;
    public static float MAIN_SYSTEM_X_OVERRIDE = 0f;
    public static float MAIN_SYSTEM_Y_OVERRIDE = 0f;

    public static float getRandomAngle() {
        return random.nextFloat() * 360f;
    }

    public static String generateProceduralName(String tag, String parent) {
        NamePick namePick = ProcgenUsedNames.pickName(tag, parent, null);
        return namePick.nameWithRomanSuffixIfAny;
    }

    public static PlanetAPI createPlanet(StarSystemAPI system, String planetId, SectorEntityToken parentOrbit, String planetType, Float planetAngle, float planetRadius, float orbitDistance, float orbitDays,
                                         List<String> marketConditions) {
        String planetName = generateProceduralName(Tags.PLANET, null);
        String id = planetId;
        if (id.isEmpty()) {
            id = planetName.toLowerCase();
        }
        float angle = getRandomAngle();
        if (planetAngle != null) {
            angle = planetAngle;
        }
        PlanetAPI planet = system.addPlanet(id, parentOrbit, planetName, planetType, angle, planetRadius, orbitDistance, orbitDays);
        Misc.initConditionMarket(planet);
        MarketAPI market = planet.getMarket();
        for (String condition : marketConditions) {
            market.addCondition(condition);
        }

        return planet;
    }

    public static PlanetAPI createPlanet(StarSystemAPI system, SectorEntityToken parentOrbit, String planetType, float planetRadius, float orbitDistance, float orbitDays,
                                         List<String> marketConditions) {
        return createPlanet(system, "", parentOrbit, planetType, null, planetRadius, orbitDistance, orbitDays, marketConditions);
    }

    public static PlanetAPI createPlanet(StarSystemAPI system, String planetId, SectorEntityToken parentOrbit, String planetType, float planetRadius, float orbitDistance, float orbitDays,
                                         List<String> marketConditions) {
        return createPlanet(system, planetId, parentOrbit, planetType, null, planetRadius, orbitDistance, orbitDays, marketConditions);
    }

    public static PlanetAPI createPlanet(StarSystemAPI system, SectorEntityToken parentOrbit, String planetType, float planetAngle, float planetRadius, float orbitDistance, float orbitDays,
                                         List<String> marketConditions) {
        return createPlanet(system, "", parentOrbit, planetType, planetAngle, planetRadius, orbitDistance, orbitDays, marketConditions);
    }

    public static void createAsteroidField(StarSystemAPI system, SectorEntityToken orbitFocus, float orbitAngle, float orbitDistance, float orbitDays, float minRadius, float maxRadius,
                                           int minAsteroids, int maxAsteroids, float minAsteroidRadius, float maxAsteroidRadius) {
        SectorEntityToken asteroidField1 = system.addTerrain(Terrain.ASTEROID_FIELD,
                new AsteroidFieldParams(
                        minRadius, // min radius
                        maxRadius, // max radius
                        minAsteroids, // min asteroid count
                        maxAsteroids, // max asteroid count
                        minAsteroidRadius, // min asteroid radius
                        maxAsteroidRadius, // max asteroid radius
                        generateProceduralName(Terrain.ASTEROID_FIELD, null)));
        asteroidField1.setCircularOrbit(orbitFocus, orbitAngle, orbitDistance, orbitDays);
    }

    public static void createAsteroidBelt(StarSystemAPI system, int numAsteroids, SectorEntityToken orbitFocus, float orbitDistance,
                                          float orbitDays, String category, String key, float bandWidthInTexture, int bandIndex, Color color, float bandWidthInEngine) {
        system.addAsteroidBelt(orbitFocus, numAsteroids, orbitDistance, bandWidthInEngine, orbitDays, orbitDays, Terrain.ASTEROID_BELT, generateProceduralName(Terrain.ASTEROID_BELT, null));
        system.addRingBand(orbitFocus, category, key, bandWidthInTexture, bandIndex, color, bandWidthInEngine, orbitDistance, orbitDays);
    }

    public static void createRingBelt(StarSystemAPI system, SectorEntityToken orbitFocus, float orbitDistance, float orbitDays, String category, String key, float bandWidthInTexture, int bandIndex,
                                      Color color, float bandWidthInEngine) {
        system.addRingBand(orbitFocus, category, key, bandWidthInTexture, bandIndex, color, bandWidthInEngine, orbitDistance, orbitDays);
        SectorEntityToken ring = system.addTerrain(Terrain.RING, new RingParams(256f, orbitDistance, null, generateProceduralName(Terrain.RING, null)));
        ring.setCircularOrbit(orbitFocus, 0, 0, orbitDays);
    }

    public static void createMagneticField(SectorEntityToken focus, float bandWidthInEngine, float middleRadius, float innerRadius, float outerRadius, float auroraFrequency) {
        int baseIndex = (int) (MagFieldGenPlugin.baseColors.length * random.nextDouble());
        int auroraIndex = (int) (MagFieldGenPlugin.auroraColors.length * random.nextDouble());
        SectorEntityToken magneticField = focus.getStarSystem().addTerrain("magnetic_field",
                new MagneticFieldTerrainPlugin.MagneticFieldParams(
                        bandWidthInEngine,
                        middleRadius,
                        focus,
                        innerRadius,
                        outerRadius,
                        MagFieldGenPlugin.baseColors[baseIndex],
                        auroraFrequency,
                        MagFieldGenPlugin.auroraColors[auroraIndex]));
        magneticField.setCircularOrbit(focus, 0f, 0f, 0f);
    }

    public static void createAccretionDisk(SectorEntityToken focus, float radius, float minSpiralRadius) {
        float orbitRadius = radius * 3f;
        float bandWidth = 256f;
        for (float i = 0f; i < 12f; i++) {
            float middleRadius = orbitRadius - i * bandWidth * 0.25f - i * bandWidth * 0.1f;
            TexAndIndex tex = getTextureAndIndex();
            float orbitDays = middleRadius / (30f + 10f * random.nextFloat());
            RingBandAPI visual = focus.getStarSystem().addRingBand(focus, "misc", tex.tex, bandWidth,
                    tex.index, Color.WHITE, bandWidth, middleRadius + bandWidth / 2f, -orbitDays);
            float spiralFactor = 2f + 5f * random.nextFloat();
            visual.setSpiral(true);
            visual.setMinSpiralRadius(minSpiralRadius);
            visual.setSpiralFactor(spiralFactor);
        }

        SectorEntityToken ring = focus.getStarSystem().addTerrain("ring",
                new BaseRingTerrain.RingParams(orbitRadius, orbitRadius / 2f, focus, null));
        ring.addTag("accretion_disk");
        if (((CampaignTerrainAPI) ring).getPlugin() instanceof RingSystemTerrainPlugin) {
            ((RingSystemTerrainPlugin) ((CampaignTerrainAPI) ring).getPlugin()).setNameForTooltip("Accretion Disk");
        }
        ring.setCircularOrbit(focus, 0f, 0f, -100f);
    }

    public static TexAndIndex getTextureAndIndex() {
        TexAndIndex result = new TexAndIndex();
        WeightedRandomPicker<Integer> indexPicker = new WeightedRandomPicker<>(random);
        WeightedRandomPicker<String> ringSet = new WeightedRandomPicker<>(random);
        ringSet.add("ring_ice", 10f);
        ringSet.add("ring_dust", 10f);
        String set = ringSet.pick();
        if (set.equals("ring_ice")) {
            result.tex = "rings_ice0";
            indexPicker.add(0);
            indexPicker.add(1);
        } else if (set.equals("ring_dust")) {
            result.tex = "rings_dust0";
            indexPicker.add(0);
            indexPicker.add(1);
        }
        result.index = indexPicker.pick();
        return result;
    }

    public static CampaignFleetAPI addAIBattlestation(SectorEntityToken focus, float orbitRadius, float orbitDays) {
        CampaignFleetAPI fleet = FleetFactoryV3.createEmptyFleet("remnant", "battlestation", null);
        FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "remnant_station2_Standard");
        fleet.getFleetData().addFleetMember(member);
        fleet.getMemoryWithoutUpdate().set("$cfai_makeAggressive", true);
        fleet.getMemoryWithoutUpdate().set("$cfai_noJump", true);
        fleet.getMemoryWithoutUpdate().set("$cfai_makeAllowDisengage", true);
        fleet.addTag("neutrino_high");
        fleet.setStationMode(true);
        RemnantThemeGenerator.addRemnantStationInteractionConfig(fleet);
        focus.getStarSystem().addEntity(fleet);
        fleet.clearAbilities();
        fleet.addAbility("transponder");
        fleet.getAbility("transponder").activate();
        fleet.getDetectedRangeMod().modifyFlat("gen", 1000f);
        fleet.setAI(null);
        fleet.setCircularOrbitWithSpin(focus, getRandomAngle(), orbitRadius, orbitDays, 5f, 5f);
        AICoreOfficerPlugin plugin = Misc.getAICoreOfficerPlugin("alpha_core");
        PersonAPI commander = plugin.createPerson("alpha_core", "remnant", random);
        fleet.setCommander(commander);
        fleet.getFlagship().setCaptain(commander);
        RemnantOfficerGeneratorPlugin.integrateAndAdaptCoreForAIFleet(fleet.getFlagship());
        RemnantOfficerGeneratorPlugin.addCommanderSkills(commander, fleet, null, 4, random);
        member.getRepairTracker().setCR(member.getRepairTracker().getMaxCR());
        return fleet;
    }

    public static float updateBinaryStarsOrbit(PlanetAPI primaryStar, PlanetAPI secondaryStar, StarSystemAPI system) {
        float dist = STARTING_RADIUS_STAR_BASE;

        float r1 = primaryStar.getRadius();
        float r2 = secondaryStar.getRadius();
        if (primaryStar.getSpec().getPlanetType().equals("black_hole")) {
            r1 *= 5f;
        }
        if (secondaryStar.getSpec().getPlanetType().equals("black_hole")) {
            r2 *= 5f;
        }

        float totalRadius = r1 + r2;
        dist += totalRadius;

        float orbitPrimary = dist * r2 / totalRadius;
        float orbitSecondary = dist * r1 / totalRadius;

        float anglePrimary = Utils.random.nextFloat() * 360f;
        float orbitDays = dist / (30f + Utils.random.nextFloat() * 50f);

        primaryStar.setCircularOrbit(system.getCenter(), anglePrimary, orbitPrimary, orbitDays);
        secondaryStar.setCircularOrbit(system.getCenter(), anglePrimary + 180f, orbitSecondary, orbitDays);

        return Math.max(orbitPrimary + primaryStar.getRadius(), orbitSecondary + secondaryStar.getRadius());
    }

    public static Vector2f getCentroid(List<StarSystemAPI> systems) {
        float centroidX = 0, centroidY = 0;
        for (StarSystemAPI system : systems) {
            centroidX += system.getHyperspaceAnchor().getLocationInHyperspace().getX();
            centroidY += system.getHyperspaceAnchor().getLocationInHyperspace().getY();
        }
        return new Vector2f(centroidX / systems.size(), centroidY / systems.size());
    }

    public static Constellation getNearestConstellation(Vector2f origin) {
        float minDist = Float.MAX_VALUE;
        Constellation closest = null;
        for (Constellation constellation : getAllConstellations()) {
            float dist = Misc.getDistance(origin, constellation.getLocation());
            if (dist < minDist) {
                minDist = dist;
                closest = constellation;
            }
        }
        return closest;
    }

    public static Constellation getNearestConstellation(Vector2f origin, Set<Constellation> constellations) {
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

    public static Set<Constellation> getAllConstellations() {
        Set<Constellation> constellations = new HashSet<>();
        for (StarSystemAPI system : Global.getSector().getStarSystems()) {
            if (!system.isInConstellation() || !system.isProcgen()) {
                continue;
            }
            Constellation c = system.getConstellation();
            if (c != null) {
                constellations.add(c);
            }
        }
        return constellations;
    }

    public static void addBlackHoleVisuals(StarSystemAPI system, PlanetAPI star) {
        if (star == null) {
            return;
        }

        if (star.getSpec().getPlanetType().equals("black_hole")) {
            StarCoronaTerrainPlugin coronaPlugin = Misc.getCoronaFor(star);
            if (coronaPlugin != null) {
                system.removeEntity(coronaPlugin.getEntity());
            }

            StarGenDataSpec starData = (StarGenDataSpec) Global.getSettings().getSpec(StarGenDataSpec.class, star.getSpec().getPlanetType(), false);
            float corona = star.getRadius() * (starData.getCoronaMult() + starData.getCoronaVar() * (random.nextFloat() - 0.5f));
            if (corona < starData.getCoronaMin()) {
                corona = starData.getCoronaMin();
            }

            SectorEntityToken eventHorizon = system.addTerrain(Terrain.EVENT_HORIZON,
                    new StarCoronaTerrainPlugin.CoronaParams(star.getRadius() + corona, (star.getRadius() + corona) / 2f,
                            star, starData.getSolarWind(),
                            starData.getMinFlare() + (starData.getMaxFlare() - starData.getMinFlare()) * random.nextFloat(),
                            starData.getCrLossMult()));
            eventHorizon.setCircularOrbit(star, 0, 0, 100);

            createAccretionDisk(star, star.getRadius() * 5f, 0f);

            system.setLightColor(new Color(125, 90, 125, 255));
        }
    }

    public static StarSystemAPI getStarSystemWithTag(String tag) {
        for (StarSystemAPI s : Global.getSector().getStarSystems()) {
            if (s.hasTag(tag)) {
                return s;
            }
        }
        return null;
    }

    public static void clearHyperspaceNebulaAroundSystem(StarSystemAPI system) {
        // Clear nebula in hyperspace
        HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
        NebulaEditor editor = new NebulaEditor(plugin);
        float minRadius = plugin.getTileSize() * 2f;
        float hyperspaceRadius = system.getMaxRadiusInHyperspace();
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0f, hyperspaceRadius + minRadius, 0f, 360f);
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0f, hyperspaceRadius + minRadius, 0f, 360f, 0.25f);
    }

    public static Vector2f findLocationInConstellation(Constellation constellation, Random random) {
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
            if (!isIntersect) {
                result = new Vector2f(x0, y0);
            }
        }
        return result;
    }

    public static boolean isLunaLibEnabled() {
        return Global.getSettings().getModManager().isModEnabled("lunalib");
    }
}
