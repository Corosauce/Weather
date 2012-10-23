package weather;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.src.BaseMod;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.GuiChat;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.MLProp;
import net.minecraft.src.ModLoader;
import net.minecraft.src.World;
import net.minecraftforge.common.Configuration;
import paulscode.sound.SoundSystem;
import weather.renderer.RotatingEffectRenderer;

import java.util.Random;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import java.util.Map;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Side;

@Mod(modid = "ExtendedRenderer", name = "Extended Renderer", version = "v1.1 for MC v1.3.2")
public class ExtendedRenderer extends BaseMod
    implements Runnable {

    @SideOnly(Side.CLIENT)
    public static Minecraft mc;
    public static World worldRef;
    public static EntityPlayer player;
    public static int timeout;
    public static String msg;
    public static int color;
    public static int defaultColor = 0xffffff;
    public static boolean ingui;

    public static int rainRate = 50;
    public static boolean smoothRain = true;
    public static float rainVolumeOutside = 0.1F;
    public static float rainVolumeInside = 0.05F;
    
    public static boolean fogColorOverride = false;
    public static float fogColorRed;
    public static float fogColorGreen;
    public static float fogColorBlue;


    public static int effRainID;

    public static RotatingEffectRenderer rotEffRenderer;
    
    public static int plBrightness;

    public Configuration preInitConfig;
    
    @PreInit
    public void preInit(FMLPreInitializationEvent event)
    {
        preInitConfig = new Configuration(event.getSuggestedConfigurationFile());

        try
        {
            preInitConfig.load();
            
            rainRate = preInitConfig.getOrCreateIntProperty("rainRate", Configuration.CATEGORY_ITEM, 50).getInt(50);
            smoothRain = preInitConfig.getOrCreateBooleanProperty("smoothRain", Configuration.CATEGORY_GENERAL, true).getBoolean(true);
            
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
    
    public String getVersion() {
        return "Version 1.0 for MC ";//+ModLoader.VERSION.substring(ModLoader.VERSION.indexOf(" ")+1);
    }

    @SideOnly(Side.CLIENT)
    public void run() {
        try {
            while(true) {
                if(mc == null) {
                    mc = ModLoader.getMinecraftInstance();
                }

                if(mc == null) {
                    Thread.sleep(5000L);
                } else {
                    if(mc.thePlayer == null) {
                        Thread.sleep(5000L);
                    } else {
                        worldRef = mc.theWorld;
                        player = mc.thePlayer;
                        Thread.sleep(5000L);
                    }
                }
            }
        } catch(Throwable throwable) {
            throwable.printStackTrace();
        }
    }
    
    public ExtendedRenderer() {
        //ModLoader.setInGUIHook(this, true, false);
        //ModLoader.setInGameHook(this, true, false);
    }

    @Override
    public void load() {
    }

    public void addRenderer() {
        effRainID = ModLoader.addOverride("/gui/items.png", "/coro/weather/raindrop.png");
    }
    @SideOnly(Side.CLIENT)
    
    @PostInit
    public void modsLoaded(FMLPostInitializationEvent event) {
        mc = ModLoader.getMinecraftInstance();
        this.rotEffRenderer = new RotatingEffectRenderer(mc.theWorld, mc.renderEngine);
        EntityRendererProxyWeatherMini temp = new EntityRendererProxyWeatherMini(mc);
        temp.rainRate = this.rainRate;
        mc.entityRenderer = temp;
        
        addRenderer();
    }

    public static void displayMessage(String s, int i) {
        msg = s;
        timeout = 85;
        color = i;
    }
    public static void dM(String s) {
        displayMessage(s, defaultColor);
    }
    public static void dM(float f) {
        displayMessage((new StringBuilder()).append(f).toString(), defaultColor);
    }
    public static void displayMessage(String s) {
        displayMessage(s, defaultColor);
    }
    @SideOnly(Side.CLIENT)
    @Override
    public boolean onTickInGame(float f, Minecraft var1) {
        if(!ingui) {
            //playerLastTick = System.currentTimeMillis();
            this.OSDHook(var1, false);
        }
        
        ingui = false;
        return true;
    }
    @SideOnly(Side.CLIENT)
    @Override
    public boolean onTickInGUI(float f, Minecraft var1, GuiScreen gui) {
        if (ModLoader.getMinecraftInstance().thePlayer != null) {
            //long ticksRan = System.currentTimeMillis();
            if (!(gui instanceof GuiContainer) && !(gui instanceof GuiChat) && gui != null) {
                ingui = true;
                //lastTickRun = 0;
            }

            //System.out.println(gui);
            //playerTick(mc.thePlayer);
        }

        return true;
    }


    @SideOnly(Side.CLIENT)
    public static void OSDHook(Minecraft minecraft, boolean flag) {
        if (worldRef == null) {
            worldRef = ModLoader.getMinecraftInstance().theWorld;
        }

        if (player == null) {
            player = ModLoader.getMinecraftInstance().thePlayer;
        }

        if (worldRef == null || player == null) {
            return;
        }

        if(timeout > 0 && msg != null) {
            //ScaledResolution scaledresolution = new ScaledResolution(minecraft.gameSettings, minecraft.displayWidth, minecraft.displayHeight);
            minecraft.fontRenderer.drawStringWithShadow(msg, 3, 85, 0xffffff);
            timeout--;
        }
    }
}
