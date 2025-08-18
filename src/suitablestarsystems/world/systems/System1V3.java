package suitablestarsystems.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.CoronalTapParticleScript;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.Constellation;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.MiscellaneousThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageSpecialAssigner;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import org.lwjgl.util.vector.Vector2f;
import suitablestarsystems.Utils;

import java.awt.*;
import java.util.Arrays;

public class System1V3 {
    public void generate(SectorAPI sector) {
        Constellation constellation = Utils.getNearestConstellation(new Vector2f(-6000, -6000));
        String nameRef = "";
        if (constellation != null) {
            nameRef = constellation.getName();
        }
        String systemName = Utils.generateProceduralName(Tags.STAR, nameRef);
        StarSystemAPI system = sector.createStarSystem(systemName);

        // Add system themes / tags
        system.addTag(Tags.THEME_INTERESTING);
        system.addTag(Tags.THEME_RUINS);
        system.addTag(Tags.THEME_RUINS_MAIN);
        system.addTag(Tags.THEME_DERELICT_CRYOSLEEPER);
        system.addTag(Tags.HAS_CORONAL_TAP);
        system.addTag("sss_system_1");

        // Add custom memory data for other mods
        system.getMemoryWithoutUpdate().set("$nex_do_not_colonize", true);

        PlanetAPI star = system.initStar(systemName.toLowerCase().replace(" ", ""), StarTypes.BLUE_SUPERGIANT, 1200f, 500f, 17f, 3f, 6f);

        // Set distance and orbit time for 1st zone
        float distanceFromStar = star.getRadius() + 150f + 100f;
        float orbitDays = distanceFromStar * 0.10f;

        // Add custom entities
        SectorEntityToken coronalTap = system.addCustomEntity(null, null, Entities.CORONAL_TAP, Factions.NEUTRAL);
        coronalTap.setCircularOrbit(star, Utils.getRandomAngle(), distanceFromStar, orbitDays);
        system.addScript(new MiscellaneousThemeGenerator.MakeCoronalTapFaceNearestStar(coronalTap));
        system.addScript(new CoronalTapParticleScript(coronalTap));

        // Set distance and orbit time for 2nd zone
        distanceFromStar = Misc.getDistance(star, coronalTap) + 120f + 1000f;
        orbitDays = distanceFromStar * 0.10f;

        // Add custom entities
        float randomAngle1 = Utils.getRandomAngle();
        SectorEntityToken inactiveGate = system.addCustomEntity(null, null, Entities.INACTIVE_GATE, Factions.NEUTRAL);
        inactiveGate.setCircularOrbit(star, randomAngle1, distanceFromStar, orbitDays);
        SectorEntityToken commRelay = system.addCustomEntity(null, null, Entities.COMM_RELAY, Factions.NEUTRAL);
        commRelay.setCircularOrbit(star, randomAngle1 + 120f, distanceFromStar, orbitDays);
        JumpPointAPI jumpPoint1 = Global.getFactory().createJumpPoint(null, "Inner System Jump-point");
        jumpPoint1.setStandardWormholeToHyperspaceVisual();
        jumpPoint1.setCircularOrbit(star, randomAngle1 + 240f, distanceFromStar, orbitDays);
        system.addEntity(jumpPoint1);

        // Set distance and orbit time for 3rd zone
        distanceFromStar = Misc.getDistance(star, inactiveGate) + 60f + 130f + 1000f;
        orbitDays = distanceFromStar * 0.10f;

        // Add planet 1
        PlanetAPI planet1 = Utils.createPlanet(system, star, "barren_castiron",
                130f, distanceFromStar, orbitDays,
                Arrays.asList(
                        Conditions.ORE_ULTRARICH,
                        Conditions.RARE_ORE_ULTRARICH,
                        Conditions.NO_ATMOSPHERE,
                        Conditions.VERY_HOT));
        if (Utils.isModEnabled("IndEvo") && Utils.ADD_INDEVO_ARTILLERY) {
            planet1.getMarket().addCondition("IndEvo_ArtilleryStationCondition");
        }
        if (Utils.isModEnabled("IndEvo") && Utils.ADD_INDEVO_MINEFIELDS) {
            planet1.getMarket().addCondition("IndEvo_mineFieldCondition");
        }
        // Add planet 1 moon 1
        PlanetAPI planet1Moon = Utils.createPlanet(system, planet1, "lava_minor",
                60f, planet1.getRadius() + 500f, orbitDays,
                Arrays.asList(
                        Conditions.ORE_ULTRARICH,
                        Conditions.RARE_ORE_ULTRARICH,
                        Conditions.RUINS_VAST,
                        Conditions.TECTONIC_ACTIVITY,
                        Conditions.HOT));

        // Set distance and orbit time for 4th zone
        distanceFromStar = Misc.getDistance(star, planet1) + Misc.getDistance(planet1, planet1Moon) + 130f + 1000f;
        orbitDays = distanceFromStar * 0.10f;

        // Add planet 2
        PlanetAPI planet2 = Utils.createPlanet(system, star, "terran",
                130f, distanceFromStar, orbitDays,
                Arrays.asList(
                        Conditions.FARMLAND_BOUNTIFUL,
                        Conditions.ORGANICS_PLENTIFUL,
                        Conditions.ORE_ULTRARICH,
                        Conditions.RUINS_VAST,
                        Conditions.HABITABLE,
                        Conditions.MILD_CLIMATE));
        if (Utils.isModEnabled("IndEvo") && Utils.ADD_INDEVO_ARTILLERY) {
            planet2.getMarket().addCondition("IndEvo_ArtilleryStationCondition");
        }
        if (Utils.isModEnabled("IndEvo") && Utils.ADD_INDEVO_MINEFIELDS) {
            planet2.getMarket().addCondition("IndEvo_mineFieldCondition");
        }
        // Add planet 2 moon 1
        PlanetAPI planet2Moon = Utils.createPlanet(system, planet2, "barren-desert",
                60f, planet2.getRadius() + 500f, orbitDays,
                Arrays.asList(
                        Conditions.ORE_ULTRARICH,
                        Conditions.RARE_ORE_ULTRARICH,
                        Conditions.ORGANICS_PLENTIFUL,
                        Conditions.THIN_ATMOSPHERE));
        // Add asteroid field 1
        Utils.createAsteroidField(system, star, planet2.getCircularOrbitAngle() + 180f, distanceFromStar,
                orbitDays, 300f, 500f, 16, 24, 4f,
                16f);

        // Set distance and orbit time for 5th zone
        distanceFromStar = Misc.getDistance(star, planet2) + Misc.getDistance(planet2, planet2Moon) + 250f + 1000f;
        orbitDays = distanceFromStar * 0.10f;

        // Add planet 3
        PlanetAPI planet3 = Utils.createPlanet(system, star, Planets.GAS_GIANT,
                250f, distanceFromStar, orbitDays,
                Arrays.asList(
                        Conditions.VOLATILES_PLENTIFUL,
                        Conditions.HIGH_GRAVITY));
        if (Utils.isModEnabled("IndEvo") && Utils.ADD_INDEVO_ARTILLERY) {
            planet3.getMarket().addCondition("IndEvo_ArtilleryStationCondition");
        }
        if (Utils.isModEnabled("IndEvo") && Utils.ADD_INDEVO_MINEFIELDS) {
            planet3.getMarket().addCondition("IndEvo_mineFieldCondition");
        }
        float planet3Radius = planet3.getRadius() + 300f;
        Utils.createMagneticField(planet3, planet3Radius, (planet3Radius) / 2f, planet3.getRadius() + 50f, planet3Radius, 1f);
        // Add planet 3 moon 1
        Utils.createPlanet(system, planet3, "frozen",
                60f, planet3.getRadius() + 500f, orbitDays,
                Arrays.asList(
                        Conditions.VOLATILES_PLENTIFUL,
                        Conditions.ORE_ULTRARICH,
                        Conditions.RUINS_VAST,
                        Conditions.VERY_COLD));
        // Add asteroid field 2
        Utils.createAsteroidField(system, star, planet3.getCircularOrbitAngle() + 180f, distanceFromStar,
                orbitDays, 300f, 500f, 16, 24, 4f,
                16f);
        SectorEntityToken cryoSleeper = system.addCustomEntity(null, null, Entities.DERELICT_CRYOSLEEPER, Factions.NEUTRAL);
        cryoSleeper.setCircularOrbit(star, planet3.getCircularOrbitAngle() + 180f, distanceFromStar, orbitDays);

        // Set distance and orbit time for 6th zone
        distanceFromStar = Misc.getDistance(star, planet3) + 130f + 60f + 75f + 1000f;
        orbitDays = distanceFromStar * 0.10f;

        // Add custom entities
        float randomAngle2 = Utils.getRandomAngle();
        SectorEntityToken sensorArray = system.addCustomEntity(null, null, Entities.SENSOR_ARRAY, Factions.NEUTRAL);
        sensorArray.setCircularOrbit(star, randomAngle2, distanceFromStar, orbitDays);
        SectorEntityToken navBuoy = system.addCustomEntity(null, null, Entities.NAV_BUOY, Factions.NEUTRAL);
        navBuoy.setCircularOrbit(star, randomAngle2 + 120f, distanceFromStar, orbitDays);
        JumpPointAPI jumpPoint2 = Global.getFactory().createJumpPoint(null, "Fringe Jump-point");
        jumpPoint2.setStandardWormholeToHyperspaceVisual();
        jumpPoint2.setCircularOrbit(star, randomAngle2 + 240f, distanceFromStar, orbitDays);
        system.addEntity(jumpPoint2);

        distanceFromStar = Misc.getDistance(star, sensorArray) + 256f + 1000f;
        orbitDays = distanceFromStar * 0.10f;

        // Add asteroid belt 1
        Utils.createAsteroidBelt(system, 64, star, distanceFromStar, orbitDays, "misc", "rings_dust0", 256f, 3, Color.WHITE, 256f);

        // Auto generate jump points
        system.autogenerateHyperspaceJumpPoints(true, false);

        // Add procedural entities
        MiscellaneousThemeGenerator theme = new MiscellaneousThemeGenerator();
        BaseThemeGenerator.StarSystemData systemData = BaseThemeGenerator.computeSystemData(system);
        WeightedRandomPicker<String> factions = SalvageSpecialAssigner.getNearbyFactions(Utils.random, system.getCenter(), 15f, 5f, 5f);
        theme.addResearchStations(systemData, 1f, 1, 1, theme.createStringPicker(Entities.STATION_RESEARCH_REMNANT, 10f));
        theme.addMiningStations(systemData, 1f, 1, 1, theme.createStringPicker(Entities.STATION_MINING_REMNANT, 10f));
        theme.addShipGraveyard(systemData, 1f, 2, 2, factions);
        theme.addDerelictShips(systemData, 1f, 4, 4, factions);
        theme.addCaches(systemData, 1f, 2, 2, theme.createStringPicker(Entities.EQUIPMENT_CACHE, 10f));

        if (!Utils.CAN_OVERRIDE_MAIN_SYSTEM_LOC) {
            constellation.getSystems().add(system);
            system.setConstellation(constellation);
            system.getLocation().set(Utils.findLocationInConstellation(constellation, Utils.random));
        } else {
            Vector2f systemLocation = new Vector2f(Utils.MAIN_SYSTEM_X_OVERRIDE, Utils.MAIN_SYSTEM_Y_OVERRIDE);
            system.getLocation().set(systemLocation);
        }
        Utils.clearHyperspaceNebulaAroundSystem(system);
    }
}
