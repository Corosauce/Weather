package weather;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import net.minecraftforge.common.Configuration;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.TextureFXManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.TickRegistry;

import paulscode.sound.SoundSystem;
import weather.blocks.BlockTSensor;
import weather.blocks.BlockTSiren;
import weather.blocks.MovingBlock;
import weather.renderer.EntityAnimTexFX;
import weather.renderer.EntityFallingRainFX;
import weather.renderer.EntityRotFX;
import weather.renderer.EntityTexFX;
import weather.storm.EntTornado;
import weather.storm.EntityCloud;
import weather.storm.EntityWindFX;
import weather.storm.StormCluster;
import weather.storm.StormManager;
import weather.waves.BlockDataPoint;
import weather.waves.EntitySurfboard;
import weather.waves.ItemSurfboard;
import weather.waves.WaveRenderer;
import weather.wind.WindHandler;
import weather.worldObjects.EntDrill;
import weather.worldObjects.EntShockWave;
import weather.worldObjects.EntWorldData;
import weather.worldObjects.EntWorm;
import weather.worldObjects.ItemTornado;

@NetworkMod(channels = { "StormData", "WindData", "Tornado" }, clientSideRequired = true, serverSideRequired = true, packetHandler = WeatherPacketHandler.class)
@Mod(modid = "WeatherMod", name = "Weather and Tornadoes", version = "v1.22 for MC v1.3.2")

public class WeatherMod implements Runnable
{
    public static String modName = "Weather Mod";
    
    @Mod.Instance( value = "WeatherMod" )
	public static WeatherMod instance;

    @SideOnly(Side.CLIENT)
    public static Minecraft mc;
    public static World worldRef;
    public static EntityPlayer player;
    public static int timeout;
    public static String msg;
    public static int color;
    public static int defaultColor = 0xffffff;

    public static Item itemTornado;
    public static Item itemWorm;
    public static Item itemShockWave;
    public static Item itemDrill;
    public static Item itemTest;
    public static Item itemSurfboard;

    public static Block blockSiren;
    public static Block blockTSensor;

    public static int blockCount;
    public static int particleCount;
    public static int particleCount2;

    public static boolean tryFinite = true;
    public static int finiteWaterId = 0;

    public static boolean doInit = true;

    public static int playerInAirTime;

    public static long areaCheckTimer;

    public static int activeTornadoes = 0;
    public static EntTornado activeTornado;
    public static boolean t_SpawnTornado = false;
    public static boolean t_trySpawnTornado = false;
    public static int t_SpawnTornado_X;
    public static int t_SpawnTornado_Y;
    public static int t_SpawnTornado_Z;
    public static long newCacheDelay;

    public static long playerLastTick;

    public static int weatherInfoKey = Keyboard.KEY_ADD;
    public static boolean showWeatherInfo = false;
    //public static int activeBlocks = 0;

    public static int blockIndexID = 1900;
    public static int itemIndexID = 22501;
    
    //START CONFIGURABLES
    
    public static int sensorID;
    public static int sirenID;

    //@MLProp public static int tornadoTime = 1500;
    public static int Storm_Tornado_maxBlocks;
    public static int Storm_Tornado_maxParticles;
    //@MLProp public static int tornadoBaseSize = 3;
    public static double Storm_Tornado_height;
    public static double Storm_Tornado_maxYChange = 10D;
    //@MLProp public static float tornadoPullRate = 0.3F;
    //@MLProp public static double tornadoLiftRate = 0.048D;
    //@MLProp public static double tornadoInitialSpeed = 0.15D;
    public static int Storm_Tornado_rarityOfFirenado;
    public static int Storm_Tornado_rarityOfDisintegrate;
    public static int Storm_Tornado_rarityOfBreakOnFall;
    //@MLProp public static float tornadoWidthScale = 1.0F;
    public static boolean grabFiniteWater = false;
    public static boolean Storm_Tornado_grabPlayer;
    public static boolean Storm_Tornado_blockStrengthGrabbing = true;//no config
    public static boolean Storm_Tornado_blockBlacklistMode = false;//no config
    public static String Storm_Tornado_blockList = "2,3,5,6,12,31,18,20,35,43,44,53,79,87";//no config
    public static float Storm_chanceOfTornado;
    public static long Storm_rarityOfLightning = 50000;//unused since 1.3.2
    //@MLProp public static int storm_Length_rnd = 12000;
    //@MLProp public static int storm_Length_fixed = 12000;
    //@MLProp public static int storm_Delay_rnd = 84000;
    //@MLProp public static int storm_Delay_fixed = 12000;
    public static boolean Storm_noRain = false;//no config
    public static int Storm_Tornado_safetyCutOffFPS = 20;//no config
    public static boolean debug;
    public static int Storm_rarityOfIncrease;
    public static int Storm_maxClusters = 10;//no config
    //public static boolean newRain = false;

    public static boolean Wind_active = true;
    public static int Wind_rarityOfIncrease = 400;
    public static boolean Wind_Particle_leafs = true;
    public static boolean Wind_Particle_air = true;
    public static boolean Wind_Particle_sand = true;//not used since 1.3.2

    public static boolean Storm_Tornado_oldParticles = false;
    public static boolean Storm_Tornado_makeClouds = true;
    public static boolean Storm_Lightning_active = true;
    public static int Storm_Tornado_maxActive = 1;
    
    public static int waveRenderRange = 50;
    public static int backupWaveRenderRange = 50;
    
    //END CONFIGURABLES

    //dynamicly adjusted fields for tornado size / severity
    //public static int relTornadoSize = 0;

    public static Map blockIDToUseMapping = new HashMap();
    public static Map entToAge = new HashMap();
    public static boolean ingui = false;

    public static IBlockAccess tWorld;

    public static float incr = 0.1F;

    @SideOnly(Side.CLIENT)
    public static SoundSystem sndSystem;
    @SideOnly(Side.CLIENT)
    public static SoundPool soundPool;
    public static int lastSoundID;

    public static int updateLCG;

    public static EntWorldData worldSaver = null;

    public static List fxLayers[];

    //stuff for generic wind....
    //posX, Y, Z
    //motion X Y Z

    //if no EntTornado, wind direction dictated from windVecX Y Z .... or a rotation yaw

    //stage based wind str:
    //- wind sounds
    //- spawning of leafs
    //- bounding box scan FROM PLAYER, since that is where things are seen most, find entities to move with wind, sky check

    //random variation for wind, try lightning style randomness for a sub intensity level, with stage as a base level intensity

    //wind str = stage * range * randomwindvariate

    public static ItemStack itemStr;
    public static long lastWorldTime;

    //weather stuff
    public static List weatherEntTypesTest;
    public static List<WeatherEntityConfig> weatherEntTypes;

    public static WeatherManager weatherMan;
    public static StormManager stormMan;

    //dont use!!! use stage!!
    //public static int stormStage;

    public static World lastWorld;

    public static int effLeafID;
    public static int effSandID;
    //public static int effWindID[];

    public static int effWindAnimID[];
    public static int effWind2ID;

    public static List p_blocks_leaf;
    public static List p_blocks_sand;

    public static long checkAreaDelay;

    public static long lastSoundPositionUpdate;
    public static String snd_dmg_close[] = new String[3];
    public static String snd_wind_close[] = new String[3];
    public static String snd_wind_far[] = new String[3];
    public static Map soundToLength = new HashMap();
    public static int snd_rand[] = new int[3];
    public static long soundTimer[] = new long[3];
    public static int soundID[] = new int[3];

    public static boolean tornadoItems = true;

    public static List stormClusters;

    public static Random rand = new Random();

    //NEW FORGE FIELDS
    @SidedProxy(clientSide = "weather.ClientProxy", serverSide = "weather.CommonProxy")
    public static CommonProxy proxy;

    public static void doBlockList()
    {
        blockIDToUseMapping.clear();
        //System.out.println("Blacklist: ");
        String[] splEnts = Storm_Tornado_blockList.split(",");
        int[] blocks = new int[splEnts.length];

        for (int i = 0; i < splEnts.length; i++)
        {
            splEnts[i] = splEnts[i].trim();
            blocks[i] = Integer.valueOf(splEnts[i]);
            //System.out.println(splEnts[i]);
        }

        //HashMap hashmap = null;
        //System.out.println("?!?!" + Block.blocksList.length);
        blockIDToUseMapping.put(0, false);

        for (int i = 1; i < Block.blocksList.length; i++)
        {
            //System.out.println(i);
            //Object o = i$.next();
            //String s = (String)o;

            /*Class class1 = (Class)hashmap.get(o);
            try
            {
              class1.getDeclaredConstructor(new Class[] { EntityList.class }); } catch (Throwable throwable1) {
              	blockIDToUseMapping.put(class1, false);//continue;
            }*/

            //if ((!Modifier.isAbstract(class1.getModifiers())))
            //{
            //SettingBoolean settingboolean = new SettingBoolean("mobarrow_" + s, Boolean.valueOf(true));
            //mod_Arrows303.Settings.append(settingboolean);
            //widgetclassictwocolumn.add(new WidgetBoolean(settingboolean, s));
            //mobSettings.put(s, settingboolean);
            //if ((IMob.class.isAssignableFrom(class1))) {
            if (Block.blocksList[i] != null)
            {
                boolean foundEnt = false;

                for (int j = 0; j < blocks.length; j++)
                {
                    int uh = blocks[j];

                    if (uh == i)
                    {
                        foundEnt = true;
                        //blackList.append(s + " ");
                        //System.out.println("adding to list: " + blocks[j]);
                        break;
                    }
                }

                //entList.append(s + " ");
                blockIDToUseMapping.put(Block.blocksList[i].blockID, foundEnt);
            }
            else
            {
                blockIDToUseMapping.put(i, false);
            }

            /*} else {
              //non mobs
              blockIDToUseMapping.put(class1, false);
            }*/
            //System.out.println("hmmmm? " + s);
            //}
        }

        //System.out.println(entList.toString());
        //System.out.println(blackList.toString());
    }

