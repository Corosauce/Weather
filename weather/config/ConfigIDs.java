package weather.config;

import java.io.File;

import modconfig.IConfigCategory;




import weather.WeatherMod;

public class ConfigIDs implements IConfigCategory {

	public static int sensorID;
	public static int sirenID;
	public static int treeID;
	public static int itemTornadoID;
	public static int itemWormID;
	public static int itemShockWaveID;
	public static int itemDrillID;
	public static int itemTestID;
	public static int itemSurfboardID;

	public ConfigIDs() {
		sensorID = WeatherMod.blockIndexID++;
		sirenID = WeatherMod.blockIndexID++;
		treeID = WeatherMod.blockIndexID++;
		
		itemTornadoID = WeatherMod.itemIndexID++;
		itemWormID = WeatherMod.itemIndexID++;
		itemShockWaveID = WeatherMod.itemIndexID++;
		itemShockWaveID = WeatherMod.itemIndexID++;
		itemDrillID = WeatherMod.itemIndexID++;
		itemTestID = WeatherMod.itemIndexID++;
		itemSurfboardID = WeatherMod.itemIndexID++;
	}
	
	@Override
	public String getConfigFileName() {
		return "WeatherMod" + File.separator + "IDs";
	}

	@Override
	public String getCategory() {
		return "Weather: Block/Item IDs";
	}

	@Override
	public void hookUpdatedValues() {
		// TODO Auto-generated method stub
		
	}

}
