package data.campaign.procgen.themes;

import java.util.Random;

import com.fs.starfarer.api.campaign.CampaignEventListener;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.BattleCreationContext;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.SourceBasedFleetManager;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.OmegaOfficerGeneratorPlugin;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantAssignmentAI;
import com.fs.starfarer.api.util.WeightedRandomPicker;

public class OmegaStationFleetManager extends SourceBasedFleetManager {
    protected int totalLost;

    public static class OmegaFleetInteractionConfigGen implements FleetInteractionDialogPluginImpl.FIDConfigGen {
        public FleetInteractionDialogPluginImpl.FIDConfig createConfig() {
            FleetInteractionDialogPluginImpl.FIDConfig config = new FleetInteractionDialogPluginImpl.FIDConfig();
            config.showTransponderStatus = false;
            config.delegate = (FleetInteractionDialogPluginImpl.FIDDelegate)new FleetInteractionDialogPluginImpl.BaseFIDDelegate() {
                public void battleContextCreated(InteractionDialogAPI dialog, BattleCreationContext bcc) {
                    bcc.aiRetreatAllowed = false;
                    bcc.enemyDeployAll = true;
                }
            };
            return config;
        }
    }

    public OmegaStationFleetManager(SectorEntityToken source, float thresholdLY, int minFleets, int maxFleets, float respawnDelay) {
        super(source, thresholdLY, minFleets, maxFleets, respawnDelay);
    }

    @Override
    protected CampaignFleetAPI spawnFleet() {
        if (this.source == null)
            return null;
        Random random = StarSystemGenerator.random;
        CampaignFleetAPI fleet = FleetFactoryV3.createEmptyFleet("omega", "patrolLarge",
            this.source.getMarket());
        WeightedRandomPicker<String> picker = new WeightedRandomPicker<String>(random);
        picker.add("tesseract_Attack");
        picker.add("tesseract_Attack2");
        picker.add("tesseract_Strike");
        picker.add("tesseract_Disruptor");
        picker.add("tesseract_Shieldbreaker");
        int numOfShips = 1 + random.nextInt(1);
        numOfShips += this.totalLost;
        if (numOfShips > 5)
            numOfShips = 5;
        for (int i = 0; i <= numOfShips; i++) {
            fleet.getFleetData().addFleetMember(picker.pick());
        }
        this.source.getContainingLocation().addEntity((SectorEntityToken)fleet);
        initOmegaFleetProperties(random, fleet);
        OmegaOfficerGeneratorPlugin plugin = new OmegaOfficerGeneratorPlugin();
        plugin.addCommanderAndOfficers(fleet, null, random);
        for (FleetMemberAPI member : fleet.getFleetData().getMembersListCopy()) {
            integrateAndAdaptCoreForOmegaFleet(member);
            member.getRepairTracker().setCR(member.getRepairTracker().getMaxCR());
        }
        fleet.setName("Unidentified Vessels");
        fleet.setNoFactionInName(true);
        fleet.setLocation((this.source.getLocation()).x, (this.source.getLocation()).y);
        fleet.setFacing(random.nextFloat() * 360f);
        fleet.addScript(new RemnantAssignmentAI(fleet, (StarSystemAPI)this.source.getContainingLocation(), this.source));
        fleet.getMemoryWithoutUpdate().set("$sourceId", this.source.getId());
        return fleet;
    }

    public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, CampaignEventListener.FleetDespawnReason reason, Object param) {
        super.reportFleetDespawnedToListener(fleet, reason, param);
        if (reason == CampaignEventListener.FleetDespawnReason.DESTROYED_BY_BATTLE) {
            String sid = fleet.getMemoryWithoutUpdate().getString("$sourceId");
            if (sid != null && this.source != null && sid.equals(this.source.getId()))
                this.totalLost++;
        }
    }

    public static void integrateAndAdaptCoreForOmegaFleet(FleetMemberAPI member) {
        PersonAPI person = member.getCaptain();
        if (!person.isAICore())
            return;
        person.getStats().setLevel(person.getStats().getLevel() + 1);
        person.getStats().setSkipRefresh(true);
        person.getStats().setSkillLevel("missile_specialization", 2f);
        if (person.getStats().getSkillLevel("polarized_armor") <= 0f) {
            person.getStats().setSkillLevel("helmsmanship", 0f);
            person.getStats().setSkillLevel("polarized_armor", 2f);
        }
        if (person.getStats().getSkillLevel("systems_expertise") <= 0f) {
            person.getStats().setSkillLevel("point_defense", 0f);
            person.getStats().setSkillLevel("systems_expertise", 2f);
        }
        person.getStats().setSkipRefresh(false);
    }

    public static void initOmegaFleetProperties(Random random, CampaignFleetAPI fleet) {
        if (random == null)
            random = new Random();
        fleet.getMemoryWithoutUpdate().set("$cfai_noJump", Boolean.valueOf(true));
        fleet.getMemoryWithoutUpdate().set("$cfai_makeHostile", Boolean.valueOf(true));
        fleet.getMemoryWithoutUpdate().set("$cfai_makeAggressive", Boolean.valueOf(true));
        fleet.getMemoryWithoutUpdate().set("$cfai_makeAlwaysPursue", Boolean.valueOf(true));
        fleet.getMemoryWithoutUpdate().set("$cfai_makeAllowDisengage", Boolean.valueOf(false));
        fleet.getMemoryWithoutUpdate().set("$cfai_makePreventDisengage", Boolean.valueOf(true));
        fleet.getMemoryWithoutUpdate().set("$isPatrol", Boolean.valueOf(true));
        fleet.getMemoryWithoutUpdate().set("$noRepImpact", Boolean.valueOf(true));
        fleet.getMemoryWithoutUpdate().set("$lowRepImpact", Boolean.valueOf(true));
        fleet.getMemoryWithoutUpdate().set("$noShipRecovery", Boolean.valueOf(true));
        fleet.getMemoryWithoutUpdate().set("$core_fightToTheLast", Boolean.valueOf(true));
        addOmegaInteractionConfig(fleet);
        long salvageSeed = random.nextLong();
        fleet.getMemoryWithoutUpdate().set("$salvageSeed", Long.valueOf(salvageSeed));
    }

    public static void addOmegaInteractionConfig(CampaignFleetAPI fleet) {
        fleet.getMemoryWithoutUpdate().set("$fidConifgGen", new OmegaFleetInteractionConfigGen());
    }
}
