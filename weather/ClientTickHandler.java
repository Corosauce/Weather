package weather;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import weather.config.ConfigWavesMisc;
import weather.worldObjects.GuiWeatherCP;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;

public class ClientTickHandler implements ITickHandler
{
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {}

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        if (type.equals(EnumSet.of(TickType.RENDER)))
        {
            onRenderTick();
        }
        else if (type.equals(EnumSet.of(TickType.CLIENT)))
        {
            GuiScreen guiscreen = Minecraft.getMinecraft().currentScreen;

            if (guiscreen != null)
            {
                onTickInGUI(guiscreen);
            }
            else
            {
                onTickInGame();
            }
        }
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.RENDER, TickType.CLIENT);
        // In my testing only RENDER, CLIENT, & PLAYER did anything on the client side.
        // Read 'cpw.mods.fml.common.TickType.java' for a full list and description of available types
    }

    @Override
    public String getLabel()
    {
        return null;
    }

    public void onRenderTick()
    {
    	Minecraft mc = FMLClientHandler.instance().getClient();
    	
    	if (ConfigWavesMisc.debug && WeatherMod.showWeatherInfo && mc != null && mc.thePlayer != null)
        {
    		WeatherMod.proxy.weatherDbg();
    		WeatherMod.proxy.windDbg();
        }
    	
    	int var6 = 0;
    	int var7 = 0;
    	
    	//GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture("/gui/gui.png"));
    	
    	//drawTexturedModalRect(200, 80, 0, 0, 21, 22);
        //System.out.println("onRenderTick");
        //TODO: Your Code Here
    }
    
    public void drawTexturedModalRect(int par1, int par2, int par3, int par4, int par5, int par6)
    {
    	
    	float zLevel = 90.0F;
    	
        float var7 = 0.00390625F;
        float var8 = 0.00390625F;
        Tessellator var9 = Tessellator.instance;
        var9.startDrawingQuads();
        var9.addVertexWithUV((double)(par1 + 0), (double)(par2 + par6), (double)zLevel, (double)((float)(par3 + 0) * var7), (double)((float)(par4 + par6) * var8));
        var9.addVertexWithUV((double)(par1 + par5), (double)(par2 + par6), (double)zLevel, (double)((float)(par3 + par5) * var7), (double)((float)(par4 + par6) * var8));
        var9.addVertexWithUV((double)(par1 + par5), (double)(par2 + 0), (double)zLevel, (double)((float)(par3 + par5) * var7), (double)((float)(par4 + 0) * var8));
        var9.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), (double)zLevel, (double)((float)(par3 + 0) * var7), (double)((float)(par4 + 0) * var8));
        var9.draw();
    }

    public void onTickInGUI(GuiScreen guiscreen)
    {
        onTickInGame();
        //System.out.println("onTickInGUI");
        //TODO: Your Code Here
    }

    Field curPlayingStr = null;

    public void onTickInGame()
    {
        /*if (curPlayingStr == null) {
        	try {
        		curPlayingStr = GuiIngame.class.getDeclaredField("recordPlaying");
        		curPlayingStr.setAccessible(true);
        		Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(curPlayingStr, curPlayingStr.getModifiers() & ~Modifier.FINAL & ~Modifier.PRIVATE & +Modifier.PUBLIC);
        	} catch (Exception exx) {
        		exx.printStackTrace();
        	}
        }
        try {
        	//System.out.println("hmm" + curPlayingStr.get(Minecraft.getMinecraft().ingameGUI));
        	String record = (String)curPlayingStr.get(Minecraft.getMinecraft().ingameGUI);
        	if (record.contains("C418")) {
        		c_CoroWeatherUtil.setPrivateValueBoth(GuiIngame.class, Minecraft.getMinecraft().ingameGUI, "g", "recordPlaying", "DERP");
        	}
        } catch (Exception exxx) {
        	exxx.printStackTrace();
        }*/
    	
    	WeatherMod.instance.mc = FMLClientHandler.instance().getClient();
        World world = WeatherMod.instance.mc.theWorld;

        if (world != null && !FMLClientHandler.instance().getClient().isGamePaused)
        {
            WeatherMod.weather(Side.CLIENT, world);
            WeatherMod.weatherMan.tick(Side.CLIENT, world);
            WeatherMod.stormMan.tick(Side.CLIENT, world);
        }
        
        if (ConfigWavesMisc.debug && Keyboard.isKeyDown(WeatherMod.weatherInfoKey) && !(WeatherMod.instance.mc.currentScreen instanceof GuiWeatherCP)) {
        	WeatherMod.instance.mc.displayGuiScreen(new GuiWeatherCP());
    		//showWeatherInfo = false;
    	}
    }

    static void getField(Field field, Object newValue) throws Exception
    {
        field.setAccessible(true);
        // remove final modifier from field
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }
}
