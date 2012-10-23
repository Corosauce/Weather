package weather.storm;

import net.minecraft.src.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import weather.ExtendedRenderer;
import weather.WeatherEntityConfig;
import weather.WeatherMod;
import weather.renderer.EntityAnimTexFX;
import weather.renderer.EntityRotFX;

import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.Side;
@SideOnly(Side.CLIENT)
public class StormCluster extends Entity
{
    public World worldRef;
    public List cloudEffects;
    public int age;
    public int maxAge = 900;

    public static int updateLCG;

    public StormCluster()
    {
        super(WeatherMod.worldRef);
        cloudEffects = new ArrayList();
        worldRef = WeatherMod.worldRef;
        this.updateLCG = (new Random()).nextInt();
        age = 0;
    }

    public void onUpdate()
    {
        //Entity target = null;//mod_EntMover.activeTornado;
        byte radius = 3;
        byte baseRadius = 9;
        //System.out.println("dadasdasd!");
        int var3;
        int var4;
        int var6;
        int var7;
        int var8;
        int var9;
        //var3 = MathHelper.floor_double(target.posX / 16.0D);
        //var4 = MathHelper.floor_double(target.posZ / 16.0D);
        var3 = MathHelper.floor_double(posX / 16.0D);
        var4 = MathHelper.floor_double(posZ / 16.0D);
        byte var5 = radius;

        for (int var66 = -var5; var66 <= var5; ++var66)
        {
            for (int var77 = -var5; var77 <= var5; ++var77)
            {
                //worldRef.positionsToUpdate.add(new ChunkCoordIntPair(var6 + var3, var7 + var4));
                int var33 = (var66 + var3) * 16;
                int var44 = (var77 + var4) * 16;

                if (worldRef.rand.nextInt(1 + (int)(((float)1000 * ((float)radius / (float)baseRadius)))) == 0)
                {
                    //System.out.println("blam!");
                    updateLCG = updateLCG * 3 + 1013904223;
                    var6 = updateLCG >> 2;
                    var7 = var33 + (var6 & 15);
                    var8 = var44 + (var6 >> 8 & 15);
                    var9 = 100;//worldRef.findTopSolidBlock(var7, var8);
                    //if(worldRef.canBlockBeRainedOn(var7, var9, var8)) {
                    EntityAnimTexFX var31 = new EntityAnimTexFX(worldRef, posX, posY, posZ, worldRef.rand.nextGaussian() * 0.8D, worldRef.rand.nextGaussian() * 0.8D, worldRef.rand.nextGaussian() * 0.8D, 20D, WeatherMod.effWindAnimID);
                    var31.spawnY = ((int)128 - 5) + rand.nextFloat() * 5;
                    //this.effR.addEffect(var31);
                    //this.funnelEffects.add(var31);
                    //mod_EntMover.particleCount++;
                    var31.rotationPitch = 90F;
                    var31.renderDistanceWeight = 10.0D;
                    var31.noClip = true;
                    var31.setSize(1.25F, 1.25F);
                    //var31.posY = var6 + 0D;
                    var31.setPosition(posX, var31.spawnY, posZ);
                    var31.type = 1;
                    ExtendedRenderer.rotEffRenderer.addEffect(var31);
                    //System.out.println(var31.posY);
                    cloudEffects.add(var31);
                    WeatherMod.particleCount2++;
                    //EntityCloud ent = new EntityCloud(worldRef);
                    //ent.setPosition(var7, var9, var8);
                    /*ent.motionX = ent.rand.nextFloat()*2-1;
                    ent.motionY = ent.rand.nextFloat()*2-1;
                    ent.motionZ = ent.rand.nextFloat()*2-1;*/
                    //ent.setPosition((double)var7, (double)var9, (double)var8);
                    //worldRef.addWeatherEffect(ent);
                    //}
                }
            }
        }

        if (cloudEffects.size() > 0)
        {
            for (int var99 = 0; var99 < cloudEffects.size(); ++var99)
            {
                EntityRotFX var30 = (EntityRotFX)cloudEffects.get(var99);

                //rotations!
                /*double var16 = this.posX - var30.posX;
                double var18 = this.posZ - var30.posZ;
                float var20 = this.rotationYaw;
                var30.rotationYaw = (float)(Math.atan2(var18, var16) * 180.0D / Math.PI) - 90.0F;
                var30.rotationPitch = -30F;*/

                if (var30.getAge() >= var30.getMaxAge())
                {
                    var30.setDead();
                    //mod_ExtendedRenderer.rotEffRenderer.fxLayers[4].remove(var30);
                }

                if (var30.isDead)
                {
                    cloudEffects.remove(var30);
                    WeatherMod.particleCount2--;
                }
                else if (var30 != null)
                {
                    WeatherMod.spin(this, (WeatherEntityConfig)WeatherMod.weatherEntTypes.get(1), var30);
                    //var30.setPosition(var30.posX, 127+var30.rand.nextDouble(), var30.posZ);
                    var30.setPosition(var30.posX, var30.spawnY/*var30.posY+0.4*/, var30.posZ);
                }
            }
        }
    }

    @Override
    public void setDead()
    {
        super.setDead();

        if (cloudEffects.size() > 0)
        {
            for (int var99 = 0; var99 < cloudEffects.size(); ++var99)
            {
                EntityRotFX var30 = (EntityRotFX)cloudEffects.get(var99);
                //var30.setAge(9999999);
                var30.setDead();
                cloudEffects.remove(var30);
                //mod_ExtendedRenderer.rotEffRenderer.fxLayers[4].remove(var30);
            }
        }

        //mod_ExtendedRenderer.rotEffRenderer.clearEffects(worldRef);
    }

    @Override
    protected void entityInit()
    {
        // TODO Auto-generated method stub
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound var1)
    {
        // TODO Auto-generated method stub
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound var1)
    {
        // TODO Auto-generated method stub
    }
}
