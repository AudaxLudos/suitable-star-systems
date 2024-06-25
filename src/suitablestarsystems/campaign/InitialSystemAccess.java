package suitablestarsystems.campaign;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.intel.misc.FleetLogIntel;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class InitialSystemAccess implements EveryFrameScript {
    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {
        if (Global.getSector().getPlayerFleet() == null) {
            return;
        }
        if (Global.getSector().getPlayerFleet().getStarSystem() == null) {
            return;
        }

        boolean visitedOmegaSystem = Global.getSector().getMemoryWithoutUpdate().getBoolean("$sss_omegaPlanetFound");
        boolean visitedRemnantSystem = Global.getSector().getMemoryWithoutUpdate().getBoolean("$sss_remnantPlanetFound");

        if (visitedOmegaSystem && visitedRemnantSystem) {
            Global.getSector().removeTransientScript(this);
            return;
        }

        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        StarSystemAPI playerPresentSystem = playerFleet.getStarSystem();

        if (!visitedOmegaSystem && playerPresentSystem.getEntityById("planet_sss_omega") != null) {
            Global.getSector().getMemoryWithoutUpdate().set("$sss_omegaPlanetFound", true);

            FleetLogIntel intel = new FleetLogIntel() {
                @Override
                public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
                    float oPad = 10f;
                    Color boldColor = Misc.getHighlightColor();
                    Color badColor = Misc.getNegativeHighlightColor();
                    Color omegaColor = Global.getSettings().getFactionSpec(Factions.OMEGA).getColor();
                    Color textColor = Misc.getTextColor();

                    LabelAPI label;
                    float planetInfoSize = 150f;
                    float textHeight = planetInfoSize;

                    info.showPlanetInfo(getSite(), planetInfoSize, planetInfoSize, true, 0f);
                    info.getPrev().getPosition().inTMid(-oPad);
                    if (Global.getSector().getMemoryWithoutUpdate().getBoolean("$sss_omegaPlanetQuestOverride")) {
                        label = info.addPara("You discover an extremely hostile system with fleets of %s origin and type.", 0f, textColor, omegaColor, "Unknown");
                        label.getPosition().inTMid(textHeight);
                        textHeight += label.getPosition().getHeight() + oPad;
                        label = info.addPara("The only clue you could find is a %s designated as %s with a shield surrounding it.", 0f, textColor, boldColor, getSite().getTypeNameWithWorld(), getSite().getName());
                        label.getPosition().inTMid(textHeight);
                        textHeight += label.getPosition().getHeight() + oPad;
                        label = info.addPara("You mark the %s system for future investigations.", 0f, textColor, boldColor, getSite().getStarSystem().getName());
                        label.getPosition().inTMid(textHeight);
                    } else if (Global.getSector().getMemoryWithoutUpdate().getBoolean("$sss_omegaPlanetCracked")) {
                        label = info.addPara("Your actions had adverse effects, hostile fleets of %s origin has started to spawn around the planet %s.", 0f, new Color[]{omegaColor, boldColor}, "Unknown", getSite().getName());
                        label.getPosition().inTMid(textHeight);
                        textHeight += label.getPosition().getHeight() + oPad;
                        label = info.addPara("You can only hope your actions does not %s the sector.", 0f, textColor, badColor, "doom");
                        label.getPosition().inTMid(textHeight);
                    } else {
                        label = info.addPara("You discover a %s designated as %s with a purple shield surrounding it.", 0f, textColor, boldColor, getSite().getTypeNameWithWorld(), getSite().getName());
                        label.getPosition().inTMid(textHeight);
                        textHeight += label.getPosition().getHeight() + oPad;
                        label = info.addPara("You mark the %s system for future investigations.", 0f, textColor, boldColor, getSite().getStarSystem().getName());
                        label.getPosition().inTMid(textHeight);
                    }

                    addDeleteButton(info, width);
                }

                private PlanetAPI getSite() {
                    return (PlanetAPI) Global.getSector().getEntityById("planet_sss_omega");
                }

                @Override
                protected String getName() {
                    if (Global.getSector().getMemoryWithoutUpdate().getBoolean("$sss_omegaPlanetCracked") ||
                            Global.getSector().getMemoryWithoutUpdate().getBoolean("$sss_omegaPlanetQuestOverride")) {
                        return "The Omega Planet";
                    } else {
                        return "The Purple Planet";
                    }
                }

                @Override
                public String getIcon() {
                    return "graphics/icons/intel/purple_planet.png";
                }

                @Override
                public SectorEntityToken getMapLocation(SectorMapAPI map) {
                    return getSite();
                }
            };

            Global.getSector().getIntelManager().addIntel(intel);
        }

        if (!visitedRemnantSystem && playerPresentSystem.getEntityById("planet_sss_remnant") != null) {
            Global.getSector().getMemoryWithoutUpdate().set("$sss_remnantPlanetFound", true);

            FleetLogIntel intel = new FleetLogIntel() {
                @Override
                public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
                    float oPad = 10f;
                    Color textColor = Misc.getTextColor();
                    Color boldColor = Misc.getHighlightColor();
                    Color remnantColor = Misc.getDesignTypeColor("Remnant");

                    LabelAPI label;
                    float planetInfoSize = 150f;
                    float textHeight = planetInfoSize;

                    info.showPlanetInfo(getSite(), planetInfoSize, planetInfoSize, true, 0f);
                    info.getPrev().getPosition().inTMid(-oPad);
                    label = info.addPara("You find a strange system with %s fleets but no signs of a %s station.", 0f, textColor, remnantColor, "Remnant", "Remnant");
                    label.getPosition().inTMid(textHeight);
                    textHeight += label.getPosition().getHeight() + oPad;
                    label = info.addPara("The only clue you could find is a %s designated as %s with a cyan shield surrounding it.", 0f, textColor, boldColor, getSite().getTypeNameWithWorld(), getSite().getName());
                    label.getPosition().inTMid(textHeight);
                    textHeight += label.getPosition().getHeight() + oPad;
                    label = info.addPara("You mark the %s system for future investigations.", 0f, textColor, boldColor, getSite().getStarSystem().getName());
                    label.getPosition().inTMid(textHeight);

                    addDeleteButton(info, width);
                }

                private PlanetAPI getSite() {
                    return (PlanetAPI) Global.getSector().getEntityById("planet_sss_remnant");
                }

                @Override
                protected String getName() {
                    return "The Remnant Planet";
                }

                @Override
                public String getIcon() {
                    return "graphics/icons/intel/cyan_planet.png";
                }

                @Override
                public SectorEntityToken getMapLocation(SectorMapAPI map) {
                    return getSite();
                }
            };

            Global.getSector().getIntelManager().addIntel(intel);
        }
    }
}
