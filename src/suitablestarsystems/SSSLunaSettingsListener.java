package suitablestarsystems;

import lunalib.lunaSettings.LunaSettingsListener;

public class SSSLunaSettingsListener implements LunaSettingsListener {
    @Override
    public void settingsChanged(String modId) {
        Utils.loadSettings();
    }
}
