package suitablestarsystems.campaign;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.intel.misc.FleetLogIntel;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

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
        if (Global.getSector().getPlayerFleet() == null)
            return;
        if (Global.getSector().getPlayerFleet().getStarSystem() == null)
            return;

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

                    info.addImage(getIcon(), width, 128, oPad);

                    if (Global.getSector().getMemoryWithoutUpdate().getBoolean("$sss_omegaPlanetQuestOverride")) {
                        info.addPara("You discover an extremely hostile system with fleets of unknown origin and type. The only clue you could find is a lone shielded planet.", oPad);
                        info.addPara("You mark the peculiar system for future investigations.", oPad);
                    } else if (Global.getSector().getMemoryWithoutUpdate().getBoolean("$sss_omegaPlanetCracked")) {
                        info.addPara("Your actions has changed the system, hostile fleets of unknown origin has started to spawn around the shielded planet.", oPad);
                        info.addPara("You can only hope your actions does not doom the sector.", oPad);
                    } else {
                        info.addPara("You discover a peculiar system, upon doing a preliminary survey you find a lone shielded planet.", oPad);
                        info.addPara("You mark the peculiar system for future investigations.", oPad);
                    }
                }

                private SectorEntityToken getSite() {
                    return Global.getSector().getEntityById("planet_sss_omega");
                }

                @Override
                protected String getName() {
                    if (Global.getSector().getMemoryWithoutUpdate().getBoolean("$sss_omegaPlanetCracked") ||
                            Global.getSector().getMemoryWithoutUpdate().getBoolean("$sss_omegaPlanetQuestOverride")) {
                        return "Unique Omega System";
                    } else {
                        return "Unknown Peculiar System";
                    }
                }

                @Override
                public String getIcon() {
                    return "graphics/icons/missions/visit_object.png";
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

                    info.addImage(getIcon(), width, 128, oPad);
                    info.addPara("You discover a peculiar system with dozens of active remnant fleet presence, but have not seen or detected an active station other than a lone shielded planet.", oPad);
                    info.addPara("You mark the peculiar system for future investigations.", oPad);
                }

                private SectorEntityToken getSite() {
                    return Global.getSector().getEntityById("planet_sss_remnant");
                }

                @Override
                protected String getName() {
                    return "Peculiar Remnant System";
                }

                @Override
                public String getIcon() {
                    return "graphics/icons/missions/visit_object.png";
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
