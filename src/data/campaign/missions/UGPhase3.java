package data.campaign.missions;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithBarEvent;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class UGPhase3 extends HubMissionWithBarEvent {
    public static enum Stage {
        REPORT_TO_PERSON,
        SET_PERSON_FREE,
        PERSON_TO_MARKET,
        COMPLETED,
    }

    protected PersonAPI person;
    protected MarketAPI newMarket;
    protected MarketAPI targetMarket;

    public boolean shouldShowAtMarket(MarketAPI market) {
        return false;
    }

    @Override
    protected boolean create(MarketAPI createdAt, boolean barEvent) {
        person = (PersonAPI)Global.getSector().getMemoryWithoutUpdate().get("$UGPhase1_person");
        if (person == null)
            return false;

        if (!setPersonMissionRef(person, "$UGPhase3_ref"))
            return false;

        if (!setGlobalReference("$UGPhase3_ref"))
            return false;

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

        if (!setMarketMissionRef(targetMarket, "$UGPhase3_ref"))
            return false;

        // set up starting and end stages
        setStartingStage(Stage.REPORT_TO_PERSON);
        addSuccessStages(Stage.COMPLETED);
        setNoAbandon();

        // Make this locations important
        makeImportant(person.getMarket(), "$UGPhase3_reportTo", Stage.REPORT_TO_PERSON);
        makeImportant(targetMarket, "$UGPhase3_freePersonFrom", Stage.SET_PERSON_FREE);
        makeImportant(newMarket, "$UGPhase3_personTo", Stage.PERSON_TO_MARKET);

        connectWithGlobalFlag(Stage.REPORT_TO_PERSON, Stage.SET_PERSON_FREE, "$UGPhase3_freeHere");
        connectWithGlobalFlag(Stage.SET_PERSON_FREE, Stage.PERSON_TO_MARKET, "$UGPhase3_marketHere");
        setStageOnGlobalFlag(Stage.COMPLETED, "$UGPhase3_completed");

        return true;
    }

    protected void updateInteractionDataImpl() {
        set("$UGPhase3_danger", MarketCMD.RaidDangerLevel.EXTREME);
        set("$UGPhase3_marines", Misc.getWithDGS(getMarinesRequiredForCustomObjective(targetMarket, MarketCMD.RaidDangerLevel.EXTREME)));
    }

    @Override
    public void addDescriptionForNonEndStage(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;
        // Color h = Misc.getHighlightColor();
        if (currentStage == Stage.REPORT_TO_PERSON) {
            info.addPara("Report back to " + person.getNameString() + " and tell " + person.getHimOrHer() + " what you've uncovered.", opad);
            addStandardMarketDesc(person.getNameString() + " is located " + person.getMarket().getOnOrAt(), person.getMarket(), info, opad);
        } else if (currentStage == Stage.SET_PERSON_FREE) {
            info.addPara(person.getNameString() + " has been captured, figure out a way to free " + person.getHimOrHer() + ".", opad);
            addStandardMarketDesc(person.getNameString() + " is being held " + targetMarket.getOnOrAt(), targetMarket, info, opad);
        } else if (currentStage == Stage.PERSON_TO_MARKET) {
            info.addPara("Escort " + person.getNameString() + " to the system " + person.getHeOrShe() + " mentioned", opad);
            addStandardMarketDesc(person.getNameString() + " has a sanctuary " + newMarket.getOnOrAt(), newMarket, info, opad);
        }
    }

    @Override
    public boolean addNextStepText(TooltipMakerAPI info, Color tc, float pad) {
        // Color h = Misc.getHighlightColor();
        if (currentStage == Stage.REPORT_TO_PERSON) {
            info.addPara("Report back to " + person.getNameString(), tc, pad);
            return true;
        } else if (currentStage == Stage.SET_PERSON_FREE) {
            info.addPara("Free " + person.getNameString() + " from captivity", tc, pad);
            return true;
        } else if (currentStage == Stage.PERSON_TO_MARKET) {
            info.addPara("Escort " + person.getNameString() + " to " + person.getHisOrHer() + " sanctuary", tc, pad);
            return true;
        }
        return false;
    }

    @Override
    public String getBaseName() {
        return "Unknown Genesis: The Escape";
    }
}
