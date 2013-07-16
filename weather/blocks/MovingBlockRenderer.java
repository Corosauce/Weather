package weather.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class MovingBlockRenderer extends Render
{
    private RenderBlocks a;

    public MovingBlockRenderer()
    {
        this.shadowSize = 0.5F;
        this.a = new RenderBlocks();
    }

    public void a(MovingBlock var1, double var2, double var4, double var6, float var8, float var9)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)var2, (float)var4, (float)var6);
        this.loadTexture("/terrain.png");
        Block var10 = Block.blocksList[var1.tile];
        World var11 = var1.worldObj;
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glRotatef((float)(var1.vecX * 180.0D / 12.566370964050293D - 0.0D), 1.0F, 0.0F, 0.0F);
        GL11.glRotatef((float)(var1.vecY * 180.0D / (Math.PI * 2D) - 0.0D), 0.0F, 1.0F, 0.0F);
        GL11.glRotatef((float)(var1.vecZ * 180.0D / (Math.PI * 2D) - 0.0D), 0.0F, 0.0F, 1.0F);
        this.renderFallingCube(var1, var10, var11, MathHelper.floor_double(var1.posX), MathHelper.floor_double(var1.posY), MathHelper.floor_double(var1.posZ), var1.metadata);
        
        //renderAABB(var1.boundingBox);
        
        //passSpecialRender(var1, var2, var4, var6);
        //passSpecialRender(var1, 0, 0, 0);
        
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    public void renderFallingCube(MovingBlock var1, Block var2, World var3, int var4, int var5, int var6, int var7)
    {
        float var8 = 0.5F;
        float var9 = 1.0F;
        float var10 = 0.8F;
        float var11 = 0.6F;
        Tessellator var12 = Tessellator.instance;
        var12.startDrawingQuads();
        float var13 = var2.getBlockBrightness(var3, var4, var5, var6);
        float var14 = var2.getBlockBrightness(var3, var4, var5 - 1, var6);
        var12.setBrightness(var2.getMixedBrightnessForBlock(var3, var4, var5, var6));

        if (var14 < var13)
        {
            ;
        }

        var13 = 1.0F;
        var14 = 1.0F;
        float var15 = 1.0F;
        float var16 = 1.0F;
        float var17 = 1.0F;

        if (var2.blockID == Block.leaves.blockID)
        {
            int var18 = var2.colorMultiplier(var3, (int)var1.posX, (int)var1.posY, (int)var1.posZ);
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
        
        //NEW! - set block render size
        a.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
        //a.setRenderMinMax(0.25D, 0.25D, 0.25D, 0.75D, 0.75D, 0.75D);

        var12.setColorOpaque_F(var15 * var8 * var14, var16 * var8 * var14, var17 * var8 * var14);
        this.a.renderFaceYNeg(var2, -0.5D, -0.5D, -0.5D, var2.getIcon(0, var7));

        if (var14 < var13)
        {
            var14 = var13;
        }

        var12.setColorOpaque_F(var15 * var9 * var14, var16 * var9 * var14, var17 * var9 * var14);
        this.a.renderFaceYPos(var2, -0.5D, -0.5D, -0.5D, var2.getIcon(1, var7));

        if (var14 < var13)
        {
            var14 = var13;
        }

        var12.setColorOpaque_F(var15 * var10 * var14, var16 * var10 * var14, var17 * var10 * var14);
        this.a.renderFaceZNeg(var2, -0.5D, -0.5D, -0.5D, var2.getIcon(2, var7));

        if (var14 < var13)
        {
            var14 = var13;
        }

        var12.setColorOpaque_F(var15 * var10 * var14, var16 * var10 * var14, var17 * var10 * var14);
        this.a.renderFaceZPos(var2, -0.5D, -0.5D, -0.5D, var2.getIcon(3, var7));

        if (var14 < var13)
        {
            var14 = var13;
        }

        var12.setColorOpaque_F(var15 * var11 * var14, var16 * var11 * var14, var17 * var11 * var14);
        this.a.renderFaceXNeg(var2, -0.5D, -0.5D, -0.5D, var2.getIcon(4, var7));

        if (var14 < var13)
        {
            var14 = var13;
        }

        var12.setColorOpaque_F(var15 * var11 * var14, var16 * var11 * var14, var17 * var11 * var14);
        this.a.renderFaceXPos(var2, -0.5D, -0.5D, -0.5D, var2.getIcon(5, var7));
        var12.draw();
    }

    public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9)
    {
        this.a((MovingBlock)var1, var2, var4, var6, var8, var9);
        
    }
    
    protected void passSpecialRender(Entity entityliving, double d, double d1, double d2)
	{
		renderName(entityliving, d, d1, d2);
		//c_CoroAIUtil.renderJobs((c_EnhAI)entityliving);
	}


	protected void renderName(Entity entitykoa, double d, double d1, double d2)
	{
		//	System.out.println("counter: " + counter);

		if(Minecraft.isGuiEnabled() && entitykoa != renderManager.livingPlayer)
		{
			float f = 1.6F;
			float f1 = 0.01666667F * f;
			float f2 = entitykoa.getDistanceToEntity(renderManager.livingPlayer);
			float f3 = entitykoa.isSneaking() ? 32F : 64F;
			String s = "sdfsdfsfsdf";
			if(f2 < f3)
			{
				

				if(!entitykoa.isSneaking())
				{
					renderLivingLabel(entitykoa, s, d, d1, d2, 64);
				
				} else
				{
					FontRenderer fontrenderer = getFontRendererFromRenderManager();
					GL11.glPushMatrix();
					GL11.glTranslatef((float)d + 0.0F, (float)d1 + 2.3F, (float)d2);
					GL11.glNormal3f(0.0F, 1.0F, 0.0F);
					GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
					GL11.glScalef(-f1, -f1, f1);
					GL11.glDisable(2896 /*GL_LIGHTING*/);
					GL11.glTranslatef(0.0F, 0.25F / f1, 0.0F);
					GL11.glDepthMask(false);
					GL11.glEnable(3042 /*GL_BLEND*/);
					GL11.glBlendFunc(770, 771);
					Tessellator tessellator = Tessellator.instance;
					GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
					tessellator.startDrawingQuads();
					int i = fontrenderer.getStringWidth(s) / 2;
					tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
					tessellator.addVertex(-i - 1, -1D, 0.0D);
					tessellator.addVertex(-i - 1, 8D, 0.0D);
					tessellator.addVertex(i + 1, 8D, 0.0D);
					tessellator.addVertex(i + 1, -1D, 0.0D);
					tessellator.draw();
					GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
					GL11.glDepthMask(true);
					fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, 0, 0x20ffffff);
					GL11.glEnable(2896 /*GL_LIGHTING*/);
					GL11.glDisable(3042 /*GL_BLEND*/);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					GL11.glPopMatrix();
				}
			}
		}
	}
	
	protected void renderLivingLabel(Entity entityliving, String s, double d, double d1, double d2, int i)
	{
		float f = entityliving.getDistanceToEntity(renderManager.livingPlayer);
		if(f > (float)i)
		{
			return;
		}
		FontRenderer fontrenderer = getFontRendererFromRenderManager();
		float f1 = 1.6F;
		float f2 = 0.01666667F * f1;
		GL11.glPushMatrix();
		GL11.glTranslatef((float)d + 0.0F, (float)d1 + 2.3F, (float)d2);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(-f2, -f2, f2);
		GL11.glDisable(2896 /*GL_LIGHTING*/);
		GL11.glDepthMask(false);
		GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
		GL11.glEnable(3042 /*GL_BLEND*/);
		GL11.glBlendFunc(770, 771);
		Tessellator tessellator = Tessellator.instance;
		byte byte0 = 0;
		if(s != null && s.equals("deadmau5"))
		{
			byte0 = -10;
		}
		GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
		tessellator.startDrawingQuads();
		int j = fontrenderer.getStringWidth(s) / 2;
		tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
		tessellator.addVertex(-j - 1, -1 + byte0, 0.0D);
		tessellator.addVertex(-j - 1, 8 + byte0, 0.0D);
		tessellator.addVertex(j + 1, 8 + byte0, 0.0D);
		tessellator.addVertex(j + 1, -1 + byte0, 0.0D);
		tessellator.draw();
		GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
		fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, byte0, 0x20ffffff);
		GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
		GL11.glDepthMask(true);
		fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, byte0, -1);
		GL11.glEnable(2896 /*GL_LIGHTING*/);
		GL11.glDisable(3042 /*GL_BLEND*/);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}
}
