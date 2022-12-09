package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;

import data.scripts.world.SSS_Gen;

public class SSS_ModPlugin extends BaseModPlugin {
	@Override
	public void onNewGameAfterEconomyLoad() {
		new SSS_Gen().generate(Global.getSector());
	}
}