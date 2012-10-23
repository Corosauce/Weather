package weather;

import weather.blocks.TileEntityTSiren;
import weather.storm.EntTornado;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ModLoader;
import net.minecraft.src.ServerCommandManager;
import net.minecraft.src.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;

public class CommonProxy implements IGuiHandler
{
    public World mainWorld;
    private int entityId = 6444;

    public WeatherMod mod;

    public CommonProxy()
    {
    }

    public void init(WeatherMod pMod)
    {
        mod = pMod;
        TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
        
        
        //EntityRegistry.registerModEntity(EntitySurfboard.class, "EntitySurfboard", entityId++, mod, 64, 10, true);
        
		//EntityRegistry.registerGlobalEntityID(c_w_MovingBlockStructure.class, "c_w_MovingBlockStructure", entityId-1,0,0);
        //EntityRegistry.registerModEntity(EntityKoaManly.class, "Koa Man", entityId++, mod, 64, 1, true);
        GameRegistry.registerTileEntity(TileEntityTSiren.class, "c_w_TileEntityTSiren");
    }

    public int getUniqueTextureLoc()
    {
        return 0;
    }

    public int getArmorNumber(String type)
    {
        return 0;
    }

    public int getUniqueTropicraftLiquidID()
    {
        return 0;
    }

    public void loadSounds()
    {
    }

    public void registerRenderInformation()
    {
    }

    public void registerTileEntitySpecialRenderer()
    {
    }

    public void displayRecordGui(String displayText)
    {
    }

    public World getClientWorld()
    {
        return null;
    }

    public World getServerWorld()
    {
        if (FMLCommonHandler.instance().getMinecraftServerInstance() != null)
        {
            return FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(0);
        }
        else
        {
            return null;
        }
    }

    public World getSidesWorld()
    {
        return FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(0);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world,
            int x, int y, int z)
    {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world,
            int x, int y, int z)
    {
        return null;
    }

    public void newAnimParticle(String name, World worldRef, EntTornado tornadoRef, double posX, double posY, double posZ, double velX, double velY, double velZ, double maxAge, int[] texArr, int colorID)
    {
        //nada
    }

    public void newParticle(String name, World worldRef, EntTornado tornadoRef, double posX, double posY, double posZ, double velX, double velY, double velZ, double maxAge, int colorID)
    {
        //nada
    }

    public void weatherDbg()
    {
        // TODO Auto-generated method stub
    }

    public void windDbg()
    {
        // TODO Auto-generated method stub
    }
    
    public Entity getEntByID(int id) {
		System.out.println("common getEntByID being used, this is bad");
		return null;
	}
}
