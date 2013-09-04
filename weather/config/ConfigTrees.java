package weather.config;

import java.io.File;

import modconfig.IConfigCategory;
import net.minecraft.block.Block;


public class ConfigTrees implements IConfigCategory {

	public static double branchInitialSpreadRangeXZMax = 0.3D;
	public static double branchInitialSpreadRangeY = 0.3D;
	public static double branchDistBetweenSubPieces = 5D;
	public static double branchGenerationShortenRate = 0.3D;
	public static double growSpeed = 1D;
	public static int branchSubPieces = 3;
	public static int branchGenerationMax = 2;
	public static int leafPlaceRate = 1;
	public static int leafMinBranchGenerationForLeafs = 1;
	public static int blockIDBranch = Block.wood.blockID;
	public static int blockIDLeaf = Block.leaves.blockID;
	public static int branchNewBranchRate = 1;
	public static double leafCreationRadius = 4D;

	public ConfigTrees() {
		
	}
	
	@Override
	public String getConfigFileName() {
		return "WeatherMod" + File.separator + "Trees";
	}

	@Override
	public String getCategory() {
		return "Weather: Vector Trees";
	}

	@Override
	public void hookUpdatedValues() {
		// TODO Auto-generated method stub
		
	}

}
