package data.campaign.procgen.themes;

import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.ThemeGenContext;

public class CustomThemeGenerator extends BaseThemeGenerator {
    @Override
    public void generateForSector(ThemeGenContext arg0, float arg1) {
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public String getThemeId() {
        return null;
    }
    
}
