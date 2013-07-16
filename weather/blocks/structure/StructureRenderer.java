package weather.blocks.structure;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class StructureRenderer extends Render
{
    private RenderBlocks a;

    public StructureRenderer()
    {
        this.shadowSize = 0.5F;
        this.a = new RenderBlocks();
    }

    public void a(Structure var1, double var2, double var4, double var6, float var8, float var9)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)var2, (float)var4, (float)var6);
        GL11.glScalef(var1.sizeScale, var1.sizeScale, var1.sizeScale);
        this.loadTexture("/terrain.png");
        this.loadTexture("/tropicalmod/tropiterrain.png");
        
        //
        
        World var11 = var1.worldObj;
        GL11.glDisable(GL11.GL_LIGHTING);
        
        //var1.vecX = var1.vecY = var1.vecZ = var1.vecX + 0.1F;
        
        //var1.vecY += var1.worldObj.rand.nextFloat() * 0.05F;
        
        //GL11.glRotatef((float)((var1.rotationPitch) * 180.0D / 12.566370964050293D - 0.0D), 1.0F, 0.0F, 0.0F);
        //GL11.glRotatef((float)(var1.vecY * 180.0D / (Math.PI * 2D) - 0.0D), 0.0F, 1.0F, 0.0F);
        GL11.glRotatef((float)(var1.rotationYaw * 180.0D / (Math.PI * 2D) - 0.0D), 0.0F, 1.0F, 0.0F);
        
        boolean render = true;
        
        if (var1.state == 1) render = true;
        
        if (render) {
	        Block var10;
	        for (int i = 0; i < var1.pieces.size(); i++) {
	        	StructurePiece sp = var1.pieces.get(i);
	        	
	        	GL11.glRotatef((float)(sp.vecX * 180.0D / 12.566370964050293D - 0.0D), 1.0F, 0.0F, 0.0F);
	            GL11.glRotatef((float)(sp.vecY * 180.0D / (Math.PI * 2D) - 0.0D), 0.0F, 1.0F, 0.0F);
	            GL11.glRotatef((float)(sp.vecZ * 180.0D / (Math.PI * 2D) - 0.0D), 0.0F, 0.0F, 1.0F);
	        	
	            GL11.glTranslatef((float)sp.relX, (float)sp.relY, (float)sp.relZ);
	            
	        	for (int j = 0; j < sp.nodes.size(); j++) {
		        	StructureNode sn = sp.nodes.get(j);
		        	
		        	if (var1.state == 1/* || sn.render*/) {
			        	var10 = Block.blocksList[sn.id];
			        	if (var1.state == 0) var10 = Block.blocksList[18]; 
			        	GL11.glTranslatef((float)sn.relX, (float)sn.relY, (float)sn.relZ);
			        	/*GL11.glRotatef((float)(sn.vecX * 180.0D / 12.566370964050293D - 0.0D), 1.0F, 0.0F, 0.0F);
			            GL11.glRotatef((float)(sp.vecY * 180.0D / (Math.PI * 2D) - 0.0D), 0.0F, 1.0F, 0.0F);
			            GL11.glRotatef((float)(sp.vecZ * 180.0D / (Math.PI * 2D) - 0.0D), 0.0F, 0.0F, 1.0F);*/
			        	this.renderFallingCube(var1, var10, var11, MathHelper.floor_double(var1.posX), MathHelper.floor_double(var1.posY), MathHelper.floor_double(var1.posZ), sn.meta);
			        	GL11.glTranslatef((float)-sn.relX, (float)-sn.relY, (float)-sn.relZ);
		        	}
	        	}
	        	
	        	GL11.glTranslatef((float)-sp.relX, (float)-sp.relY, (float)-sp.relZ);
	        	
	        	GL11.glRotatef((float)(-sp.vecX * 180.0D / 12.566370964050293D - 0.0D), 1.0F, 0.0F, 0.0F);
	            GL11.glRotatef((float)(-sp.vecY * 180.0D / (Math.PI * 2D) - 0.0D), 0.0F, 1.0F, 0.0F);
	            GL11.glRotatef((float)(-sp.vecZ * 180.0D / (Math.PI * 2D) - 0.0D), 0.0F, 0.0F, 1.0F);
	        }
        }
        
        GL11.glRotatef((float)-(var1.rotationYaw * 180.0D / (Math.PI * 2D) - 0.0D), 0.0F, 0.0F, 1.0F);
        
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    public void renderFallingCube(Structure var1, Block var2, World var3, int var4, int var5, int var6, int var7)
    {
        float var8 = 0.5F;
        float var9 = 1.0F;
        float var10 = 0.8F;
        float var11 = 0.6F;
        Tessellator var12 = Tessellator.instance;
        var12.startDrawingQuads();
        float var13 = var2.getBlockBrightness(var3, var4, var5, var6);
        float var14 = var2.getBlockBrightness(var3, var4, var5 - 1, var6);
        //var12.setBrightness(var2.getMixedBrightnessForBlock(var3, var4, var5, var6));

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
        this.a((Structure)var1, var2, var4, var6, var8, var9);
    }
}
