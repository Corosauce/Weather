package weather.worldObjects;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class RenderWorm extends Render
{
    public static final boolean renderLine = true;
    public static final boolean renderBobber = false;
    public static float yoffset = 0.4F;
    public static float caughtOffset = 0.8F;
    public static int stringColor = 8947848;

    public void doRenderNode(Entity var1, double var2, double var4, double var6, float var8, float var9)
    {
        if (((EntWorm)var1).type == 1)
        {
            ;
        }
    }

    public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9)
    {
        this.shadowSize = 1.0F;
        GL11.glPushMatrix();
        this.doRenderNode(var1, var2, var4, var6, var8, var9);
        this.shadowSize = 0.0F;
        GL11.glPopMatrix();
    }
    
    private World b()
    {
        return this.renderManager.worldObj;
    }

    public static void renderOffsetAABB(AxisAlignedBB var0, double var1, double var3, double var5, boolean var7)
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Tessellator var8 = Tessellator.instance;

        if (var7)
        {
            GL11.glColor4f(0.0F, 0.0F, 1.0F, 1.0F);
        }
        else
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }

        var8.startDrawingQuads();
        var8.setTranslation(var1, var3, var5);
        var8.setNormal(0.0F, 0.0F, -1.0F);
        var8.addVertex(var0.minX, var0.maxY, var0.minZ);
        var8.addVertex(var0.maxX, var0.maxY, var0.minZ);
        var8.addVertex(var0.maxX, var0.minY, var0.minZ);
        var8.addVertex(var0.minX, var0.minY, var0.minZ);
        var8.setNormal(0.0F, 0.0F, 1.0F);
        var8.addVertex(var0.minX, var0.minY, var0.maxZ);
        var8.addVertex(var0.maxX, var0.minY, var0.maxZ);
        var8.addVertex(var0.maxX, var0.maxY, var0.maxZ);
        var8.addVertex(var0.minX, var0.maxY, var0.maxZ);
        var8.setNormal(0.0F, -1.0F, 0.0F);
        var8.addVertex(var0.minX, var0.minY, var0.minZ);
        var8.addVertex(var0.maxX, var0.minY, var0.minZ);
        var8.addVertex(var0.maxX, var0.minY, var0.maxZ);
        var8.addVertex(var0.minX, var0.minY, var0.maxZ);
        var8.setNormal(0.0F, 1.0F, 0.0F);
        var8.addVertex(var0.minX, var0.maxY, var0.maxZ);
        var8.addVertex(var0.maxX, var0.maxY, var0.maxZ);
        var8.addVertex(var0.maxX, var0.maxY, var0.minZ);
        var8.addVertex(var0.minX, var0.maxY, var0.minZ);
        var8.setNormal(-1.0F, 0.0F, 0.0F);
        var8.addVertex(var0.minX, var0.minY, var0.maxZ);
        var8.addVertex(var0.minX, var0.maxY, var0.maxZ);
        var8.addVertex(var0.minX, var0.maxY, var0.minZ);
        var8.addVertex(var0.minX, var0.minY, var0.minZ);
        var8.setNormal(1.0F, 0.0F, 0.0F);
        var8.addVertex(var0.maxX, var0.minY, var0.minZ);
        var8.addVertex(var0.maxX, var0.maxY, var0.minZ);
        var8.addVertex(var0.maxX, var0.maxY, var0.maxZ);
        var8.addVertex(var0.maxX, var0.minY, var0.maxZ);
        var8.setTranslation(0.0D, 0.0D, 0.0D);
        var8.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
}
