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

public class System1 {
	public void generate(SectorAPI sector) {
		// Get character seed
		Random random = StarSystemGenerator.random;
		// Get star system
		StarSystemAPI system = sector.getStarSystem("system1");

		// Add system theme / tags
		system.addTag(Tags.THEME_INTERESTING);
		system.addTag(Tags.THEME_RUINS);
		system.addTag(Tags.THEME_RUINS_MAIN);
		system.addTag(Tags.THEME_UNSAFE);
		system.addTag(Tags.THEME_REMNANT);
		system.addTag(Tags.THEME_REMNANT_MAIN);
		system.addTag(Tags.THEME_REMNANT_DESTROYED);

		// Rename system with procedural name
		String systemName = SSS_Utils.generateProceduralName(Tags.STAR, system.getConstellation().getName());
		system.setBaseName(systemName);
		system.setName(systemName);

		// Create star for system
		PlanetAPI star = system.initStar(systemName.toLowerCase(), StarTypes.ORANGE, 750f, 400f, 10f, 05f, 3f);

		// Create custom entities
		float randomAngle1 = random.nextFloat() * 360f;
		SectorEntityToken commRelay = system.addCustomEntity(null, null, Entities.COMM_RELAY, Factions.NEUTRAL);
		commRelay.setCircularOrbit(star, randomAngle1, 2000f, 200f);
		SectorEntityToken derelictCryosleeper = system.addCustomEntity(null, null, Entities.DERELICT_CRYOSLEEPER, Factions.NEUTRAL);
		derelictCryosleeper.setCircularOrbit(star, (randomAngle1 + 120f) % 360f, 2000f, 200f);
		JumpPointAPI jumpPoint1 = Global.getFactory().createJumpPoint(null, "Inner System Jump-point");
		jumpPoint1.setStandardWormholeToHyperspaceVisual();
		jumpPoint1.setCircularOrbit(star, (randomAngle1 - 120f) % 360f, 2000f, 200f);
		system.addEntity(jumpPoint1);

		// Create planet 1
		String planet1Name = SSS_Utils.generateProceduralName(Tags.PLANET, star.getName());
		PlanetAPI planet1 = system.addPlanet(planet1Name.toLowerCase(), star, planet1Name, "barren_castiron", random.nextFloat() * 360f, 90f, 3000f, 300f);
		Misc.initConditionMarket(planet1);
		MarketAPI planet1Market = planet1.getMarket();
		planet1Market.addCondition(Conditions.ORE_SPARSE);
		planet1Market.addCondition(Conditions.RARE_ORE_SPARSE);
		planet1Market.addCondition(Conditions.NO_ATMOSPHERE);
		planet1Market.addCondition(Conditions.VERY_HOT);

		// Create planet 2
		String planet2Name = SSS_Utils.generateProceduralName(Tags.PLANET, star.getName());
		PlanetAPI planet2 = system.addPlanet(planet2Name.toLowerCase(), star, planet2Name, "arid", random.nextFloat() * 360f, 130f, 4000f, 400f);
		Misc.initConditionMarket(planet2);
		MarketAPI planet2Market = planet2.getMarket();
		planet2Market.addCondition(Conditions.FARMLAND_POOR);
		planet2Market.addCondition(Conditions.ORE_SPARSE);
		planet2Market.addCondition(Conditions.ORGANICS_TRACE);
		planet2Market.addCondition(Conditions.RUINS_WIDESPREAD);
		planet2Market.addCondition(Conditions.HABITABLE);
		planet2Market.addCondition(Conditions.LOW_GRAVITY);
		planet2Market.addCondition(Conditions.HOT);

		// Create planet 3
		String planet3Name = SSS_Utils.generateProceduralName(Tags.PLANET, star.getName());
		PlanetAPI planet3 = system.addPlanet(planet3Name.toLowerCase(), star, planet3Name, "frozen1", random.nextFloat() * 360f, 130f, 5000f, 500f);
		Misc.initConditionMarket(planet3);
		MarketAPI planet3Market = planet3.getMarket();
		planet3Market.addCondition(Conditions.ORE_SPARSE);
		planet3Market.addCondition(Conditions.RARE_ORE_SPARSE);
		planet3Market.addCondition(Conditions.VOLATILES_TRACE);
		planet3Market.addCondition(Conditions.VERY_COLD);

		// Create planet 4
		String planet5Name = SSS_Utils.generateProceduralName(Tags.PLANET, star.getName());
		PlanetAPI planet5 = system.addPlanet(planet5Name.toLowerCase(), star, planet5Name, "gas_giant", random.nextFloat() * 360f, 250f, 7000f, 700f);
		Misc.initConditionMarket(planet5);
		MarketAPI planet5Market = planet5.getMarket();
		planet5Market.addCondition(Conditions.VOLATILES_TRACE);
		planet5Market.addCondition(Conditions.HIGH_GRAVITY);
		float planet5Radius = planet5.getRadius();
		SSS_Utils.createMagneticField(planet5, planet5Radius + 300f, (planet5Radius + 300f) / 2f, planet5Radius + 50f, planet5Radius + 300f, 1f);

		// Create planet 3
		String planet4Name = SSS_Utils.generateProceduralName(Tags.PLANET, star.getName());
		PlanetAPI planet4 = system.addPlanet(planet4Name.toLowerCase(), star, planet4Name, "barren-desert", random.nextFloat() * 360f, 90f, 6000f, 600f);
		Misc.initConditionMarket(planet4);
		MarketAPI planet4Market = planet4.getMarket();
		planet4Market.addCondition(Conditions.ORE_SPARSE);
		planet4Market.addCondition(Conditions.RARE_ORE_SPARSE);
		planet4Market.addCondition(Conditions.ORGANICS_TRACE);
		planet4Market.addCondition(Conditions.THIN_ATMOSPHERE);

		// Create custom entities
		float randomAngle2 = random.nextFloat() * 360f;
		SectorEntityToken stableLocation2 = system.addCustomEntity(null, null, Entities.STABLE_LOCATION, Factions.NEUTRAL);
		stableLocation2.setCircularOrbit(star, randomAngle2, 8000f, 800f);
		SectorEntityToken stableLocation3 = system.addCustomEntity(null, null, Entities.STABLE_LOCATION, Factions.NEUTRAL);
		stableLocation3.setCircularOrbit(star, (randomAngle2 + 120f) % 360f, 8000f, 800f);
		JumpPointAPI jumpPoint2 = Global.getFactory().createJumpPoint(null, "Fringe Jump-point");
		jumpPoint2.setStandardWormholeToHyperspaceVisual();
		jumpPoint2.setCircularOrbit(star, (randomAngle2 - 120f) % 360f, 8000f, 800f);
		system.addEntity(jumpPoint2);

		// Create asteroid belt 2
		String ring2Name = SSS_Utils.generateProceduralName(Terrain.ASTEROID_BELT, star.getName());
		system.addAsteroidBelt(star, 64, 9000f, 256f, 900f, 900f, Terrain.ASTEROID_BELT, ring2Name);
		system.addRingBand(star, "misc", "rings_dust0", 256f, 3, Color.WHITE, 256f, 9000f, 900f);

		// Auto generate jump points
		system.autogenerateHyperspaceJumpPoints(true, false);

		// Clear nebula in hyperspace
		HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
		NebulaEditor editor = new NebulaEditor(plugin);
		float minRadius = plugin.getTileSize() * 2f;
		float radius = system.getMaxRadiusInHyperspace();
		editor.clearArc(system.getLocation().x, system.getLocation().y, 0f, radius + minRadius, 0f, 360f);
		editor.clearArc(system.getLocation().x, system.getLocation().y, 0f, radius + minRadius, 0f, 360f, 0.25f);

		// Generate custom entities
		MiscellaneousThemeGenerator theme = new MiscellaneousThemeGenerator();
		StarSystemData systemData = BaseThemeGenerator.computeSystemData(system);
		theme.addResearchStations(systemData, 0.25f, 1, 1, theme.createStringPicker(new Object[] {
				Entities.STATION_RESEARCH_REMNANT, Float.valueOf(10f) }));
		theme.addMiningStations(systemData, 0.25f, 1, 2, theme.createStringPicker(new Object[] {
				Entities.STATION_MINING_REMNANT, Float.valueOf(10f) }));
		theme.addShipGraveyard(systemData, 0.25f, 1, 1, theme.createStringPicker(new Object[] {
				Factions.TRITACHYON, Float.valueOf(10f),
				Factions.HEGEMONY, Float.valueOf(7f),
				Factions.INDEPENDENT, Float.valueOf(3f) }));
		theme.addDerelictShips(systemData, 0.25f, 1, 3, theme.createStringPicker(new Object[] {
				Factions.TRITACHYON, Float.valueOf(10f),
				Factions.HEGEMONY, Float.valueOf(7f),
				Factions.INDEPENDENT, Float.valueOf(3f) }));
		theme.addCaches(systemData, 0.25f, 1, 1, theme.createStringPicker(new Object[] {
				Entities.WEAPONS_CACHE_REMNANT, Float.valueOf(10f),
				Entities.WEAPONS_CACHE_SMALL_REMNANT, Float.valueOf(10f),
				Entities.SUPPLY_CACHE, Float.valueOf(10f),
				Entities.SUPPLY_CACHE_SMALL, Float.valueOf(10f),
				Entities.EQUIPMENT_CACHE, Float.valueOf(10f),
				Entities.EQUIPMENT_CACHE_SMALL, Float.valueOf(10f) }));
		RemnantThemeGenerator.addBeacon(system, RemnantSystemType.DESTROYED);
		// Add dormant or active remnant fleets
		RemnantSeededFleetManager remnantFleets = new RemnantSeededFleetManager(system, 4, 8, 4, 8, 0.25f);
		system.addScript((EveryFrameScript) remnantFleets);
		// Add remnant station 1 that spawns remnant fleets
		float station1Radius = planet2.getRadius() + 150f;
		CampaignFleetAPI station1 = SSS_Utils.addAIBattlestation(planet2, false, station1Radius, station1Radius / 10f);
		RemnantStationFleetManager station1Fleets = new RemnantStationFleetManager((SectorEntityToken) station1, 1f, 0, 8, 25f, 4, 8);
		system.addScript((EveryFrameScript) station1Fleets);
		// Add remnant station 2 that spawns remnant fleets
		float station2Radius = planet4.getRadius() + 150f;
		CampaignFleetAPI station2 = SSS_Utils.addAIBattlestation(planet4, false, station2Radius, station2Radius / 10f);
		RemnantStationFleetManager station2Fleets = new RemnantStationFleetManager((SectorEntityToken) station2, 1f, 0, 8, 25f, 4, 8);
		system.addScript((EveryFrameScript) station2Fleets);
	}
}
