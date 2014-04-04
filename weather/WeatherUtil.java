package weather;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.src.ModLoader;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import CoroUtil.OldUtil;
import CoroUtil.entity.EntityTropicalFishHook;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WeatherUtil
{

    @SideOnly(Side.CLIENT)
    public static Minecraft mc;
    
    //Tropicraft reflection
    public static boolean hasTropicraft = true; //try reflection once
    public static boolean genCache = true;
	public static String tcE = "tropicraft.entities.";
	public static String[] noFloatEnts_tropical = {"passive.water.EntityTropicalFish", "EntityTropicraftWaterMob", "passive.water.EntityAmphibian", "passive.water.EntityManOWar", "passive.water.EntityMarlin", "hostile.land.TreeFrog"};
	public static String[] noWindEnts_tropical = {"passive.water.EntityTropicalFish", "EntityTropicraftWaterMob", "passive.water.EntityAmphibian", "passive.water.EntityManOWar", "passive.water.EntityMarlin"};
	public static Class[] noFloatEnts_tropical_class;
	public static Class[] noWindEnts_tropical_class;
	public static int tropiDimID = -127;
	public static int mainDimID = 0;

    public WeatherUtil()
    {
    }
    
    public static boolean hasTropicraft() {
    	try {
    		if (hasTropicraft) {
    			
    			Class clazz = Class.forName("tropicraft.Tropicraft");
    			
    			if (clazz != null) {
    				return true;
    			} else {
    				hasTropicraft = false;    				
    			}
    		}
    	} catch (Exception ex) {
    		hasTropicraft = false;
    	}
    	return hasTropicraft;
    }
    
    public static boolean canUseWavesOn(Entity ent) {
    	if (ent instanceof EntityTropicalFishHook) {
    		return false;
    	}
    	if (!hasTropicraft) return false;
    	if (genCache) genCache();
    	
    	try {
    		for (Class cl : noFloatEnts_tropical_class) {
    			if (cl.isInstance(ent)) {
    				return false;
    			}
    		}
    	} catch (Exception ex) {
    		hasTropicraft = false;
    		//ex.printStackTrace();
    	}
    	return true;
    }
    
    public static boolean canUseWindOn(Entity ent) {
    	if (!hasTropicraft) return false;
    	if (genCache) genCache();
    	try {
    		for (Class cl : noWindEnts_tropical_class) {
    			if (cl.isInstance(ent)) {
    				return false;
    			}
    		}
    	} catch (Exception ex) {
    		hasTropicraft = false;
    		ex.printStackTrace();
    	}
    	return true;
    }
    
    public static void genCache() {
    	genCache = false;
    	
    	noFloatEnts_tropical_class = new Class[noFloatEnts_tropical.length];
    	noWindEnts_tropical_class = new Class[noWindEnts_tropical.length];
    	
    	try {
    		int i = 0;
	    	for (String entStr : noFloatEnts_tropical) {
	    		
	    		Class clazz = Class.forName(tcE + entStr);
				if (clazz != null) {
					noFloatEnts_tropical_class[i] = clazz;
				} else {
					System.out.println("CRITICAL CLASS CACHING FAIL, CHECK TROPICRAFT CLASS NAMES! OR TELL CORO!");
				}
				i++;
			}
	    	i = 0;
	    	for (String entStr : noWindEnts_tropical) {
	    		
	    		Class clazz = Class.forName(tcE + entStr);
				if (clazz != null) {
					noWindEnts_tropical_class[i] = clazz;
				} else {
					System.out.println("CRITICAL CLASS CACHING FAIL, CHECK TROPICRAFT CLASS NAMES! OR TELL CORO!");
				}
				i++;
			}
	    } catch (Exception ex) {
			hasTropicraft = false;
			System.out.println("CRITICAL CLASS CACHING FAIL, CHECK TROPICRAFT CLASS NAMES! OR TELL CORO!");
			ex.printStackTrace();
		}
    }

    public static int getParticleAge(EntityFX ent)
    {
        return ent.particleAge;
    }

    public static void setParticleAge(EntityFX ent, int val)
    {
        ent.particleAge = val;
    }

    public static int getEntityAge(EntityLivingBase ent)
    {
        return ent.entityAge;
    }

    public static void setEntityAge(EntityLivingBase ent, int val)
    {
        ent.entityAge = val;
    }

    public static float getParticleGravity(EntityFX ent)
    {
        return ent.particleGravity;
    }

    public static void setParticleGravity(EntityFX ent, float val)
    {
        ent.particleGravity = val;
    }

    public static float getParticleScale(EntityFX ent)
    {
        return ent.particleScale;
    }

    public static void setParticleScale(EntityFX ent, float val)
    {
        ent.particleScale = val;
    }
    
    public static Icon particleTextureIndex(EntityFX ent) {
    	return ent.particleIcon;
    }

    public static float getThunderStr(World world)
    {
        return world.thunderingStrength;
    }

    public static void setThunderStr(World world, float val)
    {
        world.thunderingStrength = val;
    }

    public static void watchWorldObj()
    {
        if (mc != null)
        {
            //if (mc.theWorld != PFQueue.worldMap) {
            //System.out.println("PFQueue detecting new world, updating...");
            //PFQueue.worldMap = mc.theWorld;
            //}
        }
        else
        {
            mc = ModLoader.getMinecraftInstance();
        }
    }

    public static boolean getIsJumping(EntityLivingBase ent)
    {
        if (ent instanceof EntityPlayer)
        {
            if (ent.worldObj.isRemote)
            {
                return !ent.isCollidedHorizontally && hmm();
            }
            else
            {
                return false; // server side player, no need for moving it on server, since you cant
            }
        }
        else
        {
            if (/*ent instanceof EntityTropicraftWaterMob || */ent instanceof EntityWaterMob || !canUseWavesOn(ent))
            {
                return false;
            }
            else
            {
                return true;
            }
        }

        //return ent.isJumping;
    }

    @SideOnly(Side.CLIENT)
    public static boolean hmm()
    {
    	try {
	        if (Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindJump.keyCode))
	        {
	            return true;
	        }
    	} catch (Exception ex) {
    		//nope
    		return false;
    	}

        return false;
    }

    public static Field tryGetField(Class theClass, String obf, String mcp)
    {
        Field field = null;

        try
        {
            field = theClass.getDeclaredField(obf);
            field.setAccessible(true);
        }
        catch (Exception ex)
        {
            try
            {
                field = theClass.getDeclaredField(mcp);
                field.setAccessible(true);
            }
            catch (Exception ex2)
            {
                ex2.printStackTrace();
            }
        }

        return field;
    }

    public static void setPrivateValueBoth(Class var0, Object var1, String obf, String mcp, Object var3)
    {
    	OldUtil.setPrivateValueBoth(var0, var1, obf, mcp, var3);
    }

    public static Object getPrivateValueBoth(Class var0, Object var1, String obf, String mcp)
    {
        return OldUtil.getPrivateValueBoth(var0, var1, obf, mcp);
    }

    public static Object getPrivateValue(Class var0, Object var1, String var2) throws IllegalArgumentException, SecurityException, NoSuchFieldException
    {
        try
        {
            Field var3 = var0.getDeclaredField(var2);
            var3.setAccessible(true);
            return var3.get(var1);
        }
        catch (IllegalAccessException var4)
        {
            ModLoader.throwException("An impossible error has occured!", var4);
            return null;
        }
    }

    static Field field_modifiers = null;

    public static void setPrivateValue(Class var0, Object var1, int var2, Object var3) throws IllegalArgumentException, SecurityException, NoSuchFieldException
    {
        try
        {
            Field var4 = var0.getDeclaredFields()[var2];
            var4.setAccessible(true);
            int var5 = field_modifiers.getInt(var4);

            if ((var5 & 16) != 0)
            {
                field_modifiers.setInt(var4, var5 & -17);
            }

            var4.set(var1, var3);
        }
        catch (IllegalAccessException var6)
        {
            //logger.throwing("ModLoader", "setPrivateValue", var6);
            //throwException("An impossible error has occured!", var6);
        }
    }

    public static void setPrivateValue(Class var0, Object var1, String var2, Object var3) throws IllegalArgumentException, SecurityException, NoSuchFieldException
    {
        try
        {
            if (field_modifiers == null)
            {
                field_modifiers = Field.class.getDeclaredField("modifiers");
                field_modifiers.setAccessible(true);
            }

            Field var4 = var0.getDeclaredField(var2);
            int var5 = field_modifiers.getInt(var4);

            if ((var5 & 16) != 0)
            {
                field_modifiers.setInt(var4, var5 & -17);
            }

            var4.setAccessible(true);
            var4.set(var1, var3);
        }
        catch (IllegalAccessException var6)
        {
            //logger.throwing("ModLoader", "setPrivateValue", var6);
            //throwException("An impossible error has occured!", var6);
        }
    }

    public static void renderString(String par2Str, double par3, double par5, double par7)
    {
        RenderManager rm = RenderManager.instance;
        FontRenderer var11 = rm.getFontRenderer();
        float var12 = 1.6F;
        float var13 = 0.016666668F * var12;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par3 + 0.0F, (float)par5 + 2.3F, (float)par7);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-var13, -var13, var13);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator var14 = Tessellator.instance;
        byte var15 = 0;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        var14.startDrawingQuads();
        int var16 = var11.getStringWidth(par2Str) / 2;
        var14.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
        var14.addVertex((double)(-var16 - 1), (double)(-1 + var15), 0.0D);
        var14.addVertex((double)(-var16 - 1), (double)(8 + var15), 0.0D);
        var14.addVertex((double)(var16 + 1), (double)(8 + var15), 0.0D);
        var14.addVertex((double)(var16 + 1), (double)(-1 + var15), 0.0D);
        var14.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        var11.drawString(par2Str, -var11.getStringWidth(par2Str) / 2, var15, 553648127);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        var11.drawString(par2Str, -var11.getStringWidth(par2Str) / 2, var15, -1);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }
}
