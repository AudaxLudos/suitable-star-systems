package suitablestarsystems.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.MiscellaneousThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageSpecialAssigner;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import suitablestarsystems.Utils;
import suitablestarsystems.world.OmegaFleetSpawnerManager;

import java.awt.*;
import java.util.Arrays;

public class System3 {
    public void generate(SectorAPI sector) {
        // Add star system
        StarSystemAPI system = sector.getStarSystem("system_SSS_3");

        // Add system themes / tags
        system.addTag(Tags.THEME_INTERESTING);
        system.addTag(Tags.THEME_UNSAFE);

        // Add custom memory data for other mods
        system.getMemoryWithoutUpdate().set("$nex_do_not_colonize", true);

        // Rename system with procedural name
        String systemName = Utils.generateProceduralName(Tags.STAR, system.getConstellation().getName());
        system.setBaseName(systemName);
        system.setName(systemName);

        // Add star
        PlanetAPI star = system.initStar(systemName.toLowerCase(), StarTypes.BLACK_HOLE, 150f, 1000f, -10f, 0f, 25f);
        Utils.addBlackHoleVisuals(system, star);

        // Add planet 1
        PlanetAPI planet1 = Utils.createPlanet(system, "planet_sss_omega", star,
                "frozen",
                130f,
                2000f,
                200f,
                Arrays.asList(
                        Conditions.DARK,
                        Conditions.VERY_COLD,
                        Conditions.NO_ATMOSPHERE,
                        Conditions.IRRADIATED,
                        Conditions.RUINS_VAST));
        planet1.getSpec().setShieldTexture(Global.getSettings().getSpriteName("suitablestarsystems", "planetary_shield_purple_strong"));
        planet1.getSpec().setShieldThickness(0.1f);
        planet1.getSpec().setShieldColor(Color.GRAY);
        planet1.applySpecChanges();
        planet1.addTag(Tags.NOT_RANDOM_MISSION_TARGET);
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

        // Auto generate jump points
        system.autogenerateHyperspaceJumpPoints(true, false);

        // Clear nebula in hyperspace
        HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
        NebulaEditor editor = new NebulaEditor(plugin);
        float minRadius = plugin.getTileSize() * 2f;
        float radius = system.getMaxRadiusInHyperspace();
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0f, radius + minRadius, 0f, 360f);
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0f, radius + minRadius, 0f, 360f, 0.25f);

        // Add procedural entities
        MiscellaneousThemeGenerator theme = new MiscellaneousThemeGenerator();
        BaseThemeGenerator.StarSystemData systemData = BaseThemeGenerator.computeSystemData(system);
        WeightedRandomPicker<String> factions = SalvageSpecialAssigner.getNearbyFactions(Utils.random, system.getCenter(), 15f, 5f, 5f);
        theme.addResearchStations(systemData, 1f, 1, 1, theme.createStringPicker(Entities.STATION_RESEARCH_REMNANT, 10f));
        theme.addMiningStations(systemData, 1f, 1, 1, theme.createStringPicker(Entities.STATION_MINING_REMNANT, 10f));
        theme.addShipGraveyard(systemData, 1f, 2, 2, factions);
        theme.addDerelictShips(systemData, 1f, 4, 4, factions);
        theme.addCaches(systemData, 1f, 2, 2, theme.createStringPicker(Entities.EQUIPMENT_CACHE, 10f));
        // Add omega fleet spawner
        // if planet is removed, the game might crash (not tested)
        OmegaFleetSpawnerManager omegaFleetManager = new OmegaFleetSpawnerManager(planet1, 1f, 0, 8, 15f);
        system.addScript(omegaFleetManager);
    }
}
