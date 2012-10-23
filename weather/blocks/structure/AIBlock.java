package weather.blocks.structure;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.src.BaseMod;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.GuiChat;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MLProp;
import net.minecraft.src.MathHelper;
import net.minecraft.src.ModLoader;
import net.minecraft.src.SoundPool;
import net.minecraft.src.Vec3;
import net.minecraft.src.World;
import paulscode.sound.SoundSystem;
import java.util.Random;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import java.util.Map;
import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.TickRegistry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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

        world.setBlockWithNotify(tryX,tryY,tryZ, 0);
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
