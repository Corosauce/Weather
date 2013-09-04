package weather.entities.particles;

import java.awt.Color;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import extendedrenderer.particle.entity.EntityRotFX;
@SideOnly(Side.CLIENT)
public class EntityWindFX extends EntityRotFX
{
    public int age;
    public float brightness;

    public EntityWindFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, int var16)
    {
        super(var1, var2, var4, var6, var8, var10, var12);
        this.motionX = var8 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.05F);
        this.motionY = var10 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.05F);
        this.motionZ = var12 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.05F);
        Color var17 = null;

        if (var16 == 0)
        {
            this.particleRed = this.particleGreen = this.particleBlue = this.rand.nextFloat() * 0.3F;
        }
        else if (var16 == 1)
        {
            var17 = new Color(7951674);
        }
        else if (var16 == 2)
        {
            var17 = new Color(14077848);
        }
        else if (var16 == 3)
        {
            var17 = new Color(10973);
        }
        else if (var16 == 4)
        {
            var17 = new Color(15663103);
        }
        else if (var16 == 5)
        {
            var17 = new Color(7951674);
        }

        this.brightness = 2.0F;

        if (var17 != null && var16 != 0)
        {
            this.particleRed = (float)var17.getRed() / 255.0F;
            this.particleGreen = (float)var17.getGreen() / 255.0F;
            this.particleBlue = (float)var17.getBlue() / 255.0F;
        }

        this.particleScale = this.rand.nextFloat() * this.rand.nextFloat() * 6.0F + 6.0F;
        this.particleMaxAge = 18;
        this.particleMaxAge = (int)((double)((float)this.particleMaxAge) * var14);
        this.particleGravity = 0.1F;
        //this.particleScale = 5.0F;
    }

    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        float f6 = ((float)this.particleAge + par2) / (float)this.particleMaxAge * 32.0F;

        if (f6 < 0.0F)
        {
            f6 = 0.0F;
        }

        if (f6 > 1.0F)
        {
            f6 = 1.0F;
        }

        //this.particleScale = f6;
        super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
        }

        this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
        this.motionY += 0.004D;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        if (this.posY == this.prevPosY)
        {
            this.motionX *= 1.1D;
            this.motionZ *= 1.1D;
        }

        this.motionX *= 0.9999999785423279D;
        this.motionY *= 0.9599999785423279D;
        this.motionZ *= 0.9999999785423279D;

        if (this.onGround)
        {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }
    
    public int getFXLayer()
    {
        return 0;
    }
}
