package data.scripts;

import java.util.Random;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AICoreOfficerPlugin;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.procgen.MagFieldGenPlugin;
import com.fs.starfarer.api.impl.campaign.procgen.ProcgenUsedNames;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.ProcgenUsedNames.NamePick;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantOfficerGeneratorPlugin;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantThemeGenerator;
import com.fs.starfarer.api.impl.campaign.terrain.MagneticFieldTerrainPlugin;
import com.fs.starfarer.api.util.Misc;

public class SSS_Utils {
	public static String generateProceduralName(String tag, String parent) {
		NamePick namePick = ProcgenUsedNames.pickName(tag, parent, null);
		String name = namePick.nameWithRomanSuffixIfAny;
		return name;
	}

	public static void createMagneticField(SectorEntityToken focus, float bandWidthInEngine, float middleRadius, float innerRadius, float outerRadius,
			float auroraFreqency) {
		Random random = StarSystemGenerator.random;
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

	public static CampaignFleetAPI addAIBattlestation(SectorEntityToken focus, Boolean isOmega, float orbitRadius, float orbitDays) {
		Random random = StarSystemGenerator.random;
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
