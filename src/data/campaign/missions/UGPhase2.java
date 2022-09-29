package data.campaign.missions;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
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
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class UGPhase2 extends HubMissionWithBarEvent {
    public static enum Stage {
        WAIT,
        GET_DATA,
        REPORT_TO_PERSON,
        SET_PERSON_FREE,
        PERSON_TO_MARKET,
        KILL_FLEET,
        RETURN_TO_PERSON,
        COMPLETED,
    }

    protected PlanetAPI planet;
    protected StarSystemAPI system;
    protected CampaignFleetAPI fleet;
    protected PersonAPI q1Person;
    protected PersonAPI q2Person;
    protected MarketAPI newMarket;
    protected MarketAPI targetMarket;

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

        q2Person = getPerson();
        if (q2Person == null)
            return false;

        q1Person = (PersonAPI)Global.getSector().getMemoryWithoutUpdate().get("$ASS_Q1P1_person");
        Global.getSector().getMemoryWithoutUpdate().set("$ASS_Q1P2_person", q2Person);

        if (!setPersonMissionRef(q2Person, "$ASS_Q1P2_ref"))
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
        requireMarketFactionNotPlayer();
        requireMarketNotHidden();
        requireMarketNotInHyperspace();
        requireMarketFaction(Factions.HEGEMONY);
        preferMarketSizeAtLeast(6);
        preferMarketSizeAtMost(8);

        targetMarket = pickMarket();
        if (targetMarket == null)
            return false;

        newMarket = Global.getSector().getEconomy().getMarket("donn");
        if (newMarket == null)
            return false;

        if (!setMarketMissionRef(targetMarket, "$ASS_Q1P2_ref"))
            return false;

        // set up starting and end stages
        setStartingStage(Stage.WAIT);
        addSuccessStages(Stage.COMPLETED);
        setNoAbandon();

        // Make this locations important
        makeImportant(q2Person, "$ASS_Q1P2_getDataFrom", Stage.GET_DATA);
        makeImportant(q1Person.getMarket(), null, Stage.REPORT_TO_PERSON);
        makeImportant(targetMarket, "$ASS_Q1P2_freePersonFrom", Stage.SET_PERSON_FREE);
        makeImportant(newMarket, "$ASS_Q1P2_personTo", Stage.PERSON_TO_MARKET);
        makeImportant(q1Person, "$ASS_Q1P2_returnTo", Stage.RETURN_TO_PERSON);

        connectWithDaysElapsed(Stage.WAIT, Stage.GET_DATA, 1f);
        connectWithGlobalFlag(Stage.GET_DATA, Stage.REPORT_TO_PERSON, "$ASS_Q1P2_reportHere");
        connectWithGlobalFlag(Stage.REPORT_TO_PERSON, Stage.SET_PERSON_FREE, "$ASS_Q1P2_freeHere");
        connectWithGlobalFlag(Stage.SET_PERSON_FREE, Stage.PERSON_TO_MARKET, "$ASS_Q1P2_marketHere");
        connectWithGlobalFlag(Stage.PERSON_TO_MARKET, Stage.KILL_FLEET, "$ASS_Q1P2_remnantHere");
        connectWithGlobalFlag(Stage.KILL_FLEET, Stage.RETURN_TO_PERSON, "$ASS_Q1P2_returnHere");
        setStageOnGlobalFlag(Stage.COMPLETED, "$ASS_Q1P2_completed");

        beginWithinHyperspaceRangeTrigger(q1Person.getMarket().getStarSystem(), 1f, true, Stage.REPORT_TO_PERSON);
        triggerHideCommListing(q1Person);
        triggerCreateFleet(FleetSize.SMALL, FleetQuality.DEFAULT, Factions.INDEPENDENT, FleetTypes.TRADE_SMALL, q1Person.getMarket().getStarSystem());
        triggerSetFleetOfficers(OfficerNum.FC_ONLY, OfficerQuality.DEFAULT);
        triggerFleetInterceptPlayerNearby(false, Stage.REPORT_TO_PERSON);
        triggerPickLocationAroundPlayer(100f);
        triggerSpawnFleetAtPickedLocation();
        triggerFleetMakeImportant("$ASS_Q1P2_reportToFleet", Stage.REPORT_TO_PERSON);
        triggerOrderFleetInterceptPlayer(false, true);
        triggerOrderFleetEBurn(1f);
        endTrigger();

        beginStageTrigger(Stage.PERSON_TO_MARKET);
        triggerMovePersonToMarket(q1Person, newMarket, true);
        triggerUnhideCommListing(q1Person);
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
        fleet = fleets.get(0);

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
        set("$ASS_Q1P2_marines", Misc.getWithDGS(getMarinesRequiredForCustomObjective(targetMarket, MarketCMD.RaidDangerLevel.EXTREME)));
    }

    @Override
    public void addDescriptionForNonEndStage(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;
        // Color h = Misc.getHighlightColor();
        if (currentStage == Stage.WAIT) {
            info.addPara(q2Person.getNameString() + " is decrypting the data core, " + q2Person.getHeOrShe() + "will contact you when it's done.", opad);
        } else if (currentStage == Stage.GET_DATA) {
            info.addPara(q2Person.getNameString() + " has finished decrypting the data core, return to " + q2Person.getHimOrHer() + " to find out what was decrypted.", opad);
            addStandardMarketDesc(q2Person.getNameString() + " is located " + q2Person.getMarket().getOnOrAt(), q2Person.getMarket(), info, opad);
        } else if (currentStage == Stage.REPORT_TO_PERSON) {
            info.addPara("Report back to " + q1Person.getNameString() + " and tell " + q1Person.getHimOrHer() + " what you've uncovered.", opad);
            addStandardMarketDesc(q1Person.getNameString() + " is located " + q1Person.getMarket().getOnOrAt(), q1Person.getMarket(), info, opad);
        } else if (currentStage == Stage.SET_PERSON_FREE) {
            info.addPara(q1Person.getNameString() + " has been captured, figure out a way to free " + q1Person.getHimOrHer() + ".", opad);
            addStandardMarketDesc(q1Person.getNameString() + " is being held " + targetMarket.getOnOrAt(), targetMarket, info, opad);
        } else if (currentStage == Stage.PERSON_TO_MARKET) {
            info.addPara("Escort " + q1Person.getNameString() + " to the system " + q1Person.getHeOrShe() + " mentioned", opad);
            addStandardMarketDesc(q1Person.getNameString() + " has a sanctuary " + targetMarket.getOnOrAt(), targetMarket, info, opad);
        } else if (currentStage == Stage.KILL_FLEET) {
            info.addPara("Kill fleet", opad);
        } else if (currentStage == Stage.RETURN_TO_PERSON) {
            info.addPara("Return to " + q1Person.getNameString() + " and tell " + q1Person.getHimOrHer() + " about the new data core you found.", opad);
            addStandardMarketDesc(q1Person.getNameString() + " is located " + q1Person.getMarket().getOnOrAt(), q1Person.getMarket(), info, opad);
        }
    }

    @Override
    public boolean addNextStepText(TooltipMakerAPI info, Color tc, float pad) {
        // Color h = Misc.getHighlightColor();
        if (currentStage == Stage.WAIT) {
            info.addPara("Wait for 1 day", tc, pad);
            return true;
        } else if (currentStage == Stage.GET_DATA) {
            info.addPara("Return to ~Q2 Person~", tc, pad);
            return true;
        } else if (currentStage == Stage.REPORT_TO_PERSON) {
            info.addPara("Report back to ~Q1 Person~", tc, pad);
            return true;
        } else if (currentStage == Stage.SET_PERSON_FREE) {
            info.addPara("Free ~Q1 Person~", tc, pad);
            return true;
        } else if (currentStage == Stage.PERSON_TO_MARKET) {
            info.addPara("Send ~Q1 Person~", tc, pad);
            return true;
        } else if (currentStage == Stage.KILL_FLEET) {
            info.addPara("Kill fleet", tc, pad);
            return true;
        } else if (currentStage == Stage.RETURN_TO_PERSON) {
            info.addPara("Return to ~Q1 Person~", tc, pad);
            return true;
        }
        return false;
    }

    @Override
    public String getBaseName() {
        return "ASS: System Investigation 2";
    }
}
