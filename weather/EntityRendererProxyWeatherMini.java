package weather;

import net.minecraft.client.Minecraft;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityFX;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityRainFX;
import net.minecraft.src.EntityRenderer;
import net.minecraft.src.EntityRendererProxy;
import net.minecraft.src.EntitySmokeFX;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.Tessellator;
import net.minecraft.src.World;
import net.minecraft.src.c_CoroWeatherUtil;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;

import weather.renderer.EntityFallingRainFX;
import weather.renderer.EntityRotFX;

import java.util.Random;

public class EntityRendererProxyWeatherMini extends EntityRendererProxy
{
    private Minecraft mc;
    private Random random = new Random();
    public int rendererUpdateCount;
    public long lastWorldTime = 0;

    private int rainSoundCounter = 0;

    public boolean basicRain = false;
    public int rainRate = 50;
    
    /** Rain X coords */
    public float[] rainXCoords;

    /** Rain Y coords */
    public float[] rainYCoords;

    public EntityRendererProxyWeatherMini(Minecraft var1)
    {
        super(var1);
        this.mc = var1;
        rendererUpdateCount = 0;
    }

    @Override
    public void updateCameraAndRender(float var1)
    {
        super.updateCameraAndRender(var1);
        //ModLoader.OnTick(var1, this.game);
    }

