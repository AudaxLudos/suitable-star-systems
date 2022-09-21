package data.campaign.missions;

import java.awt.Color;

import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.ids.Voices;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithBarEvent;
import com.fs.starfarer.api.impl.campaign.missions.hub.ReqMode;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class ASS_Q1P1 extends HubMissionWithBarEvent {
    public static enum Stage {
        SURVEY_PLANET,
        RETURN_TO_PERSON,
        COMPLETED,
    }

    protected PlanetAPI planet;
    protected StarSystemAPI system;
    protected PersonAPI person;

    public boolean shouldShowAtMarket(MarketAPI market) {
        return market.getFactionId().equals(Factions.TRITACHYON);
    }

    @Override
    protected boolean create(MarketAPI createdAt, boolean barEvent) {
        if (barEvent) {
            setGiverRank(Ranks.AGENT);
            setGiverPost(Ranks.POST_EXECUTIVE);
            setGiverImportance(PersonImportance.HIGH);
            setGiverFaction(Factions.TRITACHYON);
            setGiverTags(Tags.CONTACT_MILITARY);
            setGiverVoice(Voices.BUSINESS);
            findOrCreateGiver(createdAt, true, false);
        }

        person = getPerson();
        if (person == null)
            return false;

        if (!setPersonMissionRef(person, "$ASS_Q1P1_ref"))
            return false;

        if (!setGlobalReference("$ASS_Q1P1_ref"))
            return false;

        if (barEvent)
            setGiverIsPotentialContactOnSuccess(1f);

        requireSystemTags(ReqMode.ANY, new String[] { Tags.THEME_REMNANT, Tags.THEME_REMNANT_MAIN, Tags.THEME_REMNANT_RESURGENT });
        requireSystemTags(ReqMode.NOT_ANY, new String[] { Tags.THEME_REMNANT_NO_FLEETS, Tags.THEME_REMNANT_SECONDARY, Tags.THEME_REMNANT_SUPPRESSED });
        preferSystemUnexplored();
        requirePlanetNotStar();
        requirePlanetNotGasGiant();
        requirePlanetNotFullySurveyed();
        requirePlanetWithRuins();

        planet = pickPlanet();
        if (planet == null)
            return false;

        system = planet.getStarSystem();

        setStartingStage(Stage.SURVEY_PLANET);
        addSuccessStages(Stage.COMPLETED);
        setNoAbandon();

        makeImportant(planet, "$ASS_Q1P1_targetPlanet", Stage.SURVEY_PLANET);
        makeImportant(person, "$ASS_Q1P1_returnHere", Stage.RETURN_TO_PERSON);

        connectWithGlobalFlag(Stage.SURVEY_PLANET, Stage.RETURN_TO_PERSON, "$ASS_Q1P1_returnHere");
        setStageOnGlobalFlag(Stage.COMPLETED, "$ASS_Q1P1_completed");

        setCreditReward(CreditReward.VERY_HIGH);

        return true;
    }

    protected void updateInteractionDataImpl() {
        set("$ASS_Q1P1_reward", Misc.getWithDGS(getCreditsReward()));
        set("$ASS_Q1P1_systemName", system.getNameWithLowercaseTypeShort());
        set("$ASS_Q1P1_dist", getDistanceLY(system));
    }

    @Override
    public void addDescriptionForNonEndStage(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;
        // Color h = Misc.getHighlightColor();
        if (currentStage == Stage.SURVEY_PLANET) {
            info.addPara("Go to the " + system.getNameWithLowercaseTypeShort() + " system then find the planet and survey it.", opad);
        } else if (currentStage == Stage.RETURN_TO_PERSON) {
            info.addPara("Return to ~Quest Giver~ at ~System~ and give the data to them", opad);
        }
    }

    @Override
    public boolean addNextStepText(TooltipMakerAPI info, Color tc, float pad) {
        // Color h = Misc.getHighlightColor();
        if (currentStage == Stage.SURVEY_PLANET) {
            info.addPara("Survey the planet " + planet.getFullName(), tc, pad);
            return true;
        } else if (currentStage == Stage.RETURN_TO_PERSON) {
            info.addPara("Return the data to ~Quest Giver~", tc, pad);
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
        return "ASS: Survey planet";
    }
}
