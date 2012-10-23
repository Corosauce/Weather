package weather.blocks.structure;

import java.util.List;
import java.util.LinkedList;
import java.util.Random;
import java.lang.reflect.Field;

import weather.WeatherMod;

import net.minecraft.src.Entity;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;

public class StructureTemplates
{
	
	static Random rand = new Random();

    public StructureTemplates()
    {
        
        
    }
    
    public static void fillTest(List<StructureNode> pieces, int id, int meta) { 
    	
    	pieces.add(new StructureNode(0, 0, 0, id, meta));
    	pieces.add(new StructureNode(-1, 0, 0, id, meta));
        pieces.add(new StructureNode(1, 0, 0, id, meta));
        //pieces.add(new StructureNode(-2, 0, 0, id, meta));
        //pieces.add(new StructureNode(2, 0, 0, id, meta));
        
        //pieces.add(new StructureNode(0, 0, -1, id, meta));
        //pieces.add(new StructureNode(0, 0, +1, id, meta));
        //pieces.add(new StructureNode(0, 0, -2, id, meta));
        //pieces.add(new StructureNode(0, 0, +2, id, meta));
        
        //pieces.add(new StructureNode(0, +1, 0, id, meta));
        //pieces.add(new StructureNode(0, -1, 0, id, meta));
        //pieces.add(new StructureNode(0, +2, 0, id, meta));
        //pieces.add(new StructureNode(0, -2, 0, id, meta));
        
    }
    
    public static void fillBody(List<StructureNode> pieces, int id, int meta) { 
    	
    	pieces.add(new StructureNode(-1, 0, 0, id, meta));
        pieces.add(new StructureNode(1, 0, 0, id, meta));
        //pieces.add(new StructureNode(-2, 0, 0, id, meta));
        //pieces.add(new StructureNode(2, 0, 0, id, meta));
        
        pieces.add(new StructureNode(0, 0, -1, id, meta));
        pieces.add(new StructureNode(0, 0, +1, id, meta));
        //pieces.add(new StructureNode(0, 0, -2, id, meta));
        //pieces.add(new StructureNode(0, 0, +2, id, meta));
        
        pieces.add(new StructureNode(0, +1, 0, id, meta));
        pieces.add(new StructureNode(0, -1, 0, id, meta));
        //pieces.add(new StructureNode(0, +2, 0, id, meta));
        //pieces.add(new StructureNode(0, -2, 0, id, meta));
        
    }
    
    public static void fillArm(List<StructureNode> pieces, int id, int meta) { 
    	
    	pieces.add(new StructureNode(0, 0, 0, id, meta));
        pieces.add(new StructureNode(0, -1, 0, id, meta));
        //pieces.add(new StructureNode(0, -2, 0, id, meta));
        pieces.add(new StructureNode(0, -1, 0, id, meta));
        pieces.add(new StructureNode(0, -1, 1, id, meta));
        pieces.add(new StructureNode(0, -1, 2, id, meta));
        
    }
    
    public static void fillCross(List<StructureNode> pieces, int id, int meta) { 
    	
    	pieces.add(new StructureNode(-1, 0, 0, id, meta));
        pieces.add(new StructureNode(1, 0, 0, id, meta));
        pieces.add(new StructureNode(-2, 0, 0, id, meta));
        pieces.add(new StructureNode(2, 0, 0, id, meta));
        
        pieces.add(new StructureNode(0, 0, -1, id, meta));
        pieces.add(new StructureNode(0, 0, +1, id, meta));
        pieces.add(new StructureNode(0, 0, -2, id, meta));
        pieces.add(new StructureNode(0, 0, +2, id, meta));
        
        pieces.add(new StructureNode(0, +1, 0, id, meta));
        pieces.add(new StructureNode(0, -1, 0, id, meta));
        pieces.add(new StructureNode(0, +2, 0, id, meta));
        pieces.add(new StructureNode(0, -2, 0, id, meta));
        
    }
    
    public static void fillRandom(List<StructureNode> pieces, int id, int meta) {
    	
    	int x = 0;
        int y = 0;
        int z = 0;
        for (int i = 0; i < 40; i++) {
        	x += rand.nextInt(3)-1;
        	y += rand.nextInt(3)-1;
        	z += rand.nextInt(3)-1;
        	pieces.add(new StructureNode(x, y, z, id, meta));
        }
    }
    
    public static void fillTower(List<StructureNode> pieces, int id, int meta) {
    	
    	int x = 0;
        int y = 0;
        int z = 0;
        for (int i = 0; i < 4; i++) {
        	for (int j = 0; j < 40; j++) {
        		for (int k = 0; k < 4; k++) {
        			pieces.add(new StructureNode(i, j, k, id, meta));
        		}
        	}
        }
    }
    
    public static void fillTree(List<StructureNode> pieces, int id, int meta) {
        
        int i = 0;
        int j = 0;
        int k = 0;
        int height = 7;
        
        /*pieces.add(new StructureNode(i, j + height + 2, k, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i, j + height + 1, k + 1, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i, j + height + 1, k + 2, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i, j + height + 1, k + 3, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i, j + height, k + 4, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i + 1, j + height + 1, k, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i + 2, j + height + 1, k, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i + 3, j + height + 1, k, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i + 4, j + height, k, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i, j + height + 1, k - 1, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i, j + height + 1, k - 2, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i, j + height + 1, k - 3, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i, j + height, k - 4, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i - 1, j + height + 1, k, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i - 1, j + height + 1, k - 1, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i - 1, j + height + 1, k + 1, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i + 1, j + height + 1, k - 1, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i + 1, j + height + 1, k + 1, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i - 2, j + height + 1, k, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i - 3, j + height + 1, k, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i - 4, j + height, k, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i + 2, j + height + 1, k + 2, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i + 2, j + height + 1, k - 2, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i - 2, j + height + 1, k + 2, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i - 2, j + height + 1, k - 2, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i + 3, j + height, k + 3, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i + 3, j + height, k - 3, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i - 3, j + height, k + 3, TropicraftMod.tropicLeaves.blockID, 0));
        pieces.add(new StructureNode(i - 3, j + height, k - 3, TropicraftMod.tropicLeaves.blockID, 0));
        
        for(int ii = 0 ; ii < 10 ; ii ++) {
        	pieces.add(new StructureNode(i, (j + ii) - 2, k, TropicraftMod.tropicalWood.blockID, 0));
    	}*/
    }
}
