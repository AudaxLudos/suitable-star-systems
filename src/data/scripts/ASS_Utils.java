package data.scripts;

import com.fs.starfarer.api.impl.campaign.procgen.ProcgenUsedNames;
import com.fs.starfarer.api.impl.campaign.procgen.ProcgenUsedNames.NamePick;

public class ASS_Utils {
    public static String generateProceduralName(String tag, String parent) {
        NamePick namePick = ProcgenUsedNames.pickName(tag, parent, null);
        String name = namePick.nameWithRomanSuffixIfAny;
        return name;
    }
}
