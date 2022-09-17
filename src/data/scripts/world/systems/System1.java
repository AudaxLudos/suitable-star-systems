package data.scripts.world.systems;

import java.util.Random;

import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;

public class System1 {
    public void generate(SectorAPI sector) {
        // Get character seed
        Random random = StarSystemGenerator.random;
        // Get star system
        StarSystemAPI system = sector.createStarSystem("system1");

        // Create star for system
        PlanetAPI star = system.initStar("system1", "star_orange", 750f, 400f, 10f, 05f, 3f);

        // Create planet 1
        String planet1Name = "Planet1";
        PlanetAPI planet1 = system.addPlanet(planet1Name.toLowerCase(), star, planet1Name, "barren_castiron", random.nextFloat() * 360f, 90f, 3000f, 300f);
        Misc.initConditionMarket(planet1);
        MarketAPI planet1Market = planet1.getMarket();
        planet1Market.addCondition("ore_moderate");
        planet1Market.addCondition("rare_ore_moderate");
        planet1Market.addCondition("no_atmosphere");
        planet1Market.addCondition("hot");

        // Create planet 2
        String planet2Name = "Planet2";
        PlanetAPI planet2 = system.addPlanet(planet2Name.toLowerCase(), star, planet2Name, "arid", random.nextFloat() * 360f, 130f, 4000f, 400f);
        Misc.initConditionMarket(planet2);
        MarketAPI planet2Market = planet2.getMarket();
        planet2Market.addCondition("farmland_adequate");
        planet2Market.addCondition("ore_moderate");
        planet2Market.addCondition("ruins_widespread");
        planet2Market.addCondition("organics_common");
        planet2Market.addCondition("habitable");
        planet2Market.addCondition("hot");

        // Create planet 3
        String planet3Name = "Planet3";
        PlanetAPI planet3 = system.addPlanet(planet3Name.toLowerCase(), star, planet3Name, "barren-desert", random.nextFloat() * 360f, 90f, 6000f, 600f);
        Misc.initConditionMarket(planet3);
        MarketAPI planet3Market = planet3.getMarket();
        planet3Market.addCondition("ore_sparse");
        planet3Market.addCondition("rare_ore_sparse");
        planet3Market.addCondition("organics_trace");
        planet3Market.addCondition("thin_atmosphere");

        // Create planet 4
        String planet4Name = "Planet4";
        PlanetAPI planet4 = system.addPlanet(planet4Name.toLowerCase(), star, planet4Name, "gas_giant", random.nextFloat() * 360f, 250f, 7000f, 700f);
        Misc.initConditionMarket(planet4);
        MarketAPI planet4Market = planet4.getMarket();
        planet4Market.addCondition("volatiles_diffuse");
        planet4Market.addCondition("high_gravity");

        // Auto generate jump points
        system.autogenerateHyperspaceJumpPoints(true, false);

        // Clear nebula in hyperspace
        HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin)Misc.getHyperspaceTerrain().getPlugin();
        NebulaEditor editor = new NebulaEditor(plugin);
        float minRadius = plugin.getTileSize() * 2f;
        float radius = system.getMaxRadiusInHyperspace();
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0f, radius + minRadius, 0f, 360f);
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0f, radius + minRadius, 0f, 360f, 0.25f);
    }
}
