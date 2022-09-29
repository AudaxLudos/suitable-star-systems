package data.campaign.missions;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithBarEvent;
import com.fs.starfarer.api.impl.campaign.missions.hub.ReqMode;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class UGPhase6 extends HubMissionWithBarEvent {
    public static enum Stage {
        KILL_FLEET,
        RETURN_TO_PERSON,
        COMPLETED,
    }

    protected StarSystemAPI system;
    protected CampaignFleetAPI fleet;
    protected PersonAPI person;

    public boolean shouldShowAtMarket(MarketAPI market) {
        return market.getFactionId().equals(Factions.TRITACHYON);
    }

    @Override
    protected boolean create(MarketAPI createdAt, boolean barEvent) {
        person = getImportantPerson("UGPerson1");
        if (person == null)
            return false;

        if (!setPersonMissionRef(person, "$UGPhase4_ref"))
            return false;

        if (!setGlobalReference("$UGPhase4_ref"))
            return false;

        // Find and pick a planet to use for quest
        requireSystemTags(ReqMode.ANY, new String[] { Tags.THEME_REMNANT, Tags.THEME_REMNANT_MAIN, Tags.THEME_REMNANT_RESURGENT });
        requireSystemTags(ReqMode.NOT_ANY, new String[] { Tags.THEME_REMNANT_NO_FLEETS, Tags.THEME_REMNANT_SECONDARY, Tags.THEME_REMNANT_SUPPRESSED });
        preferSystemUnexplored();

        system = pickSystem(true);
        if (system == null)
            return false;

        // set up starting and end stages
        setStoryMission();
        setStartingStage(Stage.KILL_FLEET);
        addSuccessStages(Stage.COMPLETED);

        // Make this locations important
        makeImportant(person, "$UGPhase4_returnTo", Stage.RETURN_TO_PERSON);

        connectWithGlobalFlag(Stage.KILL_FLEET, Stage.RETURN_TO_PERSON, "$UGPhase4_returnHere");
        setStageOnGlobalFlag(Stage.COMPLETED, "$UGPhase4_completed");

        beginStageTrigger(Stage.KILL_FLEET);
        triggerCreateFleet(FleetSize.MAXIMUM, FleetQuality.SMOD_3, Factions.REMNANTS, FleetTypes.PATROL_LARGE, system);
        triggerSetFleetOfficers(OfficerNum.ALL_SHIPS, OfficerQuality.AI_ALPHA);
        triggerAutoAdjustFleetStrengthExtreme();
        triggerMakeHostileAndAggressive();
        triggerFleetAllowLongPursuit();
        triggerSetFleetAlwaysPursue();
        triggerFleetNoAutoDespawn();
        triggerFleetNoJump();
        triggerSetPatrol();
        triggerPickLocationAtInSystemJumpPoint(system);
        triggerSpawnFleetAtPickedLocation();
        triggerFleetMakeImportant("$UGPhase4_remnant", Stage.KILL_FLEET);
        triggerFleetAddDefeatTrigger("UGPhase4_RemnantDefeated");
        endTrigger();
        List<CampaignFleetAPI> fleets = runStageTriggersReturnFleets(Stage.KILL_FLEET);
        fleet = fleets.get(0);

        return true;
    }

    @Override
    protected boolean callAction(String action, String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (action.equals("remnantDefeated")) {
            beginStageTrigger(Stage.KILL_FLEET);
            SectorEntityToken loc = this.fleet.getContainingLocation().createToken(this.fleet.getLocation());
            triggerSpawnEntity(Entities.WEAPONS_CACHE_REMNANT, new LocData(loc));
            triggerEntityMakeImportant("$UGPhase4_cache", Stage.KILL_FLEET);
            endTrigger();
            return true;
        }
        return false;
    }

    @Override
    public void addDescriptionForNonEndStage(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;
        // Color h = Misc.getHighlightColor();
        if (currentStage == Stage.KILL_FLEET) {
            info.addPara("Find the fleet recorded in the decrypted data core", opad);
        } else if (currentStage == Stage.RETURN_TO_PERSON) {
            info.addPara("Return to " + person.getNameString() + " and tell " + person.getHimOrHer() + " about the new data core you found.", opad);
            addStandardMarketDesc(person.getNameString() + " is located " + person.getMarket().getOnOrAt(), person.getMarket(), info, opad);
        }
    }

    @Override
    public boolean addNextStepText(TooltipMakerAPI info, Color tc, float pad) {
        // Color h = Misc.getHighlightColor();
        if (currentStage == Stage.KILL_FLEET) {
            info.addPara("Find the fleet recorded in the decrypted data core", tc, pad);
            return true;
        } else if (currentStage == Stage.RETURN_TO_PERSON) {
            info.addPara("Return to " + person.getNameString(), tc, pad);
            return true;
        }
        return false;
    }

    @Override
    public String getBaseName() {
        return "Unknown Genesis: The Omega";
    }
}
