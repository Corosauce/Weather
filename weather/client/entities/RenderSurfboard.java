package weather.client.entities;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import weather.WeatherMod;
import weather.entities.EntitySurfboard;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSurfboard extends Render
{
    /** instance of ModelBoat for rendering */
    protected ModelSurfboard modelBoat;

    public RenderSurfboard()
    {
        this.shadowSize = 0.5F;
        this.modelBoat = new ModelSurfboard();
    }

	@Override

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(Entity entity) {
		return new ResourceLocation(WeatherMod.modID + ":textures/entities/SurfBoard.png");
	}

    /**
     * The render method used in RenderBoat that renders the boat model.
     */
    public void renderBoat(EntitySurfboard par1EntityBoat, double par2, double par4, double par6, float par8, float par9)
    {
    	this.bindEntityTexture(par1EntityBoat);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        GL11.glRotatef(270.0F - par8, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.25F, 0.15F, 0);
        float var10 = (float)par1EntityBoat.getTimeSinceHit() - par9;
        float var11 = (float)par1EntityBoat.getDamageTaken() - par9;

        if (var11 < 0.0F)
        {
            var11 = 0.0F;
        }

        if (var10 > 0.0F)
        {
            GL11.glRotatef(MathHelper.sin(var10) * var10 * var11 / 10.0F * (float)par1EntityBoat.getForwardDirection(), 1.0F, 0.0F, 0.0F);
        }

        float var12 = 0.75F;
        GL11.glScalef(var12, var12, var12);
        GL11.glScalef(1.0F / var12, 1.0F / var12, 1.0F / var12);
        //this.loadTexture("/coro/weather/SurfBoard.png");
        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        this.modelBoat.render(par1EntityBoat, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderBoat((EntitySurfboard)par1Entity, par2, par4, par6, par8, par9);
    }
}
