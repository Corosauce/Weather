package weather.blocks;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.src.ModLoader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import weather.WeatherMod;
import weather.entities.storm.EntTornado;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityTSiren extends TileEntity
{
    public long lastPlayTime = 0L;
    public long lastVolUpdate = 0L;
    public int soundID = -1;
    public int lineBeingEdited = -1;

    public void updateEntity()
    {
    	if (worldObj.isRemote) {
    		herp();
    	}
    }
    
    @SideOnly(Side.CLIENT)
    public void herp() {
    	if (this.lastPlayTime < System.currentTimeMillis())
        {
            /*List var1 = this.worldObj.getEntitiesWithinAABB(EntTornado.class, AxisAlignedBB.getBoundingBoxFromPool((double)this.xCoord, (double)this.yCoord, (double)this.zCoord, (double)this.xCoord + 1.0D, (double)this.yCoord + 1.0D, (double)this.zCoord + 1.0D).expand(140.0D, 140.0D, 140.0D));
            if(var1.size() > 0) {
               this.lastPlayTime = System.currentTimeMillis() + 13000L;
               this.soundID = mod_EntMover.playMovingSound("tornado.siren", (float)ModLoader.getMinecraftInstance().thePlayer.posX, (float)ModLoader.getMinecraftInstance().thePlayer.posY, (float)ModLoader.getMinecraftInstance().thePlayer.posZ, 1.0F, 1.0F);
               System.out.println("alarm!");
            }*/
            EntTornado entT = WeatherMod.activeTornado;

            if (entT != null && !entT.isDead && entT.getStorm().type != entT.getStorm().TYPE_SPOUT)
            {
                if (WeatherMod.activeTornado.getDistance(xCoord, yCoord, zCoord) < 180)
                {
                    this.lastPlayTime = System.currentTimeMillis() + 13000L;
                    this.soundID = WeatherMod.playMovingSound(WeatherMod.modID + ":tornado.siren", (float)ModLoader.getMinecraftInstance().thePlayer.posX, (float)ModLoader.getMinecraftInstance().thePlayer.posY, (float)ModLoader.getMinecraftInstance().thePlayer.posZ, 1.0F, 1.0F);
                }
            }
        }

        if (this.lastVolUpdate < System.currentTimeMillis())
        {
            this.lastVolUpdate = System.currentTimeMillis() + 100L;
            Entity pl = ModLoader.getMinecraftInstance().thePlayer;

            if (pl != null)
            {
                float var3 = (float)((120.0D - (double)MathHelper.sqrt_double(this.getDistanceFrom(pl.posX, pl.posY, pl.posZ))) / 120.0D);

                if (var3 < 0.0F)
                {
                    var3 = 0.0F;
                }

                String var2 = "sound_" + this.soundID;
                WeatherMod.setVolume(var2, var3);
            }
        }
    }

    public void writeToNBT(NBTTagCompound var1)
    {
        super.writeToNBT(var1);
    }

    public void readFromNBT(NBTTagCompound var1)
    {
        super.readFromNBT(var1);

    }
}
