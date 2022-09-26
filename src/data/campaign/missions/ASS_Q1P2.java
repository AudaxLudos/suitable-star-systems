package data.campaign.missions;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.ids.Voices;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithBarEvent;
import com.fs.starfarer.api.impl.campaign.missions.hub.ReqMode;
import com.fs.starfarer.api.impl.campaign.missions.hub.MissionTrigger.TriggerAction;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class ASS_Q1P2 extends HubMissionWithBarEvent {
    public static enum Stage {
        WAIT,
        GO_BACK_TO_PERSON,
        REPORT_TO_PERSON,
        FREE_THE_PERSON,
        KILL_FLEET,
        RETURN_TO_PERSON,
        COMPLETED,
    }

    protected PlanetAPI planet;
    protected StarSystemAPI system;
    protected PersonAPI person;
    protected CampaignFleetAPI fleet;
    protected MarketAPI market;

    public boolean shouldShowAtMarket(MarketAPI market) {
        return market.getFactionId().equals(Factions.TRITACHYON);
    }

    @Override
    protected boolean create(MarketAPI createdAt, boolean barEvent) {
        // Create or find a quest giver
        if (barEvent) {
            setGiverRank(Ranks.CITIZEN);
            setGiverPost(Ranks.POST_SCIENTIST);
            setGiverImportance(PersonImportance.HIGH);
            setGiverFaction(Factions.TRITACHYON);
            setGiverTags(Tags.CONTACT_SCIENCE);
            setGiverVoice(Voices.SCIENTIST);
            findOrCreateGiver(createdAt, true, false);
        }

        person = getPerson();
        if (person == null)
            return false;

        if (!setPersonMissionRef(person, "$ASS_Q1P2_ref"))
            return false;

        if (!setGlobalReference("$ASS_Q1P2_ref"))
            return false;

        // Find and pick a planet to use for quest
        requireSystemTags(ReqMode.ANY, new String[] { Tags.THEME_REMNANT, Tags.THEME_REMNANT_MAIN, Tags.THEME_REMNANT_RESURGENT });
        requireSystemTags(ReqMode.NOT_ANY, new String[] { Tags.THEME_REMNANT_NO_FLEETS, Tags.THEME_REMNANT_SECONDARY, Tags.THEME_REMNANT_SUPPRESSED });
        preferSystemUnexplored();
        requirePlanetNotGasGiant();
        requirePlanetNotStar();
        preferPlanetNotFullySurveyed();

        planet = pickPlanet();
        if (planet == null)
            return false;

        system = planet.getStarSystem();

        requireMarketIsNot(createdAt);
        requireMarketNotHidden();
        requireMarketNotInHyperspace();
        requireMarketFaction(Factions.HEGEMONY);
        preferMarketSizeAtLeast(6);
        preferMarketSizeAtMost(8);

        market = pickMarket();
        if (market == null)
            return false;

        // set up starting and end stages
        setStartingStage(Stage.WAIT);
        addSuccessStages(Stage.COMPLETED);
        setNoAbandon();

        // Make this locations important
        makeImportant(person, "$ASS_Q1P2_goBackTo", Stage.GO_BACK_TO_PERSON);
        makeImportant(market, "$ASS_Q1P2_freeTo", Stage.FREE_THE_PERSON);
        makeImportant(person, "$ASS_Q1P2_returnTo", Stage.RETURN_TO_PERSON);

        connectWithDaysElapsed(Stage.WAIT, Stage.GO_BACK_TO_PERSON, 1f);
        connectWithGlobalFlag(Stage.GO_BACK_TO_PERSON, Stage.REPORT_TO_PERSON, "$ASS_Q1P2_reportHere");
        connectWithGlobalFlag(Stage.REPORT_TO_PERSON, Stage.FREE_THE_PERSON, "$ASS_Q1P2_freeHere");
        connectWithGlobalFlag(Stage.FREE_THE_PERSON, Stage.KILL_FLEET, "$ASS_Q1P2_killRemnant");
        connectWithGlobalFlag(Stage.KILL_FLEET, Stage.RETURN_TO_PERSON, "$ASS_Q1P2_returnHere");
        setStageOnGlobalFlag(Stage.COMPLETED, "$ASS_Q1P2_completed");

        beginWithinHyperspaceRangeTrigger(person.getMarket(), 0.5f, true, Stage.REPORT_TO_PERSON);
        triggerCreateFleet(FleetSize.SMALL, FleetQuality.DEFAULT, Factions.INDEPENDENT, FleetTypes.TRADE_SMALL, person.getMarket().getStarSystem());
        triggerSetFleetOfficers(OfficerNum.FC_ONLY, OfficerQuality.DEFAULT);
        triggerFleetInterceptPlayerNearby(false, Stage.REPORT_TO_PERSON);
        triggerPickLocationInHyperspace(system);
        triggerSpawnFleetAtPickedLocation("$ASS_Q1P2_reportFleet", null);
        triggerFleetMakeImportant("$ASS_Q1P2_reportTo", Stage.REPORT_TO_PERSON);
        triggerOrderFleetInterceptPlayer(false, true);
        triggerOrderFleetEBurn(1f);
        endTrigger();

        beginStageTrigger(Stage.KILL_FLEET);
        triggerCreateFleet(FleetSize.MAXIMUM, FleetQuality.SMOD_3, Factions.REMNANTS, FleetTypes.PATROL_LARGE, planet);
        triggerSetFleetOfficers(OfficerNum.ALL_SHIPS, OfficerQuality.AI_ALPHA);
        triggerAutoAdjustFleetStrengthExtreme();
        triggerMakeHostileAndAggressive();
        triggerFleetAllowLongPursuit();
        triggerSetFleetAlwaysPursue();
        triggerFleetNoAutoDespawn();
        triggerFleetNoJump();
        triggerSetPatrol();
        triggerPickLocationAroundEntity(planet, 1000f);
        triggerSpawnFleetAtPickedLocation();
        triggerFleetMakeImportant("$ASS_Q1P2_remnant", Stage.KILL_FLEET);
        triggerFleetAddDefeatTrigger("ASS_Q1P2_RemnantDefeated");
        endTrigger();
        List<CampaignFleetAPI> fleets = runStageTriggersReturnFleets(Stage.KILL_FLEET);
        this.fleet = fleets.get(0);
        return true;
    }

    @Override
    protected boolean callAction(String action, String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (action.equals("remnantDefeated")) {
            beginStageTrigger(Stage.KILL_FLEET);
            SectorEntityToken loc = this.fleet.getContainingLocation().createToken(this.fleet.getLocation());
            triggerSpawnEntity(Entities.WEAPONS_CACHE_REMNANT, new LocData(loc));
            triggerEntityMakeImportant("$ASS_Q1P2_cache", Stage.KILL_FLEET);
            endTrigger();
            return true;
        }
        return false;
    }

    protected void updateInteractionDataImpl() {
        set("$ASS_Q1P2_askingPrice", Misc.getWithDGS(100000f));
        set("$ASS_Q1P2_danger", MarketCMD.RaidDangerLevel.EXTREME);
        set("$ASS_Q1P2_marines", Misc.getWithDGS(getMarinesRequiredForCustomObjective(this.market, MarketCMD.RaidDangerLevel.EXTREME)));
    }

    @Override
    public void addDescriptionForNonEndStage(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;
        // Color h = Misc.getHighlightColor();
        if (currentStage == Stage.WAIT) {
            info.addPara("Wait for 1 day", opad);
        } else if (currentStage == Stage.GO_BACK_TO_PERSON) {
            info.addPara("Return to " + person.getName().getFullName() + " at the " + system.getNameWithLowercaseTypeShort() + " to find out what they want", opad);
        } else if (currentStage == Stage.KILL_FLEET) {
            info.addPara("Kill fleet", opad);
        } else if (currentStage == Stage.RETURN_TO_PERSON) {
            info.addPara("Return to " + person.getName().getFullName() + " at the " + system.getNameWithLowercaseTypeShort() + " to find out what they want", opad);
        }
    }

    @Override
    public boolean addNextStepText(TooltipMakerAPI info, Color tc, float pad) {
        // Color h = Misc.getHighlightColor();
        if (currentStage == Stage.WAIT) {
            info.addPara("Wait for 1 day", tc, pad);
            return true;
        } else if (currentStage == Stage.GO_BACK_TO_PERSON) {
            info.addPara("Return to " + person.getName().getFullName(), tc, pad);
            return true;
        } else if (currentStage == Stage.KILL_FLEET) {
            info.addPara("Kill fleet", tc, pad);
            return true;
        } else if (currentStage == Stage.RETURN_TO_PERSON) {
            info.addPara("Return to " + person.getName().getFullName(), tc, pad);
            return true;
        }
        return false;
    }

    @Override
    public String getBaseName() {
        return "ASS: System Investigation 2";
    }
}
