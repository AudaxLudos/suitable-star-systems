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

public class System2 {
    public void generate(SectorAPI sector) {
        // Get character seed
        Random random = StarSystemGenerator.random;
        // Get star system
        StarSystemAPI system = sector.createStarSystem("system2");

        // Rename system with procedural name
        String systemName = ASS_Utils.generateProceduralName("star", null);
        // system.setBaseName(systemName);
        // system.setName(systemName);

        // Create star for system
        PlanetAPI star = system.initStar("system2", "star_yellow", 900f, 400f, 10f, 0.5f, 3f);

        // Create planet 1
        String planet1Name = ASS_Utils.generateProceduralName("planet", star.getName());
        PlanetAPI planet1 = system.addPlanet(planet1Name.toLowerCase(), star, planet1Name, "barren_castiron", random.nextFloat() * 360f, 90f, 2000f, 200f);
        Misc.initConditionMarket(planet1);
        MarketAPI planet1Market = planet1.getMarket();
        planet1Market.addCondition("ore_rich");
        planet1Market.addCondition("rare_ore_rich");
        planet1Market.addCondition("ruins_scattered");
        planet1Market.addCondition("no_atmosphere");
        planet1Market.addCondition("very_hot");

        // Create custom entities
        float entities1Angle = random.nextFloat() * 360f;
        SectorEntityToken stableLocation = system.addCustomEntity(null, null, "stable_location", "neutral");
        stableLocation.setCircularOrbit(star, entities1Angle, 3000f, 300f);
        SectorEntityToken stableLocation3 = system.addCustomEntity(null, null, "stable_location", "neutral");
        stableLocation3.setCircularOrbit(star, (entities1Angle + 120f) % 360f, 3000f, 300f);
        JumpPointAPI jumpPoint1 = Global.getFactory().createJumpPoint(null, "Inner System Jump-point");
        jumpPoint1.setStandardWormholeToHyperspaceVisual();
        jumpPoint1.setCircularOrbit(star, (entities1Angle - 120f) % 360f, 3000f, 300f);
        system.addEntity(jumpPoint1);

        // Create planet 2
        String planet2Name = ASS_Utils.generateProceduralName("planet", star.getName());
        PlanetAPI planet2 = system.addPlanet(planet2Name.toLowerCase(), star, planet2Name, "jungle", random.nextFloat() * 360f, 130f, 4000f, 400f);
        Misc.initConditionMarket(planet2);
        MarketAPI planet2Market = planet2.getMarket();
        planet2Market.addCondition("farmland_rich");
        planet2Market.addCondition("ore_rich");
        planet2Market.addCondition("ruins_extensive");
        planet2Market.addCondition("organics_abundant");
        planet2Market.addCondition("habitable");
        planet2Market.addCondition("mild_climate");
        planet2Market.addCondition("hot");

        // Create asteroid belt 1
        String ring1Name = ASS_Utils.generateProceduralName("asteroid_belt", star.getName());
        system.addAsteroidBelt(star, 64, 5000f, 256f, 500f, 500f, "asteroid_belt", ring1Name);
        system.addRingBand(star, "misc", "rings_dust0", 256f, 3, Color.WHITE, 256f, 5000f, 500f);

        // Create planet 3
        String planet3Name = ASS_Utils.generateProceduralName("planet", star.getName());
        PlanetAPI planet3 = system.addPlanet(planet3Name.toLowerCase(), star, planet3Name, "gas_giant", random.nextFloat() * 360f, 250f, 7000f, 700f);
        Misc.initConditionMarket(planet3);
        MarketAPI planet3Market = planet3.getMarket();
        planet3Market.addCondition("high_gravity");
        planet3Market.addCondition("volatiles_abundant");

        // Create planet 3 moon 1
        String planet4Name = ASS_Utils.generateProceduralName("planet", star.getName());
        PlanetAPI planet4 = system.addPlanet(planet4Name.toLowerCase(), planet3, planet4Name, "toxic", random.nextFloat() * 360f, 90f, 1000f, 100f);
        Misc.initConditionMarket(planet4);
        MarketAPI planet4Market = planet4.getMarket();
        planet4Market.addCondition("ore_moderate");
        planet4Market.addCondition("rare_ore_moderate");
        planet4Market.addCondition("organics_trace");
        planet1Market.addCondition("ruins_scattered");
        planet4Market.addCondition("toxic_atmosphere");

        // Create custom entities
        float entities2Angle = random.nextFloat() * 360f;
        SectorEntityToken stableLocation2 = system.addCustomEntity(null, null, "stable_location", "neutral");
        stableLocation2.setCircularOrbit(star, entities2Angle, 9000f, 900f);
        SectorEntityToken inactiveGate = system.addCustomEntity(null, null, "derelict_cryosleeper", "neutral");
        inactiveGate.setCircularOrbit(star, (entities2Angle + 120f) % 360f, 9000f, 900f);
        JumpPointAPI jumpPoint2 = Global.getFactory().createJumpPoint(null, "Fringe Jump-point");
        jumpPoint2.setStandardWormholeToHyperspaceVisual();
        jumpPoint2.setCircularOrbit(star, (entities2Angle - 120f) % 360f, 9000f, 900f);
        system.addEntity(jumpPoint2);

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
