package weather;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

import weather.storm.StormManager;
import weather.waves.CommandWaveHeight;

import net.minecraft.client.Minecraft;
import net.minecraft.src.ContainerPlayer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ServerCommandManager;
import net.minecraft.src.Slot;
import net.minecraft.src.World;
import net.minecraft.src.c_CoroWeatherUtil;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.registry.EntityRegistry;

public class ServerTickHandler implements ITickHandler
{
    //public static WeatherManager sWMan = new WeatherManager();
    //public static StormManager sSMan = new StormManager();
    
    //public static HashMap<Integer, WeatherManager> wMans;
    //public static HashMap<Integer, StormManager> sMans;
    
    public static ArrayList<WeatherManager> wMans;
    public static ArrayList<StormManager> sMans;
    
    public static HashMap<Integer, StormManager> dimToStormMan;
    
	private World lastWorld;
    
    public ServerTickHandler() {
    	
    	wMans = new ArrayList();
    	sMans = new ArrayList();
    	dimToStormMan = new HashMap();
    	
    	wMans.add(new WeatherManager());
    	sMans.add(new StormManager());
    	dimToStormMan.put(0, sMans.get(0));
    	
    	if (c_CoroWeatherUtil.hasTropicraft()) {
    		wMans.add(new WeatherManager(c_CoroWeatherUtil.tropiDimID));
        	sMans.add(new StormManager(c_CoroWeatherUtil.tropiDimID));
        	dimToStormMan.put(c_CoroWeatherUtil.tropiDimID, sMans.get(1));
    	}
    	
    	//sMans.g
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        if (type.equals(EnumSet.of(TickType.SERVER)))
        {
            onTickInGame();
        }
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.SERVER);
    }

    @Override
    public String getLabel()
    {
        return null;
    }
    

    public void onTickInGame()
    {
    	
        if (FMLCommonHandler.instance() == null || FMLCommonHandler.instance().getMinecraftServerInstance() == null)
        {
            return;
        }

        World world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(0);
        
        if (world != null && lastWorld != world) {
        	lastWorld = world;
        	((ServerCommandManager)FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager()).registerCommand(new CommandWaveHeight());
        }
        
        //Called first
        for (int i = 0; i < sMans.size(); i++) {
        	StormManager sMan = sMans.get(i);
        
        	World dimWorld = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(sMan.dimension);
        	
        	if (dimWorld != null) {
        		sMan.tick(Side.SERVER, dimWorld);
        	}
        }

        for (int i = 0; i < wMans.size(); i++) {
        	WeatherManager wMan = wMans.get(i);
        
        	World dimWorld = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(wMan.dimension);
        	
        	if (dimWorld != null) {
        		WeatherMod.weather(Side.SERVER, dimWorld); // hopefully this being called here should work ok for multidimensional handling
        		wMan.tick(Side.SERVER, dimWorld);
        	}
        }
        
        
    }
}
