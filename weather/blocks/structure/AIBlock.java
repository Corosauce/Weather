package weather.blocks.structure;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = "AIBlock", name = "AIBlock", version = "v1.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class AIBlock {

    private static long areaCheckTimer;
    
    @SidedProxy(clientSide = "weather.blocks.structure.ClientProxy", serverSide = "weather.blocks.structure.CommonProxy")
    public static CommonProxy proxy;
    
    @Mod.Instance( value = "AIBlock" )
	public static AIBlock instance;
    
    //@Instance
	//public static AIBlock instance;

	public AIBlock() {
        
        
    }

    public static boolean canGrab(int blockID) {
        if (blockID == Block.dirt.blockID || blockID == Block.grass.blockID || blockID == Block.stone.blockID || blockID == Block.sand.blockID) {
            return true;
        }

        return false;
    }
    
    @Init
    public void load(FMLInitializationEvent event) {
    	
    	int entityId = 32586;
    	
    	proxy.init(this);
    	proxy.registerRenderInformation();
    }
    
    

    public static boolean tryRip(World world, EntityPlayer player, int tryX, int tryY, int tryZ, boolean notify) {
        if (world.rand.nextInt(10) != 0) {
            //return false;
        }

        int blockID = world.getBlockId(tryX,tryY,tryZ);

        if (!canGrab(blockID)) {
            return false;
        }

        if (blockID == 0 || world.getBlockId(tryX,tryY+1,tryZ) != 0) {
            return false;
        }

        if (player.getDistance(tryX, tryY, tryZ) < 5) {
            return false;
        }

        world.setBlock(tryX,tryY,tryZ, 0, 0, 2);
        PhantomBlock mBlock;

        if (blockID == Block.grass.blockID) {
            mBlock = new PhantomBlock(world,tryX,tryY,tryZ, Block.dirt.blockID);
        } else {
            mBlock = new PhantomBlock(world,tryX,tryY,tryZ, blockID);
        }

        mBlock.controller = null;
        mBlock.type = 0;
        mBlock.motionY = 0.1;
        mBlock.target = player;
        mBlock.pathfind(player);
        world.spawnEntityInWorld(mBlock);
        return true;
    }

    public static void tick(Side side, World world) {
    	
        /*if (areaCheckTimer < System.currentTimeMillis() && Keyboard.isKeyDown(Keyboard.getKeyIndex("N"))) {
            areaCheckTimer = System.currentTimeMillis() + 50;

            for (int i = 0; i < world.playerEntities.size(); i++) {
            	EntityPlayer player = (EntityPlayer)world.playerEntities.get(i);
            	
            	int posX = (int)player.posX;
                int posY = (int)player.posY;
                int posZ = (int)player.posZ;

                for (int k = 0; k < 400; k++) {
                    int tryX = (int)posX+world.rand.nextInt(40)-20;
                    int tryY = (int)posY-40+world.rand.nextInt(80);
                    int tryZ = (int)posZ+world.rand.nextInt(40)-20;
                    tryRip(world, player, tryX, tryY, tryZ, true);
                }
            }
        }*/
    }
}
