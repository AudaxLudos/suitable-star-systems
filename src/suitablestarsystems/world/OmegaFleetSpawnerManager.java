package suitablestarsystems.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.BattleCreationContext;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.SourceBasedFleetManager;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.procgen.themes.OmegaOfficerGeneratorPlugin;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantAssignmentAI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import suitablestarsystems.Utils;

import java.util.Random;

public class OmegaFleetSpawnerManager extends SourceBasedFleetManager {
    protected int totalLost = 0;

    public OmegaFleetSpawnerManager(SectorEntityToken source, float thresholdLY, int minFleets, int maxFleets, float respawnDelay) {
        super(source, thresholdLY, minFleets, maxFleets, respawnDelay);
    }

    @Override
    protected CampaignFleetAPI spawnFleet() {
        if (!Global.getSector().getMemoryWithoutUpdate().getBoolean("$sss_omegaPlanetQuestOverride"))
            if (!Global.getSector().getMemoryWithoutUpdate().getBoolean("$sss_omegaPlanetCracked")) return null;
        if (this.source == null) return null;

        Random random = Utils.random;

        CampaignFleetAPI fleet = FleetFactoryV3.createEmptyFleet("omega", "patrolLarge", this.source.getMarket());
        WeightedRandomPicker<String> variants = new WeightedRandomPicker<>(random);
        variants.add("tesseract_Attack");
        variants.add("tesseract_Attack2");
        variants.add("tesseract_Strike");
        variants.add("tesseract_Disruptor");
        variants.add("tesseract_Shieldbreaker");

        int numOfShips = 2;
        numOfShips += this.totalLost;
        if (numOfShips > 4) numOfShips = 4;
        for (int i = 0; i <= numOfShips - 1; i++)
            fleet.getFleetData().addFleetMember(variants.pick());

        initOmegaFleetProperties(random, fleet);
        OmegaOfficerGeneratorPlugin plugin = new OmegaOfficerGeneratorPlugin();
        plugin.addCommanderAndOfficers(fleet, null, random);
        // Omega faction lacks a doctrine, so leadership skills are not set
        fleet.getCommander().getStats().setSkipRefresh(true);
        fleet.getCommander().getStats().setSkillLevel(Skills.CREW_TRAINING, 2f);
        fleet.getCommander().getStats().setSkillLevel(Skills.TACTICAL_DRILLS, 2f);
        fleet.getCommander().getStats().setSkillLevel(Skills.WOLFPACK_TACTICS, 2f);
        fleet.getCommander().getStats().setSkipRefresh(false);
        fleet.getCommander().getStats().refreshCharacterStatsEffects();
        // Integrate the AI and add omega specific details
        for (FleetMemberAPI member : fleet.getFleetData().getMembersListCopy()) {
            integrateAndAdaptCoreForOmegaFleet(member);
            member.getVariant().addTag(Tags.SHIP_LIMITED_TOOLTIP);
            member.getRepairTracker().setCR(member.getRepairTracker().getMaxCR());
        }
        fleet.setName("Unidentified Vessels");
        fleet.setNoFactionInName(true);
        fleet.setFacing(Utils.getRandomAngle());
        // Spawn the fleet in the system
        source.getContainingLocation().addEntity(fleet);
        fleet.updateFleetView(); // so that ship views exist and can do the jump-in warping animation
        source.getContainingLocation().removeEntity(fleet);
        Global.getSector().getHyperspace().addEntity(fleet);
        fleet.setLocation(1000000000, 0);
        SectorEntityToken token = this.source.getContainingLocation().createToken(Misc.getPointAtRadius(source.getLocation(), getRandomNumberInRange(1000f, 1500f)));
        Global.getSector().doHyperspaceTransition(fleet, null, new JumpPointAPI.JumpDestination(token, null));
        fleet.addScript(new RemnantAssignmentAI(fleet, (StarSystemAPI) this.source.getContainingLocation(), this.source));
        fleet.getMemoryWithoutUpdate().set("$sourceId", this.source.getId());

        return fleet;
    }

    @Override
    public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, CampaignEventListener.FleetDespawnReason reason, Object param) {
        super.reportFleetDespawnedToListener(fleet, reason, param);
        if (reason == CampaignEventListener.FleetDespawnReason.DESTROYED_BY_BATTLE) {
            String sid = fleet.getMemoryWithoutUpdate().getString("$sourceId");
            if (sid != null && source != null && sid.equals(source.getId()))
                totalLost++;
        }
    }

    public void integrateAndAdaptCoreForOmegaFleet(FleetMemberAPI member) {
        PersonAPI person = member.getCaptain();
        if (!person.isAICore())
            return;
        person.getStats().setLevel(person.getStats().getLevel() + 1);
        person.getStats().setSkipRefresh(true);
        person.getStats().setSkillLevel(Skills.ORDNANCE_EXPERTISE, 2f);
        if (person.getStats().getSkillLevel(Skills.BALLISTIC_MASTERY) > 0) {
            person.getStats().setSkillLevel(Skills.BALLISTIC_MASTERY, 0f);
            person.getStats().setSkillLevel(Skills.POLARIZED_ARMOR, 2f);
        }
        person.getStats().setSkipRefresh(false);
        person.getStats().refreshCharacterStatsEffects();
    }

    public void initOmegaFleetProperties(Random random, CampaignFleetAPI fleet) {
        if (random == null)
            random = new Random();
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_NO_JUMP, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_HOSTILE, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_ALWAYS_PURSUE, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_PREVENT_DISENGAGE, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PATROL_FLEET, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_NO_REP_IMPACT, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_NO_SHIP_RECOVERY, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_FIGHT_TO_THE_LAST, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_INTERACTION_DIALOG_CONFIG_OVERRIDE_GEN, new OmegaFleetInteractionConfigGen());
        fleet.getMemoryWithoutUpdate().set(MemFlags.SALVAGE_SEED, random.nextLong());
    }

    public float getRandomNumberInRange(float min, float max) {
        return (float) (Math.random() * (max - min) + min);
    }

    public static class OmegaFleetInteractionConfigGen implements FleetInteractionDialogPluginImpl.FIDConfigGen {
        public FleetInteractionDialogPluginImpl.FIDConfig createConfig() {
            FleetInteractionDialogPluginImpl.FIDConfig config = new FleetInteractionDialogPluginImpl.FIDConfig();
            config.showTransponderStatus = false;
            config.delegate = new FleetInteractionDialogPluginImpl.BaseFIDDelegate() {
                public void battleContextCreated(InteractionDialogAPI dialog, BattleCreationContext bcc) {
                    bcc.aiRetreatAllowed = false;
                    bcc.enemyDeployAll = true;
                }
            };
            return config;
        }
    }
}
