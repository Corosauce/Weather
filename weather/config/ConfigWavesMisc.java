package weather.config;

import java.io.File;

import modconfig.IConfigCategory;




import weather.c_CoroWeatherUtil;


public class ConfigWavesMisc implements IConfigCategory {

	public static int waveRenderRange = 50;
	public static int TropicraftRealm_waveRenderRange = 50;
	public static boolean demoItems = true;
	public static boolean weatherItems = true;
	public static boolean debug = false;

	public ConfigWavesMisc() {
		if (c_CoroWeatherUtil.hasTropicraft()) {
			waveRenderRange = 0;
		} else {
			
		}
	}
	
	@Override
	public String getConfigFileName() {
		return "WeatherMod" + File.separator + "Waves&Misc";
	}

	@Override
	public String getCategory() {
		return "Weather: Waves & Misc";
	}

	@Override
	public void hookUpdatedValues() {
		// TODO Auto-generated method stub
		
	}

}
