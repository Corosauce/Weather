package weather.blocks.structure;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy implements IGuiHandler
{
    public World mainWorld;
    private int entityId = 6444;
    
    public AIBlock mod;

    public CommonProxy()
    {
    }

    public void init(AIBlock pMod)
    {
        mod = pMod;

    	EntityRegistry.registerModEntity(PhantomBlock.class, "PhantomBlock", entityId++, pMod, 64, 1, true);
    	EntityRegistry.registerModEntity(Structure.class, "Structure", entityId++, mod, 64, 1, true);
    	TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
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

    public void newAnimParticle(String name, World worldRef, double posX, double posY, double posZ, double velX, double velY, double velZ, double maxAge, int[] texArr, int colorID)
    {
        //nada
    }

    public void newParticle(String name, World worldRef, double posX, double posY, double posZ, double velX, double velY, double velZ, double maxAge, int colorID)
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
