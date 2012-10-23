package weather.worldObjects;

import weather.WeatherMod;
import net.minecraft.src.Entity;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.Side;
//@SideOnly(Side.CLIENT)
public class EntWorldData extends Entity
{
    public EntWorldData(World var1)
    {
        super(var1);
        this.setSize(1.0F, 1.0F);
    }

    public void setEntityDead()
    {
        super.setDead();
    }

    protected boolean canTriggerWalking()
    {
        return false;
    }

    public boolean isInRangeToRenderDist(double var1)
    {
        return true;
    }

    public void entityInit()
    {
        WeatherMod.stormMan.setStage(0);
        WeatherMod.stormMan.resetStorm();
        WeatherMod.weatherMan.setStage(0);
        WeatherMod.weatherMan.resetStorm();
    }

    public void onUpdate()
    {
        if (ModLoader.getMinecraftInstance().thePlayer != null)
        {
            this.posX = ModLoader.getMinecraftInstance().thePlayer.posX;
            this.posZ = ModLoader.getMinecraftInstance().thePlayer.posZ;
            this.posY = 128.0D;
            this.setPosition(this.posX, this.posY, this.posZ);
        }
    }

    public void writeEntityToNBT(NBTTagCompound var1)
    {
        var1.setBoolean("stormActive", WeatherMod.stormMan.stormActive);
        var1.setInteger("stormTime", WeatherMod.stormMan.stormTime);
        var1.setInteger("stormStartTime", WeatherMod.stormMan.stormStartTime);
        var1.setInteger("stage", (int)WeatherMod.stormMan.stage);
        var1.setInteger("activeStormID", WeatherMod.stormMan.activeStormID);
        var1.setBoolean("stormDying", WeatherMod.stormMan.stormDying);
        var1.setBoolean("stormDeathStage", WeatherMod.stormMan.stormDeathStage);
        //Wind
        var1.setBoolean("w_stormActive", WeatherMod.weatherMan.stormActive);
        var1.setInteger("w_stormTime", WeatherMod.weatherMan.stormTime);
        var1.setInteger("w_stormStartTime", WeatherMod.weatherMan.stormStartTime);
        var1.setInteger("w_stage", (int)WeatherMod.weatherMan.stage);
        var1.setBoolean("w_stormDying", WeatherMod.weatherMan.stormDying);
        var1.setBoolean("w_stormDeathStage", WeatherMod.weatherMan.stormDeathStage);
    }

    public void readEntityFromNBT(NBTTagCompound var1)
    {
        WeatherMod.stormMan.stormActive = var1.getBoolean("stormActive");
        WeatherMod.stormMan.stormTime = var1.getInteger("stormTime");
        WeatherMod.stormMan.stormStartTime = var1.getInteger("stormStartTime");
        WeatherMod.stormMan.stage = var1.getInteger("stage");
        WeatherMod.stormMan.activeStormID = var1.getInteger("activeStormID");
        WeatherMod.stormMan.stormDying = var1.getBoolean("stormDying");
        WeatherMod.stormMan.stormDeathStage = var1.getBoolean("stormDeathStage");
        //Wind
        WeatherMod.weatherMan.stormActive = var1.getBoolean("w_stormActive");
        WeatherMod.weatherMan.stormTime = var1.getInteger("w_stormTime");
        WeatherMod.weatherMan.stormStartTime = var1.getInteger("w_stormStartTime");
        WeatherMod.weatherMan.stage = var1.getInteger("w_stage");
        WeatherMod.weatherMan.stormDying = var1.getBoolean("w_stormDying");
        WeatherMod.weatherMan.stormDeathStage = var1.getBoolean("w_stormDeathStage");
    }
}
