package weather.waves;

import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityFX;
import net.minecraft.src.EntityRenderer;
import net.minecraft.src.Material;
import net.minecraft.src.ModLoader;
import net.minecraft.src.Render;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.RenderManager;
import net.minecraft.src.Tessellator;
import net.minecraft.src.World;
import net.minecraft.src.c_CoroWeatherUtil;

import org.lwjgl.opengl.GL11;

import weather.WeatherMod;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.Side;
@SideOnly(Side.CLIENT)
public class WaveRenderer extends Render
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
        this.shadowSize = 0.5F;
        this.rb = new RenderBlocks();
        entRef = ModLoader.getMinecraftInstance().thePlayer;
    }

    public int getParticleTextureIndex(int dim, Block block)
    {
    	
    	return block.getBlockTextureFromSideAndMetadata(1, 0);
    	
        /*if (dim == 0) {
        	return Block.waterStill.getBlockTextureFromSideAndMetadata(1, 0);
        } else if (dim == 127) {
        	return TropicraftMod.waterStillTropics.getBlockTextureFromSideAndMetadata(1, 0);
        } else {
        	return Block.waterStill.getBlockTextureFromSideAndMetadata(1, 0);
        }*/
    }

    public void vboRender(Block block, float var2, float height, float ampheight, float magnitude, float angle, float waveTime, int xx, int zz, float var3, float var4, float var5, float var6, float var7)
    {
        //this.rotationYaw = ModLoader.getMinecraftInstance().thePlayer.rotationYaw;
        //this.rotationPitch += this.rand.nextInt(4) - 2;//ModLoader.getMinecraftInstance().thePlayer.rotationPitch;
        int dim = ModLoader.getMinecraftInstance().thePlayer.dimension;
        Tessellator var1 = Tessellator.instance;
        float var8 = (float)(getParticleTextureIndex(dim, block) % 16) / 16.0F;
        float var9 = var8 + 0.0624375F;
        float var10 = (float)(getParticleTextureIndex(dim, block) / 16) / 16.0F;
        float var11 = var10 + 0.0624375F;
        float var12 = 0.5F;// * this.particleScale;
        float var13 = renderX - (float)EntityFX.interpPosX;//(float)(this.prevPosX + (this.posX - this.prevPosX) * (double)var2 - interpPosX);
        float var14 = renderY - (float)EntityFX.interpPosY;//(float)(this.prevPosY + (this.posY - this.prevPosY) * (double)var2 - interpPosY) + 0.0F;
        float var15 = renderZ - (float)EntityFX.interpPosZ;//(float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)var2 - interpPosZ);
        //var13 += i;//rand.nextInt(6)-3;
        //var14 += j;
        //var15 += k;
        //System.out.println(height);
        //float var16 = /*this.getEntityBrightness(var2) * */brightness;
        float br = entRef.getBrightness(var2);
        br = (ModLoader.getMinecraftInstance().gameSettings.gammaSetting) - (ModLoader.getMinecraftInstance().theWorld.calculateSkylightSubtracted(var2) * 0.03F);

        //if (ModLoader.getMinecraftInstance().theWorld.isDaytime()/*ModLoader.getMinecraftInstance().gameSettings.gammaSetting == 1F*/) {
        if (dim == 127)
        {
            br = br - (height * 1.5F);
        }
        else
        {
            br = br - (height * 0.5F);
        }

        if (br < 0.4F)
        {
            br = 0.4F;
        }

        float var16 = 0.35F * br * (1F + ModLoader.getMinecraftInstance().gameSettings.gammaSetting);//mod_ExtendedRenderer.plBrightness * 0.0000001F;
        //System.out.println(br);
        int var18 = 0;//Block.waterStill.colorMultiplier(entRef.worldObj, (int)renderX, (int)renderY, (int)renderZ);
        //var18 = 16781375;
        
        if (block != null)
        {
            //var18 = LAPI.getColorValue(TropicraftMod.instance.tropicwaterID);
            var18 = block.colorMultiplier(entRef.worldObj, (int)renderX, (int)renderY, (int)renderZ);
        }

        this.red = (float)(var18 >> 16 & 255) / 255.0F;
        this.green = (float)(var18 >> 8 & 255) / 255.0F;
        this.blue = (float)(var18 & 255) / 255.0F;
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

        var1.setColorOpaque_F(this.red * var16, this.green * var16, this.blue * var16);
        var1.addVertexWithUV((double)(var13 - var3 * var12 - var6 * var12), (double)(height0 + var14 - var4 * var12), (double)(var15 - var5 * var12 - var7 * var12), (double)var9, (double)var11);
        var1.addVertexWithUV((double)(var13 - var3 * var12 + var6 * var12), (double)(height1 + var14 + var4 * var12), (double)(var15 - var5 * var12 + var7 * var12), (double)var9, (double)var10);
        var1.addVertexWithUV((double)(var13 + var3 * var12 + var6 * var12), (double)(height2 + var14 + var4 * var12), (double)(var15 + var5 * var12 + var7 * var12), (double)var8, (double)var10);
        var1.addVertexWithUV((double)(var13 + var3 * var12 - var6 * var12), (double)(height3 + var14 - var4 * var12), (double)(var15 + var5 * var12 - var7 * var12), (double)var8, (double)var11);
        /*}
        }
        }*/
    }

    public void tryRender(int x, int y, int z, float var2)
    {
        Entity var1 = ModLoader.getMinecraftInstance().thePlayer;
        double var3 = renderX;
        double var5 = renderY;
        double var7 = renderZ;
        /*double var3 = var1.lastTickPosX + renderX + (var1.posX - var1.lastTickPosX) * (double)var2;
        double var5 = var1.lastTickPosY + renderY + (var1.posY - var1.lastTickPosY) * (double)var2;
        double var7 = var1.lastTickPosZ + renderZ + (var1.posZ - var1.lastTickPosZ) * (double)var2;*/
        float var9 = var1.prevRotationYaw + (var1.rotationYaw - var1.prevRotationYaw) * var2;
        //'caching'
        int var10 = 1;//var1.getEntityBrightnessForRender(var2);
        int var11 = var10 % 65536;
        int var12 = var10 / 65536;
        //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapEnabled, (float)var11 / 1.0F, (float)var12 / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.doRender(var3 - RenderManager.renderPosX, var5 - RenderManager.renderPosY, var7 - RenderManager.renderPosZ, var9, var2);
    }

    public void preRender(double var2, double var4, double var6, float var8, float var9)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)var2, (float)var4, (float)var6);
        this.loadTexture("/terrain.png");
        Block var10 = Block.waterStill;
        World var11 = ModLoader.getMinecraftInstance().theWorld;
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        float vecX = 0F;
        float vecY = 0F;
        float vecZ = 0F;
        int meta = 0;
        GL11.glRotatef((float)(vecX * 180.0D / 12.566370964050293D - 0.0D), 1.0F, 0.0F, 0.0F);
        GL11.glRotatef((float)(vecY * 180.0D / (Math.PI * 2D) - 0.0D), 0.0F, 1.0F, 0.0F);
        GL11.glRotatef((float)(vecZ * 180.0D / (Math.PI * 2D) - 0.0D), 0.0F, 0.0F, 1.0F);
        this.renderFallingCube(var10, var11, renderX, renderY, renderZ, meta);

        try
        {
            //RenderBlocks rb = (RenderBlocks)ModLoader.getPrivateValue(RenderGlobal.class, WeatherMod.mc.renderGlobal, "globalRenderBlocks");
            //if (rb.blockAccess != null) {
            //rb.renderBlockFluids(var10, (int)renderX, (int)70, (int)renderZ);
            //}
        }
        catch (Exception ex) { }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    public void renderFallingCube(Block var2, World var3, float var4, float var5, float var6, int var7)
    {
        float var8 = 0.5F;
        float var9 = 1.0F;
        float var10 = 0.8F;
        float var11 = 0.6F;
        Tessellator var12 = Tessellator.instance;
        var12.startDrawingQuads();
        float var13;// = var2.getBlockBrightness(var3, (int)var4, (int)var5, (int)var6);
        float var14;//= var2.getBlockBrightness(var3, (int)var4, (int)var5 - 1, (int)var6);
        //var12.setBrightness(999+var2.getMixedBrightnessForBlock(var3, (int)var4, (int)var5, (int)var6));
        //var12.setBrightness(999);
        var13 = 0.7F;
        var14 = 0.7F;
        float uh = var2.getMixedBrightnessForBlock(var3, (int)var4, (int)var5 + 20, (int)var6);
        //System.out.println(uh);
        //var14 = uh * 1.5F;
        float var15 = 1.0F;
        float var16 = 1.0F;
        float var17 = 1.0F;

        if (var2.blockMaterial == Material.water)
        {
            int var18 = var2.colorMultiplier(var3, (int)var4, (int)var5, (int)var6);
            var15 = (float)(var18 >> 16 & 255) / 255.0F;
            var16 = (float)(var18 >> 8 & 255) / 255.0F;
            var17 = (float)(var18 & 255) / 255.0F;
        }

        if (var2.blockID == Block.leaves.blockID)
        {
            int var18 = 0xFFFFFF;//var2.colorMultiplier(var3, (int)var1.posX, (int)var1.posY, (int)var1.posZ);
            var15 = (float)(var18 >> 16 & 255) / 255.0F;
            var16 = (float)(var18 >> 8 & 255) / 255.0F;
            var17 = (float)(var18 & 255) / 255.0F;

            if (EntityRenderer.anaglyphEnable)
            {
                float var19 = (var15 * 30.0F + var16 * 59.0F + var17 * 11.0F) / 100.0F;
                float var20 = (var15 * 30.0F + var16 * 70.0F) / 100.0F;
                float var21 = (var15 * 30.0F + var17 * 70.0F) / 100.0F;
                var15 = var19;
                var16 = var20;
                var17 = var21;
            }
        }

        if (var14 < var13)
        {
            var14 = var13;
        }

        //var12.setColorOpaque_F(var15 * var8 * var14, var16 * var8 * var14, var17 * var8 * var14);

        /**/
        //this.rb.renderBottomFace(var2, -0.5D, -0.5D, -0.5D, var2.getBlockTextureFromSideAndMetadata(0, var7));

        if (var14 < var13)
        {
            var14 = var13;
        }

        var12.setColorOpaque_F(var15 * var9 * var14, var16 * var9 * var14, var17 * var9 * var14);
        renderTopFace(var2, -0.5D, -0.5D, -0.5D, var2.getBlockTextureFromSideAndMetadata(1, var7));

        //var12.setColorOpaque_F(var15 * var10 * var14, var16 * var10 * var14, var17 * var10 * var14);
        //this.rb.renderEastFace(var2, -0.5D, -0.5D, -0.5D, var2.getBlockTextureFromSideAndMetadata(2, var7));

        if (var14 < var13)
        {
            var14 = var13;
        }

        //var12.setColorOpaque_F(var15 * var10 * var14, var16 * var10 * var14, var17 * var10 * var14);
        //this.rb.renderWestFace(var2, -0.5D, -0.5D, -0.5D, var2.getBlockTextureFromSideAndMetadata(3, var7));

        if (var14 < var13)
        {
            var14 = var13;
        }

        //var12.setColorOpaque_F(var15 * var11 * var14, var16 * var11 * var14, var17 * var11 * var14);
        //this.rb.renderNorthFace(var2, -0.5D, -0.5D, -0.5D, var2.getBlockTextureFromSideAndMetadata(4, var7));

        if (var14 < var13)
        {
            var14 = var13;
        }

        //var12.setColorOpaque_F(var15 * var11 * var14, var16 * var11 * var14, var17 * var11 * var14);
        //this.rb.renderSouthFace(var2, -0.5D, -0.5D, -0.5D, var2.getBlockTextureFromSideAndMetadata(5, var7));
        var12.draw();
    }

    public void doRender(double var2, double var4, double var6, float var8, float var9)
    {
        this.preRender(var2, var4, var6, var8, var9);
    }

    public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9)
    {
        this.preRender(var2, var4, var6, var8, var9);
    }

    public void renderTopFace(Block par1Block, double par2, double par4, double par6, int par8)
    {
        Tessellator var9 = Tessellator.instance;
        /*if (this.overrideBlockTexture >= 0)
        {
            par8 = this.overrideBlockTexture;
        }*/
        int var10 = (par8 & 15) << 4;
        int var11 = par8 & 240;
        double var12 = ((double)var10 + par1Block.func_83009_v() * 16.0D) / 256.0D;
        double var14 = ((double)var10 + par1Block.func_83007_w() * 16.0D - 0.01D) / 256.0D;
        double var16 = ((double)var11 + par1Block.func_83005_z() * 16.0D) / 256.0D;
        double var18 = ((double)var11 + par1Block.func_83006_A() * 16.0D - 0.01D) / 256.0D;

        if (par1Block.func_83009_v() < 0.0D || par1Block.func_83007_w() > 1.0D)
        {
            var12 = (double)(((float)var10 + 0.0F) / 256.0F);
            var14 = (double)(((float)var10 + 15.99F) / 256.0F);
        }

        if (par1Block.func_83005_z() < 0.0D || par1Block.func_83006_A() > 1.0D)
        {
            var16 = (double)(((float)var11 + 0.0F) / 256.0F);
            var18 = (double)(((float)var11 + 15.99F) / 256.0F);
        }

        double var20 = var14;
        double var22 = var12;
        double var24 = var16;
        double var26 = var18;
        double var28 = par2 + par1Block.func_83009_v();
        double var30 = par2 + par1Block.func_83007_w();
        double var32 = par4 + par1Block.func_83010_y();
        double var34 = par6 + par1Block.func_83005_z();
        double var36 = par6 + par1Block.func_83006_A();
        //Weather code change
        /*c_w_BlockWaveHelper.calcSide(par1Block);
        var30 += c_w_BlockWaveHelper.x;
        var32 += c_w_BlockWaveHelper.y;
        var36 += c_w_BlockWaveHelper.z;*/
        var9.addVertexWithUV(var30, var32, var36, var14, var18);
        var9.addVertexWithUV(var30, var32, var34, var20, var24);
        var9.addVertexWithUV(var28, var32, var34, var12, var16);
        var9.addVertexWithUV(var28, var32, var36, var22, var26);
    }
}
