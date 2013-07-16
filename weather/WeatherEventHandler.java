package weather;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.ChunkEvent.Unload;
import weather.blocks.MovingBlock;

public class WeatherEventHandler {
	
	@ForgeSubscribe
	public void chunkUnload(Unload event) {
		
		for (int i = 0; i < event.getChunk().entityLists.length; i++) {
            List list1 = event.getChunk().entityLists[i];

            for (int l = 0; l < list1.size(); ++l)
            {
                Entity entity1 = (Entity)list1.get(l);
                
                if (entity1 instanceof MovingBlock) {
                	entity1.setDead();
                	if (WeatherMod.blockCount > 0) WeatherMod.blockCount--;
                	
                }
            }
		}
		//System.out.println("Chunk unloaded, WeatherMod.blockCount: " + WeatherMod.blockCount);
	}
}
