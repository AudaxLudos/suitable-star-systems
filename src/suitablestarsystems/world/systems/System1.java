package suitablestarsystems.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.CoronalTapParticleScript;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.*;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import suitablestarsystems.Utils;

import java.awt.*;
import java.util.Arrays;

public class System1 {
    protected float centerRadius = 0f;

    public void generate(SectorAPI sector) {
        // Get star system
        StarSystemAPI system = sector.getStarSystem("system_SSS_1");

        // Add system themes / tags
        system.addTag(Tags.THEME_INTERESTING);
        system.addTag(Tags.THEME_RUINS);
        system.addTag(Tags.THEME_RUINS_MAIN);
        system.addTag(Tags.THEME_UNSAFE);
        system.addTag(Tags.THEME_REMNANT);
        system.addTag(Tags.THEME_REMNANT_MAIN);
        system.addTag(Tags.THEME_REMNANT_RESURGENT);
        system.addTag(Tags.THEME_DERELICT_CRYOSLEEPER);
        system.addTag(Tags.HAS_CORONAL_TAP);

        system.initNonStarCenter();
        SectorEntityToken systemCenter = system.getCenter();

        // Add star for system
        String primaryStarName = Utils.generateProceduralName(Tags.STAR, system.getConstellation().getName());
        PlanetAPI primaryStar = system.addPlanet(primaryStarName.toLowerCase(), null, primaryStarName, StarTypes.BLUE_SUPERGIANT, 0f, 1200f, 10000f, 1000f);
        system.addCorona(primaryStar, 500f, 17f, 3f, 6f);

        String secondaryStarName = Utils.generateProceduralName(Tags.STAR, system.getConstellation().getName());
        PlanetAPI secondaryStar = system.addPlanet(secondaryStarName.toLowerCase(), null, secondaryStarName, StarTypes.ORANGE, 0f, 750f, 10000f, 1000f);
        system.addCorona(secondaryStar, 400f, 10f, 1f, 3f);

        centerRadius = Utils.updateBinaryStarsOrbit(primaryStar, secondaryStar, system);

        system.setType(StarSystemGenerator.StarSystemType.BINARY_CLOSE);
        system.setStar(primaryStar);
        system.setSecondary(secondaryStar);
        system.setBaseName(primaryStarName);
        system.setName(primaryStarName);

        // Add custom entities
        SectorEntityToken coronalTap = system.addCustomEntity(null, null, Entities.CORONAL_TAP, Factions.NEUTRAL);
        coronalTap.setCircularOrbit(primaryStar, primaryStar.getCircularOrbitAngle() + 180f, primaryStar.getRadius() + 250f, primaryStar.getCircularOrbitPeriod());
        coronalTap.setSensorProfile(1f);
        coronalTap.setDiscoverable(true);
        system.addScript(new MiscellaneousThemeGenerator.MakeCoronalTapFaceNearestStar(coronalTap));
        system.addScript(new CoronalTapParticleScript(coronalTap));

        // Add custom entities
        float randomAngle1 = Utils.getRandomAngle();
        SectorEntityToken inactiveGate = system.addCustomEntity(null, null, Entities.INACTIVE_GATE, Factions.NEUTRAL);
        inactiveGate.setSensorProfile(1f);
        inactiveGate.setDiscoverable(true);
        inactiveGate.setCircularOrbit(systemCenter, randomAngle1, centerRadius + 1000f, 100f);
        SectorEntityToken commRelay = system.addCustomEntity(null, null, Entities.COMM_RELAY, Factions.NEUTRAL);
        commRelay.setCircularOrbit(systemCenter, randomAngle1 + 120f, centerRadius + 1000f, 100f);
        JumpPointAPI jumpPoint1 = Global.getFactory().createJumpPoint(null, "Inner System Jump-point");
        jumpPoint1.setStandardWormholeToHyperspaceVisual();
        jumpPoint1.setCircularOrbit(systemCenter, randomAngle1 + 240f, centerRadius + 1000f, 100f);
        system.addEntity(jumpPoint1);

        // Add planet 1
        Utils.createPlanet(system, systemCenter,
                "barren_castiron",
                90f,
                centerRadius + 2000f,
                200f,
                Arrays.asList(
                        Conditions.ORE_ULTRARICH,
                        Conditions.RARE_ORE_ULTRARICH,
                        Conditions.NO_ATMOSPHERE,
                        Conditions.VERY_HOT));

        // Add planet 2
        PlanetAPI planet2 = Utils.createPlanet(system, systemCenter,
                "terran",
                130f,
                centerRadius + 3500f,
                350f,
                Arrays.asList(
                        Conditions.FARMLAND_BOUNTIFUL,
                        Conditions.ORGANICS_PLENTIFUL,
                        Conditions.ORE_MODERATE,
                        Conditions.RUINS_VAST,
                        Conditions.HABITABLE,
                        Conditions.MILD_CLIMATE));
        // Add planet 3 moon of planet 2
        Utils.createPlanet(system, planet2,
                "barren-desert",
                60f,
                planet2.getRadius() + 500f,
                (planet2.getRadius() + 500f) / 10f,
                Arrays.asList(
                        Conditions.ORE_ULTRARICH,
                        Conditions.RARE_ORE_ULTRARICH,
                        Conditions.ORGANICS_COMMON,
                        Conditions.THIN_ATMOSPHERE));
        // Add asteroid field 1
        Utils.createAsteroidField(system, systemCenter, planet2.getCircularOrbitAngle() + 180f,
                centerRadius + 3500f,
                350f,
                300f,
                500f,
                16,
                24,
                4f,
                16f);

        // Add planet 4
        PlanetAPI planet4 = Utils.createPlanet(system, systemCenter,
                "gas_giant",
                250f,
                centerRadius + 6000f,
                600f,
                Arrays.asList(
                        Conditions.VOLATILES_PLENTIFUL,
                        Conditions.HIGH_GRAVITY));
        float planet4Radius = planet4.getRadius() + 300f;
        Utils.createMagneticField(planet4, planet4Radius, (planet4Radius) / 2f, planet4.getRadius() + 50f, planet4Radius, 1f);
        // Add planet 5 moon of planet 4
        Utils.createPlanet(system, planet4,
                "frozen",
                60f,
                planet4.getRadius() + 500f,
                (planet4.getRadius() + 500f) / 10f,
                Arrays.asList(
                        Conditions.VOLATILES_PLENTIFUL,
                        Conditions.RARE_ORE_ULTRARICH,
                        Conditions.RUINS_VAST,
                        Conditions.VERY_COLD));
        // Add asteroid field 2
        Utils.createAsteroidField(system, systemCenter, planet4.getCircularOrbitAngle() + 180f,
                centerRadius + 6000f,
                600f,
                300f,
                500f,
                16,
                24,
                4f,
                16f);

        // Add custom entities
        float randomAngle2 = Utils.getRandomAngle();
        SectorEntityToken cryoSleeper = system.addCustomEntity(null, null, Entities.DERELICT_CRYOSLEEPER, Factions.NEUTRAL);
        cryoSleeper.setSensorProfile(1f);
        cryoSleeper.setDiscoverable(true);
        cryoSleeper.setCircularOrbit(systemCenter, randomAngle2, centerRadius + 8000f, 800f);
        SectorEntityToken sensorArray = system.addCustomEntity(null, null, Entities.SENSOR_ARRAY, Factions.NEUTRAL);
        sensorArray.setCircularOrbit(systemCenter, randomAngle2 + 90f, centerRadius + 8000f, 800f);
        SectorEntityToken navBuoy = system.addCustomEntity(null, null, Entities.NAV_BUOY, Factions.NEUTRAL);
        navBuoy.setCircularOrbit(systemCenter, randomAngle2 + 180f, centerRadius + 8000f, 800f);
        JumpPointAPI jumpPoint2 = Global.getFactory().createJumpPoint(null, "Fringe Jump-point");
        jumpPoint2.setStandardWormholeToHyperspaceVisual();
        jumpPoint2.setCircularOrbit(systemCenter, randomAngle2 + 270f, centerRadius + 8000f, 800f);
        system.addEntity(jumpPoint2);

        // asteroid belt 1
        Utils.createAsteroidBelt(system, 64, systemCenter, centerRadius + 9000f, 900f, "misc", "rings_dust0", 256f, 3, Color.WHITE, 256f);

        // Auto generate jump points
        system.autogenerateHyperspaceJumpPoints(true, false);

        // Clear nebula in hyperspace
        HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
        NebulaEditor editor = new NebulaEditor(plugin);
        float minRadius = plugin.getTileSize() * 2f;
        float hyperspaceRadius = system.getMaxRadiusInHyperspace();
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0f, hyperspaceRadius + minRadius, 0f, 360f);
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0f, hyperspaceRadius + minRadius, 0f, 360f, 0.25f);

        // Add procedural entities
        MiscellaneousThemeGenerator theme = new MiscellaneousThemeGenerator();
        BaseThemeGenerator.StarSystemData systemData = BaseThemeGenerator.computeSystemData(system);
        WeightedRandomPicker<String> factions = SalvageSpecialAssigner.getNearbyFactions(Utils.random, system.getCenter(), 15f, 5f, 5f);
        theme.addResearchStations(systemData, 1f, 1, 1, theme.createStringPicker(Entities.STATION_RESEARCH_REMNANT, 10f));
        theme.addMiningStations(systemData, 1f, 1, 1, theme.createStringPicker(Entities.STATION_MINING_REMNANT, 10f));
        theme.addShipGraveyard(systemData, 1f, 2, 2, factions);
        theme.addDerelictShips(systemData, 1f, 4, 4, factions);
        theme.addCaches(systemData, 1f, 2, 2, theme.createStringPicker(Entities.EQUIPMENT_CACHE, 10f));
        // Add remnant station 1 that spawns remnant fleets
        RemnantThemeGenerator.addBeacon(system, RemnantThemeGenerator.RemnantSystemType.RESURGENT);
        float station1Radius = planet2.getRadius() + 150f;
        CampaignFleetAPI station1 = Utils.addAIBattlestation(planet2, station1Radius, station1Radius / 10f);
        RemnantStationFleetManager station1Fleets = new RemnantStationFleetManager(station1, 1f, 0, 8, 15f, 16, 32);
        system.addScript(station1Fleets);
    }
}
