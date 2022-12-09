package data.scripts.world.systems;

import java.awt.Color;
import java.util.Random;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.MiscellaneousThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantSeededFleetManager;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantStationFleetManager;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator.StarSystemData;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantThemeGenerator.RemnantSystemType;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;

import data.scripts.SSS_Utils;

public class System2 {
    public void generate(SectorAPI sector) {
        // Get character seed
        Random random = StarSystemGenerator.random;
        // Get star system
        StarSystemAPI system = sector.getStarSystem("system2");

        // Add system theme / tags
        system.addTag(Tags.THEME_INTERESTING);
        system.addTag(Tags.THEME_RUINS);
        system.addTag(Tags.THEME_RUINS_MAIN);
        system.addTag(Tags.THEME_UNSAFE);
        system.addTag(Tags.THEME_REMNANT);
        system.addTag(Tags.THEME_REMNANT_MAIN);
        system.addTag(Tags.THEME_REMNANT_SUPPRESSED);

        // Rename system with procedural name
        String systemName = SSS_Utils.generateProceduralName(Tags.STAR, system.getConstellation().getName());
        system.setBaseName(systemName);
        system.setName(systemName);

        // Create star for system
        PlanetAPI star = system.initStar(systemName.toLowerCase(), StarTypes.YELLOW, 900f, 400f, 10f, 0.5f, 3f);

        // Create planet 1
        String planet1Name = SSS_Utils.generateProceduralName(Tags.PLANET, star.getName());
        PlanetAPI planet1 = system.addPlanet(planet1Name.toLowerCase(), star, planet1Name, "barren_venuslike", random.nextFloat() * 360f, 90f, 2000f, 200f);
        Misc.initConditionMarket(planet1);
        MarketAPI planet1Market = planet1.getMarket();
        planet1Market.addCondition(Conditions.ORE_MODERATE);
        planet1Market.addCondition(Conditions.RARE_ORE_MODERATE);
        planet1Market.addCondition(Conditions.NO_ATMOSPHERE);
        planet1Market.addCondition(Conditions.VERY_HOT);

        // Create custom entities
        float randomAngle1 = random.nextFloat() * 360f;
        SectorEntityToken inactiveGate = system.addCustomEntity(null, null, Entities.INACTIVE_GATE, Factions.NEUTRAL);
        inactiveGate.setCircularOrbit(star, randomAngle1, 3000f, 300f);
        SectorEntityToken commRelay = system.addCustomEntity(null, null, Entities.COMM_RELAY, Factions.NEUTRAL);
        commRelay.setCircularOrbit(star, (randomAngle1 + 120f) % 360f, 3000f, 300f);
        JumpPointAPI jumpPoint1 = Global.getFactory().createJumpPoint(null, "Inner System Jump-point");
        jumpPoint1.setStandardWormholeToHyperspaceVisual();
        jumpPoint1.setCircularOrbit(star, (randomAngle1 - 120f) % 360f, 3000f, 300f);
        system.addEntity(jumpPoint1);

        // Create planet 2
        String planet2Name = SSS_Utils.generateProceduralName(Tags.PLANET, star.getName());
        PlanetAPI planet2 = system.addPlanet(planet2Name.toLowerCase(), star, planet2Name, "jungle", random.nextFloat() * 360f, 130f, 4000f, 400f);
        Misc.initConditionMarket(planet2);
        MarketAPI planet2Market = planet2.getMarket();
        planet2Market.addCondition(Conditions.FARMLAND_ADEQUATE);
        planet2Market.addCondition(Conditions.ORE_MODERATE);
        planet2Market.addCondition(Conditions.ORGANICS_COMMON);
        planet2Market.addCondition(Conditions.RUINS_EXTENSIVE);
        planet2Market.addCondition(Conditions.HABITABLE);
        planet2Market.addCondition(Conditions.MILD_CLIMATE);
        planet2Market.addCondition(Conditions.LOW_GRAVITY);
        planet2Market.addCondition(Conditions.HOT);

        // Create asteroid belt 1
        String ring1Name = SSS_Utils.generateProceduralName(Terrain.ASTEROID_BELT, star.getName());
        system.addAsteroidBelt(star, 64, 5000f, 256f, 500f, 500f, Terrain.ASTEROID_BELT, ring1Name);
        system.addRingBand(star, "misc", "rings_dust0", 256f, 3, Color.WHITE, 256f, 5000f, 500f);

        // Create planet 3
        String planet3Name = SSS_Utils.generateProceduralName(Tags.PLANET, star.getName());
        PlanetAPI planet3 = system.addPlanet(planet3Name.toLowerCase(), star, planet3Name, "gas_giant", random.nextFloat() * 360f, 250f, 7000f, 700f);
        Misc.initConditionMarket(planet3);
        MarketAPI planet3Market = planet3.getMarket();
        planet3Market.addCondition(Conditions.VOLATILES_DIFFUSE);
        planet3Market.addCondition(Conditions.HIGH_GRAVITY);
        float planet3Radius = planet3.getRadius();
        SSS_Utils.createMagneticField(planet3, planet3Radius + 300f, (planet3Radius + 300f) / 2f, planet3Radius + 50f, planet3Radius + 300f, 1f);

        // Create planet 3 moon 1
        String planet4Name = SSS_Utils.generateProceduralName(Tags.PLANET, star.getName());
        PlanetAPI planet4 = system.addPlanet(planet4Name.toLowerCase(), planet3, planet4Name, "barren-desert", random.nextFloat() * 360f, 90f, 1000f, 100f);
        Misc.initConditionMarket(planet4);
        MarketAPI planet4Market = planet4.getMarket();
        planet4Market.addCondition(Conditions.ORE_MODERATE);
        planet4Market.addCondition(Conditions.RARE_ORE_MODERATE);
        planet4Market.addCondition(Conditions.ORGANICS_TRACE);
        planet4Market.addCondition(Conditions.THIN_ATMOSPHERE);

        // Create planet 5
        String planet5Name = SSS_Utils.generateProceduralName(Tags.PLANET, star.getName());
        PlanetAPI planet5 = system.addPlanet(planet5Name.toLowerCase(), star, planet5Name, "frozen2", random.nextFloat() * 360f, 130f, 9000f, 900f);
        Misc.initConditionMarket(planet5);
        MarketAPI planet5Market = planet5.getMarket();
        planet5Market.addCondition(Conditions.ORE_MODERATE);
        planet5Market.addCondition(Conditions.RARE_ORE_MODERATE);
        planet5Market.addCondition(Conditions.ORGANICS_COMMON);
        planet5Market.addCondition(Conditions.VERY_COLD);

        // Create custom entities
        float randomAngle2 = random.nextFloat() * 360f;
        SectorEntityToken sensorArray = system.addCustomEntity(null, null, Entities.SENSOR_ARRAY, Factions.NEUTRAL);
        sensorArray.setCircularOrbit(star, randomAngle2, 10000f, 1000f);
        SectorEntityToken stableLocation = system.addCustomEntity(null, null, Entities.STABLE_LOCATION, Factions.NEUTRAL);
        stableLocation.setCircularOrbit(star, (randomAngle2 + 120f) % 360f, 10000f, 1000f);
        JumpPointAPI jumpPoint2 = Global.getFactory().createJumpPoint(null, "Fringe Jump-point");
        jumpPoint2.setStandardWormholeToHyperspaceVisual();
        jumpPoint2.setCircularOrbit(star, (randomAngle2 - 120f) % 360f, 10000f, 1000f);
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

        // Generate custom entities
        MiscellaneousThemeGenerator theme = new MiscellaneousThemeGenerator();
        StarSystemData systemData = BaseThemeGenerator.computeSystemData(system);
        theme.addResearchStations(systemData, 0.5f, 1, 1, theme.createStringPicker(new Object[] {
           Entities.STATION_RESEARCH_REMNANT, Float.valueOf(10f) }));
        theme.addMiningStations(systemData, 0.5f, 1, 2, theme.createStringPicker(new Object[] {
           Entities.STATION_MINING_REMNANT, Float.valueOf(10f) }));
        theme.addShipGraveyard(systemData, 0.5f, 1, 2, theme.createStringPicker(new Object[] {
            Factions.TRITACHYON, Float.valueOf(10f),
            Factions.HEGEMONY, Float.valueOf(7f),
           Factions.INDEPENDENT, Float.valueOf(3f) }));
        theme.addDerelictShips(systemData, 0.5f, 1, 5, theme.createStringPicker(new Object[] {
            Factions.TRITACHYON, Float.valueOf(10f),
            Factions.HEGEMONY, Float.valueOf(7f),
            Factions.INDEPENDENT, Float.valueOf(3f) }));
        theme.addCaches(systemData, 0.5f, 1, 2, theme.createStringPicker(new Object[] {
            Entities.WEAPONS_CACHE_REMNANT, Float.valueOf(10f),
            Entities.WEAPONS_CACHE_SMALL_REMNANT, Float.valueOf(10f),
            Entities.SUPPLY_CACHE, Float.valueOf(10f),
            Entities.SUPPLY_CACHE_SMALL, Float.valueOf(10f),
            Entities.EQUIPMENT_CACHE, Float.valueOf(10f),
            Entities.EQUIPMENT_CACHE_SMALL, Float.valueOf(10f) }));
        RemnantThemeGenerator.addBeacon(system, RemnantSystemType.SUPPRESSED);
        // Add dormant or active remnant fleets
        RemnantSeededFleetManager remnantFleets = new RemnantSeededFleetManager(system, 6, 12, 6, 12, 0.5f);
        system.addScript((EveryFrameScript)remnantFleets);
        // Add remnant station 1 that spawns remnant fleets
        float station1Radius = planet2.getRadius() + 150f;
        CampaignFleetAPI station1 = SSS_Utils.addAIBattlestation(planet2, false, station1Radius, station1Radius / 10f);
        RemnantStationFleetManager station1Fleets = new RemnantStationFleetManager((SectorEntityToken)station1, 1f, 0, 8, 20f, 8, 16);
        system.addScript((EveryFrameScript)station1Fleets);
        // Add remnant station 2 that spawns remnant fleets
        float station2Radius = planet4.getRadius() + 150f;
        CampaignFleetAPI station2 = SSS_Utils.addAIBattlestation(planet4, false, station2Radius, station2Radius / 10f);
        RemnantStationFleetManager station2Fleets = new RemnantStationFleetManager((SectorEntityToken)station2, 1f, 0, 8, 20f, 8, 16);
        system.addScript((EveryFrameScript)station2Fleets);
    }
}
