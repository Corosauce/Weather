package weather;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.src.ModLoader;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.MinecraftForge;
import weather.blocks.TileEntityTSiren;
import weather.blocks.TileEntityTSirenRenderer;
import weather.blocks.structure.Structure;
import weather.blocks.structure.StructureRenderer;
import weather.client.entities.MovingBlockRenderer;
import weather.client.entities.RenderCloud;
import weather.client.entities.RenderHail;
import weather.client.entities.RenderStorm;
import weather.client.entities.RenderSurfboard;
import weather.client.entities.RenderTornado;
import weather.client.entities.RenderWorm;
import weather.config.ConfigWavesMisc;
import weather.entities.EntDrill;
import weather.entities.EntShockWave;
import weather.entities.EntWorldData;
import weather.entities.EntWorm;
import weather.entities.EntitySurfboard;
import weather.entities.MovingBlock;
import weather.entities.particles.EntityAnimTexFX;
import weather.entities.particles.EntityFallingRainFX;
import weather.entities.particles.EntitySnowFX;
import weather.entities.particles.EntityWaterfallFX;
import weather.entities.particles.EntityWindFX;
import weather.entities.storm.EntTornado;
import weather.entities.storm.EntityCloud;
import weather.entities.storm.EntityHail;
import weather.entities.storm.StormCluster;
import weather.system.WeatherManager;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import extendedrenderer.ExtendedRenderer;
import extendedrenderer.particle.entity.EntityRotFX;
import extendedrenderer.particle.entity.EntityTexBiomeColorFX;
import extendedrenderer.particle.entity.EntityTexFX;
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    private static String soundZipPath = "/resources/";

    public static Minecraft mc;
    
    public static final ResourceLocation resTerrain = new ResourceLocation("terrain.png");

    public ClientProxy()
    {
        mc = ModLoader.getMinecraftInstance();
    }

    @Override
    public void init(WeatherMod pMod)
    {
        super.init(pMod);
        /*mod.hammerIndex = ModLoader.addOverride("/gui/items.png", "/coro/weather/hammer.png");
        mod.speakerTex = ModLoader.addOverride("/terrain.png", "/coro/weather/speaker.png");
        mod.sensorTex = ModLoader.addOverride("/terrain.png", "/coro/weather/sensor.png");*/
        TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
        mod.effWindAnimID = new int[5];

        for (int i = 0; i < 5; i++)
        {
            mod.effWindAnimID[i] = ModLoader.addOverride("/gui/items.png", "/coro/weather/sharp" + i + ".png");
        }

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTSiren.class, new TileEntityTSirenRenderer());
        RenderingRegistry.registerEntityRenderingHandler(EntitySurfboard.class, new RenderSurfboard());
        RenderingRegistry.registerEntityRenderingHandler(Structure.class, new StructureRenderer());
        
        /*System.out.println("WEATHER MOD SOUND INSTALLING TEMP OFF");
        if (true) return;*/
        
        // Weather sounds
		
		/*installSound("streaming/waterfall.ogg");
		installSound("sound/waterfall.ogg");*/
		
		
        
        (new Thread(mod, "Weather Mod Thread")).start();
    }
    
    @Override
    public void postInit(WeatherMod pMod)
    {
    	super.postInit(pMod);
    	if (ConfigWavesMisc.proxyRenderOverrideEnabled) {
	    	EntityRendererProxyWeatherMini temp = new EntityRendererProxyWeatherMini(mc);
	        temp.rainRate = 50; //useless (?)
	        mc.entityRenderer = temp;
    	}
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
    	MinecraftForge.EVENT_BUS.register(new SoundLoader());
    }

    @Override
    public void displayRecordGui(String displayText)
    {
        //System.out.println("displayRecordGui");
        ModLoader.getMinecraftInstance().ingameGUI.setRecordPlayingMessage(displayText);
    }

    //leaf = 0; rain = 1; air dust = 2; hail = 3; sand = 4;
    @Override
    public void registerRenderInformation()
    {
        //public void addRenderer(Map var1) {
        WeatherMod.effLeafID = 0;//RenderingRegistry.addTextureOverride("/gui/particles.png", "/coro/weather/leaf1_grey.png");
        WeatherMod.effSandID = 4;//RenderingRegistry.addTextureOverride("/gui/particles.png", "/coro/weather/sand.png");
        WeatherMod.effWind2ID = 2;//RenderingRegistry.addTextureOverride("/gui/particles.png", "/coro/weather/blurry16.png");
        int coord = 3;//RenderingRegistry.addTextureOverride("/gui/particles.png", "/coro/weather/hail.png");
        RenderingRegistry rr = RenderingRegistry.instance();
        rr.registerEntityRenderingHandler(EntWorm.class, new RenderWorm());
        rr.registerEntityRenderingHandler(EntTornado.class, new RenderTornado());
        rr.registerEntityRenderingHandler(EntityHail.class, new RenderHail(coord));
        rr.registerEntityRenderingHandler(EntityCloud.class, new RenderCloud(coord));
        rr.registerEntityRenderingHandler(EntShockWave.class, new RenderTornado());
        rr.registerEntityRenderingHandler(EntDrill.class, new RenderTornado());
        rr.registerEntityRenderingHandler(MovingBlock.class, new MovingBlockRenderer());
        
        //Entities in the loaded mc world / weather effects that need fake blank renderers
        //rr.registerEntityRenderingHandler(EntWorldData.class, new RenderStorm());
        rr.registerEntityRenderingHandler(StormCluster.class, new RenderStorm());
        rr.registerEntityRenderingHandler(EntityTexFX.class, new RenderStorm());
        rr.registerEntityRenderingHandler(EntityTexBiomeColorFX.class, new RenderStorm());
        rr.registerEntityRenderingHandler(EntityFallingRainFX.class, new RenderStorm());
        rr.registerEntityRenderingHandler(EntityWaterfallFX.class, new RenderStorm());
        rr.registerEntityRenderingHandler(EntitySnowFX.class, new RenderStorm());
        
        //MinecraftForgeClient.preloadTexture("/tropicalmod/tropiterrain.png");
        //MinecraftForgeClient.preloadTexture("/tropicalmod/tropiitems.png");
        //Minecraft.getMinecraft().renderEngine.registerTextureFX(new TextureTropicsPortalFX());
//		TropicraftMod.tropicraftPortal.blockIndexInTexture = ModLoader.getUniqueSpriteIndex("/terrain.png");
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
    public void newAnimParticle(String name, World worldRef, EntTornado tornadoRef, double posX, double posY, double posZ, double velX, double velY, double velZ, double maxAge, int[] texArr, int colorID, float scale)
    {
        EntityRotFX var31 = null;

        if (name == "AnimTexFX")
        {
            var31 = new EntityAnimTexFX(worldRef, posX, posY, posZ, velX, velY, velZ, maxAge, texArr, colorID);
        }
        else
        {
            return;
        }

        ExtendedRenderer.rotEffRenderer.addEffect(var31);

        if (tornadoRef != null)
        {
            tornadoRef.funnelEffects.add(var31);
        }

        WeatherMod.particleCount++;
        var31.renderDistanceWeight = 1000.0D;
        var31.noClip = true;
        var31.setSize(1.25F, 1.25F);
        var31.setPosition(posX, posY, posZ);
        WeatherUtil.setParticleScale(var31, WeatherUtil.getParticleScale(var31) * scale);
        //var31.particleScale *= scale;
        //var31 = new c_w_EntityWindFX(worldRef, posX, posY, posZ, velX, velY, velZ, maxAge, colorID);
        //var31 = new c_w_EntityAnimTexFX(this.worldObj, d + (double)f8 * d4, d1 + (double)f9 * d4, d2 + (double)f10 * d4, d6 / 2D, d7 / 2D, d8 / 2D, 8D, WeatherMod.effWindAnimID, colorID);
        //c_w_EntityAnimTexFX
    }

    @Override
    public void newParticle(String name, World worldRef, EntTornado tornadoRef, double posX, double posY, double posZ, double velX, double velY, double velZ, double maxAge, int colorID)
    {
        EntityRotFX var31 = null;

        if (name == "WindFX")
        {
            var31 = new EntityWindFX(worldRef, posX, posY, posZ, velX, velY, velZ, maxAge, colorID);
        }
        else
        {
            return;
        }

        ExtendedRenderer.rotEffRenderer.addEffect(var31);

        if (tornadoRef != null)
        {
            tornadoRef.funnelEffects.add(var31);
        }

        WeatherMod.particleCount++;
        var31.renderDistanceWeight = 1000.0D;
        var31.noClip = true;
        var31.setSize(1.25F, 1.25F);
        var31.posY = posY;
        var31.setPosition(posX, posY, posZ);
    }

    @Override
    public void weatherDbg()
    {
        int x = 30;
        int y = 35;
        World worldRef = WeatherMod.worldRef;
        WorldInfo wi = worldRef.getWorldInfo();
        List[] fxL = ExtendedRenderer.rotEffRenderer.fxLayers;
        List[] fxLV = WeatherMod.fxLayers;
        //mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("Rain: " + worldRef.getWorldInfo().getRainTime()).toString(), x, y, worldRef.getWorldInfo().isRaining() ? 0x0000ff : 0xffffff);
        //mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("Thundering: " + worldRef.getWorldInfo().getThunderTime()).toString(), x, y + 10, worldRef.getWorldInfo().isThundering() ? 0x0000ff : 0xffffff);
        mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("Storm Stage: " + WeatherMod.stormMan.stage).toString(), x, y + 20, 0xffffff);
        //mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("stormTime: " + WeatherMod.stormMan.stormTime).toString(), x, y + 30, WeatherMod.stormMan.stormActive ? 0x0000ff : 0xffffff);
        mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("Storm Intensity: " + WeatherMod.stormMan.stormIntensity).toString(), x, y + 30, 0xffffff);
        //mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("stormDying: " + WeatherMod.stormMan.stormDying).toString(), x, y + 50, 0xffffff);
        mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("Rotating Particles: " + (fxL[0].size()+fxL[1].size()+fxL[2].size()+fxL[3].size()+fxL[4].size()+fxL[5].size())).toString(), x, y + 90, 0xffffff);
        mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("Normal Particles: " + (fxLV[0].size()+fxLV[1].size()+fxLV[2].size()+fxLV[3].size())).toString(), x, y + 100, 0xffffff);
        mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("Particle Blocks: " + WeatherMod.lastTickFoundBlocks).toString(), x, y + 110, 0xffffff);
        
        //mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("activeTornadoes: " + WeatherMod.activeTornadoes).toString(), x, y + 70, 0xffffff);
    }

    @Override
    public void windDbg()
    {
        int x = 30;
        int y = 35;
        World worldRef = mc.theWorld;
        WeatherManager wMan = WeatherMod.weatherMan;
        //mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("Rain: " + worldRef.worldInfo.getRainTime()).toString(), x, y, worldRef.worldInfo.isRaining() ? 0x0000ff : 0xffffff);
        //mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("Thundering: " + worldRef.worldInfo.getThunderTime()).toString(), x, y+10, worldRef.worldInfo.isThundering() ? 0x0000ff : 0xffffff);
        //mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("Wind Stage: " + wMan.stage).toString(), x, y + 20, 0xffffff);
        //mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("stormTime: " + wMan.stormTime).toString(), x, y + 30, wMan.stormActive ? 0x0000ff : 0xffffff);
        //mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("stormIntensity: " + wMan.stormIntensity).toString(), x, y + 40, 0xffffff);
        //mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("stormDying: " + wMan.stormDying).toString(), x, y + 50, 0xffffff);
        mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("Wind Strength: " + wMan.wind.strength).toString(), x, y + 40, 0xffffff);
        mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("Smooth Wind Strength: " + wMan.wind.strengthSmooth).toString(), x, y + 50, 0xffffff);
        //mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("rotParticles: " + mod_ExtendedRenderer.rotEffRenderer.fxLayers[2].size()).toString(), x, y+70, 0xffffff);
        //float rot = fixYaw(mc.thePlayer.rotationYaw);
        mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("Player Aim: " + WeatherMod.fixYaw(mc.thePlayer.rotationYaw + 180F)).toString(), x, y + 70, 0xffffff);
        mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("Wind Aim: " + WeatherMod.fixYaw(wMan.wind.direction)).toString(), x, y + 80, 0xffffff);
        //mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("rainStrength: " + worldRef.rainingStrength).toString(), x, y+110, 0xffffff);
        //mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("t_trySpawnTornado: " + t_trySpawnTornado).toString(), x, y+90, 0xffffff);
        //mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("t_SpawnTornado: " + t_trySpawnTornado).toString(), x, y+100, 0xffffff);
        //mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("activeTornadoes: " + activeTornadoes).toString(), x, y+110, 0xffffff);
        //mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("Wind Event: " + wMan.windEventTime).toString(), x, y + 90, wMan.windEventTime > 0 ? 0x0000ff : 0xffffff);
        //mc.fontRenderer.drawStringWithShadow(new StringBuilder().append("Wind Gust Event: " + wMan.windGustEventTime).toString(), x, y + 100, wMan.windGustEventTime > 0 ? 0x0000ff : 0xffffff);
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
