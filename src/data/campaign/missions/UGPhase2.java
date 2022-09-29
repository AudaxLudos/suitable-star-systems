package data.campaign.missions;

import java.awt.Color;

import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.ids.Voices;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithBarEvent;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class UGPhase2 extends HubMissionWithBarEvent {
    public static enum Stage {
        WAIT,
        GET_DATA,
        COMPLETED,
    }

    protected PersonAPI person;

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
        person.setId("UGPerson2");

        if (!setPersonMissionRef(person, "$UGPhase2_ref"))
            return false;

        if (!setGlobalReference("$UGPhase2_ref"))
            return false;

        // set up starting and end stages
        setStoryMission();
        setStartingStage(Stage.WAIT);
        addSuccessStages(Stage.COMPLETED);

        // Make this locations important
        makeImportant(person, "$UGPhase2_getDataFrom", Stage.GET_DATA);

        connectWithDaysElapsed(Stage.WAIT, Stage.GET_DATA, 1f);
        setStageOnGlobalFlag(Stage.COMPLETED, "$UGPhase2_completed");

        return true;
    }

    protected void updateInteractionDataImpl() {
        set("$UGPhase2_askingPrice", Misc.getWithDGS(100000f));
    }

    @Override
    public void addDescriptionForNonEndStage(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;
        if (currentStage == Stage.WAIT) {
            info.addPara(person.getNameString() + " is decrypting the data core, " + person.getHeOrShe() + "will contact you when it's done.", opad);
        } else if (currentStage == Stage.GET_DATA) {
            info.addPara(person.getNameString() + " has finished decrypting the data core, return to " + person.getHimOrHer() + " to find out what was decrypted.", opad);
            addStandardMarketDesc(person.getNameString() + " is located " + person.getMarket().getOnOrAt(), person.getMarket(), info, opad);
        }
    }

    @Override
    public boolean addNextStepText(TooltipMakerAPI info, Color tc, float pad) {
        if (currentStage == Stage.WAIT) {
            info.addPara("Wait until " + person.getNameString() + " decrypts the data core", tc, pad);
            return true;
        } else if (currentStage == Stage.GET_DATA) {
            info.addPara("Return to " + person.getNameString(), tc, pad);
            return true;
        }
        return false;
    }

    @Override
    public String getBaseName() {
        return "Unknown Genesis: The Discovery";
    }
}
