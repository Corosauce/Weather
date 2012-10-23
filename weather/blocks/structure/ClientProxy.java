package weather.blocks.structure;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.common.Side;
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