    @SideOnly(Side.CLIENT)
    public void run()
    {
        try
        {
            while (true)
            {
                if (mc == null)
                {
                    mc = ModLoader.getMinecraftInstance();
                }

                if (mc == null)
                {
                    Thread.sleep(1000L);
                }
                else
                {
                    if (mc.thePlayer == null)
                    {
                        Thread.sleep(1000L);
                    }
                    else
                    {
                        if (lastWorld != worldRef)
                        {
                            worldSaver = null;
                            lastWorld = worldRef;

                            for (int i = 0; i < 4; i++)
                            {
                                if (ExtendedRenderer.rotEffRenderer != null && ExtendedRenderer.rotEffRenderer.fxLayers[i] != null)
                                {
                                    ExtendedRenderer.rotEffRenderer.fxLayers[i].clear();
                                }
                            }

                            getFXLayers();
                            weatherMan.waterGrid.grid.clear();
                        }

                        worldRef = mc.theWorld;
                        player = mc.thePlayer;
                        trimBlocksFromWorld();

                        if (debug)
                        {
                            //mod_EntMover.grabPlayer = false;
                            //mod_EntMover.tornadoMaxBlocks = 1000;
                            //rarityOfDisintegrate = 1;
                            //blockBlacklistMode = true;
                            //blockList = "1,7,8,9";
                        }

                        if (doInit)
                        {
                            doInit = false;
                            doBlockList();
                        }

                        if (!mc.isGamePaused)
                        {
                            tryParticles();
                        }

                        Thread.sleep(50L);
                    }
                }
            }
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    public void modsLoaded()
    {
        if (tryFinite)
        {
            try
            {
                for (int i = 0; i < Block.blocksList.length; i++)
                {
                    if (Class.forName("BlockNWater_Still").isInstance(Block.blocksList[i]))
                    {
                        finiteWaterId = i;
                        break;
                    }
                }
            }
            catch (Exception exception)
            {
                tryFinite = false;
            }
        }

        itemStr = new ItemStack(Item.axeDiamond);
    }
    @SideOnly(Side.CLIENT)
    public static void setVolume(String soundID, float vol)
    {
        if (sndSystem == null)
        {
            getSoundSystem();
        }

        if (sndSystem != null)
        {
            sndSystem.setVolume(new StringBuilder().append(soundID).toString(), vol * ModLoader.getMinecraftInstance().gameSettings.soundVolume);
        }
    }
    @SideOnly(Side.CLIENT)
    public static int getLastSoundID()
    {
        if (sndSystem == null)
        {
            getSoundSystem();
        }

        if (sndSystem != null)
        {
            Field field = null;

            try
            {
                field = (SoundManager.class).getDeclaredField("e");
                field.setAccessible(true);
                //int j = (int)(field.getFloat(item) * (petHealFactor * (float)((EntityCreature)entityliving1).enhanced));
                lastSoundID = field.getInt(sndSystem);
                return lastSoundID;//ModLoader.getMinecraftInstance().sndManager.latestSoundID;
            }
            catch (Exception ex)
            {
                try
                {
                    field = (SoundManager.class).getDeclaredField("latestSoundID");
                    field.setAccessible(true);
                    lastSoundID = field.getInt(sndSystem);
                    return lastSoundID;
                }
                catch (Exception ex2)
                {
                    return -1;
                }
            }
        }

        return -1;
    }

    @SideOnly(Side.CLIENT)
    public static void getFXLayers()
    {
        //fxLayers
        Field field = null;

        try
        {
            field = (EffectRenderer.class).getDeclaredField("b");
            field.setAccessible(true);
            fxLayers = (List[])field.get(ModLoader.getMinecraftInstance().effectRenderer);
        }
        catch (Exception ex)
        {
            try
            {
                field = (EffectRenderer.class).getDeclaredField("fxLayers");
                field.setAccessible(true);
                fxLayers = (List[])field.get(ModLoader.getMinecraftInstance().effectRenderer);
            }
            catch (Exception ex2)
            {
                ex2.printStackTrace();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void getSoundSystem()
    {
        Field field = null;

        try
        {
            field = (SoundManager.class).getDeclaredField("a");
            field.setAccessible(true);
            sndSystem = (SoundSystem)field.get(ModLoader.getMinecraftInstance().sndManager);
            field = (SoundManager.class).getDeclaredField("c");
            field.setAccessible(true);
            soundPool = (SoundPool)field.get(ModLoader.getMinecraftInstance().sndManager);
        }
        catch (Exception ex)
        {
            try
            {
                field = (SoundManager.class).getDeclaredField("sndSystem");
                field.setAccessible(true);
                sndSystem = (SoundSystem)field.get(ModLoader.getMinecraftInstance().sndManager);
                field = (SoundManager.class).getDeclaredField("soundPoolStreaming");
                field.setAccessible(true);
                soundPool = (SoundPool)field.get(ModLoader.getMinecraftInstance().sndManager);
            }
            catch (Exception ex2)
            {
                ex2.printStackTrace();
            }
        }
    }
    @SideOnly(Side.CLIENT)
    public static int playMovingSound(String var1, float var2, float var3, float var4, float var5, float var6)
    {
        //SoundManager sndMgr = ModLoader.getMinecraftInstance().sndManager;
        if (sndSystem == null)
        {
            getSoundSystem();
        }

        if (sndSystem != null)
        {
            if (var1 != null)
            {
                SoundPoolEntry var7 = soundPool.getRandomSoundFromSoundPool(var1);

                if (var7 != null && var5 > 0.0F)
                {
                    lastSoundID = (lastSoundID + 1) % 256;
                    String snd = "sound_" + lastSoundID;
                    float var9 = 16.0F;

                    if (var5 > 1.0F)
                    {
                        var9 *= var5;
                    }

                    sndSystem.backgroundMusic(snd, var7.soundUrl, var7.soundName, false);
                    sndSystem.setVolume(snd, var5 * ModLoader.getMinecraftInstance().gameSettings.soundVolume);
                    sndSystem.play(snd);
                }
            }
        }

        return lastSoundID;
    }
    //public static Block pressurePlatePlanks2;

    public static boolean togglePress;

    /** For use in preInit ONLY */
    public static Configuration preInitConfig;

    public int hammerIndex = 0;

    public int speakerTex = 0;

    public int sensorTex = 0;

    public int itemTornadoID;

    public int itemWormID;

    public int itemShockWaveID;

    public int itemDrillID;
	
    public int itemTestID;

    public int itemSurfboardID;

    public static int TropicraftRealm_Storm_Tornado_maxActive;

    public static boolean TropicraftRealm_Wind_active;

    public static int TropicraftRealm_waveRenderRange;

    @PreInit
    public void preInit(FMLPreInitializationEvent event)
    {
    	preInitConfig = new Configuration(event.getSuggestedConfigurationFile());

        try
        {
            preInitConfig.load();
            
            sensorID = preInitConfig.getOrCreateBlockIdProperty("sensorID", blockIndexID).getInt(blockIndexID++);
            sirenID = preInitConfig.getOrCreateBlockIdProperty("sirenID", blockIndexID).getInt(blockIndexID++);
            
            itemTornadoID = preInitConfig.getOrCreateIntProperty("itemTornadoID", Configuration.CATEGORY_ITEM, itemIndexID).getInt(itemIndexID++);
            itemWormID = preInitConfig.getOrCreateIntProperty("itemWormID", Configuration.CATEGORY_ITEM, itemIndexID).getInt(itemIndexID++);
            itemShockWaveID = preInitConfig.getOrCreateIntProperty("itemShockWaveID", Configuration.CATEGORY_ITEM, itemIndexID).getInt(itemIndexID++);
            itemDrillID = preInitConfig.getOrCreateIntProperty("itemDrillID", Configuration.CATEGORY_ITEM, itemIndexID).getInt(itemIndexID++);
            itemTestID = preInitConfig.getOrCreateIntProperty("itemTestID", Configuration.CATEGORY_ITEM, itemIndexID).getInt(itemIndexID++);
            itemSurfboardID = preInitConfig.getOrCreateIntProperty("itemSurfboardID", Configuration.CATEGORY_ITEM, itemIndexID).getInt(itemIndexID++);
            
            Storm_Tornado_maxBlocks = preInitConfig.getOrCreateIntProperty("Storm_Tornado_maxBlocks", Configuration.CATEGORY_GENERAL, 1200).getInt();
            Storm_Tornado_maxParticles = preInitConfig.getOrCreateIntProperty("Storm_Tornado_maxParticles", Configuration.CATEGORY_GENERAL, 3000).getInt();
            Storm_Tornado_height = preInitConfig.getOrCreateIntProperty("Storm_Tornado_height", Configuration.CATEGORY_GENERAL, 60).getInt();
            //Storm_Tornado_maxYChange = preInitConfig.getOrCreateIntProperty("Storm_Tornado_maxYChange", Configuration.CATEGORY_GENERAL, 10).getInt(); //no longer a feature
            Storm_Tornado_rarityOfFirenado = preInitConfig.getOrCreateIntProperty("Storm_Tornado_rarityOfFirenado", Configuration.CATEGORY_GENERAL, -1).getInt();
            Storm_Tornado_rarityOfDisintegrate = preInitConfig.getOrCreateIntProperty("Storm_Tornado_rarityOfDisintegrate", Configuration.CATEGORY_GENERAL, 5).getInt();
            Storm_Tornado_rarityOfBreakOnFall = preInitConfig.getOrCreateIntProperty("Storm_Tornado_rarityOfBreakOnFall", Configuration.CATEGORY_GENERAL, 5).getInt();
            
            Storm_Tornado_grabPlayer = preInitConfig.getOrCreateBooleanProperty("Storm_Tornado_grabPlayer", Configuration.CATEGORY_GENERAL, true).getBoolean(true);
            Storm_chanceOfTornado = (preInitConfig.getOrCreateIntProperty("Storm_chanceOfTornado", Configuration.CATEGORY_GENERAL, 3).getInt()) / 10F;
            
            debug = preInitConfig.getOrCreateBooleanProperty("debug", Configuration.CATEGORY_GENERAL, false).getBoolean(false);
            
            Storm_rarityOfIncrease = preInitConfig.getOrCreateIntProperty("Storm_rarityOfIncrease", Configuration.CATEGORY_GENERAL, 4000).getInt();
            
            
            Wind_rarityOfIncrease = preInitConfig.getOrCreateIntProperty("Wind_rarityOfIncrease", Configuration.CATEGORY_GENERAL, 400).getInt();
            
            Wind_Particle_leafs = preInitConfig.getOrCreateBooleanProperty("Wind_Particle_leafs", Configuration.CATEGORY_GENERAL, true).getBoolean(true);
            Wind_Particle_air = preInitConfig.getOrCreateBooleanProperty("Wind_Particle_air", Configuration.CATEGORY_GENERAL, true).getBoolean(true);
            //Wind_Particle_sand = preInitConfig.getOrCreateBooleanProperty("Wind_Particle_sand", Configuration.CATEGORY_GENERAL, true).getBoolean(true);
            Storm_Tornado_oldParticles = preInitConfig.getOrCreateBooleanProperty("Storm_Tornado_oldParticles", Configuration.CATEGORY_GENERAL, false).getBoolean(false);
            Storm_Tornado_makeClouds = preInitConfig.getOrCreateBooleanProperty("Storm_Tornado_makeClouds", Configuration.CATEGORY_GENERAL, true).getBoolean(true);
            Storm_Lightning_active = preInitConfig.getOrCreateBooleanProperty("Storm_Lightning_active", Configuration.CATEGORY_GENERAL, true).getBoolean(true);
            
            
            
            if (c_CoroWeatherUtil.hasTropicraft()) {
            	Storm_Tornado_maxActive = preInitConfig.getOrCreateIntProperty("Storm_Tornado_maxActive", Configuration.CATEGORY_GENERAL, 0).getInt();
            	Wind_active = preInitConfig.getOrCreateBooleanProperty("Wind_active", Configuration.CATEGORY_GENERAL, false).getBoolean(false);
            	waveRenderRange = preInitConfig.getOrCreateIntProperty("MainRealm_waveRenderRange", Configuration.CATEGORY_GENERAL, 0).getInt();
            	
            	TropicraftRealm_Storm_Tornado_maxActive = preInitConfig.getOrCreateIntProperty("TropicraftRealm_Storm_Tornado_maxActive", Configuration.CATEGORY_GENERAL, 1).getInt();
            	TropicraftRealm_Wind_active = preInitConfig.getOrCreateBooleanProperty("TropicraftRealm_Wind_active", Configuration.CATEGORY_GENERAL, true).getBoolean(true);
            	TropicraftRealm_waveRenderRange = preInitConfig.getOrCreateIntProperty("TropicraftRealm_waveRenderRange", Configuration.CATEGORY_GENERAL, 50).getInt();
            	
            	tornadoItems = preInitConfig.getOrCreateBooleanProperty("Tornado_Items", Configuration.CATEGORY_GENERAL, false).getBoolean(false);
            } else {
            	Storm_Tornado_maxActive = preInitConfig.getOrCreateIntProperty("Storm_Tornado_maxActive", Configuration.CATEGORY_GENERAL, 1).getInt();
            	Wind_active = preInitConfig.getOrCreateBooleanProperty("Wind_active", Configuration.CATEGORY_GENERAL, true).getBoolean(true);
            	waveRenderRange = preInitConfig.getOrCreateIntProperty("waveRenderRange", Configuration.CATEGORY_GENERAL, 50).getInt();
            	
            	//shouldnt even be used if this is off
            	TropicraftRealm_Storm_Tornado_maxActive = 0;
            	TropicraftRealm_Wind_active = false;
            	TropicraftRealm_waveRenderRange = 50;
            }
            
            backupWaveRenderRange = waveRenderRange;
            
            //tropiconfigs
            //useitems
            //max active tornadoes 
            
            int wat = 0;
            
            //setBlockIds();
            //setItemIds();
        }
        catch (Exception e)
        {
            FMLLog.log(Level.SEVERE, e, "WeatherMod has a problem loading it's configuration");
        }
        finally
        {
            preInitConfig.save();
        }
    }

    @Init
    public void load(FMLInitializationEvent event)
    {
        //proxy.loadSounds();
        //registerTileEntities();
        weatherMan = new WeatherManager();
        stormMan = new StormManager();
        proxy.init(this);
        init();
        //registerBlocks();
        //registerItems();
        proxy.registerRenderInformation();
    }

    @PostInit
    public void modsLoaded(FMLPostInitializationEvent event)
    {
        this.modsLoaded();
    }

    public WeatherMod()
    {
    }

    //@SideOnly(Side.CLIENT)
    public void init()
    {
        int meh = 0;
        //coral = (new BlockCojoCoral(215, 215)).setHardness(1.0F).setLightValue(0.0F).setStepSound(Block.soundWoodFootstep).setBlockName("coralBlock");
        //coralItem = (new ItemCojoCoral(215 - 256)).setIconIndex(8).setItemName("coralItem");
        //ModLoader.addName(coral,"Coral ");
        player = null;

        //spriteIndex = ModLoader.getUniqueSpriteIndex("/gui/items.png");
        //usedSpriteIndex = ModLoader.getUniqueSpriteIndex("/gui/items.png");
        //domrod = (new DominationRod(22501, 0)).setIconIndex(ModLoader.addOverride("/gui/items.png", "/dominationrod/item.png")).setItemName("dominationrod");
        //tornadoesEnabled = false;
        
        int entIDs = 1000;
        
        itemSurfboard = (new ItemSurfboard(itemSurfboardID)).setIconCoord(8, 8).setItemName("Surfboard").setCreativeTab(CreativeTabs.tabMisc);
        ModLoader.addName(itemSurfboard, "'Surfboard'");
        EntityRegistry.registerModEntity(EntitySurfboard.class, "EntitySurfboard", entIDs++, this, 250, 5, true);
        
        if (tornadoItems)
        {
            itemTornado = (new ItemTornado(itemTornadoID, 0)).setIconIndex(8).setItemName("tornadogun").setCreativeTab(CreativeTabs.tabMisc);
            itemWorm = (new ItemTornado(itemWormID, 1)).setIconIndex(24).setItemName("wormgun").setCreativeTab(CreativeTabs.tabMisc);
            itemShockWave = (new ItemTornado(itemShockWaveID, 2)).setIconIndex(hammerIndex).setItemName("Groundshaker").setCreativeTab(CreativeTabs.tabMisc);
            itemDrill = (new ItemTornado(itemDrillID, 3)).setIconIndex(hammerIndex).setItemName("Drill").setCreativeTab(CreativeTabs.tabMisc);
            itemTest = (new ItemTornado(itemTestID, 4)).setIconIndex(15).setItemName("Test Item").setCreativeTab(CreativeTabs.tabMisc);
            
            blockTSensor = (new BlockTSensor(sensorID, sensorTex)).setBlockName("Tornado Sensor").setCreativeTab(CreativeTabs.tabMisc);
            blockSiren = (new BlockTSiren(sirenID, speakerTex)).setBlockName("Tornado Siren").setCreativeTab(CreativeTabs.tabMisc);
            ModLoader.registerBlock(blockTSensor);
            ModLoader.addName(blockTSensor, "Tornado Sensor");
            ModLoader.registerBlock(blockSiren);
            ModLoader.addName(blockSiren, "Tornado Siren");
            //ModLoader.registerTileEntity(c_w_TileEntityTSiren.class, "TSiren", new c_w_TileEntityTSirenRenderer());
            ModLoader.addRecipe(new ItemStack(blockSiren, 1), new Object[]
                    {
                        "X X", "DID", "X X", 'D', Item.redstone, 'I', Item.ingotGold, 'X', Item.ingotIron, 'D', Item.redstone
                    });
            ModLoader.addRecipe(new ItemStack(blockTSensor, 1), new Object[]
                    {
                        "XDX", "DID", "XDX", 'D', Item.redstone, 'I', Item.ingotGold, 'X', Item.ingotIron, 'D', Item.redstone
                    });
            ModLoader.addName(itemTornado, "Tornado Gun");
            ModLoader.addName(itemWorm, "Worm Gun");
            ModLoader.addName(itemShockWave, "ShockWave Item");
            ModLoader.addName(itemDrill, "Drill Item");
            ModLoader.addName(itemTest, "Test Item");
            
            //ModLoader.registerEntityID(c_w_EntWorm.class, "EntWorm", entIDs++);
            //ModLoader.registerEntityID(c_w_EntTornado.class, "EntTornado", ModLoader.getUniqueEntityId());
            EntityRegistry.registerModEntity(EntWorm.class, "EntWorm", entIDs++, this, 250, 5, true);
            EntityRegistry.registerModEntity(EntTornado.class, "EntTornado", entIDs++, this, 500, 5, true);
            EntityRegistry.registerModEntity(EntShockWave.class, "EntShockWave", entIDs++, this, 250, 5, true);
            EntityRegistry.registerModEntity(EntDrill.class, "EntDrill", entIDs++, this, 250, 5, true);
            EntityRegistry.registerModEntity(MovingBlock.class, "MovingBlock", entIDs++, this, 250, 5, true);
            
            //ModLoader.registerEntityID(c_w_MovingBlock.class, "MovingBlock", ModLoader.getUniqueEntityId());
            //ModLoader.registerEntityID(c_w_EntShockWave.class, "EntShockWave", entIDs++);
            //ModLoader.registerEntityID(c_w_EntDrill.class, "EntDrill", entIDs++);
            ModLoader.addRecipe(new ItemStack(itemTornado, 1), new Object[]
                    {
                        " D ", " I ", " D ", 'D', Item.silk, 'I', Item.stick
                    });
            ModLoader.addRecipe(new ItemStack(itemWorm, 1), new Object[]
                    {
                        "  D", " I ", "D  ", 'D', Item.feather, 'I', Item.feather
                    });
            ModLoader.addRecipe(new ItemStack(itemShockWave, 1), new Object[]
                    {
                        "   ", "III", "   ", 'I', Item.feather
                    });
            ModLoader.addRecipe(new ItemStack(itemDrill, 1), new Object[]
                    {
                        " I ", "III", " I ", 'I', Item.stick
                    });
        }

        //effWindAnimID[0] = ModLoader.addOverride("/gui/items.png", "/item/smoke/sharp1.png");
        //effWind2ID = ModLoader.addOverride("/gui/items.png", "/coro/weather/blurry16.png");
        //pressurePlatePlanks2 = (new BlockPressurePlate2(212, Block.planks.blockIndexInTexture, EnumMobType.everything, Material.wood)).setHardness(0.5F).setStepSound(Block.soundWoodFootstep).setBlockName("pressurePlate2")/*.disableNeighborNotifyOnMetadataChange()*/;
        //ModLoader.registerBlock(pressurePlatePlanks2);
        //ModLoader.addName(pressurePlatePlanks2,"pressurePlatePlanks2");
        ModLoader.registerEntityID(EntWorldData.class, "EntWorldData", ModLoader.getUniqueEntityId());
        /*ModLoader.addRecipe(new ItemStack(pressurePlatePlanks2, 1), new Object[] {
            "DD ", "   ", "   ", 'D', Block.cobblestone
        });*/
        //ModLoader.registerEntityID(c_w_EntityHail.class, "EntityHail", ModLoader.getUniqueEntityId());
        ModLoader.registerEntityID(EntityCloud.class, "EntityCloud", ModLoader.getUniqueEntityId());
        //ModLoader.setInGUIHook(this, true, false);
        //ModLoader.setInGameHook(this, true, false);
        this.entToAge = new HashMap();
        this.updateLCG = (new Random()).nextInt();
        weatherEntTypes = new LinkedList();
        weatherEntTypesTest = new LinkedList();
        stormClusters = new LinkedList();
        WeatherEntityConfig sConf = new WeatherEntityConfig();
        //0 = spout
        //1 = F1
        //2 = F3
        //3 = F5
        //4 = F6
        //5 = Hurricane C1
        //water spout
        sConf.tornadoInitialSpeed = 0.2F;
        sConf.tornadoPullRate = 0.04F;
        sConf.tornadoLiftRate = 0.05F;
        sConf.relTornadoSize = 0;
        sConf.tornadoBaseSize = 3;
        sConf.tornadoWidthScale = 1.0F;
        sConf.grabDist = 40D;
        sConf.tornadoTime = 4500;
        sConf.type = 0;
        sConf.grabsBlocks = false;
        weatherEntTypes.add(sConf);
        //F1 tornado
        sConf = new WeatherEntityConfig();
        sConf.tornadoInitialSpeed = 0.2F;
        sConf.tornadoPullRate = 0.04F;
        sConf.tornadoLiftRate = 0.05F;
        sConf.relTornadoSize = -20;
        sConf.tornadoBaseSize = 3;
        sConf.tornadoWidthScale = 1.5F;
        sConf.grabDist = 100D;
        weatherEntTypes.add(sConf);
        //F3 tornado
        sConf = new WeatherEntityConfig();
        sConf.tornadoInitialSpeed = 0.2F;
        sConf.tornadoPullRate = 0.04F;
        sConf.tornadoLiftRate = 0.05F;
        sConf.relTornadoSize = -50;
        sConf.tornadoBaseSize = 10;
        sConf.tornadoWidthScale = 1.9F;
        weatherEntTypes.add(sConf);
        //F5 tornado
        sConf = new WeatherEntityConfig();
        sConf.tornadoInitialSpeed = 0.15F;
        sConf.tornadoPullRate = 0.04F;
        sConf.tornadoLiftRate = 0.04F;
        sConf.relTornadoSize = 95;
        sConf.tornadoBaseSize = 25;
        sConf.tornadoWidthScale = 2.5F;
        weatherEntTypes.add(sConf);
        //F6
        sConf = new WeatherEntityConfig();
        sConf.tornadoInitialSpeed = 0.15F;
        sConf.tornadoPullRate = 0.15F;
        sConf.tornadoLiftRate = 0.04F;
        sConf.relTornadoSize = 95;
        sConf.tornadoBaseSize = 95;
        sConf.tornadoWidthScale = 3.5F;
        weatherEntTypes.add(sConf);
        //Hurricane
        sConf = new WeatherEntityConfig();
        sConf.tornadoInitialSpeed = 0.15F;
        sConf.tornadoPullRate = 0.15F;
        sConf.tornadoLiftRate = 0.04F;
        sConf.relTornadoSize = 95;
        sConf.tornadoBaseSize = 155;
        sConf.tornadoWidthScale = 3.5F;
        sConf.tornadoTime = 4500;
        sConf.type = 2;
        weatherEntTypes.add(sConf);
        //storm stages max, wind should use different one
        stormMan.maxStage = weatherEntTypes.size();
        for (int i = 0; i < ServerTickHandler.sMans.size(); i++) {
        	StormManager sMan = ServerTickHandler.sMans.get(i);
        	sMan.maxStage = stormMan.maxStage;
    	}
        //ServerTickHandler.sSMan.maxStage = weatherEntTypes.size();
        //test
        sConf = new WeatherEntityConfig();
        sConf.tornadoInitialSpeed = 0.0F;
        sConf.tornadoPullRate = 0.1F;
        sConf.tornadoLiftRate = 0.03F;
        sConf.relTornadoSize = 0;
        sConf.tornadoBaseSize = 3;
        sConf.tornadoWidthScale = 1.0F;
        sConf.grabDist = 1D;
        sConf.tornadoTime = 600;
        sConf.grabsBlocks = false;
        weatherEntTypesTest.add(sConf);
        p_blocks_leaf = new LinkedList();
        p_blocks_sand = new LinkedList();
        p_blocks_leaf.add(Block.leaves);
        p_blocks_leaf.add(Block.vine);
        p_blocks_leaf.add(Block.tallGrass);
        p_blocks_sand.add(Block.sand);
        //p_blocks_leaf.add(Block.torchWood);
        //mc = ModLoader.getMinecraftInstance();
        worldRef = proxy.getClientWorld();
        Random rand = new Random();
        snd_dmg_close[0] = "destruction_0_";
        snd_dmg_close[1] = "destruction_1_";
        snd_dmg_close[2] = "destruction_2_";
        snd_wind_close[0] = "wind_close_0_";
        snd_wind_close[1] = "wind_close_1_";
        snd_wind_close[2] = "wind_close_2_";
        snd_wind_far[0] = "wind_far_0_";
        snd_wind_far[1] = "wind_far_1_";
        snd_wind_far[2] = "wind_far_2_";
        snd_rand[0] = rand.nextInt(3);
        snd_rand[1] = rand.nextInt(3);
        snd_rand[2] = rand.nextInt(3);
        soundID[0] = -1;
        soundID[1] = -1;
        soundID[2] = -1;
        soundToLength.put(snd_dmg_close[0], 2515);
        soundToLength.put(snd_dmg_close[1], 2580);
        soundToLength.put(snd_dmg_close[2], 2741);
        soundToLength.put(snd_wind_close[0], 4698);
        soundToLength.put(snd_wind_close[1], 7324);
        soundToLength.put(snd_wind_close[2], 6426);
        soundToLength.put(snd_wind_far[0], 12892);
        soundToLength.put(snd_wind_far[1], 9653);
        soundToLength.put(snd_wind_far[2], 12003);

        try
        {
            //ModLoader.setPrivateValue(Block.class, Block.blocksList[Block.portal.blockID], "portal", portal2);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        //Block.portal.blockIndexInTexture = 8;
        //this.rotEffRenderer = new RotatingEffectRenderer(mc.theWorld, mc.renderEngine);
    }

    //Terrain grabbing
    public static boolean shouldGrabBlock(int id)
    {
        try
        {
            if (Storm_Tornado_blockStrengthGrabbing)
            {
                float strMin = 0.0F;
                float strMax = 0.74F;
                Block block = Block.blocksList[id];

                if (block == null)
                {
                    return false;
                }

                float strVsBlock = block.getBlockHardness(worldRef, 0, 0, 0) - (((itemStr.getStrVsBlock(block) - 1) / 4F));

                //System.out.println(strVsBlock);
                if (/*block.getHardness() <= 10000.6*/ (strVsBlock <= strMax && strVsBlock >= strMin) || block.blockMaterial == Material.wood || block.blockMaterial == Material.cloth)
                {
                    /*if (block.blockMaterial == Material.water) {
                    	return false;
                    }*/
                    if (safetyCheck(block.blockID))
                    {
                        return true;
                    }
                }

                return false;
            }
            else
            {
                if (!Storm_Tornado_blockBlacklistMode)
                {
                    return ((Boolean)blockIDToUseMapping.get(id)).booleanValue();
                }
                else
                {
                    return !((Boolean)blockIDToUseMapping.get(id)).booleanValue();
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }
    public static boolean safetyCheck(int id)
    {
        if (id != Block.bedrock.blockID && id != Block.wood.blockID && id != Block.chest.blockID && id != Block.jukebox.blockID/* && id != Block.waterMoving.blockID && id != Block.waterStill.blockID */)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public static boolean shouldRemoveBlock(int blockID)
    {
        if (tryFinite)
        {
            try
            {
                if (Class.forName("BlockNWater").isInstance(Block.blocksList[blockID]))
                {
                    return false;
                }

                if (Class.forName("BlockNOcean").isInstance(Block.blocksList[blockID]))
                {
                    return false;
                }

                /*if (Class.forName("BlockNWater_Pressure").isInstance(Block.blocksList[blockID])) {
                    return false;
                }*/

                if (Class.forName("BlockNWater_Still").isInstance(Block.blocksList[blockID]))
                {
                    return true;
                }
            }
            catch (Exception exception)
            {
                tryFinite = false;
                return false;
            }
        }

        //water no
        if (blockID == 8 || blockID == 9)
        {
            return false;
        }

        return true;
    }
    public static boolean isOceanBlock(int blockID)
    {
        if (tryFinite)
        {
            try
            {
                if (Class.forName("BlockNOcean").isInstance(Block.blocksList[blockID]))
                {
                    return true;
                }
            }
            catch (Exception exception)
            {
                tryFinite = false;
                return false;
            }
        }

        return false;
    }
    public static boolean isSolidBlock(int id)
    {
        return (id == Block.stone.blockID ||
                id == Block.cobblestone.blockID ||
                id == Block.sandStone.blockID);
    }

    public static int getEntStormStage()
    {
        //put stormStage -> entStormStage stuff here
        if (stormMan.stage == 0)
        {
            return 1;
        }

        return stormMan.stage - 1;
    }

    //Tornado Functions
    public static void spawnTornado(World world, int x, int y, int z)
    {
    	//World world = proxy.getServerWorld();

    	/*int count = 0;
    	
    	//new max spawn enforcer
    	for (int i = 0; i < world.weatherEffects.size(); i++) {
    		if (world.weatherEffects.get(i) instanceof EntTornado) {
    			count++;
    		}
    	}
    	
    	System.out.println("active tornadoes: " + count);*/
    	
    	//if (count >= Storm_Tornado_maxActive)
    	int maxActive = Storm_Tornado_maxActive;
    	
    	if (world.provider.dimensionId == c_CoroWeatherUtil.tropiDimID) maxActive = TropicraftRealm_Storm_Tornado_maxActive;
    	
        if (activeTornadoes >= maxActive)
        {
            //System.out.println("max spawned");
            return;
        }

        if (debug)
        {
            System.out.println("spawn!");
        }

        //activeTornadoes++;
        t_trySpawnTornado = false;
        EntTornado ent = null;
        WeatherEntityConfig uh = (WeatherEntityConfig)weatherEntTypes.get(getEntStormStage());
        
        ent = new EntTornado(world, uh, getEntStormStage());
        //ent.setPosition(entityplayer.posX, height, entityplayer.posZ);
        double var11 = player.posX - x;
        //double var13 = this.targetedEntity.boundingBox.minY + (double)(this.targetedEntity.height / 2.0F) - (this.posY + (double)(this.height / 2.0F));
        double var15 = player.posZ - z;
        float yaw = -((float)Math.atan2(var11, var15)) * 180.0F / (float)Math.PI;
        //weather override!
        yaw = weatherMan.wind.direction;
        int size = 15;
        yaw += rand.nextInt(size) - (size / 2);
        ent.setLocationAndAngles(x, y, z, yaw, 0F);
        
        //world.spawnEntityInWorld(ent);
        
        if (MinecraftServer.getServer() != null)
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
            DataOutputStream dos = new DataOutputStream(bos);

            try
            {
            	dos.writeInt(0);
            	dos.writeInt(((Entity)ent).entityId);
                dos.writeFloat((float)((Entity)ent).posX);
                dos.writeFloat((float)((Entity)ent).posY);
                dos.writeFloat((float)((Entity)ent).posZ);
                dos.writeInt(1);
                
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            Packet250CustomPayload pkt = new Packet250CustomPayload();
            pkt.channel = "Tornado";
            pkt.data = bos.toByteArray();
            pkt.length = bos.size();
            //pkt.isChunkDataPacket = true;
            MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(pkt);
            
            world.weatherEffects.add(((Entity)ent));
        }
        
        //world.weatherEffects.add(ent);
        double speed = ent.entConf.tornadoInitialSpeed;
        ent.motionX = speed * (double)(-Math.sin(ent.rotationYaw / 180.0F * (float)Math.PI) * Math.cos(ent.rotationPitch / 180.0F * (float)Math.PI));
        ent.motionZ = speed * (double)(Math.cos(ent.rotationYaw / 180.0F * (float)Math.PI) * Math.cos(ent.rotationPitch / 180.0F * (float)Math.PI));
        ent.motionY = speed * (double)(-Math.sin((ent.rotationPitch) / 180.0F * (float)Math.PI));
        ((EntTornado)ent).lastMotionX = ent.motionX;
        ((EntTornado)ent).lastMotionZ = ent.motionZ;
        ((EntTornado)ent).realYaw = ent.rotationYaw;
        //ent.posX += (double)(-Math.sin(ent.rotationYaw / 180.0F * (float)Math.PI) * Math.cos(ent.rotationPitch / 180.0F * (float)Math.PI)) * 20.5D;
        //ent.posZ += (double)(Math.cos(ent.rotationYaw / 180.0F * (float)Math.PI) * Math.cos(ent.rotationPitch / 180.0F * (float)Math.PI)) * 20.5D;
        ent.setPosition(x, y, z);
    }
    public static boolean tryTornadoSpawn(int dim)
    {
        //checkArea((int)player.posX, (int)player.posY, (int)player.posZ);
    	
    	int maxActive = Storm_Tornado_maxActive;
    	
    	if (dim == c_CoroWeatherUtil.tropiDimID) maxActive = TropicraftRealm_Storm_Tornado_maxActive;
    	
        if (activeTornadoes >= maxActive)
        {
            //System.out.println("max spawned");
            return false;
        }

        //odds based off 40% of max storm time * odds
        int maxchance = (int)(1 + ((float)ServerTickHandler.dimToStormMan.get(dim).stormStartTime * 0.4F * Storm_chanceOfTornado));
        int chance = rand.nextInt(maxchance);

        //chance = 0;

        if (!t_trySpawnTornado)
        {
            //System.out.println(maxchance);
            //if (debug) System.out.println("trying to spawn, chance - " + chance);
            if (chance != 0)
            {
                return false;
            }
            else
            {
            	//attempted bug fix for tornado at end of rainstorm, predicted 1 maxchance - did not fix it
            	if (maxchance > 10) {
            		t_trySpawnTornado = true;
            	}
                return true;
            }
        }

        return true;
    }

    public static boolean checkArea(int x, int y, int z)
    {
        if (checkAreaDelay < System.currentTimeMillis())
        {
            checkAreaDelay = System.currentTimeMillis() + 5000L;
        }
        else
        {
            return false;
        }

        int range = 512;
        int tryX;
        int tryZ;
        /*double var11 = player.posX - x;
        double var15 = player.posZ - z;
        float yaw = -((float)Math.atan2(var11, var15)) * 180.0F / (float)Math.PI;
        yaw += rand.nextInt(45)-22;*/
        int randval = worldRef.playerEntities.size() - 1;

        if (randval < 1)
        {
            randval = 1;
        }

        EntityPlayer randPlayer = (EntityPlayer)worldRef.playerEntities.get(rand.nextInt(randval));
        float look = 0F;
        //int height = 10;
        double dist = 512F;
        double gatherX = randPlayer.posX + ((double)(-Math.sin((fixYaw(weatherMan.wind.direction + 180F) + look) / 180.0F * (float)Math.PI) * Math.cos(90F / 180.0F * (float)Math.PI)) * dist);
        //double gatherY = player.posY-0.5 + (double)(-MathHelper.sin(center.rotationPitch / 180.0F * (float)Math.PI) * dist) - 0D; //center.posY - 0D;
        double gatherZ = randPlayer.posZ + ((double)(Math.cos((fixYaw(weatherMan.wind.direction + 180F) + look) / 180.0F * (float)Math.PI) * Math.cos(90F / 180.0F * (float)Math.PI)) * dist);
        tryX = (int)randPlayer.posX + (rand.nextInt(range) - (range / 2));
        tryZ = (int)randPlayer.posZ + (rand.nextInt(range) - (range / 2));
        tryX = (int)gatherX + (rand.nextInt(range) - (range / 2));
        tryZ = (int)gatherZ + (rand.nextInt(range) - (range / 2));
        float plDist = (float)randPlayer.getDistance(tryX, 70, tryZ);
        System.out.println("DISTANCE!! " + plDist);
        int tries = 0;

        while (plDist > 512 || plDist < 128)
        {
            /*tryX = (int)player.posX + (rand.nextInt(range) - (range/2));
            tryZ = (int)player.posZ + (rand.nextInt(range) - (range/2));*/
            tryX = (int)gatherX + (rand.nextInt(range) - (range / 2));
            tryZ = (int)gatherZ + (rand.nextInt(range) - (range / 2));
            plDist = (float)randPlayer.getDistance(tryX, 70, tryZ);

            if (debug)
            {
                System.out.println("tryX: " + tryX + " - tryZ: " + tryZ);
            }

            if (debug)
            {
                System.out.println("distance: " + randPlayer.getDistance(tryX, 70, tryZ));
            }

            if (tries++ > 100)
            {
                System.out.println("FAIL DIST");
                break;
            }
        }

        System.out.println("FINAL DISTANCE!! " + plDist);

        //SPOUT CHECK
        if (weatherEntTypes.get(getEntStormStage()).type == WeatherEntityConfig.TYPE_SPOUT)
        {
            int yGround = worldRef.getHeightValue(tryX, tryZ);
            int id = worldRef.getBlockId((int)tryX, yGround - 1, (int)tryZ);

            if (debug)
            {
                System.out.println("X: " + tryX + " - Z: " + tryZ);
            }

            if (Block.blocksList[id] != null && Block.blocksList[id].blockMaterial != Material.water)
            {
                System.out.println("bad spot for spout");
                return false;
            }
        } else {
        	int fgnfghgf = 0;
        }

        //System.out.println("X: " + tryX + " - Z: " + tryZ);
        int hSize = 64;
        //System.out.println("tornado chunkcache!");
        tWorld = worldRef;//new ChunkCache(ModLoader.getMinecraftInstance().theWorld, tryX-hSize, 0, tryZ-hSize, tryX+hSize, 128, tryZ+hSize);
        //if (checkArea(tryX, 70, tryZ)) {
        t_SpawnTornado_X = tryX;
        t_SpawnTornado_Y = 70;
        t_SpawnTornado_Z = tryZ;

        if (debug)
        {
            System.out.println("while loop start - t_trySpawnTornado = " + t_trySpawnTornado);
        }

        //t_SpawnTornado = true;
        if (!t_trySpawnTornado)
        {
            //t_trySpawnTornado = true;
        }

        //spawnTornado(tryX, 70, tryZ);
        //return true;
        //}
        int scanSize = 128;
        int hScanSize = scanSize / 2;
        int scanResolution = 15;
        int height = 10;
        int tolerance = 30;
        int clearAreas = 0;
        int blockedAreas = 0;

        for (int xx = (int)x - hScanSize; xx <= x + hScanSize; xx += scanResolution)
        {
            int yy = y;

            //for (int yy = (int)player.posY + height; yy <= player.posY + scanSize; yy+= scanResolution) {
            for (int zz = (int)z - hScanSize; zz <= z + hScanSize; zz += scanResolution)
            {
                if (canSpawnHere(xx, (int)yy, zz))
                {
                    clearAreas++;
                }
                else
                {
                    blockedAreas++;
                }
            }

            //}
        }

        //TEMP!!
        //tolerance = 90;

        if (debug)
        {
            System.out.println("clearAreas - " + clearAreas + " - blockedAreas - " + blockedAreas);
        }

        //if (debug) return true;

        if (blockedAreas < tolerance || clearAreas >= 10)
        {
            return true;
        }
        else
        {
            return false;
        }

        //System.out.println("??? - " + this.rotationYaw + " - pl - " + ModLoader.getMinecraftInstance().thePlayer.rotationYaw);
    }
    public static boolean canSpawnHere(int x, int y, int z)
    {
        if (!worldRef.checkChunksExist(x, 0, z , x, 128, z))
        {
            return false;
        }

        if (isSolidBlock(tWorld.getBlockId(x, y, z))/* ||
    			!tWorld.checkChunksExist(x, 0, z, x, 128, z) ||
    			!tWorld.canBlockSeeTheSky(x, y, z)
    			*/)
        {
            return false;
        }

        if (stormMan.stage == 1)
        {
            int hY = worldRef.getHeightValue(x, z);

            if (tWorld.getBlockId(x, hY - 1, z) == 8 || tWorld.getBlockId(x, hY - 1, z) == 9)
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        return true;
    }
    public static void spin(Entity entity, WeatherEntityConfig conf, Entity entity1)
    {
        double maxHeight = Storm_Tornado_height;
        double radius = 10D;
        double scale = conf.tornadoWidthScale;
        double d1 = entity.posX - entity1.posX;
        double d2 = entity.posZ - entity1.posZ;
        float f = (float)((Math.atan2(d2, d1) * 180D) / Math.PI) - 90F;
        float f1;

        for (f1 = f; f1 < -180F; f1 += 360F) { }

        for (; f1 >= 180F; f1 -= 360F) { }

        double distY = entity.posY - entity1.posY;
        double distXZ = Math.sqrt(Math.abs(d1)) + Math.sqrt(Math.abs(d2));

        if (entity1.posY - entity.posY < 0.0D)
        {
            distY = 1.0D;
        }
        else
        {
            distY = entity1.posY - entity.posY;
        }

        if (distY > maxHeight)
        {
            distY = maxHeight;
        }

        double grab = (10D / getWeight(entity1))/* / ((distY / maxHeight) * 1D)*/ * ((Math.abs((maxHeight - distY)) / maxHeight));
        float pullY = 0.0F;

        //some random y pull
        if (rand.nextInt(5) != 0)
        {
            //pullY = 0.035F;
        }

        if (distXZ > 5D)
        {
            grab = grab * (radius / distXZ);
        }

        pullY += (float)(conf.tornadoLiftRate / (getWeight(entity1) / 2F)/* * (Math.abs(radius - distXZ) / radius)*/);
        //} else {
        //pullY = pullY / getWeight(entity1);
        //}

        if (entity1 instanceof EntityPlayer)
        {
            double adjPull = 0.2D / ((getWeight(entity1) * ((distXZ + 1D) / radius)));
            /*if (!entity1.onGround) {
            	adjPull /= (((float)(((double)playerInAirTime+1D) / 200D)) * 15D);
            }*/
            pullY += adjPull;
            //0.2D / ((getWeight(entity1) * ((distXZ+1D) / radius)) * (((distY) / maxHeight)) * 3D);
            //grab = grab + (10D * ((distY / maxHeight) * 1D));
            double adjGrab = (10D * (((float)(((double)playerInAirTime + 1D) / 200D))));

            if (adjGrab > 50)
            {
                adjGrab = 50D;
            }

            grab = grab - adjGrab;

            if (entity1.motionY > -2.0)
            {
                entity1.fallDistance = 0F;
            }

            //System.out.println(adjPull);
        }
        else if (entity1 instanceof EntityLiving)
        {
            double adjPull = 0.005D / ((getWeight(entity1) * ((distXZ + 1D) / radius)));
            /*if (!entity1.onGround) {
            	adjPull /= (((float)(((double)playerInAirTime+1D) / 200D)) * 15D);
            }*/
            pullY -= adjPull;
            //0.2D / ((getWeight(entity1) * ((distXZ+1D) / radius)) * (((distY) / maxHeight)) * 3D);
            //grab = grab + (10D * ((distY / maxHeight) * 1D));
            double adjGrab = (10D * (((float)(((double)(c_CoroWeatherUtil.getEntityAge((EntityLiving)entity1) + 150) + 1D) / 200D))));

            if (adjGrab > 50)
            {
                adjGrab = 50D;
            }

            grab = grab - adjGrab;

            if (entity1.motionY > -2.0)
            {
                entity1.fallDistance = 0F;
            }

            //System.out.println(adjPull);
        }

        if (debug)
        {
            //globals
            //tornadoMaxParticles = 2200;
            //tornadoMaxBlocks = 1000;
            //rarityOfDisintegrate = 150;
            //rarityOfBreakOnFall = 5;
            //mod_EntMover.activeTornado.getStorm().tor
            //temp debugs
            //tornadoInitialSpeed = 0.0F;
            //grabPlayer = false;
            //mod_EntMover.activeTornado.getStorm().tornadoInitialSpeed = 0.0F;
        }

        grab += conf.relTornadoSize;
        f1 = (float)((double)f1 + (75D + grab - (10D * scale)));
        float f3 = (float)Math.cos(-f1 * 0.01745329F - (float)Math.PI);
        float f4 = (float)Math.sin(-f1 * 0.01745329F - (float)Math.PI);
        float f5 = conf.tornadoPullRate;

        if (entity1 instanceof EntityPlayer || entity1 instanceof EntityLiving)
        {
            f5 /= (getWeight(entity1) * ((distXZ + 1D) / radius));
        }

//
        float moveX = f3 * f5;
        float moveZ = f4 * f5;
        //tornado strength changes
        float str = 1F;

        if (entity instanceof EntTornado)
        {
            str = ((EntTornado)entity).strength;
        }

        pullY *= str / 100F;
        setVel(entity1, -moveX, pullY, moveZ);
        //player Yaxis safety check
        /*if  (entity1 instanceof EntityPlayer && entity1.posY > 100D) {
        	entity1.posY = 100D;
        	//entity1.motionY -= 1.0D;
        	entity1.setPosition(entity1.posX, entity1.posY, entity1.posZ);
        }*/
    }
    public static boolean forceRotate(Entity entity, WeatherEntityConfig conf)
    {
        double dist = conf.grabDist;
        List list = entity.worldObj.getEntitiesWithinAABBExcludingEntity(entity, entity.boundingBox.expand(dist, Storm_Tornado_height, dist));
        boolean foundEnt = false;
        int killCount = 0;

        if (list != null)
        {
            for (int i = 0; i < list.size(); i++)
            {
                Entity entity1 = (Entity)list.get(i);

                if (/*(entity1 instanceof EntityLiving || entity1 instanceof EntityItem || entity1 instanceof MovingBlock) && */(!(entity1 instanceof EntityPlayer) || Storm_Tornado_grabPlayer) && entity1 != entity && !(entity1 instanceof EntTornado))
                {
                    if (getDistanceXZ(entity, entity1.posX, entity1.posY, entity1.posZ) < dist)
                    {
                        if ((entity1 instanceof MovingBlock && !((MovingBlock)entity1).collideFalling) || canEntityBeSeen(entity, entity1))
                        {
                            if (!(entity1 instanceof EntityPlayer))
                            {
                                if (entity1 instanceof EntityLiving)
                                {
                                    //((EntityLiving)entity1).entityAge = 0;
                                }
                            }

                            spin(entity, conf, entity1);
                            foundEnt = true;
                        }
                    }
                }

                if (entity1 instanceof MovingBlock && !entity1.isDead)
                {
                    int var3 = MathHelper.floor_double(entity1.posX);
                    int var4 = MathHelper.floor_double(entity1.posZ);
                    byte var5 = 32;
                    /*if(!entity1.worldObj.checkChunksExist(var3 - var5, 0, var4 - var5, var3 + var5, 128, var4 + var5) || !entity1.addedToChunk) {
                        entity1.setEntityDead();
                        mod_EntMover.blockCount--;
                    }*/
                }

                if (entity instanceof EntTornado)
                {
                    if (entity1 instanceof MovingBlock)
                    {
                        if (blockCount + 5 > Storm_Tornado_maxBlocks)
                        {
                            if (entity1.posY > 128)
                            {
                                entity1.setDead();
                                //System.out.println(blockCount);
                            }
                        }

                        if (entity1.motionX < 0.3F && entity1.motionY < 0.3F && entity1.motionZ < 0.3F && getFPS() < 20 && killCount < 20)
                        {
                            killCount++;
                            entity1.setDead();
                        }
                    }
                }

                if (entity1 instanceof EntityItem && player != null)
                {
                    if (entity1.getDistanceToEntity(player) > 32F)
                    {
                        //if ((((EntityItem) entity).item.itemID) == Block.sand.blockID) {
                        entity1.setDead();
                        //}
                    }
                }
            }
        }

        return foundEnt;
    }

    public synchronized static void trimBlocksFromWorld()
    {
        //System.out.println("try trim");
        //System.out.println(worldRef.loadedEntityList.size());
        int blocks = 0;
        int entities = 0;
        boolean hitMax = false;
        int tornadoCount = 0;
        float tDist = 0F;
        EntTornado furthestTornado = null;
        //activeTornado = null;
        float closeDist = 9999F;
        Map itemStrToCount = new HashMap();

        //if (worldRef == null)
        //{
            World worldRef = proxy.getServerWorld();
        //}

        if (worldRef == null)
        {
            return;
        }

        for (int var33 = 0; var33 < worldRef.loadedEntityList.size(); ++var33)
        {
            Entity var4 = (Entity)worldRef.loadedEntityList.get(var33);

            if (var4 instanceof EntWorldData)
            {
                if (worldSaver == null)
                {
                    worldSaver = (EntWorldData)var4;
                }
                else if (worldSaver != var4)
                {
                    worldSaver.setDead();
                }
            }

            if (debug && false)
            {
                //if (var4 instanceof EntityItem) {
                entities++;
                String str;// = ((EntityItem)var4).item.getItemName();
                str = (var4).getClass().toString();

                if (itemStrToCount.containsKey(str))
                {
                    itemStrToCount.put(str, (((Integer)itemStrToCount.get(str)).intValue() + 1));
                }
                else
                {
                    itemStrToCount.put(str, 1);
                }

                //System.out.println(((EntityItem)var4).item.getItemName());
                //}

                if (var4 instanceof EntityPainting || var4 instanceof EntityFallingSand || var4 instanceof EntityMinecart)
                {
                    var4.setDead();
                }
            }

            /*if (var4 instanceof EntityItem) {
            	if ((((EntityItem) var4).item.itemID) == Block.sand.blockID ||
            			(((EntityItem) var4).item.itemID) == Block.sapling.blockID ||
            			(((EntityItem) var4).item.itemID) == Block.sand.blockID) {
            		if (var4.getDistanceToEntity(player) > 32) {
            			var4.setEntityDead();
            		}
            	}
            }*/

            if (var4 instanceof MovingBlock)
            {
                blocks++;

                if (blocks > Storm_Tornado_maxBlocks)
                {
                    hitMax = true;
                    var4.setDead();
                    continue;
                }

                int var3 = MathHelper.floor_double(var4.posX);
                int var44 = MathHelper.floor_double(var4.posZ);
                byte var5 = 32;

                if (!worldRef.checkChunksExist(var3 - var5, 0, var44 - var5, var3 + var5, 128, var44 + var5) || !var4.addedToChunk)
                {
                    //System.out.println("nuuuuuu");
                    var4.setDead();
                }
            }
        }

        /*for(int var33 = 0; var33 < worldRef.weatherEffects.size(); ++var33) {
            Entity var4 = (Entity)worldRef.weatherEffects.get(var33);*/
        for (int var33 = 0; var33 < worldRef.loadedEntityList.size(); ++var33)
        {
            Entity var4 = (Entity)worldRef.loadedEntityList.get(var33);

            if (var4 instanceof EntTornado)
            {
                tornadoCount++;
                float tempDist = var4.getDistanceToEntity(player);

                if (tempDist > tDist)
                {
                    tDist = tempDist;
                    furthestTornado = (EntTornado)var4;
                }

                if (tempDist < closeDist)
                {
                    closeDist = tempDist;
                    activeTornado = (EntTornado)var4;
                }

                //doesnt account for pausing :/ - just the modded chat bar i think
                //if (!mc.isGamePaused && System.currentTimeMillis() - ((c_w_EntTornado) var4).lastTickRunTime > 1000 && System.currentTimeMillis() - playerLastTick < 100) {
                //System.out.println("mod_ despawn tornado no ticks");
                //var4.setEntityDead();
                //}
            }
        }

        //System.out.println("entities: "+entities);
        //System.out.println("----------------");
        Set entries = itemStrToCount.entrySet();
        Iterator it = entries.iterator();

        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();

            if (debug)
            {
                System.out.println(entry.getKey() + " --> " + entry.getValue());
            }
        }

        /*for(int var33 = 0; var33 < worldRef.unloadedEntityList.size(); ++var33) {
               Entity var4 = (Entity)worldRef.unloadedEntityList.get(var33);

              if(var4 instanceof EntTornado) {
              	tornadoCount++;

              	float tempDist = var4.getDistanceToEntity(player);
              	if (tempDist > tDist) {
              		tDist = tempDist;
              		furthestTornado = (EntTornado)var4;
              	}
              }

               if(var4 instanceof MovingBlock) {
              	  blocks++;
              	  if (blocks > tornadoMaxBlocks) {
              		 hitMax = true;
              		 var4.setEntityDead();
              		 continue;
              	  }
              	  int var3 = MathHelper.floor_double(var4.posX);
           	      int var44 = MathHelper.floor_double(var4.posZ);
           	      byte var5 = 32;
           	      if(!worldRef.checkChunksExist(var3 - var5, 0, var44 - var5, var3 + var5, 128, var44 + var5)) {
           	    	  var4.setEntityDead();
           	      }
               }
            }*/

        if (worldSaver == null && worldRef != null)
        {
            worldSaver = new EntWorldData(worldRef);
            worldSaver.setPosition(player.posX, 128, player.posZ);
            worldRef.spawnEntityInWorld(worldSaver);
        }

        //activeTornadoes = tornadoCount;
        //System.out.println("tornadoCount - " + tornadoCount);

        if (tornadoCount > Storm_Tornado_maxActive && furthestTornado != null)
        {
            //System.out.println("despawn!");
            //furthestTornado.startDissipate();
        }

        /*System.out.println("blocks - " + blocks);
        System.out.println("tDist - " + tDist);
        System.out.println("LEL - " + worldRef.loadedEntityList.size());*/
        //if (hitMax) { blockCount = tornadoMaxBlocks; }

        if (debug)
        {
            //Minecraft.hasPaidCheckTime = 0;
            float strMin = 0.0F;
            float strMax = 0.74F;
            ItemStack itemStr = new ItemStack(Item.axeDiamond);

            //System.out.println("BLOCKS TO GRAB");
            for (int i = 0; i < Block.blocksList.length; i++)
            {
                Block block = Block.blocksList[i];

                if (block == null)
                {
                    continue;
                }

                float strVsBlock = block.getBlockHardness(worldRef, 0, 0, 0) - (((itemStr.getStrVsBlock(block) - 1) / 4F));

                if (/*block.getHardness() <= 10000.6*/ (strVsBlock <= strMax && strVsBlock >= strMin) || block.blockMaterial == Material.wood || block.blockMaterial == Material.cloth)
                {
                    //System.out.println("str: " + itemStr.getStrVsBlock(block) + " - hrd: " + block.getHardness() + " - alg: " + strVsBlock + " - " + block.getBlockName());
                    //0.7 or 0.8 be the max weakness to pull ?
                }
            }

            //System.out.println("SOLID BLOCKS");
            for (int i = 0; i < Block.blocksList.length; i++)
            {
                Block block = Block.blocksList[i];

                if (block == null)
                {
                    continue;
                }

                float strVsBlock = block.getBlockHardness(worldRef, 0, 0, 0) - (((itemStr.getStrVsBlock(block) - 1) / 4F));

                if (/*block.getHardness() <= 10000.6*/ (strVsBlock <= strMax && strVsBlock >= strMin) || block.blockMaterial == Material.wood || block.blockMaterial == Material.cloth)
                {
                    //0.7 or 0.8 be the max weakness to pull ?
                }
                else
                {
                    //System.out.println("str: " + itemStr.getStrVsBlock(block) + " - hrd: " + block.getHardness() + " - alg: " + strVsBlock + " - " + block.getBlockName());
                }
            }
        }

        blockCount = blocks;
    }

    public static boolean canEntityBeSeen(Entity var1, Entity ent)
    {
        return ent.worldObj.rayTraceBlocks(Vec3.createVectorHelper(ent.posX, ent.posY + (double)ent.getEyeHeight(), ent.posZ), Vec3.createVectorHelper(var1.posX, var1.posY + (double)var1.getEyeHeight(), var1.posZ)) == null;
    }
    public static double getDistanceXZ(Entity ent, double var1, double var3, double var5)
    {
        double var7 = ent.posX - var1;
        //double var9 = ent.posY - var3;
        double var11 = ent.posZ - var5;
        return (double)MathHelper.sqrt_double(var7 * var7/* + var9 * var9*/ + var11 * var11);
    }
    public static float getWeight(Entity entity1)
    {
    	
    	if (entity1 instanceof WindHandler) {
    		return ((WindHandler) entity1).getWindWeight();
    	}
    	
        if (entity1 instanceof MovingBlock)
        {
            return 1.0F + ((float)((MovingBlock) entity1).age / 200);
        }

        if (entity1 instanceof EntityPlayer)
        {
            if (entity1.onGround || entity1.handleWaterMovement())
            {
                playerInAirTime = 0;
            }
            else
            {
                //System.out.println(playerInAirTime);
                playerInAirTime++;
            }

            if ((((EntityPlayer)entity1).inventory.armorInventory[2] != null) && ((EntityPlayer)entity1).inventory.armorInventory[2].itemID == Item.plateSteel.shiftedIndex)
            {
                return 50F;
            }

            if ((((EntityPlayer)entity1).inventory.armorInventory[2] != null) && ((EntityPlayer)entity1).inventory.armorInventory[2].itemID == Item.plateDiamond.shiftedIndex)
            {
                return 75F;
            }

            return 5.0F + ((float)(playerInAirTime / 200));
        }

        if (entity1.worldObj.isRemote)
        {
            float var = getParticleWeight(entity1);

            if (var != -1)
            {
                return var;
            }
        }

        if (entity1 instanceof EntitySquid)
        {
            return 400F;
        }

        /*if (entity1 instanceof EntityPlayerProxy) {
        	return 50F;
        }*/

        if (entity1 instanceof EntityLiving)
        {
            if (entity1.onGround || entity1.handleWaterMovement())
            {
                //entity1.onGround = false;
                c_CoroWeatherUtil.setEntityAge((EntityLiving)entity1, -150);
            }

            if (!entToAge.containsKey(entity1))
            {
                try
                {
                    /*float stepdist = 0F;
                    try {
                    	stepdist = (float)Float.valueOf(ModLoader.getPrivateValue(Entity.class, entity1, "b").toString()).floatValue();
                    } catch (Exception ex) {
                    	stepdist = (float)Float.valueOf(ModLoader.getPrivateValue(Entity.class, entity1, "nextStepDistance").toString()).floatValue();
                    }*/
                    //entToAge.put(entity1, ((EntityLiving)entity1).entityAge);
                    //entity1.onGround = false;
                    //return 1.0F + ((entity1.distanceWalkedModified - stepdist) / 150);
                }
                catch (Exception ex)
                {
                    //if (this.distanceWalkedModified > (float)this.nextStepDistance)
                }
            }

            //System.out.println(((EntityLiving)entity1).entityAge+150);
            //int age = ((Integer)entToAge.get(entity1)).intValue();
            //System.out.println(age);
            return 5.0F + (entity1.onGround ? 2.0F : 0.0F) + ((c_CoroWeatherUtil.getEntityAge((EntityLiving)entity1) + 150) / 50);
        }

        if (entity1 instanceof EntitySurfboard || entity1 instanceof EntityBoat || entity1 instanceof EntityItem)
        {
            return 4000F;
        }

        if (entity1 instanceof EntityMinecart)
        {
            return 80F;
        }

        return 1F;
    }

    @SideOnly(Side.CLIENT)
    public static float getParticleWeight(Entity entity1)
    {
        if (entity1 instanceof EntityFallingRainFX)
        {
            return 1.1F;
        }

        if (entity1 instanceof EntityTexFX)
        {
            return 5.0F + ((float)c_CoroWeatherUtil.getParticleAge((EntityFX)entity1) / 200);
        }

        if (entity1 instanceof EntityWindFX)
        {
            return 1.4F + ((float)c_CoroWeatherUtil.getParticleAge((EntityFX)entity1) / 200);
        }

        if (entity1 instanceof EntityFX)
        {
            return 5.0F + ((float)c_CoroWeatherUtil.getParticleAge((EntityFX)entity1) / 200);
        }

        return -1;
    }

    //Weather Functions

    public static float fixYaw(float yaw)
    {
        while (yaw >= 180.0F)
        {
            yaw -= 360.0F;
        }

        while (yaw <= -180.0F)
        {
            yaw += 360.0F;
        }

        return yaw;
    }

    public static void weather(Side side, World world)
    {
        //Storm_Tornado_oldParticles = false;
        //Storm_Tornado_makeClouds = true;
        //Wind_Particle_leafs = false;
        //World world = worldRef;

        if (worldRef == null/* || worldRef.isRemote*/)
        {
            return;
        }

        //testHook();
        //superfun random debug area! \\
        //player.playerLevel = 7;
        //maxActiveTornadoes = 2;
        //if (mc.entityRenderer instanceof c_w_EntityRendererProxyWeatherMini) {
        //((EntityRendererProxyWeatherMini)mc.entityRenderer).thirdPersonDistance = 5F;
        //}
        //Storm_Tornado_rarityOfDisintegrate = 9999;
        //mc.entityRenderer
        //                             //
        //debug = true;

        

        //
        //world.getWorldInfo().setThunderTime(stormTime);
        //world.getWorldInfo().setRainTime(stormTime);

        //temp
        if (debug && Keyboard.isKeyDown(82))
        {
            //setStage(0);
            //world.getWorldInfo().setThundering(true);
            /*float oldchance = storm_ChanceOfTornado;
            storm_ChanceOfTornado = 0.00001F;
            tryTornadoSpawn();
            storm_ChanceOfTornado = oldchance;*/
        }
        
        if (side == Side.SERVER) {
        	World worldt = proxy.getServerWorld();

        	int count = 0;
        	
        	//new max spawn enforcer
        	for (int i = 0; i < worldt.weatherEffects.size(); i++) {
        		if (worldt.weatherEffects.get(i) instanceof EntTornado) {
        			count++;
        		}
        	}
        	
        	activeTornadoes = count;
        	
        	//activeTornadoes >= Storm_Tornado_maxActive
        }

        if (side == Side.CLIENT)
        {
            //Render hook fields that need updating
            if (mc.entityRenderer instanceof EntityRendererProxyWeatherMini)
            {
                //System.out.println((int)mod_EntMover.stage * 10);
                int rr = (int)stormMan.stage * 3;

                if (rr > 30)
                {
                    rr = 30;
                }

                 
                int finalrate = 3 + ((stormMan.stage + 0) * 2);//(int)(rr * worldRef.getRainStrength(1F));
                if (finalrate > 10) {
                	finalrate = 10;
                }
                ((EntityRendererProxyWeatherMini)mc.entityRenderer).rainRate = finalrate;
                float str = weatherMan.wind.strengthSmooth >= 0.05F ? weatherMan.wind.strengthSmooth : 0.05F;
            }

            if (debug && Keyboard.isKeyDown(Keyboard.KEY_BACKSLASH))
            {
                if (!togglePress)
                {
                    togglePress = true;
                    //setStage(stage+1);
                }
            }
            else if (debug && Keyboard.isKeyDown(weatherInfoKey))
            {
                if (!togglePress)
                {
                    togglePress = true;
                    //showWeatherInfo = !showWeatherInfo;
                }
            }
            else
            {
                togglePress = false;
            }
        }

        //World tick
        if (lastWorldTime != world.getWorldInfo().getWorldTime())
        {
            //Server controlled
            if (!worldRef.isRemote || side == side.SERVER)
            {
                if (t_trySpawnTornado) //changed
                {
                    //disabled so that it keeps trying till one spawns once the random decides it should spawn one
                    //t_trySpawnTornado = false;
                    if (checkArea(t_SpawnTornado_X, t_SpawnTornado_Y, t_SpawnTornado_Z))
                    {
                        if (debug)
                        {
                            System.out.println("t_SpawnTornado = " + t_SpawnTornado);
                        }

                        t_SpawnTornado = true;
                        //t_trySpawnTornado = false; //new
                    }
                }

                if (t_SpawnTornado)
                {
                    if (debug)
                    {
                        System.out.println("checking ... t_SpawnTornado = " + t_SpawnTornado);
                    }

                    t_SpawnTornado = false;
                    spawnTornado(world, t_SpawnTornado_X, t_SpawnTornado_Y, t_SpawnTornado_Z);
                }

                //tornadoItems = true;
                
                if (tornadoItems && stormMan.stage == 1)
                {
                    tryTornadoSpawn(world.provider.dimensionId);
                }

                if ((Wind_active && world.provider.dimensionId == c_CoroWeatherUtil.mainDimID) || (TropicraftRealm_Wind_active && world.provider.dimensionId == c_CoroWeatherUtil.tropiDimID))
                {
                    tryWind(Side.SERVER, world);
                }

                lastWorldTime = world.getWorldInfo().getWorldTime();
                //Client controlled
            }
            else
            {
                tryClouds();
                tryParticleSpawning();

                if ((Wind_active && world.provider.dimensionId == c_CoroWeatherUtil.mainDimID) || (TropicraftRealm_Wind_active && world.provider.dimensionId == c_CoroWeatherUtil.tropiDimID))
                {
                    tryWind(Side.CLIENT, world);
                }

                if (Storm_Lightning_active)
                {
                    tryLightning();
                }
            }
        }
    }

    public static void tryLightning()
    {
        if (worldRef == null || stormMan.stage < 2)
        {
            return;    //worldRef = ModLoader.getMinecraftInstance().theWorld;
        }

        Entity target = activeTornado;
        byte radius = 3;
        byte baseRadius = 9;

        if (target == null)
        {
            target = player;
            radius = 9;
        }

        if (target == null)
        {
            return;
        }

        //System.out.println("dadasdasd!");
        int var3;
        int var4;
        int var6;
        int var7;
        int var8;
        int var9;
        var3 = MathHelper.floor_double(target.posX / 16.0D);
        var4 = MathHelper.floor_double(target.posZ / 16.0D);
        byte var5 = radius;

        for (int var66 = -var5; var66 <= var5; ++var66)
        {
            for (int var77 = -var5; var77 <= var5; ++var77)
            {
                //worldRef.positionsToUpdate.add(new ChunkCoordIntPair(var6 + var3, var7 + var4));
                int var33 = (var66 + var3) * 16;
                int var44 = (var77 + var4) * 16;

                if (worldRef.rand.nextInt((int)(((float)Storm_rarityOfLightning / stormMan.stormIntensity * ((float)radius / (float)baseRadius)) / (stormMan.stage > 2 ? (stormMan.stage - 2) : 1))) == 0)
                {
                    //System.out.println("blam!");
                    updateLCG = updateLCG * 3 + 1013904223;
                    var6 = updateLCG >> 2;
                    var7 = var33 + (var6 & 15);
                    var8 = var44 + (var6 >> 8 & 15);
                    var9 = 20 + worldRef.rand.nextInt(60);//worldRef.findTopSolidBlock(var7, var8);
                    //if(worldRef.canBlockBeRainedOn(var7, var9, var8)) {
                    worldRef.addWeatherEffect(new EntityLightningBolt(worldRef, (double)var7, (double)var9, (double)var8));
                    //}
                }
            }
        }
    }
    /*public static void tryHail() {
    	if (worldRef == null) {
            return;    //worldRef = ModLoader.getMinecraftInstance().theWorld;
        }

        Entity target = null;//mod_EntMover.activeTornado;
        byte radius = 3;
        byte baseRadius = 9;

        if (target == null) {
            target = player;
            radius = 3;
        }

        if (target == null) {
            return;
        }

        //System.out.println("dadasdasd!");
        int var3;
        int var4;
        int var6;
        int var7;
        int var8;
        int var9;
        var3 = MathHelper.floor_double(target.posX / 16.0D);
        var4 = MathHelper.floor_double(target.posZ / 16.0D);
        byte var5 = radius;

        for(int var66 = -var5; var66 <= var5; ++var66) {
            for(int var77 = -var5; var77 <= var5; ++var77) {
                //worldRef.positionsToUpdate.add(new ChunkCoordIntPair(var6 + var3, var7 + var4));
                int var33 = (var66 + var3) * 16;
                int var44 = (var77 + var4) * 16;

                if(worldRef.rand.nextInt(1+(int)(((float)10 * ((float)radius / (float)baseRadius)))) == 0) {
                    //System.out.println("blam!");
                    updateLCG = updateLCG * 3 + 1013904223;
                    var6 = updateLCG >> 2;
                    var7 = var33 + (var6 & 15);
                    var8 = var44 + (var6 >> 8 & 15);
                    var9 = 120;//worldRef.findTopSolidBlock(var7, var8);
                    //if(worldRef.canBlockBeRainedOn(var7, var9, var8)) {
                    c_w_EntityHail ent = new c_w_EntityHail(worldRef);
                    ent.setPosition(var7, var9, var8);
                    worldRef.addWeatherEffect(ent);

                    ent.motionX = ent.rand.nextFloat()*2-1;
                    ent.motionY = ent.rand.nextFloat()*2-1;
                    ent.motionZ = ent.rand.nextFloat()*2-1;
                    //ent.setPosition((double)var7, (double)var9, (double)var8);
                    //
                    //}

                    Entity ent2 = new EntityArrow(worldRef);
                    ent2.setPosition(var7, var9, var8);
                    worldRef.entityJoinedWorld(ent2);
                }
            }
        }
    }*/

    public static synchronized void tryParticleSpawning()
    {
        for (int i = 0; i < spawnQueue.size(); i++)
        {
            Entity ent = spawnQueue.get(i);

            if (ent instanceof EntityRotFX)
            {
                ((EntityRotFX) ent).spawnAsWeatherEffect();
            }
            else
            {
                ent.worldObj.addWeatherEffect(ent);
            }
        }

        spawnQueue.clear();
    }

    public static List<Entity> spawnQueue = new ArrayList();
    public static long threadLastWorldTickTime;
    //Threaded function
    @SideOnly(Side.CLIENT)
    public static void tryParticles()
    {
        //tryClouds();
        //if (true) return;
    	
    	worldRef = mc.theWorld;
    	
        if (worldRef == null)
        {
            return;
        }

        if (threadLastWorldTickTime == worldRef.getWorldTime())
        {
            return;
        }

        threadLastWorldTickTime = worldRef.getWorldTime();
        //TROPICRAFT FLOWER SPAWN POLLEN!
        //mining a tree causes leaves to fall
        int size = 50;
        int hsize = size / 2;
        int curX = (int)player.posX;
        int curY = (int)player.posY;
        int curZ = (int)player.posZ;
        //if (true) return;
        float windStr = (weatherMan.wind.strength <= 1F ? weatherMan.wind.strength : 1F);

        /*if (mc.objectMouseOver != null) {
        	int id = mc.theWorld.getBlockId(mc.objectMouseOver.blockX,mc.objectMouseOver.blockY,mc.objectMouseOver.blockZ);
        	//System.out.println(mc.theWorld.getBlockId(mc.objectMouseOver.blockX,mc.objectMouseOver.blockY,mc.objectMouseOver.blockZ));
        	if (id > 0 && Block.blocksList[id].blockMaterial == Material.wood) {
        		float var5 = 0;

        		var5 = (Float)c_CoroWeatherUtil.getPrivateValueBoth(PlayerControllerMP.class, (PlayerControllerMP)ModLoader.getMinecraftInstance().playerController, "f", "curBlockDamageMP");

        		try {
                    Object var9 = ModLoader.getPrivateValue(PlayerControllerMP.class, (PlayerControllerMP)ModLoader.getMinecraftInstance().playerController, "f");
                    var5 = Float.valueOf(var9.toString()).floatValue();
        		} catch (Exception ex) {
        			try {
            			Object var9 = ModLoader.getPrivateValue(PlayerControllerMP.class, (PlayerControllerMP)ModLoader.getMinecraftInstance().playerController, "curBlockDamageMP");
                        var5 = Float.valueOf(var9.toString()).floatValue();
        			} catch (Exception ex2) { }
        		}

                if (var5 > 0) {
                	shakeTrees(8);
                }
        	}
        }*/

        if ((!Wind_Particle_leafs && !Wind_Particle_air && !Wind_Particle_sand) || weatherMan.wind.strength < 0.10)
        {
            return;
        }

        //Wind requiring code goes below
        int uh = (int)(70 / (windStr + 0.001));

        //performance fix
        if (uh < 120)
        {
            uh = 120;
        }

        //if (debug) System.out.println("windStr: " + windStr + " chance: " + uh);
        //Semi intensive area scanning code
        for (int xx = curX - hsize; xx < curX + hsize; xx++)
        {
            for (int yy = curY - hsize; yy < curY + hsize; yy++)
            {
                for (int zz = curZ - hsize; zz < curZ + hsize; zz++)
                {
                    //REMOVE VINES!!!!!
                    if (uh < 1)
                    {
                        uh = 1;
                    }

                    if (worldRef.rand.nextInt(uh) == 0)
                    {
                        //for (int i = 0; i < p_blocks_leaf.size(); i++)
                        //{
                            int id = getBlockId(xx, yy, zz);

                            Block block = Block.blocksList[id];
                            
                            //if (block != null && block.blockMaterial == Material.leaves)
                            
                            if (/*id == ((Block)p_blocks_leaf.get(i)).blockID*/block != null && (block.blockMaterial == Material.leaves || block.blockMaterial == Material.vine))
                            {
                                if (Wind_Particle_leafs && getBlockId(xx, yy - 1, zz) == 0)
                                {
                                    EntityRotFX var31 = new EntityTexFX(worldRef, (double)xx, (double)yy - 0.5, (double)zz, 0D, 0D, 0D, 10D, 0, effLeafID);
                                    c_CoroWeatherUtil.setParticleGravity((EntityFX)var31, 0.1F);

                                    for (int ii = 0; ii < 10; ii++)
                                    {
                                        applyWindForce(var31);
                                    }

                                    var31.rotationYaw = rand.nextInt(360);
                                    //var31.spawnAsWeatherEffect();
                                    spawnQueue.add(var31);
                                    
                                }
                                else
                                {
                                    if (Wind_Particle_leafs)
                                    {
                                        //This is non leaves, as in wildgrass or wahtever is in the p_blocks_leaf list (no special rules)
                                        EntityRotFX var31 = new EntityTexFX(worldRef, (double)xx, (double)yy + 0.5, (double)zz, 0D, 0D, 0D, 10D, 0, effLeafID);
                                        c_CoroWeatherUtil.setParticleGravity((EntityFX)var31, 0.1F);
                                        var31.rotationYaw = rand.nextInt(360);
                                        //var31.spawnAsWeatherEffect();
                                        spawnQueue.add(var31);
                                        //mc.effectRenderer.addEffect(var31);
                                    }
                                }
                            }
                            else if (id == 0)
                            {
                                if (Wind_Particle_air && weatherMan.wind.strength > 0.02)
                                {
                                    if (worldRef.rand.nextInt(400 - (int)(weatherMan.wind.strength * 100)) == 0)
                                    {
                                        //EntityFX var31 = new EntitySmokeFX(worldRef, (double)xx, (double)yy+0.5, (double)zz, 0D, 0D, 0D);
                                        EntityRotFX var31 = new EntityTexFX(worldRef, (double)xx, (double)yy + 0.5, (double)zz, 0D, 0D, 0D, 10D, 0, effWind2ID);
                                        //var31.particleGravity = 0.3F;
                                        //mod_ExtendedRenderer.rotEffRenderer.addEffect(var31);

                                        for (int ii = 0; ii < 20; ii++)
                                        {
                                            applyWindForce(var31);
                                        }

                                        c_CoroWeatherUtil.setParticleGravity((EntityFX)var31, 0.0F);
                                        var31.noClip = true;
                                        c_CoroWeatherUtil.setParticleScale((EntityFX)var31, 0.3F);
                                        var31.rotationYaw = rand.nextInt(360);
                                        //var31.spawnAsWeatherEffect();
                                        spawnQueue.add(var31);
                                    }
                                }
                            }
                        //}

                        /*if (Wind_Particle_sand) {
                        	int id = getBlockId(xx, yy, zz);
                        	if (id == ((Block)p_blocks_sand.get(0)).blockID) {
                        		if (id == Block.sand.blockID) {
                        			if (getBlockId(xx, yy+1, zz) == 0) {
                        				c_w_EntityTexFX var31 = new c_w_EntityTexFX(worldRef, (double)xx, (double)yy+0.5, (double)zz, 0D, 0D, 0D, 10D, 0, effSandID);
                        				//var31 = new EntityWindFX(worldRef, (double)xx, (double)yy+1.2, (double)zz, 0D, 0.0D, 0D, 9.5D, 1);
                        				var31.rotationYaw = rand.nextInt(360)-180F;
                        				var31.type = 1;
                        				c_CoroWeatherUtil.setParticleGravity((EntityFX)var31, 0.6F);
                        				c_CoroWeatherUtil.setParticleScale((EntityFX)var31, 0.3F);
                                        //var31.spawnAsWeatherEffect();
                        				spawnQueue.add(var31);
                        			}
                        		}
                        	}
                        }*/
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void shakeTrees(int range)
    {
        int size = range;
        int hsize = size / 2;
        int curX = (int)player.posX;
        int curY = (int)player.posY - 1;
        int curZ = (int)player.posZ;
        //if (true) return;
        float windStr = 1F;

        for (int xx = curX - hsize; xx < curX + hsize; xx++)
        {
            for (int yy = curY - hsize; yy < curY + hsize + 10; yy++)
            {
                for (int zz = curZ - hsize; zz < curZ + hsize; zz++)
                {
                    //REMOVE VINES!!!!!
                    int uh = (int)(40 / (windStr + 0.001));

                    //System.out.println(uh);
                    if (uh < 1)
                    {
                        uh = 1;
                    }

                    if (worldRef.rand.nextInt(uh) == 0)
                    {
                        for (int i = 0; i < p_blocks_leaf.size(); i++)
                        {
                            int id = getBlockId(xx, yy, zz);

                            if (id == ((Block)p_blocks_leaf.get(i)).blockID)
                            {
                                if (id == Block.leaves.blockID)
                                {
                                    if (getBlockId(xx, yy - 1, zz) == 0)
                                    {
                                        EntityRotFX var31 = new EntityTexFX(worldRef, (double)xx, (double)yy - 0.5, (double)zz, 0D, 0D, 0D, 10D, 0, effLeafID);
                                        c_CoroWeatherUtil.setParticleGravity((EntityFX)var31, 0.3F);

                                        for (int ii = 0; ii < 10; ii++)
                                        {
                                            applyWindForce(var31);
                                        }

                                        var31.rotationYaw = rand.nextInt(360);
                                        //var31.spawnAsWeatherEffect();
                                        spawnQueue.add(var31);
                                    }
                                }
                                else
                                {
                                    //This is non leaves, as in wildgrass or wahtever is in the p_blocks_leaf list (no special rules)
                                    EntityRotFX var31 = new EntityTexFX(worldRef, (double)xx, (double)yy + 0.5, (double)zz, 0D, 0D, 0D, 10D, 0, effLeafID);
                                    c_CoroWeatherUtil.setParticleGravity((EntityFX)var31, 0.3F);
                                    //var31.spawnAsWeatherEffect();
                                    spawnQueue.add(var31);
                                }
                            }
                            else if (id == 0)
                            {
                                if (weatherMan.wind.strength > 0.02)
                                {
                                    if (worldRef.rand.nextInt(400 - (int)(weatherMan.wind.strength * 100)) == 0)
                                    {
                                        //EntityFX var31 = new EntitySmokeFX(worldRef, (double)xx, (double)yy+0.5, (double)zz, 0D, 0D, 0D);
                                        EntityRotFX var31 = new EntityTexFX(worldRef, (double)xx, (double)yy + 0.5, (double)zz, 0D, 0D, 0D, 10D, 0, effWind2ID);
                                        //var31.particleGravity = 0.3F;
                                        //mod_ExtendedRenderer.rotEffRenderer.addEffect(var31);

                                        for (int ii = 0; ii < 20; ii++)
                                        {
                                            applyWindForce(var31);
                                        }

                                        c_CoroWeatherUtil.setParticleGravity((EntityFX)var31, 0.0F);
                                        var31.noClip = true;
                                        c_CoroWeatherUtil.setParticleScale((EntityFX)var31, 0.3F);
                                        var31.rotationYaw = rand.nextInt(360);
                                        //var31.spawnAsWeatherEffect();
                                        spawnQueue.add(var31);
                                    }
                                }
                            }
                        }

                        int id = getBlockId(xx, yy, zz);

                        if (id == ((Block)p_blocks_sand.get(0)).blockID)
                        {
                            if (id == Block.sand.blockID)
                            {
                                if (getBlockId(xx, yy + 1, zz) == 0)
                                {
                                    EntityTexFX var31 = new EntityTexFX(worldRef, (double)xx, (double)yy + 0.5, (double)zz, 0D, 0D, 0D, 10D, 0, effSandID);
                                    //var31 = new EntityWindFX(worldRef, (double)xx, (double)yy+1.2, (double)zz, 0D, 0.0D, 0D, 9.5D, 1);
                                    var31.rotationYaw = rand.nextInt(360) - 180F;
                                    var31.type = 1;
                                    c_CoroWeatherUtil.setParticleGravity((EntityFX)var31, 0.6F);
                                    c_CoroWeatherUtil.setParticleScale((EntityFX)var31, 0.3F);
                                    //var31.spawnAsWeatherEffect();
                                    spawnQueue.add(var31);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void tryWind(Side side, World world)
    {
        //if pre stage...
        //look for leaves? spawn particles

        //RAIN!! MODIFY BLUE COLOR SHADES IN CODE BITCH

        //logic
        //wind.windStrength = 0.3F;

        //if (true) return;
        if (player == null)
        {
            return;
        }

        int dist = 60;
        //if (side == Side.SERVER) {
        //List list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.expand(dist, 80, dist));
        List list = null;

        if (side == Side.CLIENT)
        {
            list = player.worldObj.loadedEntityList;
        }
        else
        {
            list = world.loadedEntityList;
        }

        //Chunk Entities
        if (list != null)
        {
            for (int i = 0; i < list.size(); i++)
            {
                Entity entity1 = (Entity)list.get(i);

                if (canPushEntity(entity1) && !(entity1 instanceof EntityPlayer))
                {
                    applyWindForce(entity1, 1F);
                }
            }
        }

        //}

        //weatherMan.wind.strength = 0.2F;

        //Weather Effects
        if (side == Side.CLIENT && weatherMan.wind.strength >= 0.10)
        {
            for (int i = 0; i < worldRef.weatherEffects.size(); i++)
            {
                Entity entity1 = (Entity)worldRef.weatherEffects.get(i);

                if (!(entity1 instanceof EntityLightningBolt))
                {
                    //applyWindForce(entity1);
                    if (entity1 instanceof EntityFX)
                    {
                        if (entity1 == null)
                        {
                            continue;
                        }

                        if ((worldRef.getHeightValue((int)(entity1.posX + 0.5F), (int)(entity1.posZ + 0.5F)) - 1 < (int)entity1.posY + 1) || (entity1 instanceof EntityTexFX))
                        {
                            if ((entity1 instanceof EntityFlameFX))
                            {
                                c_CoroWeatherUtil.setParticleAge((EntityFX)entity1, c_CoroWeatherUtil.getParticleAge((EntityFX)entity1) + 2);
                            }
                            else if (entity1 instanceof EntityAnimTexFX)
                            {
                                if (((EntityAnimTexFX) entity1).type == 1)
                                {
                                    if (activeTornado != null && !activeTornado.isDead)
                                    {
                                        //spin(activeTornado, (WeatherEntityConfig)weatherEntTypes.get(1), entity1);
                                    }
                                    else
                                    {
                                        //temp
                                        //spin(player, (WeatherEntityConfig)weatherEntTypes.get(1), entity1);
                                    }
                                }
                            }
                            else if (c_CoroWeatherUtil.getParticleAge((EntityFX)entity1) % 2 == 0)
                            {
                                c_CoroWeatherUtil.setParticleAge((EntityFX)entity1, c_CoroWeatherUtil.getParticleAge((EntityFX)entity1) + 1);
                            }

                            //((EntityFX)entity1).particleAge=1;

                            //rustle!
                            if (entity1 instanceof Entity) {}

                            if (entity1 instanceof EntityFallingRainFX)
                            {
                                if (entity1.onGround)
                                {
                                    //entity1.onGround = false;
                                    //entity1.motionY += rand.nextDouble() * entity1.motionX;
                                }

                                //entity1.motionY += -0.02 + rand.nextDouble() * 0.04;
                            }
                            else if ((entity1 instanceof EntityTexFX) && ((EntityTexFX)entity1).getParticleTextureIndex() == effLeafID)
                            {
                                if (entity1.motionX < 0.01F && entity1.motionZ < 0.01F)
                                {
                                    entity1.motionY += rand.nextDouble() * 0.02;
                                }

                                //entity1.motionX += rand.nextDouble() * 0.03;
                                //entity1.motionZ += rand.nextDouble() * 0.03;
                                entity1.motionY -= 0.01F;
                                //do it twice!
                                applyWindForce(entity1);
                            }
                        }

                        //if (canPushEntity(entity1)) {
                        if (!(entity1 instanceof EntTornado))
                        {
                            applyWindForce(entity1);
                        }
                    }
                }
            }
        }

        //Particles
        if (side == Side.CLIENT && fxLayers != null && weatherMan.wind.strength >= 0.10)
        {
            for (int layer = 0; layer < 4; layer++)
            {
                for (int i = 0; i < fxLayers[layer].size(); i++)
                {
                    Entity entity1 = (Entity)fxLayers[layer].get(i);

                    if ((worldRef.getHeightValue((int)(entity1.posX + 0.5F), (int)(entity1.posZ + 0.5F)) - 1 < (int)entity1.posY + 1) || (entity1 instanceof EntityTexFX))
                    {
                        if ((entity1 instanceof EntityFlameFX))
                        {
                            c_CoroWeatherUtil.setParticleAge((EntityFX)entity1, c_CoroWeatherUtil.getParticleAge((EntityFX)entity1) + 2);
                        }
                        else if (c_CoroWeatherUtil.getParticleAge((EntityFX)entity1) % 2 == 0)
                        {
                            c_CoroWeatherUtil.setParticleAge((EntityFX)entity1, c_CoroWeatherUtil.getParticleAge((EntityFX)entity1) + 1);
                        }

                        //rustle!
                        if (entity1.onGround)
                        {
                            //entity1.onGround = false;
                            entity1.motionY += rand.nextDouble() * entity1.motionX;
                        }

                        if (entity1.motionX < 0.01F && entity1.motionZ < 0.01F)
                        {
                            entity1.motionY += rand.nextDouble() * 0.05;
                        }

                        //entity1.motionX += rand.nextDouble() * 0.03;
                        //entity1.motionZ += rand.nextDouble() * 0.03;
                        //entity1.motionY += -0.04 + rand.nextDouble() * 0.04;
                        //if (canPushEntity(entity1)) {
                        //if (!(entity1 instanceof EntityFlameFX)) {
                        applyWindForce(entity1);
                    }
                }
            }

            //My particle renderer - actually, instead add ones you need to weatherEffects (add blank renderer file)
            for (int layer = 0; layer < 5; layer++)
            {
                for (int i = 0; i < ExtendedRenderer.rotEffRenderer.fxLayers[layer].size(); i++)
                {
                    Entity entity1 = (Entity)ExtendedRenderer.rotEffRenderer.fxLayers[layer].get(i);
                    /*if (entity1 == null) continue;
                    if ((worldRef.getHeightValue((int)(entity1.posX+0.5F), (int)(entity1.posZ+0.5F))-1 < (int)entity1.posY+1) || (entity1 instanceof EntityTexFX)) {
                        if ((entity1 instanceof EntityFlameFX)) {
                        	((EntityFX)entity1).particleAge+=2;
                        } else if (entity1 instanceof EntityAnimTexFX) {
                        	if (((EntityAnimTexFX) entity1).type == 1) {
                        		if (activeTornado != null && !activeTornado.isDead) {
                        			//spin(activeTornado, (WeatherEntityConfig)weatherEntTypes.get(1), entity1);
                        		} else {
                        			//temp
                        			//spin(player, (WeatherEntityConfig)weatherEntTypes.get(1), entity1);
                        		}
                        	}
                        } else if (((EntityFX)entity1).particleAge % 2 == 0) {
                        	((EntityFX)entity1).particleAge+=1;
                        }

                        //rustle!
                        if (entity1 instanceof Entity) {}
                        if (entity1 instanceof EntityFallingRainFX) {
                            if (entity1.onGround) {
                            	//entity1.onGround = false;
                            	//entity1.motionY += rand.nextDouble() * entity1.motionX;
                            }

                            //entity1.motionY += -0.02 + rand.nextDouble() * 0.04;
                        } else if ((entity1 instanceof EntityTexFX) && ((EntityTexFX)entity1).getParticleTextureIndex() == mod_EntMover.effLeafID) {
                        	if (entity1.motionX < 0.01F && entity1.motionZ < 0.01F) {
                            	entity1.motionY += rand.nextDouble() * 0.08;
                            }
                        	entity1.motionX += rand.nextDouble() * 0.03;
                            entity1.motionZ += rand.nextDouble() * 0.03;
                            entity1.motionY -= 0.01F;
                        }

                    //if (canPushEntity(entity1)) {
                    //if (!(entity1 instanceof EntityFlameFX)) {
                    	applyWindForce(entity1);
                    }*/
                }
            }
        }

        if (side == Side.CLIENT)
        {
            if (weatherMan.wind.strength >= 0.70)
            {
                if (canPushEntity(player))
                {
                    applyWindForce(player, 0.2F);
                    //applyWindGridForce(player, 30F);
                }
            }
        }

        //NEEEEEEEED TO STOP WIND WHEN UNDERGROUND!
        float volScaleFar = weatherMan.wind.strength * 1F;

        if (weatherMan.wind.strength <= 0.03F)
        {
            volScaleFar = 0F;
        }

        //Sound whistling noise
        //First, use volume to represent intensity, maybe get different sound samples for higher level winds as they sound different
        //Second, when facing towards wind, you're ears hear it tearing by you more, when turned 90 degrees you do not, simulate this
        tryPlaySound(snd_wind_far, 2, ModLoader.getMinecraftInstance().thePlayer, volScaleFar);

        if (lastSoundPositionUpdate < System.currentTimeMillis())
        {
            //System.out.println(sndSys);
            //int j = (int)(field.getFloat(item)
            lastSoundPositionUpdate = System.currentTimeMillis() + 100;

            //float gameVol = ModLoader.getMinecraftInstance().gameSettings.soundVolume;
            //if (soundID[0] > -1) {
            //mod_EntMover.setVolume(new StringBuilder().append("sound_"+soundID[0]).toString(), volScaleClose);
            //}

            if (soundID[2] > -1 && soundTimer[2] < System.currentTimeMillis())
            {
                setVolume(new StringBuilder().append("sound_" + soundID[2]).toString(), volScaleFar);
            }
        }

        //tryWindParticles();
        //HMMM
        //EntityRenderer.cameraZoom ?!?!
    }

    public static boolean tryPlaySound(String[] sound, int arrIndex, Entity source, float vol)
    {
        Entity soundTarget = source;

        if (source == null)
        {
            return false;
        }

        // should i?
        //soundTarget = this;
        if (soundTimer[arrIndex] <= System.currentTimeMillis())
        {
            //worldObj.playSoundAtEntity(soundTarget, new StringBuilder().append("tornado."+sound).toString(), 1.0F, 1.0F);
            //((IWorldAccess)this.worldAccesses.get(var5)).playSound(var2, var1.posX, var1.posY - (double)var1.yOffset, var1.posZ, var3, var4);
            soundID[arrIndex] = playMovingSound(new StringBuilder().append("tornado." + sound[snd_rand[arrIndex]]).toString(), (float)soundTarget.posX, (float)soundTarget.posY, (float)soundTarget.posZ, vol, 1.0F);
            //this.soundID[arrIndex] = mod_EntMover.getLastSoundID();
            //System.out.println(new StringBuilder().append("tornado."+sound[snd_rand[arrIndex]]).toString());
            //System.out.println(soundToLength.get(sound[snd_rand[arrIndex]]));
            int length = (Integer)soundToLength.get(sound[snd_rand[arrIndex]]);
            //-500L, for blending
            soundTimer[arrIndex] = System.currentTimeMillis() + length - 500L;
            snd_rand[arrIndex] = worldRef.rand.nextInt(3);
        }

        return false;
    }

    public static boolean canPushEntity(Entity ent)
    {
    	
    	if (!c_CoroWeatherUtil.canUseWindOn(ent)) return false;
    	
        double speed = 10.0D;
        int startX = (int)(ent.posX - speed * (double)(-MathHelper.sin(weatherMan.wind.direction / 180.0F * (float)Math.PI) * MathHelper.cos(weatherMan.wind.yDirection / 180.0F * (float)Math.PI)));
        int startZ = (int)(ent.posZ - speed * (double)(MathHelper.cos(weatherMan.wind.direction / 180.0F * (float)Math.PI) * MathHelper.cos(weatherMan.wind.yDirection / 180.0F * (float)Math.PI)));

        if (ent instanceof EntityPlayer)
        {
            boolean bool = true;
        }

        return ent.worldObj.rayTraceBlocks(Vec3.createVectorHelper(ent.posX, ent.posY + (double)ent.getEyeHeight(), ent.posZ), Vec3.createVectorHelper(startX, ent.posY + (double)ent.getEyeHeight(), startZ)) == null;
        //return true;
    }
    public static void applyWindForce(Entity ent)
    {
        applyWindForce(ent, 1D);
    }
    public static void applyWindForce(Entity ent, double multiplier)
    {
        double speed = weatherMan.wind.strength * 0.1D / getWeight(ent);
        speed *= multiplier;

        if ((ent.onGround && weatherMan.wind.strength < 0.7) && speed < 0.3)
        {
            speed = 0D;
        }

        ent.motionX += speed * (double)(-MathHelper.sin(weatherMan.wind.direction / 180.0F * (float)Math.PI) * MathHelper.cos(weatherMan.wind.yDirection / 180.0F * (float)Math.PI));
        ent.motionZ += speed * (double)(MathHelper.cos(weatherMan.wind.direction / 180.0F * (float)Math.PI) * MathHelper.cos(weatherMan.wind.yDirection / 180.0F * (float)Math.PI));
        ent.motionY += weatherMan.wind.yStrength * 0.1D * (double)(-MathHelper.sin((weatherMan.wind.yDirection) / 180.0F * (float)Math.PI));
    }
    public static void applyWindGridForce(Entity ent, double multiplier)
    {
        double speed = weatherMan.wind.strength * 0.1D / getWeight(ent);
        speed *= multiplier * 2F;

        if ((ent.onGround && weatherMan.wind.strength < 0.7) && speed < 0.3)
        {
            speed = 0D;
        }

        float angle;
        angle = weatherMan.wind.direction;
        angle = weatherMan.windGrid.getVecAngle((int)ent.posX, (int)ent.posY, (int)ent.posZ);
        ent.motionX += speed * (double)(-MathHelper.sin(angle / 180.0F * (float)Math.PI) * MathHelper.cos(weatherMan.wind.yDirection / 180.0F * (float)Math.PI));
        ent.motionZ += speed * (double)(MathHelper.cos(angle / 180.0F * (float)Math.PI) * MathHelper.cos(weatherMan.wind.yDirection / 180.0F * (float)Math.PI));
        //ent.motionY += weatherMan.wind.yStrength*0.1D*(double)(-MathHelper.sin((weatherMan.wind.yDirection) / 180.0F * (float)Math.PI));
        //ent.motionY += 0.001F;
    }
    //Testing vars
    public static float waveTicks;
    public static float prevTicks;

    public static long lastTime = 0;
    public static float waveTime = 0F;

    public static void testHook()
    {
    }

    public static World lastWorldForRender;
    
    @SideOnly(Side.CLIENT)
    public static void renderHook(float var1)
    {
        //if (true) return;

        //Map test = new HashMap();
        if (worldRef == null)
        {
            worldRef = ModLoader.getMinecraftInstance().theWorld;
        }

        if (lastWorldForRender != worldRef) {
        	lastWorldForRender = worldRef;
        	if (c_CoroWeatherUtil.hasTropicraft()) {
        		if (lastWorldForRender.provider.dimensionId == c_CoroWeatherUtil.tropiDimID) {
        			waveRenderRange = TropicraftRealm_waveRenderRange;
        		} else {
        			waveRenderRange = backupWaveRenderRange;
        		}
        		
        	}
        }
        
        
        //RenderManager.instance.renderEngine = mc.renderGlobal.renderEngine;

        if (RenderManager.instance.renderEngine == null)
        {
            return;
        }

        //Wave Renderer stuff
        WaveRenderer wr = new WaveRenderer();
        wr.setRenderManager(RenderManager.instance);
        Entity player = mc.thePlayer;

        if (true)
        {
            float time = ModLoader.getMinecraftInstance().theWorld.getWorldTime();
            //if (str < 0.1F) str = 0.1F;
            //System.out.println("wave dir: " + dir);
            //str = 1.5F;
            /*if (!useWindDir) {
            	dir = 225F;
            }*/
            //dir = 45F;
            //if (lastTime != time) {
            //if (weatherMan.wind.strengthSmooth > 0.15F) {
            //waveTime += ((float)(time - lastTime)) * str * 0.05F;
            //}
            //lastTime = (long)time;
            //}
            float str = baseStr;// + (weatherMan.wind.strengthSmooth * 0.3F);
            //float str2 = 0.8F + (weatherMan.wind.strengthSmooth * 0.8F);
            float dir = weatherMan.wind.directionSmoothWaves;
            float magnitude = 0.25F;
            float angle = (float)((float)(Math.PI * 2) / 360F) * /*player.rotationYaw*/dir;
            //System.out.println(weatherMan.wind.strengthSmooth);
            int x = (int)player.posX;
            //int y = (int)player.posY;
            int z = (int)player.posZ;
            //range = 7;
            boolean renderVBOWaves = true;
            Tessellator var12 = Tessellator.instance;

            if (renderVBOWaves)
            {
                int var9 = 0;//mod_ExtendedRenderer.rotEffRenderer.renderer.getTexture("/terrain.png");

                if (ModLoader.getMinecraftInstance().thePlayer.dimension == c_CoroWeatherUtil.tropiDimID)
                {
                    var9 = ExtendedRenderer.rotEffRenderer.renderer.getTexture("/lapi/lapiterrain.png");
                    //var9 = TextureFXManager.instance().getEffectTexture(TropicraftMod.proxy.altfx1);
                    //var9 = mod_ExtendedRenderer.rotEffRenderer.renderer.getTexture("/terrain.png");
                    //var9 = LAPI.getTextureIDFromLiquidID(TropicraftMod.instance.tropicwaterID, true);
                }
                else
                {
                    var9 = ExtendedRenderer.rotEffRenderer.renderer.getTexture("/terrain.png");
                }

                GL11.glBindTexture(3553, var9);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                //GL11.glColor4f(0.2F, 0.9F, 0.2F, 0.1F);
                var12.startDrawingQuads();
                //wr.loadTexture("/terrain.png");
                //GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_CULL_FACE);
                GL11.glEnable(GL11.GL_BLEND);
                //GL11.glBlendFunc(GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_SRC_ALPHA);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            }

            float height;

            for (int xx = x - (waveRenderRange / 2); xx < x + (waveRenderRange / 2); xx += 1)
            {
                for (int zz = z - (waveRenderRange / 2); zz < z + (waveRenderRange / 2); zz += 1)
                {
                    waveTicks += (System.currentTimeMillis() - prevTicks) / 1000F;
                    //time = System.currentTimeMillis() / 100000000000F;
                    //waveTicks = time;
                    //System.out.println(var1);
                    //height = (heights[(int)(((waveTime)-xx % 8)+var1) % 8]) * 0.1F;
                    int yy = worldRef.getHeightValue(xx, zz);
                    yy = seaLevel;
                    float vecX = (float)Math.sin(angle) * magnitude;
                    float vecZ = (float)Math.cos(angle) * magnitude;
                    float maxheight = 1F;
                    //float coordadj = 0.3F;
                    height = getHeight(worldRef, xx, yy, zz);
                    //height = ((float)Math.sin(waveTime-xx*vecX-(zz*vecZ)) + 1F) * magnitude * maxheight;
                    //System.out.println(height);
                    //if (true) continue;
                    float half = (float)Math.cos(0) / 2;
                    wr.renderX = xx + 0.5F;
                    wr.renderY = yy - 0.10F;///* + (float)Math.PI*/ + (height);//-1.827F + (height*2F)/* + (xx/10F)*/;
                    wr.renderY = yy - 0.10F;
                    wr.renderZ = zz + 0.5F;
                    int id = worldRef.getBlockId(xx, yy - 1, zz);
                    int meta = worldRef.getBlockMetadata(xx, yy - 1, zz);
                    Block block = Block.blocksList[id];

                    //Chunk var48 = mc.theWorld.getChunkFromBlockCoords((int)wr.renderX, (int)wr.renderZ);
                    //BiomeGenBase bgb = var48.getBiomeGenForWorldCoords((int)wr.renderX & 15, (int)wr.renderZ & 15, mc.theWorld.getWorldChunkManager());

                    if (id != 0/* && meta == 0*/ && Block.blocksList[id].blockMaterial == Material.water/* && bgb.biomeName.startsWith("Ocean")*/)
                    {
                    	
                    	//System.out.println(meta);
                    	
                        double dist = player.getDistance(wr.renderX, wr.renderY, wr.renderZ);

                        if (dist < waveRenderRange / 2)
                        {
                            if (!renderVBOWaves)
                            {
                                wr.tryRender(xx, yy, zz, (float)var1);
                            }
                            else
                            {
                                float rotationYaw = 0F;
                                float rotationPitch = 90F;
                                float var3 = MathHelper.cos(rotationYaw * (float)Math.PI / 180.0F);
                                float var4 = MathHelper.sin(rotationYaw * (float)Math.PI / 180.0F);
                                float var5 = -var4 * MathHelper.sin(rotationPitch * (float)Math.PI / 180.0F);
                                float var6 = var3 * MathHelper.sin(rotationPitch * (float)Math.PI / 180.0F);
                                float var7 = MathHelper.cos(rotationPitch * (float)Math.PI / 180.0F);
                                BlockDataPoint bdp = weatherMan.waterGrid.getPoint((int)xx, (int)yy - 1, (int)zz);

                                if (bdp.height < 0F)
                                {
                                    bdp.height = 0F;
                                }

                                //if (worldRef.rand.nextInt(2) == 0) {
                                bdp.height = (height);
                                //}
                                wr.vboRender(block, (float)var1, height, str, magnitude, angle, waveTime, xx, zz, var3, var7, var4, var5, var6);
                            }
                        }
                    }
                }
            }

            if (renderVBOWaves)
            {
                var12.draw();
                GL11.glDisable(GL11.GL_BLEND);
                //GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_CULL_FACE);
            }

            //
        }

        //c_w_MovingBlockStructure mbs = new c_w_MovingBlockStructure(mc.theWorld);
    }

    //CONFIGS SO FAR
    public static boolean useWindDir = true;
    public static int seaLevel = 63;
    public static float baseStr = 3F;
    public static float baseWaveHeight = 1F;

    @SideOnly(Side.CLIENT)
    public static float getBaseHeight()
    {
        return WeatherMod.stormMan.baseWaveHeight;
    }
    
    public static long timeCache = 0; 

    public static float getHeight(World world, int x, int y, int z)
    {
        //weatherMan.wind.strengthSmooth = 0.9F;
        if (world.isRemote)
        {
            baseWaveHeight = getBaseHeight();
        }
        else
        {
            baseWaveHeight = ServerTickHandler.dimToStormMan.get(world.provider.dimensionId).baseWaveHeight;
        }

        baseStr = baseWaveHeight + ((weatherMan.wind.strengthSmooth + 0.0F) * 2F) + ((stormMan.stage + 0) * 0.2F);
        //waveRenderRange = 50;
        if (!world.isRemote || FMLCommonHandler.instance().getMinecraftServerInstance() == null) {
        	timeCache = world.getWorldTime();
        }
        float time = timeCache;//world.getWorldTime(); //System.currentTimeMillis() / 1000000000
        float str = baseStr;// + (weatherMan.wind.strengthSmooth * 0.1F);
        float dir = weatherMan.wind.directionSmoothWaves;

        //System.out.println("wave dir: " + dir);

        //str = 1.5F;
        //dir = 45F;

        if (lastTime != time)
        {
            //if (weatherMan.wind.strengthSmooth > 0.15F) {
            waveTime += ((float)(time - lastTime)) * 0.05F;
            //}
            lastTime = (long)time;
        }

        float magnitude = 0.25F;
        float angle = (float)((float)(Math.PI * 2) / 360F) * /*player.rotationYaw*/dir;
        int yy = seaLevel;
        float vecX = (float)Math.sin(angle) * magnitude;
        float vecZ = (float)Math.cos(angle) * magnitude;
        float maxheight = str;
        //float coordadj = 0.3F;
        float height = ((float)Math.sin(waveTime - x * vecX - (z * vecZ)) + 1F) * magnitude * maxheight;
        return height;
    }

    @SideOnly(Side.CLIENT)
    public static void spawnStorm(int x, int y, int z)
    {
        if (stormClusters.size() >= Storm_maxClusters)
        {
            return;
        }

        StormCluster sc = new StormCluster();
        sc.setPosition(x, y, z);
        stormClusters.add(sc);
        worldRef.weatherEffects.add(sc);
    }

    @SideOnly(Side.CLIENT)
    public static void spawnStorm()
    {
        //if (stormClusters.size() >= maxStormClusters) return;
        StormCluster sc = new StormCluster();
        int range = 256;
        int tryX = (int)player.posX + (rand.nextInt(range) - (range / 2));
        int tryZ = (int)player.posZ + (rand.nextInt(range) - (range / 2));
        sc.setPosition(tryX, 127, tryZ);
        stormClusters.add(sc);
        worldRef.weatherEffects.add(sc);
    }

    @SideOnly(Side.CLIENT)
    public static void tryClouds()
    {
        if (worldRef == null)
        {
            return;    //worldRef = ModLoader.getMinecraftInstance().theWorld;
        }

        if (stormClusters.size() <= 0)
        {
            return;
        }

        for (int var99 = 0; var99 < stormClusters.size(); ++var99)
        {
            StormCluster sc = (StormCluster)stormClusters.get(var99);

            //Update active storms
            if (!sc.isDead)
            {
                sc.onUpdate();
                applyWindForce(sc);
                //if (var99 == 0) System.out.println(sc.posX);
                sc.age++;
                sc.setPosition(sc.posX + sc.motionX, sc.posY/* + sc.motionY*/, sc.posZ + sc.motionZ);
                sc.motionX *= 0.95F;
                sc.motionY *= 0.95F;
                sc.motionZ *= 0.95F;
            }

            if (sc.age > sc.maxAge)
            {
                sc.setDead();
                stormClusters.remove(sc);
            }
        }

        //OOOOOOOOOOLLLLLLLLLLLDDDDDDDDDDDD
    }

    //Thread safe functions
    @SideOnly(Side.CLIENT)
    private static int getBlockId(int x, int y, int z)
    {
        try
        {
            if (!ModLoader.getMinecraftInstance().theWorld.checkChunksExist(x, 0, z , x, 128, z))
            {
                return 10;
            }

            return ModLoader.getMinecraftInstance().theWorld.getBlockId(x, y, z);
        }
        catch (Exception ex)
        {
            return 10;
        }
    }
    @SideOnly(Side.CLIENT)
    private static int getBlockMetadata(int x, int y, int z)
    {
        if (!ModLoader.getMinecraftInstance().theWorld.checkChunksExist(x, 0, z , x, 128, z))
        {
            return 0;
        }

        return ModLoader.getMinecraftInstance().theWorld.getBlockMetadata(x, y, z);
    }
    public static void lookAtEnt(EntityLiving entityliving, EntityLiving entityliving1)
    {
        double d = entityliving1.posX - entityliving.posX;
        double d1 = entityliving1.posZ - entityliving.posZ;
        float f = (float)((Math.atan2(d1, d) * 180D) / Math.PI) - 90F;
        float f1;

        for (f1 = f - entityliving.rotationYaw; f1 < -180F; f1 += 360F) { }

        for (; f1 >= 180F; f1 -= 360F) { }

        if (f1 > 30F)
        {
            f1 = 30F;
        }

        if (f1 < -30F)
        {
            f1 = -30F;
        }

        entityliving.rotationYaw += f1;
    }
    public static void setVel(Entity entity, float f, float f1, float f2)
    {
        entity.motionX += f;
        entity.motionY += f1;
        entity.motionZ += f2;

        if (entity instanceof EntitySquid)
        {
            entity.setPosition(entity.posX + entity.motionX * 5F, entity.posY, entity.posZ + entity.motionZ * 5F);
        }
    }
    public static void moveEnt(Entity entity, float f, float f1, float f2)
    {
        entity.moveEntity(f, f1, f2);
    }
    public static void slowVel(Entity entity, float f)
    {
        if (entity.motionX > 0.10000000000000001D)
        {
            entity.motionX = entity.motionX * (double)f;
        }

        if (entity.motionY > 0.59999999999999998D)
        {
            entity.motionY = entity.motionY * (double)f;
        }

        entity.motionY = 0.0D;

        if (entity.motionZ > 0.10000000000000001D)
        {
            entity.motionZ = entity.motionZ * (double)f;
        }
    }

    //ML & Misc Functions
    public static void displayMessage(String s, int i)
    {
        msg = s;
        timeout = 85;
        color = i;
    }
    public static void dM(String s)
    {
        displayMessage(s, defaultColor);
    }
    public static void dM(float f)
    {
        displayMessage((new StringBuilder()).append(f).toString(), defaultColor);
    }
    public static void displayMessage(String s)
    {
        displayMessage(s, defaultColor);
    }
    public static int getFPS()
    {
        return 99;//Integer.valueOf(mc.debug.substring(0, mc.debug.indexOf(" ")).trim().toString());
    }
}
