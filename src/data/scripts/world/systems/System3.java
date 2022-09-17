package data.scripts.world.systems;

import java.awt.Color;
import java.util.Random;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;

import data.scripts.ASS_Utils;

public class System3 {
    public void generate(SectorAPI sector) {
        // Get character seed
        Random random = StarSystemGenerator.random;
        // Get star system
        StarSystemAPI system = sector.createStarSystem("system3");

        // Rename system with procedural name
        String systemName = ASS_Utils.generateProceduralName("star", null);
        // system.setBaseName(systemName);
        // system.setName(systemName);

        // Create star for system
        PlanetAPI star = system.initStar("system3", "star_blue_supergiant", 1500f, 872f, 17f, 0.5f, 6f);

        // Create custom entities
        float entities1Angle = random.nextFloat() * 360f;
        SectorEntityToken coronalTap = system.addCustomEntity(null, null, "coronal_tap", "neutral");
        coronalTap.setCircularOrbit(star, (entities1Angle) % 360f, 2000f, 200f);
        JumpPointAPI jumpPoint1 = Global.getFactory().createJumpPoint(null, "Inner System Jump-point");
        jumpPoint1.setStandardWormholeToHyperspaceVisual();
        jumpPoint1.setCircularOrbit(star, (entities1Angle + 180f) % 360f, 2000f, 200f);
        system.addEntity(jumpPoint1);

        // Add ring 1
        system.addRingBand(star, "misc", "rings_dust0", 256f, 3, Color.white, 256f, 3000f, 300f, "ring", null);

        // Create planet 1
        String planet1Name = ASS_Utils.generateProceduralName("planet", star.getName());
        PlanetAPI planet1 = system.addPlanet(planet1Name.toLowerCase(), star, planet1Name, "barren_castiron", random.nextFloat() * 360f, 90f, 4000f, 400f);
        Misc.initConditionMarket(planet1);
        MarketAPI planet1Market = planet1.getMarket();
        planet1Market.addCondition("ore_ultrarich");
        planet1Market.addCondition("rare_ore_ultrarich");
        planet1Market.addCondition("ruins_widespread");
        planet1Market.addCondition("low_gravity");
        planet1Market.addCondition("very_hot");
        planet1Market.addCondition("no_atmosphere");
        jumpPoint1.setRelatedPlanet(planet1);

        // Create planet 2
        String planet2Name = ASS_Utils.generateProceduralName("planet", star.getName());
        PlanetAPI planet2 = system.addPlanet(planet2Name.toLowerCase(), star, planet2Name, "terran", random.nextFloat() * 360f, 130f, 5000f, 500f);
        Misc.initConditionMarket(planet2);
        MarketAPI planet2Market = planet2.getMarket();
        planet2Market.addCondition("farmland_bountiful");
        planet2Market.addCondition("ore_ultrarich");
        planet2Market.addCondition("ruins_vast");
        planet2Market.addCondition("organics_plentiful");
        planet2Market.addCondition("habitable");
        planet2Market.addCondition("mild_climate");

        // Create planet 3
        String planet3Name = ASS_Utils.generateProceduralName("planet", star.getName());
        PlanetAPI planet3 = system.addPlanet(planet3Name.toLowerCase(), star, planet3Name, "toxic", random.nextFloat() * 360f, 90f, 6000f, 600f);
        Misc.initConditionMarket(planet3);
        MarketAPI planet3Market = planet3.getMarket();
        planet3Market.addCondition("ore_rich");
        planet3Market.addCondition("rare_ore_rich");
        planet3Market.addCondition("organics_common");
        planet3Market.addCondition("ruins_widespread");
        planet3Market.addCondition("hot");
        planet3Market.addCondition("toxic_atmosphere");

        // Create planet 4
        String planet4Name = ASS_Utils.generateProceduralName("planet", star.getName());
        PlanetAPI planet4 = system.addPlanet(planet4Name.toLowerCase(), star, planet4Name, "gas_giant", random.nextFloat() * 360f, 250f, 7000f, 700f);
        Misc.initConditionMarket(planet4);
        MarketAPI planet4Market = planet4.getMarket();
        planet4Market.addCondition("volatiles_plentiful");
        planet4Market.addCondition("high_gravity");

        // Create custom entities
        float entities2Angle = random.nextFloat() * 360f;
        SectorEntityToken commRelay = system.addCustomEntity(null, null, "comm_relay", "neutral");
        commRelay.setCircularOrbit(star, entities2Angle, 8000f, 800f);
        SectorEntityToken navBuoy = system.addCustomEntity(null, null, "nav_buoy", "neutral");
        navBuoy.setCircularOrbit(star, (entities2Angle + 90f) % 360f, 8000f, 800f);
        SectorEntityToken sensorArray = system.addCustomEntity(null, null, "sensor_array", "neutral");
        sensorArray.setCircularOrbit(star, (entities2Angle + 180f) % 360f, 8000f, 800f);
        JumpPointAPI jumpPoint2 = Global.getFactory().createJumpPoint(null, "Fringe Jump-point");
        jumpPoint2.setStandardWormholeToHyperspaceVisual();
        jumpPoint2.setCircularOrbit(star, (entities2Angle + 270f) % 360f, 8000f, 800f);
        system.addEntity(jumpPoint2);

        // Add ring 2
        system.addRingBand(star, "misc", "rings_dust0", 256f, 3, Color.white, 256f, 9000f, 900f, "ring", null);

        // Auto generate jump points
        system.autogenerateHyperspaceJumpPoints(true, false);

        // Clear nebula in hyperspace
        HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin)Misc.getHyperspaceTerrain().getPlugin();
        NebulaEditor editor = new NebulaEditor(plugin);
        float minRadius = plugin.getTileSize() * 2f;
        float radius = system.getMaxRadiusInHyperspace();
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0f, radius + minRadius, 0f, 360f);
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0f, radius + minRadius, 0f, 360f, 0.25f);
    }
}
