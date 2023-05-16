package data.scripts;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AICoreOfficerPlugin;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CampaignTerrainAPI;
import com.fs.starfarer.api.campaign.RingBandAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.procgen.MagFieldGenPlugin;
import com.fs.starfarer.api.impl.campaign.procgen.ProcgenUsedNames;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.AccretionDiskGenPlugin.TexAndIndex;
import com.fs.starfarer.api.impl.campaign.procgen.ProcgenUsedNames.NamePick;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantOfficerGeneratorPlugin;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantThemeGenerator;
import com.fs.starfarer.api.impl.campaign.terrain.BaseRingTerrain;
import com.fs.starfarer.api.impl.campaign.terrain.MagneticFieldTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.RingSystemTerrainPlugin;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;

public class SSS_Utils {
	public static Random random = StarSystemGenerator.random;

	public static String generateProceduralName(String tag, String parent) {
		NamePick namePick = ProcgenUsedNames.pickName(tag, parent, null);
		String name = namePick.nameWithRomanSuffixIfAny;
		return name;
	}

	public static void createMagneticField(SectorEntityToken focus, float bandWidthInEngine, float middleRadius, float innerRadius, float outerRadius, float auroraFreqency) {
		int baseIndex = (int) (MagFieldGenPlugin.baseColors.length * random.nextDouble());
		int auroraIndex = (int) (MagFieldGenPlugin.auroraColors.length * random.nextDouble());
		SectorEntityToken magneticField = focus.getStarSystem().addTerrain("magnetic_field",
				new MagneticFieldTerrainPlugin.MagneticFieldParams(
						bandWidthInEngine,
						middleRadius,
						focus,
						innerRadius,
						outerRadius,
						MagFieldGenPlugin.baseColors[baseIndex],
						auroraFreqency,
						MagFieldGenPlugin.auroraColors[auroraIndex]));
		magneticField.setCircularOrbit(focus, 0f, 0f, 0f);
	}

	public static void createAccretionDisk(SectorEntityToken focus, float radius, float minSpiralRadius) {
		float orbitRadius = radius * (2f + 1f * random.nextFloat());
		float bandWidth = 256f;
		for (float i = 0f; i < 12f; i++) {
			float middleRadius = orbitRadius - i * bandWidth * 0.25f - i * bandWidth * 0.1f;
			TexAndIndex tex = getTextureAndIndex();
			float orbitDays = middleRadius / (30f + 10f * random.nextFloat());
			RingBandAPI visual = (RingBandAPI) focus.getStarSystem().addRingBand((SectorEntityToken) focus, "misc", tex.tex, bandWidth,
					tex.index, Color.WHITE, bandWidth, middleRadius + bandWidth / 2f, -orbitDays);
			float spiralFactor = 2f + 5f * random.nextFloat();
			visual.setSpiral(true);
			visual.setMinSpiralRadius(minSpiralRadius);
			visual.setSpiralFactor(spiralFactor);
		}

		List<SectorEntityToken> rings = new ArrayList<SectorEntityToken>();
		SectorEntityToken ring = focus.getStarSystem().addTerrain("ring",
				new BaseRingTerrain.RingParams(orbitRadius, orbitRadius / 2f, (SectorEntityToken) focus, null));
		ring.addTag("accretion_disk");
		if (((CampaignTerrainAPI) ring).getPlugin() instanceof RingSystemTerrainPlugin)
			((RingSystemTerrainPlugin) ((CampaignTerrainAPI) ring).getPlugin()).setNameForTooltip("Accretion Disk");
		ring.setCircularOrbit((SectorEntityToken) focus, 0f, 0f, -100f);
		rings.add(ring);
	}

	public static TexAndIndex getTextureAndIndex() {
		TexAndIndex result = new TexAndIndex();
		WeightedRandomPicker<Integer> indexPicker = new WeightedRandomPicker<Integer>(random);
		WeightedRandomPicker<String> ringSet = new WeightedRandomPicker<String>(random);
		ringSet.add("ring_ice", 10f);
		ringSet.add("ring_dust", 10f);
		String set = (String) ringSet.pick();
		if (set.equals("ring_ice")) {
			result.tex = "rings_ice0";
			indexPicker.add(Integer.valueOf(0));
			indexPicker.add(Integer.valueOf(1));
		} else if (set.equals("ring_dust")) {
			result.tex = "rings_dust0";
			indexPicker.add(Integer.valueOf(0));
			indexPicker.add(Integer.valueOf(1));
		}
		result.index = ((Integer) indexPicker.pick()).intValue();
		return result;
	}

	public static CampaignFleetAPI addAIBattlestation(SectorEntityToken focus, Boolean isOmega, float orbitRadius, float orbitDays) {
		String factionId = isOmega ? "omega" : "remnant";
		String coreId = isOmega ? "omega_core" : "alpha_core";
		CampaignFleetAPI fleet = FleetFactoryV3.createEmptyFleet(factionId, "battlestation", null);
		FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "remnant_station2_Standard");
		fleet.getFleetData().addFleetMember(member);
		if (isOmega) {
			fleet.setName("Unidentified Station");
			fleet.setNoFactionInName(true);
		}
		fleet.getMemoryWithoutUpdate().set("$cfai_makeAggressive", Boolean.valueOf(true));
		fleet.getMemoryWithoutUpdate().set("$cfai_noJump", Boolean.valueOf(true));
		fleet.getMemoryWithoutUpdate().set("$cfai_makeAllowDisengage", Boolean.valueOf(true));
		fleet.addTag("neutrino_high");
		fleet.setStationMode(Boolean.valueOf(true));
		RemnantThemeGenerator.addRemnantStationInteractionConfig(fleet);
		focus.getStarSystem().addEntity((SectorEntityToken) fleet);
		fleet.clearAbilities();
		fleet.addAbility("transponder");
		fleet.getAbility("transponder").activate();
		fleet.getDetectedRangeMod().modifyFlat("gen", 1000f);
		fleet.setAI(null);
		fleet.setCircularOrbitWithSpin(focus, random.nextFloat() * 360f, orbitRadius, orbitDays, 5f, 5f);
		AICoreOfficerPlugin plugin = Misc.getAICoreOfficerPlugin(coreId);
		PersonAPI commander = plugin.createPerson(coreId, factionId, random);
		fleet.setCommander(commander);
		fleet.getFlagship().setCaptain(commander);
		RemnantOfficerGeneratorPlugin.integrateAndAdaptCoreForAIFleet(fleet.getFlagship());
		RemnantOfficerGeneratorPlugin.addCommanderSkills(commander, fleet, null, 4, random);
		member.getRepairTracker().setCR(member.getRepairTracker().getMaxCR());
		return fleet;
	}
}
