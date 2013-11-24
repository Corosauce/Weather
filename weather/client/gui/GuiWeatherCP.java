package weather.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import weather.ServerTickHandler;
import weather.WeatherMod;
import weather.config.ConfigTornado;
import weather.config.ConfigWavesMisc;
import weather.storm.StormManager;
import cpw.mods.fml.common.FMLCommonHandler;

public class GuiWeatherCP extends GuiScreen
{
    private int updateCounter2 = 0;
    private int updateCounter = 0;
    private int inventoryRows = 0;

    private int ySize;
    private int xSize;
    private int xOffset;

    private static int G_RAIN = 0;
    private static int G_SPAWNTORNADO = 1;
    private static int G_SPAWNSTORM = 2;
    private static int G_PREVSTAGE = 3;
    private static int G_NEXTSTAGE = 4;
    private static int G_TOGGLEWAVERANGE = 5;
    private static int G_TOGGLEWAVEHEIGHT = 6;
    private static int G_2 = 9;
    private static int G_DEBUG = 8;
    private static int G_CLOSE = 9;
    
    public ResourceLocation resGUI = new ResourceLocation(WeatherMod.modID + ":textures/gui/weatherGui.png");
    

    //container additions \\

    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {
        //inventoryRows = 5;
        if (mc == null)
        {
            return;
        }

        //int var4 = this.mc.renderEngine.getTexture("/coro/weather/weatherGui.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        //this.mc.renderEngine.bindTexture("/coro/weather/weatherGui.png");
        mc.getTextureManager().bindTexture(resGUI);
        //GL11.glBindTexture(GL11.GL_TEXTURE_2D, var4);
        int var5 = (this.width - this.xSize) / 2;
        int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5 + xOffset, var6, 0, 0, this.xSize, ySize);
        //this.drawTexturedModalRect(var5, var6 + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
        this.drawCenteredString(this.fontRenderer, "Weather Menu", ((this.width - this.xSize) / 2) + xOffset + 45, var6 - 10, 16777215);
    }

    // end container additions //

    public boolean doesGuiPauseGame()
    {
        return false;
    }

    public void initGui()
    {
        xSize = 176;
        ySize = 200;
        xOffset = (int)(this.width / 4 * 1.92);
        this.updateCounter2 = 0;
        this.buttonList.clear();
        byte var1 = -16;
        int startX = ((this.width - this.xSize) / 2) + xOffset + 6;
        int startY = (this.height - this.ySize) / 2 + 23;
        int div = 22;
        this.buttonList.add(new GuiButton(G_RAIN, startX, startY + 0 + var1, 90, 20, "Particle Rain"));
        this.buttonList.add(new GuiButton(G_TOGGLEWAVERANGE, startX, startY + div * 1 + var1, 90, 20, "Set Wave Range"));
        if (FMLCommonHandler.instance().getMinecraftServerInstance() != null) {
        	this.buttonList.add(new GuiButton(G_TOGGLEWAVEHEIGHT, startX, startY + div * 2 + var1, 90, 20, "Set Wave Height"));
        }
        //this.buttonList.add(new GuiButton(G_SPAWNTORNADO, startX, startY + div * 1 + var1, 90, 20, "Force Tornado"));
        this.buttonList.add(new GuiButton(G_SPAWNSTORM, startX, startY + div * 3 + var1, 90, 20, "Spawn Storm"));
        //this.buttonList.add(new GuiButton(G_PREVSTAGE, startX, startY + div * 3 + var1, 90, 20, "Prev Stage"));
        //this.buttonList.add(new GuiButton(G_NEXTSTAGE, startX, startY + div * 4 + var1, 90, 20, "Next Stage"));
        this.buttonList.add(new GuiButton(G_DEBUG, startX, startY + div * 5 + var1, 90, 20, "Debug Info"));
        this.buttonList.add(new GuiButton(G_CLOSE, startX, startY + div * 6 + var1, 90, 20, "Close"));
    }

    protected void actionPerformed(GuiButton var1)
    {
        if (var1.id == G_RAIN)
        {
        	ConfigTornado.smoothRain = !ConfigTornado.smoothRain;
        }

        if (var1.id == G_DEBUG)
        {
            WeatherMod.showWeatherInfo = !WeatherMod.showWeatherInfo;
        }

        if (var1.id == G_PREVSTAGE)
        {
            WeatherMod.stormMan.prevStage();
        }

        if (var1.id == G_NEXTSTAGE)
        {
            WeatherMod.stormMan.nextStage();
        }

        if (var1.id == G_SPAWNTORNADO)
        {
            //mod_EntMover.t_SpawnTornado = true;
            //WeatherMod.t_trySpawnTornado = true;
        }
        
        if (var1.id == G_TOGGLEWAVERANGE)
        {
            ConfigWavesMisc.waveRenderRange += 20;
            if (ConfigWavesMisc.waveRenderRange > 150) {
            	ConfigWavesMisc.waveRenderRange = 0;
            }
            //cant save out D:
            //WeatherMod.preInitConfig.getOrCreateIntProperty("waveRenderRange", Configuration.CATEGORY_GENERAL, WeatherMod.waveRenderRange).getInt();
            //WeatherMod.preInitConfig.save();
        }
        
        if (var1.id == G_TOGGLEWAVEHEIGHT)
        {
        	if (ServerTickHandler.sMans != null) {
	        	for (int i = 0; i < ServerTickHandler.sMans.size(); i++) {
		        	StormManager sMan = ServerTickHandler.sMans.get(i);
		        	
		        	sMan.baseWaveHeight += 1;
		            if (sMan.baseWaveHeight > 15) {
		            	sMan.baseWaveHeight = 1;
		            }
		            
		            //syncing
		            WeatherMod.stormMan.baseWaveHeight = sMan.baseWaveHeight;
	        	}
        	}
        }

        if (var1.id == G_SPAWNSTORM)
        {
            WeatherMod.spawnStorm();
        }

        if (var1.id == G_CLOSE)
        {
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
        }
    }

    public void updateScreen()
    {
        super.updateScreen();
        ++this.updateCounter;
    }

    public void drawScreen(int var1, int var2, float var3)
    {
        //this.drawDefaultBackground();
        drawGuiContainerBackgroundLayer(0, 0, 0);
        WeatherMod.proxy.weatherDbg();
        WeatherMod.proxy.windDbg();
        super.drawScreen(var1, var2, var3);
    }
}
