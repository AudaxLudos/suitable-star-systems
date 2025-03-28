package suitablestarsystems.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.enc.EncounterManager;
import com.fs.starfarer.api.impl.campaign.enc.EncounterPoint;
import com.fs.starfarer.api.impl.campaign.enc.EncounterPointProvider;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.fleets.SourceBasedFleetManager;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantAssignmentAI;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantSeededFleetManager;
import suitablestarsystems.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RemnantFleetSpawnerManager extends SourceBasedFleetManager {

    protected int minPts;
    protected int maxPts;
    protected int totalLost;
    protected transient RemnantSystemEPGenerator epGen;
    protected transient boolean addedListener = false;

    public RemnantFleetSpawnerManager(SectorEntityToken source, float thresholdLY, int minFleets, int maxFleets, float respawnDelay, int minPts, int maxPts) {
        super(source, thresholdLY, minFleets, maxFleets, respawnDelay);
        this.minPts = minPts;
        this.maxPts = maxPts;
    }

    @Override
    public void advance(float amount) {
        if (!this.addedListener) {
            this.epGen = new RemnantSystemEPGenerator();
            Global.getSector().getListenerManager().addListener(this.epGen, true);
            this.addedListener = true;
        }
        super.advance(amount);
    }

    @Override
    protected CampaignFleetAPI spawnFleet() {
        if (this.source == null) {
            return null;
        }

        Random random = Utils.random;

        int combatPoints = this.minPts + random.nextInt(this.maxPts - this.minPts + 1);
        int bonus = this.totalLost * 4;
        if (bonus > this.maxPts) {
            bonus = this.maxPts;
        }
        combatPoints += bonus;
        String type = FleetTypes.PATROL_SMALL;
        if (combatPoints > 8) {
            type = FleetTypes.PATROL_MEDIUM;
        }
        if (combatPoints > 16) {
            type = FleetTypes.PATROL_LARGE;
        }
        combatPoints *= 8;

        FleetParamsV3 params = new FleetParamsV3(
                this.source.getLocationInHyperspace(),
                Factions.REMNANTS,
                1f,
                type,
                combatPoints, // combatPts
                0f, // freighterPts
                0f, // tankerPts
                0f, // transportPts
                0f, // linerPts
                0f, // utilityPts
                0f // qualityMod
        );
        params.random = random;

        CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);
        if (fleet == null) {
            return null;
        }

        this.source.getContainingLocation().addEntity(fleet);
        RemnantSeededFleetManager.initRemnantFleetProperties(random, fleet, false);
        fleet.setLocation(this.source.getLocation().x, this.source.getLocation().y);
        fleet.setFacing(random.nextFloat() * 360f);
        fleet.addScript(new RemnantAssignmentAI(fleet, (StarSystemAPI) this.source.getContainingLocation(), this.source));
        fleet.getMemoryWithoutUpdate().set("$sourceId", this.source.getId());

        return fleet;
    }

    @Override
    public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, CampaignEventListener.FleetDespawnReason reason, Object param) {
        super.reportFleetDespawnedToListener(fleet, reason, param);
        if (reason == CampaignEventListener.FleetDespawnReason.DESTROYED_BY_BATTLE) {
            String sid = fleet.getMemoryWithoutUpdate().getString("$sourceId");
            if (sid != null && this.source != null && sid.equals(this.source.getId())) {
                this.totalLost++;
            }
        }
    }

    public class RemnantSystemEPGenerator implements EncounterPointProvider {
        public List<EncounterPoint> generateEncounterPoints(LocationAPI where) {
            if (!where.isHyperspace()) {
                return null;
            }
            if (RemnantFleetSpawnerManager.this.totalLost > 0 && RemnantFleetSpawnerManager.this.source != null) {
                String id = "ep_" + RemnantFleetSpawnerManager.this.source.getId();
                EncounterPoint ep = new EncounterPoint(id, where, RemnantFleetSpawnerManager.this.source.getLocationInHyperspace(), EncounterManager.EP_TYPE_OUTSIDE_SYSTEM);
                ep.custom = this;
                List<EncounterPoint> result = new ArrayList<>();
                result.add(ep);
                return result; // source.getContainingLocation().getName()
            }
            return null;
        }
    }
}
