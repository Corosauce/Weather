package weather.waves;

import net.minecraft.block.Block;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.src.ModLoader;
import net.minecraft.util.Icon;
import weather.WeatherMod;
import weather.c_CoroWeatherUtil;
import weather.config.ConfigWavesMisc;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
@SideOnly(Side.CLIENT)
public class WaveRenderer
{
    private RenderBlocks rb;
    public Entity entRef;
    public float renderX;
    public float renderY;
    public float renderZ;

    public float red = 1F;
    public float green = 1F;
    public float blue = 1F;

    public WaveRenderer()
    {
        //this.shadowSize = 0.5F;
        this.rb = new RenderBlocks();
        entRef = ModLoader.getMinecraftInstance().thePlayer;
    }

    public Icon getParticleTextureIndex(int dim, Block block)
    {
    	
    	return block.getIcon(1, 0);
    	
        /*if (dim == 0) {
        	return Block.waterStill.getIcon(1, 0);
        } else if (dim == 127) {
        	return TropicraftMod.waterStillTropics.getIcon(1, 0);
        } else {
        	return Block.waterStill.getIcon(1, 0);
        }*/
    }

    public void vboRender(Block block, float var2, float height, float ampheight, float magnitude, float angle, float waveTime, int xx, int zz, float var3, float var4, float var5, float var6, float var7)
    {
        int dim = ModLoader.getMinecraftInstance().thePlayer.dimension;
        Tessellator var1 = Tessellator.instance;
        Icon icon = getParticleTextureIndex(dim, block);
        float var12 = 0.5F;
        float var13 = renderX - (float)EntityFX.interpPosX;
        float var14 = renderY - (float)EntityFX.interpPosY;
        float var15 = renderZ - (float)EntityFX.interpPosZ;
        
        double distToCamera = Math.sqrt(var13 * var13 + var14 * var14 + var15 * var15);
        double distAlpha = 1D - ((distToCamera / ConfigWavesMisc.waveRenderRange) * 2D);
        float particleAlpha = 1F;
        
        if (dim == c_CoroWeatherUtil.tropiDimID) {
        	particleAlpha = 0.9F * (float)Math.min(1D, 0.2+ModLoader.getMinecraftInstance().theWorld.getSunBrightness(1F));
        }
        
        particleAlpha = (float) Math.max(0.10D, particleAlpha * distAlpha);
        if (particleAlpha == 0.1D) return;
        
        float br = entRef.getBrightness(var2);
        br = (0.9F + (ModLoader.getMinecraftInstance().gameSettings.gammaSetting * 0.1F)) - (ModLoader.getMinecraftInstance().theWorld.calculateSkylightSubtracted(var2) * 0.03F);

        br *= ModLoader.getMinecraftInstance().theWorld.getSunBrightness(1F);
        
        //wave height darkening
        br = br - (height * 0.5F);

        if (br < 0.3F) br = 0.3F;

        float var16 = 0.35F * br * (2F/* + ModLoader.getMinecraftInstance().gameSettings.gammaSetting*/);//mod_ExtendedRenderer.plBrightness * 0.0000001F;
        
        int var18 = 0;
        if (block != null)
        {
            var18 = block.colorMultiplier(entRef.worldObj, (int)renderX, (int)renderY, (int)renderZ);
        }

        this.red = (float)(var18 >> 16 & 255) / 255.0F;
        this.green = (float)(var18 >> 8 & 255) / 255.0F;
        this.blue = (float)(var18 & 255) / 255.0F;
        
        if (dim == c_CoroWeatherUtil.tropiDimID) {
        	green -= 0.3D;
        }
        
        //particleAlpha = 1F;
        
        //var16 = this.getBrightness(var2) * (1F + ModLoader.getMinecraftInstance().gameSettings.gammaSetting);
        float vecX = (float)Math.sin(angle) * magnitude;
        float vecZ = (float)Math.cos(angle) * magnitude;
        float maxheight = ampheight;
        float adj = 0.5F;
        float adj2 = 1F;
        float adj3 = 1F;
        BlockDataGrid wg = WeatherMod.weatherMan.waterGrid;
        BlockDataPoint bdp;
        //xx -= 0.5F;
        //zz -= 0.5F;
        float height0 = ((float)Math.sin(waveTime - (xx - adj) * vecX - ((zz - adj) * vecZ)) + 1F) * magnitude * maxheight;
        float height1 = ((float)Math.sin(waveTime - (xx - adj) * vecX - ((zz + adj) * vecZ)) + 1F) * magnitude * maxheight;
        float height2 = ((float)Math.sin(waveTime - (xx + adj) * vecX - ((zz + adj) * vecZ)) + 1F) * magnitude * maxheight;
        float height3 = ((float)Math.sin(waveTime - (xx + adj) * vecX - ((zz - adj) * vecZ)) + 1F) * magnitude * maxheight;
        //xx += 1.5F;
        //zz -= 0.5F;
        float yy = renderY - 0.9F;
        boolean d = false;
        /*height0 = (float)Math.sqrt(wg.getPoint((int)(xx-adj2), (int)yy, (int)(zz)).height * wg.getPoint((int)(xx-adj2), (int)yy, (int)(zz)).height + wg.getPoint((int)(xx), (int)yy, (int)(zz-adj2)).height * wg.getPoint((int)(xx), (int)yy, (int)(zz-adj2)).height);
        height1 = (float)Math.sqrt(wg.getPoint((int)(xx-adj2), (int)yy, (int)(zz)).height * wg.getPoint((int)(xx-adj2), (int)yy, (int)(zz)).height + wg.getPoint((int)(xx), (int)yy, (int)(zz+adj2)).height * wg.getPoint((int)(xx), (int)yy, (int)(zz+adj2)).height);
        height2 = (float)Math.sqrt(wg.getPoint((int)(xx+adj2), (int)yy, (int)(zz)).height * wg.getPoint((int)(xx+adj2), (int)yy, (int)(zz)).height + wg.getPoint((int)(xx), (int)yy, (int)(zz+adj2)).height * wg.getPoint((int)(xx), (int)yy, (int)(zz+adj2)).height);
        height3 = (float)Math.sqrt(wg.getPoint((int)(xx+adj2), (int)yy, (int)(zz)).height * wg.getPoint((int)(xx+adj2), (int)yy, (int)(zz)).height + wg.getPoint((int)(xx), (int)yy, (int)(zz-adj2)).height * wg.getPoint((int)(xx), (int)yy, (int)(zz-adj2)).height);*/
        bdp = wg.getPoint((int)(xx), (int)yy, (int)(zz));

        //System.out.println(bdp.height);
        if (wg.getPoint((int)(xx - adj2), (int)yy, (int)(zz)).height == 0 ||
                wg.getPoint((int)(xx), (int)yy, (int)(zz - adj2)).height == 0 ||
                /*wg.getPoint((int)(xx), (int)yy, (int)(zz+adj2)).height == 0 ||*/
                wg.getPoint((int)(xx - adj2), (int)yy, (int)(zz - adj2)).height == 0)
        {
            if (d)
            {
                System.out.print("0 - ");
            }

            height0 = 0;
        }

        if (d)
        {
            System.out.print("xx: " + (xx - adj2) + " - zz: " + (zz - adj2) + " - ");
        }

        //height0 = bdp.height;
        bdp = wg.getPoint((int)(xx - adj2), (int)yy, (int)(zz + adj2));

        if (wg.getPoint((int)(xx - adj2), (int)yy, (int)(zz)).height == 0 || wg.getPoint((int)(xx), (int)yy, (int)(zz + adj2)).height == 0 || wg.getPoint((int)(xx - adj2), (int)yy, (int)(zz + adj2)).height == 0)
        {
            if (d)
            {
                System.out.print("1 - ");
            }

            height1 = 0;
        }

        bdp = WeatherMod.weatherMan.waterGrid.getPoint((int)(xx + adj2), (int)yy, (int)(zz + adj2));

        if (wg.getPoint((int)(xx + adj2), (int)yy, (int)(zz)).height == 0 || wg.getPoint((int)(xx), (int)yy, (int)(zz + adj2)).height == 0 || wg.getPoint((int)(xx + adj2), (int)yy, (int)(zz + adj2)).height == 0)
        {
            if (d)
            {
                System.out.print("2 - ");
            }

            height2 = 0;
        }

        bdp = WeatherMod.weatherMan.waterGrid.getPoint((int)(xx + adj2), (int)yy, (int)(zz - adj2));

        if (wg.getPoint((int)(xx + adj2), (int)yy, (int)(zz)).height == 0 || wg.getPoint((int)(xx), (int)yy, (int)(zz - adj2)).height == 0 || wg.getPoint((int)(xx + adj2), (int)yy, (int)(zz - adj2)).height == 0)
        {
            if (d)
            {
                System.out.print("3 - ");
            }

            height3 = 0;
        }

        if (d)
        {
            System.out.print("| ");
        }

        //height0 = ((float)Math.sin(vecX*vecZ)) * magnitude * maxheight;
        //height0 = ((float)Math.sin(waveTime-xx*vecX-(zz*vecZ)) + 1F) * magnitude * maxheight;

        //height0 = height1 = height2 = height3 = 0F;

        if (ModLoader.getMinecraftInstance().objectMouseOver != null)
        {
            //System.out.println(ModLoader.getMinecraftInstance().objectMouseOver.blockX + " - " + ModLoader.getMinecraftInstance().objectMouseOver.blockZ);
        }

        //var1.setColorOpaque_F(this.red * var16, this.green * var16, this.blue * var16);
        var1.setColorRGBA_F(this.red * var16, this.green * var16, this.blue * var16, Math.max(0F, particleAlpha));
        
        /*var1.addVertexWithUV((double)(var13 - var3 * var12 - var6 * var12), (double)(height0 + var14 - var4 * var12), (double)(var15 - var5 * var12 - var7 * var12), (double)var9, (double)var11);
        var1.addVertexWithUV((double)(var13 - var3 * var12 + var6 * var12), (double)(height1 + var14 + var4 * var12), (double)(var15 - var5 * var12 + var7 * var12), (double)var9, (double)var10);
        var1.addVertexWithUV((double)(var13 + var3 * var12 + var6 * var12), (double)(height2 + var14 + var4 * var12), (double)(var15 + var5 * var12 + var7 * var12), (double)var8, (double)var10);
        var1.addVertexWithUV((double)(var13 + var3 * var12 - var6 * var12), (double)(height3 + var14 - var4 * var12), (double)(var15 + var5 * var12 - var7 * var12), (double)var8, (double)var11);*/
        float f3 = icon.getMinU();
        float f4 = icon.getMaxU();
        float f5 = icon.getMinV();
        float f6 = icon.getMaxV();
        var1.addVertexWithUV((double)(var13 - var3 * var12 - var6 * var12), (double)(height0 + var14 - var4 * var12), (double)(var15 - var5 * var12 - var7 * var12), (double)f3, (double)f6);
        var1.addVertexWithUV((double)(var13 - var3 * var12 + var6 * var12), (double)(height1 + var14 + var4 * var12), (double)(var15 - var5 * var12 + var7 * var12), (double)f4, (double)f6);
        var1.addVertexWithUV((double)(var13 + var3 * var12 + var6 * var12), (double)(height2 + var14 + var4 * var12), (double)(var15 + var5 * var12 + var7 * var12), (double)f4, (double)f5);
        var1.addVertexWithUV((double)(var13 + var3 * var12 - var6 * var12), (double)(height3 + var14 - var4 * var12), (double)(var15 + var5 * var12 - var7 * var12), (double)f3, (double)f5);
        /*}
        }
        }*/
    }

    
}
