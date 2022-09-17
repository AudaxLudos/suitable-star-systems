package data.scripts.world.systems;

import java.awt.Color;
import java.util.Random;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.NascentGravityWellAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator.StarSystemData;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.StarCoronaTerrainPlugin;
import com.fs.starfarer.api.util.Misc;

import data.campaign.procgen.themes.CustomThemeGenerator;
import data.scripts.ASS_Utils;

public class System4 {
    public void generate(SectorAPI sector) {
        // Get character seed
        Random random = new Random(StarSystemGenerator.random.nextLong());
        // Get star system
        StarSystemAPI system = sector.getStarSystem("system4");

        // Rename system with procedural name
        String systemName = ASS_Utils.generateProceduralName("star", null);
        // system.setBaseName(systemName);
        // system.setName(systemName);

        // Create star for system
        PlanetAPI tempStar = system.initStar("tempStar", "star_orange", 750f, 400f);
        SectorEntityToken systemCenter = system.initNonStarCenter();

        // Create custom entities
        float randomAngle1 = random.nextFloat() * 360f;
        SectorEntityToken inactiveGate = system.addCustomEntity(null, null, "inactive_gate", "neutral");
        inactiveGate.setCircularOrbit(systemCenter, randomAngle1, 2000f, 200f);
        SectorEntityToken navBuoy = system.addCustomEntity(null, null, "nav_buoy", "neutral");
        navBuoy.setCircularOrbit(systemCenter, (randomAngle1 + 90f) % 360f, 2000f, 200f);
        SectorEntityToken commRelay = system.addCustomEntity(null, null, "comm_relay", "neutral");
        commRelay.setCircularOrbit(systemCenter, (randomAngle1 + 180f) % 360f, 2000f, 200f);
        SectorEntityToken sensorArray = system.addCustomEntity(null, null, "sensor_array", "neutral");
        sensorArray.setCircularOrbit(systemCenter, (randomAngle1 + 270f) % 360f, 2000f, 200f);

        // Create planet 1
        float randomAngle2 = random.nextFloat() * 360f;
        String planet1Name = ASS_Utils.generateProceduralName("planet", systemCenter.getName());
        PlanetAPI planet1 = system.addPlanet(planet1Name.toLowerCase(), systemCenter, planet1Name, "barren_castiron", randomAngle2, 220f, 4000f, 400f);
        Misc.initConditionMarket(planet1);
        MarketAPI planet1Market = planet1.getMarket();
        planet1Market.addCondition("ore_ultrarich");
        planet1Market.addCondition("rare_ore_ultrarich");
        planet1Market.addCondition("organics_plentiful");
        planet1Market.addCondition("volatiles_plentiful");
        planet1Market.addCondition("ruins_vast");
        planet1Market.addCondition("no_atmosphere");
        planet1Market.addCondition("very_hot");
        // Add accretion disk to planet 1
        ASS_Utils.createAccretionDisk(planet1, 8, planet1.getRadius() + 512f);

        // Create planet 2
        String planet2Name = ASS_Utils.generateProceduralName("planet", systemCenter.getName());
        PlanetAPI planet2 = system.addPlanet(planet2Name.toLowerCase(), systemCenter, planet2Name, "terran", (randomAngle2 + 120f) % 360f, 220f, 4000f, 400f);
        Misc.initConditionMarket(planet2);
        MarketAPI planet2Market = planet2.getMarket();
        planet2Market.addCondition("ore_ultrarich");
        planet2Market.addCondition("organics_plentiful");
        planet2Market.addCondition("solar_array");
        planet2Market.addCondition("ruins_vast");
        planet2Market.addCondition("habitable");
        planet2Market.addCondition("mild_climate");
        planet2Market.addCondition("low_gravity");
        // Add mirrors and shades to planet 2
        float randomAngle3 = random.nextFloat() * 360f;
        float mirrorOrbitRadius = planet2.getRadius() + 300f;
        for (int i = 0; i < 8; i++) {
            String mirrorType = i < 4 ? "stellar_mirror" : "stellar_shade";
            SectorEntityToken planet2Mirror = system.addCustomEntity(null, null, mirrorType, "neutral");
            planet2Mirror.setCircularOrbitPointingDown(planet2, (randomAngle3 + 45f * i) % 360f, mirrorOrbitRadius, mirrorOrbitRadius / 10f);
            planet2Mirror.setCustomDescriptionId(mirrorType);
        }

        // Create planet 3
        String planet3Name = ASS_Utils.generateProceduralName("planet", systemCenter.getName());
        PlanetAPI planet3 = system.addPlanet(planet3Name.toLowerCase(), systemCenter, planet3Name, "gas_giant", (randomAngle2 - 120f) % 360f, 220f, 4000f, 400f);
        Misc.initConditionMarket(planet3);
        MarketAPI planet3Market = planet3.getMarket();
        planet3Market.addCondition("ore_ultrarich");
        planet3Market.addCondition("rare_ore_ultrarich");
        planet3Market.addCondition("volatiles_plentiful");
        planet3Market.addCondition("organics_plentiful");
        planet3Market.addCondition("no_atmosphere");
        planet3Market.addCondition("ruins_vast");
        planet3Market.addCondition("very_hot");
        // Add ring and magnetic field to planet 3
        ASS_Utils.createMagneticField(planet3, 500f, planet3.getRadius() + 750f, planet3.getRadius() + 500f, planet3.getRadius() + 1000f, 1f);
        system.addRingBand(planet3, "misc", "rings_dust0", 256f, 3, Color.white, 256f, planet3.getRadius() + 500f, planet3.getRadius() + 500f / 10f, "ring", null);

        // Add ring and magnetic field around system
        ASS_Utils.createMagneticField(systemCenter, 500f, 6250f, 6000f, 6500f, 1f);
        system.addRingBand(systemCenter, "misc", "rings_dust0", 256f, 3, Color.white, 256f, 6000f, 600f, "ring", null);

        // Clear nebula in hyperspace
        HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin)Misc.getHyperspaceTerrain().getPlugin();
        NebulaEditor editor = new NebulaEditor(plugin);
        float minRadius = plugin.getTileSize() * 2f;
        float radius = system.getMaxRadiusInHyperspace();
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0f, radius + minRadius, 0f, 360f);
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0f, radius + minRadius, 0f, 360f, 0.25f);

        // Generate custom entities
        CustomThemeGenerator theme = new CustomThemeGenerator();
        StarSystemData systemData = CustomThemeGenerator.computeSystemData(system);
        theme.addResearchStations(systemData, 1f, 1, 1, theme.createStringPicker(new Object[] {
            "station_research_remnant", Float.valueOf(10f) }));
        theme.addMiningStations(systemData, 1f, 1, 2, theme.createStringPicker(new Object[] {
            "station_mining_remnant", Float.valueOf(10f) }));
        theme.addShipGraveyard(systemData, 1f, 1, 4, theme.createStringPicker(new Object[] {
            "tritachyon", Float.valueOf(10f),
            "hegemony", Float.valueOf(7f),
            "independent", Float.valueOf(3f) }));
        theme.addDerelictShips(systemData, 1f, 1, 9, theme.createStringPicker(new Object[] {
            "tritachyon", Float.valueOf(10f),
            "hegemony", Float.valueOf(7f),
            "independent", Float.valueOf(3f) }));
        theme.addCaches(systemData, 1f, 1, 4, theme.createStringPicker(new Object[] {
            "weapons_cache_remnant", Float.valueOf(10f),
            "weapons_cache_small_remnant", Float.valueOf(10f),
            "supply_cache", Float.valueOf(10f),
            "supply_cache_small", Float.valueOf(10f),
            "equipment_cache", Float.valueOf(10f),
            "equipment_cache_small", Float.valueOf(10f) }));

        // Remove star after entity generation
        system.removeEntity((SectorEntityToken)tempStar);
        StarCoronaTerrainPlugin coronaPlugin = Misc.getCoronaFor(tempStar);
        if (coronaPlugin != null)
            system.removeEntity(coronaPlugin.getEntity());
        system.setStar(null);

        // Create hyperspace anchor in hyperspace
        system.generateAnchorIfNeeded();
        LocationAPI hyper = Global.getSector().getHyperspace();
        NascentGravityWellAPI well = Global.getSector().createNascentGravityWell((SectorEntityToken)systemCenter, 50f);
        well.addTag("no_entity_tooltip");
        well.setColorOverride(new Color(125, 50, 255));
        hyper.addEntity((SectorEntityToken)well);
        well.autoUpdateHyperLocationBasedOnInSystemEntityAtRadius((SectorEntityToken)systemCenter, 0f);
    }
}
