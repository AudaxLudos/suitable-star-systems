package suitablestarsystems;

import com.fs.starfarer.api.Global;
import lunalib.lunaSettings.LunaSettings;
import lunalib.lunaSettings.LunaSettingsListener;

public class SSSLunaSettingsListener implements LunaSettingsListener {
    @Override
    public void settingsChanged(String modId) {
        Utils.CAN_SPAWN_MAIN_SYSTEM = getSettingsBoolean("sss_can_spawn_colonizable_system");
        Utils.CAN_OVERRIDE_MAIN_SYSTEM_LOC = getSettingsBoolean("sss_can_override_colonizable_system_location");
        Utils.MAIN_SYSTEM_X_OVERRIDE = getSettingsFloat("mainSystemXOverride");
        Utils.MAIN_SYSTEM_Y_OVERRIDE = getSettingsFloat("mainSystemYOverride");
    }

    protected boolean getSettingsBoolean(String fieldId) {
        Boolean val = LunaSettings.getBoolean("suitablestarsystems", fieldId);
        if (val == null) {
            return true;
        }
        return val;
    }

    protected float getSettingsFloat(String fieldId) {
        Float val = LunaSettings.getFloat("suitablestarsystems", fieldId);
        if (val == null) {
            return 0f;
        }
        return val;
    }
}
