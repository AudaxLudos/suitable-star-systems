package data.scripts.world.systems;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.MiscellaneousThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantStationFleetManager;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator.StarSystemData;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantThemeGenerator.RemnantSystemType;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;

import data.scripts.SSS_Utils;

public class System2 {
	public void generate(SectorAPI sector) {
		// star system
		StarSystemAPI system = sector.getStarSystem("system2");

		// system themes / tags
		system.addTag(Tags.THEME_INTERESTING);
		system.addTag(Tags.THEME_RUINS);
		system.addTag(Tags.THEME_RUINS_MAIN);
		system.addTag(Tags.THEME_UNSAFE);
		system.addTag(Tags.THEME_REMNANT);
		system.addTag(Tags.THEME_REMNANT_MAIN);
		system.addTag(Tags.THEME_REMNANT_RESURGENT);
		system.addTag(Tags.THEME_DERELICT_CRYOSLEEPER);

		// Rename system with procedural name
		String systemName = SSS_Utils.generateProceduralName(Tags.STAR, system.getConstellation().getName());
		system.setBaseName(systemName);
		system.setName(systemName);

		// star for system
		PlanetAPI star = system.initStar(systemName.toLowerCase(), StarTypes.RED_SUPERGIANT, 1800f, 850f, 5f, 0.5f, 2f);

		// custom entities
		float randomAngle1 = SSS_Utils.getRandomAngle();
		SectorEntityToken commRelay = system.addCustomEntity(null, null, Entities.COMM_RELAY, Factions.NEUTRAL);
		commRelay.setCircularOrbit(star, randomAngle1, 3000f, 300f);
		JumpPointAPI jumpPoint1 = Global.getFactory().createJumpPoint(null, "Inner System Jump-point");
		jumpPoint1.setStandardWormholeToHyperspaceVisual();
		jumpPoint1.setCircularOrbit(star, (randomAngle1 + 180f) % 360f, 3000f, 300f);
		system.addEntity(jumpPoint1);

		// planet 1
		PlanetAPI planet1 = SSS_Utils.createPlanet(system, star,
				"frozen3",
				130f,
				4500f,
				450f,
				new ArrayList<>(Arrays.asList(
						Conditions.RARE_ORE_ULTRARICH,
						Conditions.VOLATILES_PLENTIFUL,
						Conditions.RUINS_VAST,
						Conditions.VERY_COLD)));
		// planet 2 moon of planet 1
		SSS_Utils.createPlanet(system, planet1,
				"barren",
				60f,
				planet1.getRadius() + 500f,
				(planet1.getRadius() + 500f) / 10f,
				new ArrayList<>(Arrays.asList(
						Conditions.ORE_ULTRARICH,
						Conditions.RARE_ORE_ULTRARICH,
						Conditions.NO_ATMOSPHERE,
						Conditions.VERY_HOT)));

		// planet 3
		PlanetAPI planet3 = SSS_Utils.createPlanet(system, star,
				"terran",
				130f,
				6000f,
				600f,
				new ArrayList<>(Arrays.asList(
						Conditions.FARMLAND_BOUNTIFUL,
						Conditions.ORGANICS_PLENTIFUL,
						Conditions.ORE_ULTRARICH,
						Conditions.RUINS_VAST,
						Conditions.HABITABLE,
						Conditions.MILD_CLIMATE)));
		// asteroid field 1
		SSS_Utils.createAsteroidField(system, star, (planet3.getCircularOrbitAngle() + 180f) % 360f,
				6000f,
				600f,
				300f,
				500f,
				16,
				24,
				4f,
				16f);

		// asteroid belt 1
		SSS_Utils.createAsteroidBelt(system, 64, star, 7000f, 700f, "misc", "rings_dust0", 256f, 3, Color.WHITE, 256f);

		// custom entities
		float randomAngle2 = SSS_Utils.getRandomAngle();
		SectorEntityToken sensorArray = system.addCustomEntity(null, null, Entities.SENSOR_ARRAY, Factions.NEUTRAL);
		sensorArray.setCircularOrbit(star, randomAngle2, 8000f, 800f);
		SectorEntityToken navBuoy = system.addCustomEntity(null, null, Entities.NAV_BUOY, Factions.NEUTRAL);
		navBuoy.setCircularOrbit(star, (randomAngle2 + 180f) % 360f, 8000f, 800f);

		// planet 4
		PlanetAPI planet4 = SSS_Utils.createPlanet(system, star,
				"gas_giant",
				250f,
				9000f,
				900f,
				new ArrayList<>(Arrays.asList(
						Conditions.VOLATILES_PLENTIFUL,
						Conditions.HIGH_GRAVITY)));
		float planet4Radius = planet4.getRadius() + 300f;
		SSS_Utils.createMagneticField(planet4, planet4Radius, (planet4Radius) / 2f, planet4.getRadius() + 50f, planet4Radius, 1f);

		// planet 5
		PlanetAPI planet5 = SSS_Utils.createPlanet(system, star,
				"frozen2",
				130f,
				10000f,
				1000f,
				new ArrayList<>(Arrays.asList(
						Conditions.ORE_ULTRARICH,
						Conditions.VOLATILES_PLENTIFUL,
						Conditions.ORGANICS_COMMON,
						Conditions.VERY_COLD)));
		// asteroid field 2
		SSS_Utils.createAsteroidField(system, star, (planet5.getCircularOrbitAngle() + 180f) % 360f,
				10000f,
				1000f,
				300f,
				500f,
				16,
				24,
				4f,
				16f);

		// custom entities
		float randomAngle3 = SSS_Utils.getRandomAngle();
		SectorEntityToken cryoSleeper = system.addCustomEntity(null, null, Entities.DERELICT_CRYOSLEEPER, Factions.NEUTRAL);
		cryoSleeper.setCircularOrbit(star, randomAngle3, 11000f, 1100f);
		JumpPointAPI jumpPoint2 = Global.getFactory().createJumpPoint(null, "Fringe Jump-point");
		jumpPoint2.setStandardWormholeToHyperspaceVisual();
		jumpPoint2.setCircularOrbit(star, (randomAngle3 + 180f) % 360f, 11000f, 1100f);
		system.addEntity(jumpPoint2);

		// asteroid belt 2
		SSS_Utils.createAsteroidBelt(system, 64, star, 12000f, 1200f, "misc", "rings_dust0", 256f, 3, Color.WHITE, 256f);

		// Auto generate jump points
		system.autogenerateHyperspaceJumpPoints(true, false);

		// Clear nebula in hyperspace
		HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
		NebulaEditor editor = new NebulaEditor(plugin);
		float minRadius = plugin.getTileSize() * 2f;
		float radius = system.getMaxRadiusInHyperspace();
		editor.clearArc(system.getLocation().x, system.getLocation().y, 0f, radius + minRadius, 0f, 360f);
		editor.clearArc(system.getLocation().x, system.getLocation().y, 0f, radius + minRadius, 0f, 360f, 0.25f);

		// custom entities
		MiscellaneousThemeGenerator theme = new MiscellaneousThemeGenerator();
		StarSystemData systemData = BaseThemeGenerator.computeSystemData(system);
		theme.addResearchStations(systemData, 1f, 1, 1, theme.createStringPicker(new Object[] {
				Entities.STATION_RESEARCH_REMNANT, Float.valueOf(10f) }));
		theme.addMiningStations(systemData, 1f, 1, 1, theme.createStringPicker(new Object[] {
				Entities.STATION_MINING_REMNANT, Float.valueOf(10f) }));
		theme.addShipGraveyard(systemData, 1f, 2, 2, theme.createStringPicker(new Object[] {
				Factions.TRITACHYON, Float.valueOf(10f),
				Factions.HEGEMONY, Float.valueOf(7f),
				Factions.INDEPENDENT, Float.valueOf(3f) }));
		theme.addDerelictShips(systemData, 1f, 5, 5, theme.createStringPicker(new Object[] {
				Factions.TRITACHYON, Float.valueOf(10f),
				Factions.HEGEMONY, Float.valueOf(7f),
				Factions.INDEPENDENT, Float.valueOf(3f) }));
		theme.addCaches(systemData, 1f, 2, 2, theme.createStringPicker(new Object[] {
				Entities.EQUIPMENT_CACHE, Float.valueOf(10f) }));
		RemnantThemeGenerator.addBeacon(system, RemnantSystemType.RESURGENT);
		// Add remnant station 1 that spawns remnant fleets
		float station1Radius = planet3.getRadius() + 150f;
		CampaignFleetAPI station1 = SSS_Utils.addAIBattlestation(planet3, station1Radius, station1Radius / 10f);
		RemnantStationFleetManager station1Fleets = new RemnantStationFleetManager((SectorEntityToken) station1, 1f, 0, 8, 15f, 16, 32);
		system.addScript((EveryFrameScript) station1Fleets);
	}
}
