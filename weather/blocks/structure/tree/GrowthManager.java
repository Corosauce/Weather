package weather.blocks.structure.tree;

import java.util.ArrayList;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class GrowthManager {

	public World worldObj;
	
	public ArrayList<GrowthNode> activeGrowths;
	public GrowthNode trunk;
	public ChunkCoordinates source;
	
	public GrowthManager(World parWorld, ChunkCoordinates parSource) {
		source = parSource;
		trunk = new GrowthNode(this, null);
		trunk.curPos = Vec3.createVectorHelper(source.posX, source.posY + 0.5D, source.posZ);
		trunk.growthPoints.add(trunk.curPos);
		worldObj = parWorld;
		activeGrowths = new ArrayList<GrowthNode>();
		activeGrowths.add(trunk);
	}
	
	public void tickGrowth() {
		
		int maxIterations = 20;//activeGrowths.size()
		
		for (int i = 0; i < maxIterations && i < activeGrowths.size(); i++) {
			GrowthNode gn = activeGrowths.get(i);
			if (gn.isActive()) {
				gn.tickGrowth(false);
			} else {
				System.out.println("kill off node");
				activeGrowths.remove(i);
			}
		}
	}
	
}
