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
import com.fs.starfarer.api.impl.campaign.CoronalTapParticleScript;
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

public class System3 {
	public void generate(SectorAPI sector) {
		// star system
		StarSystemAPI system = sector.getStarSystem("system3");

		// system themes / tags
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

		// star for system
		PlanetAPI star = system.initStar(systemName.toLowerCase(), StarTypes.BLUE_SUPERGIANT, 1500f, 850f, 17f, 0.5f, 6f);

		// custom entities
		SectorEntityToken coronalTap = system.addCustomEntity(null, null, Entities.CORONAL_TAP, Factions.NEUTRAL);
		coronalTap.setCircularOrbit(star, SSS_Utils.getRandomAngle(), star.getRadius() + 250f, -175f);
		system.addScript(new MiscellaneousThemeGenerator.MakeCoronalTapFaceNearestStar(coronalTap));
		system.addScript((EveryFrameScript) new CoronalTapParticleScript(coronalTap));
		SSS_Utils.createAccretionDisk((SectorEntityToken) star, star.getRadius(), 0f);

		// planet 1 moon of planet 1
		PlanetAPI planet1 = SSS_Utils.createPlanet(system, star,
				"terran",
				130f,
				5000f,
				500f,
				new ArrayList<>(Arrays.asList(
						Conditions.FARMLAND_BOUNTIFUL,
						Conditions.ORGANICS_PLENTIFUL,
						Conditions.ORE_ULTRARICH,
						Conditions.RUINS_VAST,
						Conditions.HABITABLE,
						Conditions.MILD_CLIMATE)));
		// custom entities
		float planet1OrbitAngle = planet1.getCircularOrbitAngle();
		SectorEntityToken commRelay = system.addCustomEntity(null, null, Entities.COMM_RELAY, Factions.NEUTRAL);
		commRelay.setCircularOrbit(star, (planet1OrbitAngle + 120f) % 360f, 5000f, 500f);
		JumpPointAPI jumpPoint1 = Global.getFactory().createJumpPoint(null, "Inner System Jump-point");
		jumpPoint1.setStandardWormholeToHyperspaceVisual();
		jumpPoint1.setCircularOrbit(star, (planet1OrbitAngle + 240f) % 360f, 5000f, 500f);
		system.addEntity(jumpPoint1);

		// planet 1
		PlanetAPI planet2 = SSS_Utils.createPlanet(system, star,
				"gas_giant",
				250f,
				11000f,
				1100f,
				new ArrayList<>(Arrays.asList(
						Conditions.VOLATILES_PLENTIFUL,
						Conditions.HIGH_GRAVITY)));
		float planet2Radius = planet2.getRadius() + 300f;
		SSS_Utils.createMagneticField(planet2, planet2Radius, (planet2Radius) / 2f, planet2.getRadius() + 50f, planet2Radius, 1f);
		// planet 3 moon of planet 2
		SSS_Utils.createPlanet(system, planet2,
				"frozen2",
				60f,
				planet2.getRadius() + 500f,
				(planet2.getRadius() + 500f) / 10f,
				new ArrayList<>(Arrays.asList(
						Conditions.ORE_ULTRARICH,
						Conditions.RARE_ORE_ULTRARICH,
						Conditions.RUINS_VAST,
						Conditions.VERY_COLD)));
		// planet 4 moon of planet 2
		PlanetAPI planet4 = SSS_Utils.createPlanet(system, planet2,
				"barren_venuslike",
				130f,
				2500f,
				250f,
				new ArrayList<>(Arrays.asList(
						Conditions.ORE_ULTRARICH,
						Conditions.RARE_ORE_ULTRARICH,
						Conditions.NO_ATMOSPHERE,
						Conditions.VERY_HOT)));
		// planet 4 moon of planet 3
		SSS_Utils.createPlanet(system, planet4,
				"toxic",
				60f,
				planet4.getRadius() + 500f,
				(planet4.getRadius() + 500f) / 10f,
				new ArrayList<>(Arrays.asList(
						Conditions.ORE_ULTRARICH,
						Conditions.RARE_ORE_ULTRARICH,
						Conditions.ORGANICS_COMMON,
						Conditions.TOXIC_ATMOSPHERE)));

		// asteroid belt 1 for planet 2
		SSS_Utils.createAsteroidBelt(system, 64, planet2, 3500f, 350f, "misc", "rings_dust0", 256f, 3, Color.WHITE, 256f);

		// custom entities for planet 2
		float randomAngle2 = SSS_Utils.getRandomAngle();
		SectorEntityToken navBuoy = system.addCustomEntity(null, null, Entities.NAV_BUOY, Factions.NEUTRAL);
		navBuoy.setCircularOrbit(planet2, randomAngle2, 4000f, 400f);
		SectorEntityToken sensorArray = system.addCustomEntity(null, null, Entities.SENSOR_ARRAY, Factions.NEUTRAL);
		sensorArray.setCircularOrbit(planet2, (randomAngle2 + 120f) % 360f, 4000f, 400f);
		JumpPointAPI jumpPoint2 = Global.getFactory().createJumpPoint(null, "Fringe Jump-point");
		jumpPoint2.setStandardWormholeToHyperspaceVisual();
		jumpPoint2.setCircularOrbit(planet2, (randomAngle2 + 240f) % 360f, 4000f, 400f);
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
		float station1Radius = planet1.getRadius() + 150f;
		CampaignFleetAPI station1 = SSS_Utils.addAIBattlestation(planet1, station1Radius, station1Radius / 10f);
		RemnantStationFleetManager station1Fleets = new RemnantStationFleetManager((SectorEntityToken) station1, 1f, 0, 8, 15f, 16, 32);
		system.addScript((EveryFrameScript) station1Fleets);
	}
}
