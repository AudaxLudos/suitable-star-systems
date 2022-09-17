package data.scripts;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.campaign.CampaignTerrainAPI;
import com.fs.starfarer.api.campaign.RingBandAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.procgen.MagFieldGenPlugin;
import com.fs.starfarer.api.impl.campaign.procgen.ProcgenUsedNames;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.AccretionDiskGenPlugin.TexAndIndex;
import com.fs.starfarer.api.impl.campaign.procgen.ProcgenUsedNames.NamePick;
import com.fs.starfarer.api.impl.campaign.terrain.BaseRingTerrain;
import com.fs.starfarer.api.impl.campaign.terrain.MagneticFieldTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.RingSystemTerrainPlugin;
import com.fs.starfarer.api.util.WeightedRandomPicker;

public class ASS_Utils {
    public static String generateProceduralName(String tag, String parent) {
        NamePick namePick = ProcgenUsedNames.pickName(tag, parent, null);
        String name = namePick.nameWithRomanSuffixIfAny;
        return name;
    }

    public static TexAndIndex getTextureAndIndex() {
        TexAndIndex result = new TexAndIndex();
        WeightedRandomPicker<Integer> indexPicker = new WeightedRandomPicker<Integer>(StarSystemGenerator.random);
        WeightedRandomPicker<String> ringSet = new WeightedRandomPicker<String>(StarSystemGenerator.random);
        ringSet.add("ring_ice", 10f);
        ringSet.add("ring_dust", 10f);
        String set = (String)ringSet.pick();
        if (set.equals("ring_ice")) {
            result.tex = "rings_ice0";
            indexPicker.add(Integer.valueOf(0));
            indexPicker.add(Integer.valueOf(1));
        } else if (set.equals("ring_dust")) {
            result.tex = "rings_dust0";
            indexPicker.add(Integer.valueOf(0));
            indexPicker.add(Integer.valueOf(1));
        }
        result.index = ((Integer)indexPicker.pick()).intValue();
        return result;
    }

    public static void createAccretionDisk(SectorEntityToken focus, int numOfBands, float radius) {
        float bandWidth = 256f;
        float finalRadius = 0;
        for (float i = 0f; i < numOfBands; i++) {
            float middleRadius = radius + i * bandWidth * 0.25f - i * bandWidth * 0f;
            TexAndIndex tex = getTextureAndIndex();
            float orbitDays = middleRadius / (30f + 10f * StarSystemGenerator.random.nextFloat());
            RingBandAPI visual = (RingBandAPI)focus.getStarSystem().addRingBand((SectorEntityToken)focus, "misc", tex.tex, 256f,
                    tex.index, Color.WHITE, bandWidth, middleRadius + bandWidth / 2f, -orbitDays);
            float spiralFactor = 2f + StarSystemGenerator.random.nextFloat() * 5f;
            visual.setSpiral(true);
            visual.setMinSpiralRadius(0f);
            visual.setSpiralFactor(spiralFactor);
            finalRadius = middleRadius;
        }

        List<SectorEntityToken> rings = new ArrayList<SectorEntityToken>();
        SectorEntityToken ring = focus.getStarSystem().addTerrain("ring",
                new BaseRingTerrain.RingParams(finalRadius, finalRadius / 2f,
                        (SectorEntityToken)focus, null));
        ring.addTag("accretion_disk");
        if (((CampaignTerrainAPI)ring).getPlugin() instanceof RingSystemTerrainPlugin)
            ((RingSystemTerrainPlugin)((CampaignTerrainAPI)ring).getPlugin()).setNameForTooltip("Accretion Disk");
        ring.setCircularOrbit((SectorEntityToken)focus, 0f, 0f, 0f);
        rings.add(ring);
    }

    public static void createMagneticField(SectorEntityToken focus, float bandWidthInEngine, float middleRadius, float innerRadius, float outerRadius,
            float auroraFreqency) {
        int baseIndex = (int)(MagFieldGenPlugin.baseColors.length * StarSystemGenerator.random.nextDouble());
        int auroraIndex = (int)(MagFieldGenPlugin.auroraColors.length * StarSystemGenerator.random.nextDouble());
        SectorEntityToken magneticField = focus.getStarSystem().addTerrain("magnetic_field",
                new MagneticFieldTerrainPlugin.MagneticFieldParams(
                        bandWidthInEngine,
                        middleRadius,
                        focus,
                        innerRadius,
                        outerRadius,
                        MagFieldGenPlugin.baseColors[baseIndex],
                        auroraFreqency,
                        MagFieldGenPlugin.auroraColors[auroraIndex]));
        magneticField.setCircularOrbit(focus, 0f, 0f, 0f);
    }
}
