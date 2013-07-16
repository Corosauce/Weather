package weather.config;

import java.io.File;

import modconfig.IConfigCategory;





public class ConfigWind implements IConfigCategory {

	public static boolean Wind_active = true;
	public static int Wind_rarityOfIncrease = 400;
	public static boolean Wind_Particle_leafs = true;
	public static double Wind_Particle_leaf_rate = 1D;
	public static boolean Wind_Particle_air = true;
	public static boolean Wind_Particle_sand = true;//not used since 1.3.2
	public static boolean Wind_Particle_waterfall = true;
	public static boolean Wind_Particle_snow = false;
	public static boolean Wind_Particle_fire = false;
	public static boolean TropicraftRealm_Wind_active;
	public static double volWindScale = 0.5D;

	@Override
	public String getConfigFileName() {
		return "WeatherMod" + File.separator + "Wind";
	}

	@Override
	public String getCategory() {
		return "Weather: Wind";
	}

	@Override
	public void hookUpdatedValues() {
		// TODO Auto-generated method stub
		
	}

}
