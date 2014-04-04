package weather.config;

import java.io.File;

import modconfig.IConfigCategory;
import weather.WeatherMod;
import weather.WeatherUtil;


public class ConfigTornado implements IConfigCategory {

	public static int Storm_Tornado_maxBlocks = 800;
	public static int Storm_Tornado_maxParticles = 3000;
	public static double Storm_Tornado_height = 60;
	public static double Storm_Tornado_maxYChange = 10D;
	public static int Storm_Tornado_rarityOfFirenado = -1;
	public static int Storm_Tornado_rarityOfDisintegrate = 15;
	public static int Storm_Tornado_rarityOfBreakOnFall = 5;
	public static boolean Storm_Tornado_grabPlayer = true;
	public static boolean Storm_Tornado_grabBlocks = true;
	public static boolean Storm_Tornado_GrabCond_StrengthGrabbing = true;
	public static boolean Storm_Tornado_GrabCond_List = false;
	public static boolean Storm_Tornado_GrabListBlacklistMode = false;
	public static String Storm_Tornado_GrabList = "";
	public static double Storm_Tornado_oddsOfTornadoTo1 = 20;
	public static double Storm_Tornado_minDaysBetweenTornado = 10;
	public static boolean Storm_Tornado_hdParticles = false;
	public static boolean Storm_Tornado_makeClouds = true;
	public static boolean Storm_Lightning_active = true;
	public static int Storm_Tornado_maxActive = 1;
	public static int TropicraftRealm_Storm_Tornado_maxActive;
	public static boolean Storm_FlyingBlocksHurt = true;
	public static int Storm_rarityOfIncrease = 4000;
	public static boolean smoothRain = true;

	public ConfigTornado() {
		if (!WeatherUtil.hasTropicraft()) {
			TropicraftRealm_Storm_Tornado_maxActive = 0;
		} else {
			TropicraftRealm_Storm_Tornado_maxActive = 0;
		}
	}
	
	@Override
	public String getConfigFileName() {
		return "WeatherMod" + File.separator + "Storm&Tornado";
	}

	@Override
	public String getCategory() {
		return "Weather: Storm & Tornado";
	}

	@Override
	public void hookUpdatedValues() {
		WeatherMod.doBlockList();
	}

}
