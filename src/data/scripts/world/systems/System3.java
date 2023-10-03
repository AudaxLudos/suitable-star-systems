package data.scripts.world.systems;

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
import com.fs.starfarer.api.impl.campaign.CoronalTapParticleScript;
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
import com.fs.starfarer.api.impl.campaign.terrain.AsteroidFieldTerrainPlugin.AsteroidFieldParams;
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
		system.addTag(Tags.HAS_CORONAL_TAP);

		// Rename system with procedural name
		String systemName = SSS_Utils.generateProceduralName(Tags.STAR, system.getConstellation().getName());
		system.setBaseName(systemName);
		system.setName(systemName);

		// Create star for system
		PlanetAPI star = system.initStar(systemName.toLowerCase(), StarTypes.BLUE_SUPERGIANT, 1500f, 850f, 17f, 0.5f, 6f);

		// Create custom entities
		SectorEntityToken coronalTap = system.addCustomEntity(null, null, Entities.CORONAL_TAP, Factions.NEUTRAL);
		coronalTap.setCircularOrbit(star, random.nextFloat() * 360f, star.getRadius() + 250f, -175f);
		coronalTap.setDiscoverable(true);
		coronalTap.setSensorProfile(1f);
		system.addScript(new MiscellaneousThemeGenerator.MakeCoronalTapFaceNearestStar(coronalTap));
		system.addScript((EveryFrameScript) new CoronalTapParticleScript(coronalTap));
		SSS_Utils.createAccretionDisk((SectorEntityToken) star, star.getRadius(), 0f);

		// Create planet 1
		String planet1Name = SSS_Utils.generateProceduralName(Tags.PLANET, star.getName());
		PlanetAPI planet1 = system.addPlanet(planet1Name.toLowerCase(), star, planet1Name, "barren-bombarded", random.nextFloat() * 360f, 90f, 7000f, -700f);
		Misc.initConditionMarket(planet1);
		MarketAPI planet1Market = planet1.getMarket();
		planet1Market.addCondition(Conditions.ORE_ULTRARICH);
		planet1Market.addCondition(Conditions.RARE_ORE_ULTRARICH);
		planet1Market.addCondition(Conditions.RUINS_VAST);
		planet1Market.addCondition(Conditions.NO_ATMOSPHERE);
		planet1Market.addCondition(Conditions.VERY_HOT);

		// Create jumpoint
		float randomAngle1 = random.nextFloat() * 360f;
		JumpPointAPI jumpPoint1 = Global.getFactory().createJumpPoint(null, "Fringe Jump-point");
		jumpPoint1.setStandardWormholeToHyperspaceVisual();
		jumpPoint1.setCircularOrbit(star, randomAngle1, 8000f, -800f);
		system.addEntity(jumpPoint1);

		// Add asteroid field 1
		SectorEntityToken asteroidField1 = system.addTerrain(Terrain.ASTEROID_FIELD,
				new AsteroidFieldParams(
						300f, // min radius
						500f, // max radius
						16, // min asteroid count
						24, // max asteroid count
						4f, // min asteroid radius
						16f, // max asteroid radius
						SSS_Utils.generateProceduralName(Terrain.ASTEROID_FIELD, planet1Name)));
		asteroidField1.setCircularOrbit(star, randomAngle1 + 180f, 8000f, -800f);

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
		theme.addResearchStations(systemData, 0.75f, 1, 1, theme.createStringPicker(new Object[] {
				Entities.STATION_RESEARCH_REMNANT, Float.valueOf(10f) }));
		theme.addMiningStations(systemData, 0.75f, 1, 1, theme.createStringPicker(new Object[] {
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
		system.addScript((EveryFrameScript) remnantFleets);
		// Add remnant station 1 that spawns remnant fleets
		float station1Radius = planet1.getRadius() + 150f;
		CampaignFleetAPI station1 = SSS_Utils.addAIBattlestation(planet1, station1Radius, station1Radius / 10f);
		RemnantStationFleetManager station1Fleets = new RemnantStationFleetManager((SectorEntityToken) station1, 1f, 0, 8, 15f, 16, 32);
		system.addScript((EveryFrameScript) station1Fleets);
	}
}
