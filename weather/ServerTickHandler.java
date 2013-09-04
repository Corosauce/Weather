package weather;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

import net.minecraft.command.ServerCommandManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import weather.storm.StormManager;
import weather.system.WeatherManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;

public class ServerTickHandler implements ITickHandler
{
    //public static WeatherManager sWMan = new WeatherManager();
    //public static StormManager sSMan = new StormManager();
    
    //public static HashMap<Integer, WeatherManager> wMans;
    //public static HashMap<Integer, StormManager> sMans;
    
	//Used for easy iteration, could be replaced
    public static ArrayList<WeatherManager> wMans;
    public static ArrayList<StormManager> sMans;
    
    //Main lookup method for dim to weather systems
    public static HashMap<Integer, WeatherManager> dimToWeatherMan;
    public static HashMap<Integer, StormManager> dimToStormMan;
    
	private World lastWorld;
    
	public static NBTTagCompound worldNBT = new NBTTagCompound(); 
	
    public ServerTickHandler() {
    	
    	wMans = new ArrayList();
    	sMans = new ArrayList();
    	dimToStormMan = new HashMap<Integer, StormManager>();
    	dimToWeatherMan = new HashMap<Integer, WeatherManager>();
    	
    	addWorldToWeather(0);
    	
    	if (WeatherUtil.hasTropicraft()) {
    		addWorldToWeather(WeatherUtil.tropiDimID);
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
        	((ServerCommandManager)FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager()).registerCommand(new CommandWeather());
        }
        
        if (WeatherMod.blockIDToUseMapping.size() == 0) WeatherMod.doBlockList();
        
        World worlds[] = DimensionManager.getWorlds();
        
        for (int i = 0; i < worlds.length; i++) {
        	if (!dimToStormMan.containsKey(worlds[i].provider.dimensionId)) addWorldToWeather(worlds[i].provider.dimensionId);
        	dimToStormMan.get(worlds[i].provider.dimensionId).tick(Side.SERVER, worlds[i]);
        	WeatherMod.weather(Side.SERVER, worlds[i]);
        	dimToWeatherMan.get(worlds[i].provider.dimensionId).tick(Side.SERVER, worlds[i]);
        	//worlds[i]
        	
        }
        
        //Called first
        /*for (int i = 0; i < sMans.size(); i++) {
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
        }*/
        
        
    }
    
    public void addWorldToWeather(int dim) {
    	dbg("Registering Weather & Storm Manager for dim: " + dim);
    	WeatherManager wm = new WeatherManager(dim);
    	StormManager sm = new StormManager(dim);
    	
    	wMans.add(wm);
    	sMans.add(sm);
    	dimToWeatherMan.put(dim, wm);
    	dimToStormMan.put(dim, sm);
    }
    
    public void dbg(Object obj) {
    	System.out.println(obj);
    }
}
