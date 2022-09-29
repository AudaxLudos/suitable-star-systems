package data.campaign.missions;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.ids.Voices;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithBarEvent;
import com.fs.starfarer.api.impl.campaign.missions.hub.ReqMode;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class UGPhase1 extends HubMissionWithBarEvent {
    public static enum Stage {
        SURVEY_PLANET,
        RETURN_TO_PERSON,
        COMPLETED,
    }

    protected PlanetAPI planet;
    protected StarSystemAPI system;
    protected PersonAPI person;

    public boolean shouldShowAtMarket(MarketAPI market) {
        return (market.getStarSystem().getId().equals("corvus") ||
            market.getStarSystem().getId().equals("arcadia") ||
            market.getStarSystem().getId().equals("samarra")) &&
            market.getFactionId().equals(Factions.INDEPENDENT);
    }

    @Override
    protected boolean create(MarketAPI createdAt, boolean barEvent) {
        // Create or find a quest giver
        if (barEvent) {
            setGiverRank(Ranks.CITIZEN);
            setGiverPost(Ranks.POST_EXECUTIVE);
            setGiverImportance(PersonImportance.HIGH);
            setGiverFaction(Factions.INDEPENDENT);
            setGiverTags(Tags.CONTACT_MILITARY);
            setGiverVoice(Voices.BUSINESS);
            findOrCreateGiver(createdAt, true, false);
        }

        person = getPerson();
        if (person == null)
            return false;

        Global.getSector().getMemoryWithoutUpdate().set("$UGPhase1_person", person);

        if (!setPersonMissionRef(person, "$UGPhase1_ref"))
            return false;

        if (!setGlobalReference("$UGPhase1_ref"))
            return false;

        // Find and pick a planet to use for quest
        requireSystemTags(ReqMode.ANY, new String[] { Tags.THEME_REMNANT, Tags.THEME_REMNANT_MAIN, Tags.THEME_REMNANT_RESURGENT });
        requireSystemTags(ReqMode.NOT_ANY, new String[] { Tags.THEME_REMNANT_NO_FLEETS, Tags.THEME_REMNANT_SECONDARY, Tags.THEME_REMNANT_SUPPRESSED });
        preferSystemUnexplored();
        requirePlanetNotStar();
        requirePlanetNotGasGiant();
        requirePlanetWithRuins();
        preferPlanetNotFullySurveyed();

        planet = pickPlanet();
        if (planet == null)
            return false;

        system = planet.getStarSystem();

        // set up starting and end stages
        setStartingStage(Stage.SURVEY_PLANET);
        addSuccessStages(Stage.COMPLETED);
        setNoAbandon();

        // Make this locations important
        makeImportant(planet, "$UGPhase1_targetPlanet", Stage.SURVEY_PLANET);
        makeImportant(person, "$UGPhase1_returnHere", Stage.RETURN_TO_PERSON);

        // Flags that can be use to enter the next stage
        connectWithGlobalFlag(Stage.SURVEY_PLANET, Stage.RETURN_TO_PERSON, "$UGPhase1_returnHere");
        setStageOnGlobalFlag(Stage.COMPLETED, "$UGPhase1_completed");

        setCreditReward(CreditReward.HIGH);

        // Create ship graveyard during survey planet stage
        beginStageTrigger(Stage.SURVEY_PLANET);
        triggerSpawnShipGraveyard(Factions.INDEPENDENT, 16, 32, new LocData(planet, false));
        endTrigger();

        // Create a fleet near entity after completing survey planet
        beginStageTrigger(Stage.RETURN_TO_PERSON);
        triggerCreateFleet(FleetSize.MAXIMUM, FleetQuality.SMOD_3, Factions.REMNANTS, FleetTypes.PATROL_LARGE, planet);
        triggerSetFleetOfficers(OfficerNum.ALL_SHIPS, OfficerQuality.AI_ALPHA);
        triggerAutoAdjustFleetStrengthExtreme();
        triggerMakeHostileAndAggressive();
        triggerFleetAllowLongPursuit();
        triggerSetFleetAlwaysPursue();
        triggerPickLocationAroundPlayer(1000f);
        triggerSpawnFleetAtPickedLocation();
        triggerOrderFleetInterceptPlayer();
        triggerOrderFleetEBurn(1f);
        endTrigger();

        return true;
    }

    protected void updateInteractionDataImpl() {
        set("$UGPhase1_distance", getDistanceLY(system));
        set("$UGPhase1_systemName", system.getNameWithLowercaseTypeShort());
        set("$UGPhase1_planetName", planet.getFullName());
        set("$UGPhase1_reward", Misc.getWithDGS(getCreditsReward()));
    }

    @Override
    public void addDescriptionForNonEndStage(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;
        // Color h = Misc.getHighlightColor();
        if (currentStage == Stage.SURVEY_PLANET) {
            info.addPara("Go to the " + system.getNameWithLowercaseTypeShort() + " and investigate the planet " + planet.getName() + ".", opad);
        } else if (currentStage == Stage.RETURN_TO_PERSON) {
            info.addPara("Return to " + person.getName().getFullName() + " at the " + system.getNameWithLowercaseTypeShort() + " and tell " + person.getHimOrHer() + " about what you found", opad);
        }
    }

    @Override
    public boolean addNextStepText(TooltipMakerAPI info, Color tc, float pad) {
        // Color h = Misc.getHighlightColor();
        if (currentStage == Stage.SURVEY_PLANET) {
            info.addPara("Investigate the " + system.getNameWithLowercaseTypeShort(), tc, pad);
            return true;
        } else if (currentStage == Stage.RETURN_TO_PERSON) {
            info.addPara("Return to " + person.getName().getFullName(), tc, pad);
            return true;
        }
        return false;
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        if (currentStage == Stage.SURVEY_PLANET) {
            return getMapLocationFor(system.getCenter());
        } else if (currentStage == Stage.RETURN_TO_PERSON) {
            return getMapLocationFor(person.getMarket().getStarSystem().getCenter());
        }
        return null;
    }

    @Override
    public String getBaseName() {
        return "Unknown Genesis: The Search";
    }
}
