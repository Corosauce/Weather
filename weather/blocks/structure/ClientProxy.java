package weather.blocks.structure;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.src.ModLoader;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    private static String soundZipPath = "/resources/";

    public static Minecraft mc;

    public ClientProxy()
    {
        mc = ModLoader.getMinecraftInstance();
    }

    @Override
    public void init(AIBlock pMod)
    {
        super.init(pMod);
    }

    @Override
    public int getUniqueTextureLoc()
    {
        return RenderingRegistry.getUniqueTextureIndex("/terrain.png");
    }

    /**
     * This is for registering armor types, like ModLoader.addArmor used to do
     */
    public int getArmorNumber(String type)
    {
        return RenderingRegistry.addNewArmourRendererPrefix(type);
    }

    @Override
    public void loadSounds()
    {
    	
    }

    @Override
    public void displayRecordGui(String displayText)
    {
        //System.out.println("displayRecordGui");
        ModLoader.getMinecraftInstance().ingameGUI.setRecordPlayingMessage(displayText);
    }

    @Override
    public void registerRenderInformation()
    {
        RenderingRegistry rr = RenderingRegistry.instance();
        rr.registerEntityRenderingHandler(PhantomBlock.class, new PhantomBlockRenderer());
    }

    @Override
    public void registerTileEntitySpecialRenderer()
    {
        //ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySifter.class, new TileEntitySifterRenderer());
        //ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBambooChest.class, new TileEntityBambooChestRenderer());
    }

    @Override
    public World getClientWorld()
    {
        return FMLClientHandler.instance().getClient().theWorld;
    }

    @Override
    public World getSidesWorld()
    {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
        {
            return FMLClientHandler.instance().getClient().theWorld;
        }
        else
        {
            return FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(0);
        }
    }
    

	
	@Override
	public Entity getEntByID(int id) {
		return FMLClientHandler.instance().getClient().theWorld.getEntityByID(id);
	}
}
