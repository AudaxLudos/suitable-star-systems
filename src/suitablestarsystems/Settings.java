package suitablestarsystems;

import com.fs.starfarer.api.Global;
import lunalib.lunaSettings.LunaSettings;
import lunalib.lunaSettings.LunaSettingsListener;

public class Settings {
    public static final String MOD_ID = "suitablestarsystems";
    public static boolean SPAWN_MAIN_SYSTEM = true;
    public static boolean SPAWN_REMNANT_SYSTEM = true;
    public static boolean SPAWN_OMEGA_SYSTEM = true;
    public static boolean OVERRIDE_MAIN_SYSTEM_LOC = false;
    public static float OVERRIDE_MAIN_SYSTEM_X_POS = 0f;
    public static float OVERRIDE_MAIN_SYSTEM_Y_POS = 0f;
    public static boolean SKIP_OMEGA_PLANET_INTERACTION = false;

    public static void load() {
        Utils.random.setSeed(1);
        if (isLunaLibEnabled()) {
            LunaSettings.addSettingsListener(new SSSLunaSettingsListener());
        }
        setSettings();
    }

    public static void setSettings() {
        SPAWN_MAIN_SYSTEM = getBoolean("sss_spawnMainSystem");
        SPAWN_REMNANT_SYSTEM = getBoolean("sss_spawnRemnantSystem");
        SPAWN_OMEGA_SYSTEM = getBoolean("sss_spawnOmegaSystem");
        OVERRIDE_MAIN_SYSTEM_LOC = getBoolean("sss_overrideMainSystemLoc");
        OVERRIDE_MAIN_SYSTEM_X_POS = getFloat("sss_overrideMainSystemXPos");
        OVERRIDE_MAIN_SYSTEM_Y_POS = getFloat("sss_overrideMainSystemYPos");
        SKIP_OMEGA_PLANET_INTERACTION = getBoolean("sss_skipOmegaPlanetInteraction");
    }

    public static float getFloat(String fieldId) {
        Float val = Global.getSettings().getFloat(fieldId);
        if (isLunaLibEnabled()) {
            val = LunaSettings.getFloat(MOD_ID, fieldId);
        }
        if (val == null) {
            return 1f;
        }
        return val;
    }

    public static String getString(String fieldId) {
        String val = Global.getSettings().getString(fieldId);
        if (isLunaLibEnabled()) {
            val = LunaSettings.getString(MOD_ID, fieldId);
        }
        if (val == null || val.isEmpty()) {
            return "";
        }
        return val;
    }

    public static boolean getBoolean(String fieldId) {
        Boolean val = Global.getSettings().getBoolean(fieldId);
        if (isLunaLibEnabled()) {
            val = LunaSettings.getBoolean(MOD_ID, fieldId);
        }
        if (val == null) {
            return true;
        }
        return val;
    }

    public static boolean isLunaLibEnabled() {
        return Global.getSettings().getModManager().isModEnabled("lunalib");
    }

    public static class SSSLunaSettingsListener implements LunaSettingsListener {
        @Override
        public void settingsChanged(String s) {
            Settings.setSettings();
        }
    }
}
