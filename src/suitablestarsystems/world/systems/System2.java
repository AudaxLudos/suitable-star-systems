package suitablestarsystems.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.Constellation;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.MiscellaneousThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageSpecialAssigner;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import org.lwjgl.util.vector.Vector2f;
import suitablestarsystems.Utils;
import suitablestarsystems.world.RemnantFleetSpawnerManager;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public class System2 {
    public void generate(SectorAPI sector) {
        boolean mainSystemLocOverride = Global.getSettings().getBoolean("mainSystemLocOverride");
        Set<Constellation> constellations = Utils.getAllConstellations();
        Constellation constellation;
        StarSystemAPI system;
        String systemName;
        if (!mainSystemLocOverride) {
            Constellation constellation1 = Objects.requireNonNull(Utils.getStarSystemWithTag("sss_system_1")).getConstellation();
            constellations.remove(constellation1);
            constellation = Utils.getNearestConstellation(constellation1.getLocation(), constellations);
        } else {
            Vector2f system1Loc = Objects.requireNonNull(Utils.getStarSystemWithTag("sss_system_1")).getHyperspaceAnchor().getLocationInHyperspace();
            constellation = Utils.getNearestConstellation(system1Loc, constellations);
        }
        systemName = Utils.generateProceduralName(Tags.STAR, constellation.getName());
        system = sector.createStarSystem(systemName);

        system.addTag(Tags.THEME_INTERESTING);
        system.addTag(Tags.THEME_UNSAFE);
        system.addTag(Tags.THEME_REMNANT);
        system.addTag(Tags.THEME_REMNANT_MAIN);
        system.addTag(Tags.THEME_REMNANT_RESURGENT);
        system.addTag("sss_system_2");

        system.getMemoryWithoutUpdate().set("$nex_do_not_colonize", true);

        Misc.addNebulaFromPNG("data/campaign/terrain/system2_nebula.png", 0, 0,
                system, "terrain", "nebula_blue", 4, 4, StarAge.OLD);

        PlanetAPI star = system.initStar(systemName.toLowerCase().replace(" ", ""), StarTypes.WHITE_DWARF, 400f, 300f, 5f, 1f, 2f);

        // Add planet 1
        PlanetAPI planet1 = Utils.createPlanet(system, "planet_sss_remnant", star,
                "rocky_ice",
                130f,
                2000f,
                200f,
                Arrays.asList(
                        Conditions.DARK,
                        Conditions.VERY_COLD,
                        Conditions.NO_ATMOSPHERE,
                        Conditions.IRRADIATED,
                        Conditions.RUINS_VAST));
        planet1.getSpec().setShieldTexture(Global.getSettings().getSpriteName("suitablestarsystems", "planetary_shield_cyan_strong"));
        planet1.getSpec().setShieldThickness(0.1f);
        planet1.applySpecChanges();
        planet1.addTag(Tags.NOT_RANDOM_MISSION_TARGET);
        float planet1Radius = planet1.getRadius() + 300f;
        Utils.createMagneticField(planet1, planet1Radius, (planet1Radius) / 2f, planet1.getRadius() + 50f, planet1Radius, 6f);
        // Add custom entities
        JumpPointAPI jumpPoint1 = Global.getFactory().createJumpPoint(null, "Inner System Jump-point");
        jumpPoint1.setStandardWormholeToHyperspaceVisual();
        jumpPoint1.setCircularOrbit(star, planet1.getCircularOrbitAngle() + 180f, 2000f, 200f);
        system.addEntity(jumpPoint1);

        // Add custom entities
        float randomAngle1 = Utils.getRandomAngle();
        SectorEntityToken commRelay = system.addCustomEntity(null, null, Entities.COMM_RELAY, Factions.NEUTRAL);
        commRelay.setCircularOrbit(star, randomAngle1, 3000f, 300f);
        SectorEntityToken navBuoy = system.addCustomEntity(null, null, Entities.NAV_BUOY, Factions.NEUTRAL);
        navBuoy.setCircularOrbit(star, randomAngle1 + 120f, 3000f, 300f);
        SectorEntityToken sensorArray = system.addCustomEntity(null, null, Entities.SENSOR_ARRAY, Factions.NEUTRAL);
        sensorArray.setCircularOrbit(star, randomAngle1 + 240f, 3000f, 300f);

        // Add custom entities
        JumpPointAPI jumpPoint2 = Global.getFactory().createJumpPoint(null, "Fringe Jump-point");
        jumpPoint2.setStandardWormholeToHyperspaceVisual();
        jumpPoint2.setCircularOrbit(star, Utils.getRandomAngle(), 4000f, 400f);
        system.addEntity(jumpPoint2);

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
        RemnantThemeGenerator.addBeacon(system, RemnantThemeGenerator.RemnantSystemType.RESURGENT);
        system.addScript(new RemnantFleetSpawnerManager(planet1, 1f, 0, 8, 15f, 16, 32));

        constellation.getSystems().add(system);
        system.setConstellation(constellation);
        system.getLocation().set(Utils.findLocationInConstellation(constellation, Utils.random));
        Utils.clearHyperspaceNebulaAroundSystem(system);
    }
}
