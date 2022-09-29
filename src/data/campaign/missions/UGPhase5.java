package data.campaign.missions;

import java.awt.Color;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithBarEvent;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class UGPhase5 extends HubMissionWithBarEvent {
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
        person = getImportantPerson("UGPerson2");
        if (person == null)
            return false;

        if (!setPersonMissionRef(person, "$UGPhase5_ref"))
            return false;

        if (!setGlobalReference("$UGPhase5_ref"))
            return false;

        // set up starting and end stages
        setStoryMission();
        setStartingStage(Stage.WAIT);
        addSuccessStages(Stage.COMPLETED);

        // Make this locations important
        makeImportant(person, "$UGPhase5_getDataFrom", Stage.GET_DATA);

        connectWithDaysElapsed(Stage.WAIT, Stage.GET_DATA, 1f);
        setStageOnGlobalFlag(Stage.COMPLETED, "$UGPhase5_completed");

        return true;
    }

    protected void updateInteractionDataImpl() {
        set("$UGPhase5_askingPrice", Misc.getWithDGS(100000f));
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
        return "Unknown Genesis: The Truth";
    }
}
