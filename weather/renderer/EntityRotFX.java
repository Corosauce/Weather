package weather.renderer;

import java.awt.Color;

import weather.ExtendedRenderer;

import net.minecraft.src.EntityFX;
import net.minecraft.src.World;

public class EntityRotFX extends EntityFX
{
    public boolean weatherEffect = false;

    public float spawnY = 130F;

    public EntityRotFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12)
    {
        super(par1World, par2, par4, par6, par8, par10, par12);
    }

    public EntityRotFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, int colorIndex)
    {
        super(var1, var2, var4, var6, var8, var10, var12);
    }

    public EntityRotFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, int texIDs[])
    {
        super(var1, var2, var4, var6, var8, var10, var12);
    }

    public void spawnAsWeatherEffect()
    {
        weatherEffect = true;
        ExtendedRenderer.rotEffRenderer.addEffect(this);
        this.worldObj.addWeatherEffect(this);
    }

    public int getAge()
    {
        return particleAge;
    }

    public void setAge(int age)
    {
        particleAge = age;
    }

    public int getMaxAge()
    {
        return particleMaxAge;
    }

    public void setSize(float par1, float par2)
    {
        super.setSize(par1, par2);
    }
    
    public void setGravity(float par) {
    	particleGravity = par;
    }
}
