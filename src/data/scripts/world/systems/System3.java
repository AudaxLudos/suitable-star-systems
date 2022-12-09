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

public class System3 {
    public void generate(SectorAPI sector) {
        // Get character seed
        Random random = StarSystemGenerator.random;
        // Get star system
        StarSystemAPI system = sector.getStarSystem("system3");

        // Add system theme / tags
        system.addTag(Tags.THEME_INTERESTING);
        system.addTag(Tags.THEME_RUINS);
        system.addTag(Tags.THEME_RUINS_MAIN);
        system.addTag(Tags.THEME_UNSAFE);
        system.addTag(Tags.THEME_REMNANT);
        system.addTag(Tags.THEME_REMNANT_MAIN);
        system.addTag(Tags.THEME_REMNANT_RESURGENT);

        // Rename system with procedural name
        String systemName = SSS_Utils.generateProceduralName(Tags.STAR, system.getConstellation().getName());
        system.setBaseName(systemName);
        system.setName(systemName);

        // Create star for system
        PlanetAPI star = system.initStar(systemName.toLowerCase(), StarTypes.BLUE_SUPERGIANT, 1500f, 872f, 17f, 0.5f, 6f);

        // Create custom entities
        float randomAngle1 = random.nextFloat() * 360f;
        SectorEntityToken coronalTap = system.addCustomEntity(null, null, Entities.CORONAL_TAP, Factions.NEUTRAL);
        coronalTap.setCircularOrbit(star, (randomAngle1) % 360f, 2000f, 200f);
        JumpPointAPI jumpPoint1 = Global.getFactory().createJumpPoint(null, "Inner System Jump-point");
        jumpPoint1.setStandardWormholeToHyperspaceVisual();
        jumpPoint1.setCircularOrbit(star, (randomAngle1 + 180f) % 360f, 2000f, 200f);
        system.addEntity(jumpPoint1);

        // Create planet 1
        String planet1Name = SSS_Utils.generateProceduralName(Tags.PLANET, star.getName());
        PlanetAPI planet1 = system.addPlanet(planet1Name.toLowerCase(), star, planet1Name, "barren-bombarded", random.nextFloat() * 360f, 90f, 3000f, 300f);
        Misc.initConditionMarket(planet1);
        MarketAPI planet1Market = planet1.getMarket();
        planet1Market.addCondition(Conditions.ORE_RICH);
        planet1Market.addCondition(Conditions.RARE_ORE_RICH);
        planet1Market.addCondition(Conditions.NO_ATMOSPHERE);
        planet1Market.addCondition(Conditions.VERY_HOT);
        jumpPoint1.setRelatedPlanet(planet1);

        // Create planet 2
        String planet2Name = SSS_Utils.generateProceduralName(Tags.PLANET, star.getName());
        PlanetAPI planet2 = system.addPlanet(planet2Name.toLowerCase(), star, planet2Name, "terran", random.nextFloat() * 360f, 130f, 4000f, 400f);
        Misc.initConditionMarket(planet2);
        MarketAPI planet2Market = planet2.getMarket();
        planet2Market.addCondition(Conditions.FARMLAND_RICH);
        planet2Market.addCondition(Conditions.ORE_RICH);
        planet2Market.addCondition(Conditions.ORGANICS_ABUNDANT);
        planet2Market.addCondition(Conditions.RUINS_VAST);
        planet2Market.addCondition(Conditions.HABITABLE);
        planet2Market.addCondition(Conditions.MILD_CLIMATE);

        // Create planet 3
        String planet3Name = SSS_Utils.generateProceduralName(Tags.PLANET, star.getName());
        PlanetAPI planet3 = system.addPlanet(planet3Name.toLowerCase(), star, planet3Name, "toxic", random.nextFloat() * 360f, 90f, 5000f, 500f);
        Misc.initConditionMarket(planet3);
        MarketAPI planet3Market = planet3.getMarket();
        planet3Market.addCondition(Conditions.ORE_RICH);
        planet3Market.addCondition(Conditions.RARE_ORE_RICH);
        planet3Market.addCondition(Conditions.ORGANICS_COMMON);
        planet3Market.addCondition(Conditions.TOXIC_ATMOSPHERE);

        // Create planet 4
        String planet4Name = SSS_Utils.generateProceduralName(Tags.PLANET, star.getName());
        PlanetAPI planet4 = system.addPlanet(planet4Name.toLowerCase(), star, planet4Name, "gas_giant", random.nextFloat() * 360f, 250f, 6000f, 600f);
        Misc.initConditionMarket(planet4);
        MarketAPI planet4Market = planet4.getMarket();
        planet4Market.addCondition(Conditions.VOLATILES_ABUNDANT);
        planet4Market.addCondition(Conditions.HIGH_GRAVITY);
        float planet3Radius = planet3.getRadius();
        SSS_Utils.createMagneticField(planet3, planet3Radius + 300f, (planet3Radius + 300f) / 2f, planet3Radius + 50f, planet3Radius + 300f, 1f);

        // Create planet 5
        String planet5Name = SSS_Utils.generateProceduralName(Tags.PLANET, star.getName());
        PlanetAPI planet5 = system.addPlanet(planet5Name.toLowerCase(), star, planet5Name, "frozen3", random.nextFloat() * 360f, 130f, 7000f, 700f);
        Misc.initConditionMarket(planet5);
        MarketAPI planet5Market = planet5.getMarket();
        planet5Market.addCondition(Conditions.ORE_RICH);
        planet5Market.addCondition(Conditions.RARE_ORE_RICH);
        planet5Market.addCondition(Conditions.VOLATILES_ABUNDANT);
        planet5Market.addCondition(Conditions.VERY_COLD);

        // Create custom entities
        float randomAngle2 = random.nextFloat() * 360f;
        SectorEntityToken commRelay = system.addCustomEntity(null, null, Entities.COMM_RELAY, Factions.NEUTRAL);
        commRelay.setCircularOrbit(star, randomAngle2, 8000f, 800f);
        SectorEntityToken navBuoy = system.addCustomEntity(null, null, Entities.NAV_BUOY, Factions.NEUTRAL);
        navBuoy.setCircularOrbit(star, (randomAngle2 + 90f) % 360f, 8000f, 800f);
        SectorEntityToken sensorArray = system.addCustomEntity(null, null, Entities.SENSOR_ARRAY, Factions.NEUTRAL);
        sensorArray.setCircularOrbit(star, (randomAngle2 + 180f) % 360f, 8000f, 800f);
        JumpPointAPI jumpPoint2 = Global.getFactory().createJumpPoint(null, "Fringe Jump-point");
        jumpPoint2.setStandardWormholeToHyperspaceVisual();
        jumpPoint2.setCircularOrbit(star, (randomAngle2 + 270f) % 360f, 8000f, 800f);
        system.addEntity(jumpPoint2);

        // Add ring 2
        system.addRingBand(star, "misc", "rings_dust0", 256f, 3, Color.WHITE, 256f, 9000f, 900f, Terrain.RING, null);

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
        theme.addResearchStations(systemData, 0.75f, 1, 1, theme.createStringPicker(new Object[] {
            Entities.STATION_RESEARCH_REMNANT, Float.valueOf(10f) }));
        theme.addMiningStations(systemData, 0.75f, 1, 2, theme.createStringPicker(new Object[] {
            Entities.STATION_MINING_REMNANT, Float.valueOf(10f) }));
        theme.addShipGraveyard(systemData, 0.75f, 1, 3, theme.createStringPicker(new Object[] {
            Factions.TRITACHYON, Float.valueOf(10f),
            Factions.HEGEMONY, Float.valueOf(7f),
            Factions.INDEPENDENT, Float.valueOf(3f) }));
        theme.addDerelictShips(systemData, 0.75f, 1, 7, theme.createStringPicker(new Object[] {
            Factions.TRITACHYON, Float.valueOf(10f),
            Factions.HEGEMONY, Float.valueOf(7f),
            Factions.INDEPENDENT, Float.valueOf(3f) }));
        theme.addCaches(systemData, 0.75f, 1, 3, theme.createStringPicker(new Object[] {
            Entities.WEAPONS_CACHE_REMNANT, Float.valueOf(10f),
            Entities.WEAPONS_CACHE_SMALL_REMNANT, Float.valueOf(10f),
            Entities.SUPPLY_CACHE, Float.valueOf(10f),
            Entities.SUPPLY_CACHE_SMALL, Float.valueOf(10f),
            Entities.EQUIPMENT_CACHE, Float.valueOf(10f),
            Entities.EQUIPMENT_CACHE_SMALL, Float.valueOf(10f) }));
        RemnantThemeGenerator.addBeacon(system, RemnantSystemType.RESURGENT);
        // Add dormant or active remnant fleets
        RemnantSeededFleetManager remnantFleets = new RemnantSeededFleetManager(system, 8, 16, 8, 16, 0.75f);
        system.addScript((EveryFrameScript)remnantFleets);
        // Add remnant station 1 that spawns remnant fleets
        float station1Radius = planet2.getRadius() + 150f;
        CampaignFleetAPI station1 = SSS_Utils.addAIBattlestation(planet2, false, station1Radius, station1Radius / 10f);
        RemnantStationFleetManager station1Fleets = new RemnantStationFleetManager((SectorEntityToken)station1, 1f, 0, 8, 15f, 16, 32);
        system.addScript((EveryFrameScript)station1Fleets);
        // Add remnant station 2 that spawns remnant fleets
        float station2Radius = planet3.getRadius() + 150f;
        CampaignFleetAPI station2 = SSS_Utils.addAIBattlestation(planet3, false, station2Radius, station2Radius / 10f);
        RemnantStationFleetManager station2Fleets = new RemnantStationFleetManager((SectorEntityToken)station2, 1f, 0, 8, 15f, 16, 32);
        system.addScript((EveryFrameScript)station2Fleets);
    }
}