    public void enableLightmap2(double var1)
    {
        GL13.glClientActiveTexture(GL13.GL_TEXTURE1);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glLoadIdentity();
        float var3 = 0.00390625F;
        GL11.glScalef(var3, var3, var3);
        GL11.glTranslatef(8.0F, 8.0F, 8.0F);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        this.mc.renderEngine.bindTexture(this.lightmapTexture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL13.glClientActiveTexture(GL13.GL_TEXTURE0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
    }

    public void disableLightMap2(double var1)
    {
        GL13.glClientActiveTexture(GL13.GL_TEXTURE1);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL13.glClientActiveTexture(GL13.GL_TEXTURE0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
    }/*
    
    @Override
    public void setupFog(int par1, float par2)
    {
    	super.setupFog(par1, par2);
    	System.out.println("woooooo");
    }*/

    @Override
    protected void renderRainSnow(float par1)
    {
        //if (true) { super.renderRainSnow(par1); return; }
        this.rendererUpdateCount = (Integer)c_CoroWeatherUtil.getPrivateValueBoth(EntityRenderer.class, this, "t", "rendererUpdateCount");
        /*try {
           this.rendererUpdateCount = (Integer)ModLoader.getPrivateValue(EntityRenderer.class, this, "t");
        } catch (Exception ex) {
           try {
        	   this.rendererUpdateCount = (Integer)ModLoader.getPrivateValue(EntityRenderer.class, this, "rendererUpdateCount");
           } catch (Exception ex2) {
        	   ex2.printStackTrace();
           }
        }*/
        float var2 = this.mc.theWorld.getRainStrength(par1);

        if (var2 > 0.0F)
        {
            this.enableLightmap((double)par1);

            if (this.rainXCoords == null)
            {
                this.rainXCoords = new float[1024];
                this.rainYCoords = new float[1024];

                for (int var3 = 0; var3 < 32; ++var3)
                {
                    for (int var4 = 0; var4 < 32; ++var4)
                    {
                        float var5 = (float)(var4 - 16);
                        float var6 = (float)(var3 - 16);
                        float var7 = MathHelper.sqrt_float(var5 * var5 + var6 * var6);
                        this.rainXCoords[var3 << 5 | var4] = -var6 / var7;
                        this.rainYCoords[var3 << 5 | var4] = var5 / var7;
                    }
                }
            }

            EntityLiving var41 = this.mc.renderViewEntity;
            World var42 = this.mc.theWorld;
            int var43 = MathHelper.floor_double(var41.posX);
            int var44 = MathHelper.floor_double(var41.posY);
            int var45 = MathHelper.floor_double(var41.posZ);
            Tessellator var8 = Tessellator.instance;
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.01F);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/environment/snow.png"));
            double var9 = var41.lastTickPosX + (var41.posX - var41.lastTickPosX) * (double)par1;
            double var11 = var41.lastTickPosY + (var41.posY - var41.lastTickPosY) * (double)par1;
            double var13 = var41.lastTickPosZ + (var41.posZ - var41.lastTickPosZ) * (double)par1;
            int var15 = MathHelper.floor_double(var11);
            byte var16 = 5;

            if (this.mc.gameSettings.fancyGraphics)
            {
                var16 = 10;
            }

            boolean var17 = false;
            byte var18 = -1;
            float var19 = (float)this.rendererUpdateCount + par1;

            if (this.mc.gameSettings.fancyGraphics)
            {
                var16 = 10;
            }

            // CODE ADDITION
            if (ExtendedRenderer.smoothRain)
            {
                //var16 = 1;
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            var17 = false;

            for (int var20 = var45 - var16; var20 <= var45 + var16; ++var20)
            {
                for (int var21 = var43 - var16; var21 <= var43 + var16; ++var21)
                {
                    int var22 = (var20 - var45 + 16) * 32 + var21 - var43 + 16;
                    float var23 = this.rainXCoords[var22] * 0.5F;
                    float var24 = this.rainYCoords[var22] * 0.5F;
                    BiomeGenBase var25 = var42.getBiomeGenForCoords(var21, var20);

                    if (var25.canSpawnLightningBolt() || var25.getEnableSnow())
                    {
                        int var26 = var42.getPrecipitationHeight(var21, var20);
                        int var27 = var44 - var16;
                        int var28 = var44 + var16;

                        if (var27 < var26)
                        {
                            var27 = var26;
                        }

                        if (var28 < var26)
                        {
                            var28 = var26;
                        }

                        float var29 = 1.0F;
                        int var30 = var26;

                        if (var26 < var15)
                        {
                            var30 = var15;
                        }

                        if (var27 != var28)
                        {
                            this.random.setSeed((long)(var21 * var21 * 3121 + var21 * 45238971 ^ var20 * var20 * 418711 + var20 * 13761));
                            float var31 = var25.getFloatTemperature();
                            double var35;
                            float var32;

                            if (var42.getWorldChunkManager().getTemperatureAtHeight(var31, var26) >= 0.15F)
                            {
                                if (ExtendedRenderer.smoothRain)
                                {
                                    if (mc.theWorld != null && mc.theWorld.getWorldInfo().getWorldTime() != lastWorldTime)
                                    {
                                    	int rr = 50 - (rainRate * 4);
                                    	if (rr < 1) { rr = 1; }
                                        //rainRate = 5;
                                        if (this.random.nextInt(rr) == 0)
                                        {
                                            for (int i = 0; i < (rainRate * 1); i++)
                                            {
                                                //if (mc.theWorld.rand.nextInt(1) == 0) {
                                                //spawn particle at var21 posY+20 var22
                                                int size = 10;
                                                //EntityFX ent = new EntityFallingRainFX(mc.theWorld, (double)var21+mc.theWorld.rand.nextInt(size)-(size/2), (double)var41.posY+10, (double)var20+mc.theWorld.rand.nextInt(size)-(size/2), 0D, -5D-(mc.theWorld.rand.nextInt(5)*-1D), 0D, 1.5D, 3);
                                                EntityRotFX ent = new EntityFallingRainFX(mc.theWorld, (double)var41.posX + mc.theWorld.rand.nextInt(size) - (size / 2), (double)var41.posY + 10, (double)var41.posZ + mc.theWorld.rand.nextInt(size) - (size / 2), 0D, -5D - (mc.theWorld.rand.nextInt(5) * -1D), 0D, 1.5D, 3);
                                                //this.funnelEffects.add(var31);
                                                //mod_EntMover.particleCount++;
                                                ent.renderDistanceWeight = 1.0D;
                                                ent.setSize(1.2F, 1.2F);
                                                ent.rotationYaw = ent.worldObj.rand.nextInt(360) - 180F;
                                                //var31.posY = var6 + 0D;
                                                //var31.setPosition(tryX2, this.posY, tryZ2);
                                                //ent.noClip = true;
                                                ent.setGravity(0.00001F);
                                                //mc.effectRenderer.addEffect(ent);
                                                ent.spawnAsWeatherEffect();
                                                //}
                                            }
                                        }
                                    }
                                }
                                else
                                {
                                    if (var18 != 0)
                                    {
                                        if (var18 >= 0)
                                        {
                                            var8.draw();
                                        }

                                        var18 = 0;
                                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/environment/rain.png"));
                                        var8.startDrawingQuads();
                                    }

                                    var32 = ((float)(this.rendererUpdateCount + var21 * var21 * 3121 + var21 * 45238971 + var20 * var20 * 418711 + var20 * 13761 & 31) + par1) / 32.0F * (3.0F + this.random.nextFloat());
                                    double var33 = (double)((float)var21 + 0.5F) - var41.posX;
                                    var35 = (double)((float)var20 + 0.5F) - var41.posZ;
                                    float var37 = MathHelper.sqrt_double(var33 * var33 + var35 * var35) / (float)var16;
                                    float var38 = 1.0F;
                                    var8.setBrightness(var42.getLightBrightnessForSkyBlocks(var21, var30, var20, 0));
                                    var8.setColorRGBA_F(var38, var38, var38, ((1.0F - var37 * var37) * 0.5F + 0.5F) * var2);
                                    var8.setTranslation(-var9 * 1.0D, -var11 * 1.0D, -var13 * 1.0D);
                                    var8.addVertexWithUV((double)((float)var21 - var23) + 0.5D, (double)var27, (double)((float)var20 - var24) + 0.5D, (double)(0.0F * var29), (double)((float)var27 * var29 / 4.0F + var32 * var29));
                                    var8.addVertexWithUV((double)((float)var21 + var23) + 0.5D, (double)var27, (double)((float)var20 + var24) + 0.5D, (double)(1.0F * var29), (double)((float)var27 * var29 / 4.0F + var32 * var29));
                                    var8.addVertexWithUV((double)((float)var21 + var23) + 0.5D, (double)var28, (double)((float)var20 + var24) + 0.5D, (double)(1.0F * var29), (double)((float)var28 * var29 / 4.0F + var32 * var29));
                                    var8.addVertexWithUV((double)((float)var21 - var23) + 0.5D, (double)var28, (double)((float)var20 - var24) + 0.5D, (double)(0.0F * var29), (double)((float)var28 * var29 / 4.0F + var32 * var29));
                                    var8.setTranslation(0.0D, 0.0D, 0.0D);
                                }
                            }
                            else
                            {
                                if (var18 != 1)
                                {
                                    if (var18 >= 0)
                                    {
                                        var8.draw();
                                    }

                                    var18 = 1;
                                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/environment/snow.png"));
                                    var8.startDrawingQuads();
                                }

                                var32 = ((float)(this.rendererUpdateCount & 511) + par1) / 512.0F;
                                float var46 = this.random.nextFloat() + var19 * 0.01F * (float)this.random.nextGaussian();
                                float var34 = this.random.nextFloat() + var19 * (float)this.random.nextGaussian() * 0.001F;
                                var35 = (double)((float)var21 + 0.5F) - var41.posX;
                                double var47 = (double)((float)var20 + 0.5F) - var41.posZ;
                                float var39 = MathHelper.sqrt_double(var35 * var35 + var47 * var47) / (float)var16;
                                float var40 = 1.0F;
                                var8.setBrightness((var42.getLightBrightnessForSkyBlocks(var21, var30, var20, 0) * 3 + 15728880) / 4);
                                var8.setColorRGBA_F(var40, var40, var40, ((1.0F - var39 * var39) * 0.3F + 0.5F) * var2);
                                var8.setTranslation(-var9 * 1.0D, -var11 * 1.0D, -var13 * 1.0D);
                                var8.addVertexWithUV((double)((float)var21 - var23) + 0.5D, (double)var27, (double)((float)var20 - var24) + 0.5D, (double)(0.0F * var29 + var46), (double)((float)var27 * var29 / 4.0F + var32 * var29 + var34));
                                var8.addVertexWithUV((double)((float)var21 + var23) + 0.5D, (double)var27, (double)((float)var20 + var24) + 0.5D, (double)(1.0F * var29 + var46), (double)((float)var27 * var29 / 4.0F + var32 * var29 + var34));
                                var8.addVertexWithUV((double)((float)var21 + var23) + 0.5D, (double)var28, (double)((float)var20 + var24) + 0.5D, (double)(1.0F * var29 + var46), (double)((float)var28 * var29 / 4.0F + var32 * var29 + var34));
                                var8.addVertexWithUV((double)((float)var21 - var23) + 0.5D, (double)var28, (double)((float)var20 - var24) + 0.5D, (double)(0.0F * var29 + var46), (double)((float)var28 * var29 / 4.0F + var32 * var29 + var34));
                                var8.setTranslation(0.0D, 0.0D, 0.0D);
                            }
                        }
                    }
                }
            }

            if (var18 >= 0)
            {
                var8.draw();
            }

            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            this.disableLightmap((double)par1);
        }

        // NEW CODE START \\

        if (mc.theWorld != null && mc.theWorld.getWorldInfo().getWorldTime() != lastWorldTime)
        {
            lastWorldTime = mc.theWorld.getWorldInfo().getWorldTime();

            if (!mc.isGamePaused)
            {
                ExtendedRenderer.rotEffRenderer.updateEffects();
                //if (mc.theWorld.worldInfo.getWorldTime() % 2 == 0) {
                //}
            }
        }

        //Rotating particles hook
        ExtendedRenderer.rotEffRenderer.renderParticles((Entity)mc.renderViewEntity, (float)par1);
        WeatherMod.renderHook(par1);
        // NEW CODE END //
    }

    private void addRainParticles()
    {
        float var1 = this.mc.theWorld.getRainStrength(1.0F);

        if (!this.mc.gameSettings.fancyGraphics)
        {
            var1 /= 2.0F;
        }

        if (var1 != 0.0F)
        {
            this.random.setSeed((long)this.rendererUpdateCount * 312987231L);
            EntityLiving var2 = this.mc.renderViewEntity;
            World var3 = this.mc.theWorld;
            int var4 = MathHelper.floor_double(var2.posX);
            int var5 = MathHelper.floor_double(var2.posY);
            int var6 = MathHelper.floor_double(var2.posZ);
            byte var7 = 10;
            double var8 = 0.0D;
            double var10 = 0.0D;
            double var12 = 0.0D;
            int var14 = 0;
            int var15 = (int)(100.0F * var1 * var1);

            if (this.mc.gameSettings.particleSetting == 1)
            {
                var15 >>= 1;
            }
            else if (this.mc.gameSettings.particleSetting == 2)
            {
                var15 = 0;
            }

            for (int var16 = 0; var16 < var15; ++var16)
            {
                int var17 = var4 + this.random.nextInt(var7) - this.random.nextInt(var7);
                int var18 = var6 + this.random.nextInt(var7) - this.random.nextInt(var7);
                int var19 = var3.getPrecipitationHeight(var17, var18);
                int var20 = var3.getBlockId(var17, var19 - 1, var18);
                BiomeGenBase var21 = var3.getBiomeGenForCoords(var17, var18);

                if (var19 <= var5 + var7 && var19 >= var5 - var7 && var21.canSpawnLightningBolt() && var21.getFloatTemperature() > 0.2F)
                {
                    float var22 = this.random.nextFloat();
                    float var23 = this.random.nextFloat();

                    if (var20 > 0)
                    {
                        if (Block.blocksList[var20].blockMaterial == Material.lava)
                        {
                            this.mc.effectRenderer.addEffect(new EntitySmokeFX(var3, (double)((float)var17 + var22), (double)((float)var19 + 0.1F) - Block.blocksList[var20].minY, (double)((float)var18 + var23), 0.0D, 0.0D, 0.0D));
                        }
                        else
                        {
                            ++var14;

                            if (this.random.nextInt(var14) == 0)
                            {
                                var8 = (double)((float)var17 + var22);
                                var10 = (double)((float)var19 + 0.1F) - Block.blocksList[var20].minY;
                                var12 = (double)((float)var18 + var23);
                            }

                            this.mc.effectRenderer.addEffect(new EntityRainFX(var3, (double)((float)var17 + var22), (double)((float)var19 + 0.1F) - Block.blocksList[var20].minY, (double)((float)var18 + var23)));
                        }
                    }
                }
            }

            if (var14 > 0 && this.random.nextInt(3) < this.rainSoundCounter++)
            {
                this.rainSoundCounter = 0;

                if (var10 > var2.posY + 1.0D && var3.getPrecipitationHeight(MathHelper.floor_double(var2.posX), MathHelper.floor_double(var2.posZ)) > MathHelper.floor_double(var2.posY))
                {
                    this.mc.theWorld.playSoundEffect(var8, var10, var12, "ambient.weather.rain", 0.1F, 0.5F);
                }
                else
                {
                    this.mc.theWorld.playSoundEffect(var8, var10, var12, "ambient.weather.rain", 0.2F, 1.0F);
                }
            }
        }
    }
}
