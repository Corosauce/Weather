package weather.entities;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.src.ModLoader;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import weather.WeatherMod;
import weather.config.ConfigTornado;

public class EntShockWave extends Entity
{
    public int type = 0;
    public boolean isUsed = false;
    public int strength;
    public int age;
    public int noSeeTicks = 0;

    public EntShockWave(World var1)
    {
        super(var1);
        this.worldObj = var1;
        this.isImmuneToFire = true;
        this.setSize(1.1F, 1.1F);
        this.strength = 100;
        this.age = 0;
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

    public void entityInit() {}

    public boolean tryRip(int var1, int var2, int var3)
    {
        boolean var4 = false;
        int var5 = this.worldObj.getBlockId(var1, var2, var3);

        if (var5 != 0 && this.worldObj.getBlockTileEntity(var1, var2, var3) == null && WeatherMod.blockCount <= ConfigTornado.Storm_Tornado_maxBlocks)
        {
            this.worldObj.setBlock(var1, var2, var3, 0, 0, 2);

            if (var5 != Block.snow.blockID)
            {
                MovingBlock var6;

                if (var5 == Block.grass.blockID)
                {
                    var6 = new MovingBlock(this.worldObj, var1, var2, var3, Block.dirt.blockID);
                }
                else
                {
                    var6 = new MovingBlock(this.worldObj, var1, var2, var3, var5);
                }

                ++WeatherMod.blockCount;
                this.worldObj.spawnEntityInWorld(var6);
                var6.motionY = 0.4D;
                var6.mode = 0;
                var6.type = 2;
                var6.controller = this;
                var4 = true;
            }
        }

        return var4;
    }

    public void tryKnockback(int var1, int var2)
    {
        List var3 = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox((double)var1, this.posY, (double)var2, (double)var1 + 1.0D, this.posY + 1.0D, (double)var2 + 1.0D).expand(1.0D, 1.0D, 1.0D));

        if (var3 != null)
        {
            for (int var4 = 0; var4 < var3.size(); ++var4)
            {
                Entity var5 = (Entity)var3.get(var4);

                if (!(var5 instanceof EntityPlayer))
                {
                    double var6 = var5.posX - ModLoader.getMinecraftInstance().thePlayer.posX;
                    double var8;

                    for (var8 = var5.posZ - ModLoader.getMinecraftInstance().thePlayer.posZ; var6 * var6 + var8 * var8 < 1.0E-4D; var8 = (Math.random() - Math.random()) * 0.01D)
                    {
                        var6 = (Math.random() - Math.random()) * 0.01D;
                    }

                    if (var5 instanceof EntityLivingBase)
                    {
                        this.knockBack(var5, 0, var6, var8, 1.0F);
                        var5.motionY = 0.7D;
                    }
                }
            }
        }
    }

    public void knockBack(Entity var1, int var2, double var3, double var5, float var7)
    {
        float var8 = MathHelper.sqrt_double(var3 * var3 + var5 * var5);
        var1.motionX /= 2.0D;
        var1.motionY /= 2.0D;
        var1.motionZ /= 2.0D;
        var1.motionX = var3 / (double)var8 * (double)var7;
        var1.motionY += 0.10000000596046447D;
        var1.motionZ = var5 / (double)var8 * (double)var7;

        if (var1.motionY > 0.4000000059604645D)
        {
            var1.motionY = 0.4000000059604645D;
        }
    }

    public void onUpdate()
    {
        for (int var1 = 0; var1 < 2; ++var1)
        {
            if (this.age < 10)
            {
                boolean var2 = false;
                boolean var3 = false;
                double var4 = 5.0D;
                double var6;
                double var8;
                double var10;
                double var12;
                double var14;
                double var16;
                double var18;
                float var20;

                for (var4 = 0.0D; var4 <= 3.0D; var4 += 0.5D)
                {
                    for (var20 = this.rotationYaw + 90.0F; this.rotationYaw >= 180.0F; this.rotationYaw -= 360.0F)
                    {
                        ;
                    }

                    while (this.rotationYaw < -180.0F)
                    {
                        this.rotationYaw += 360.0F;
                    }

                    var6 = (double)(-MathHelper.sin(var20 / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
                    var8 = (double)(MathHelper.cos(var20 / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
                    var10 = (double)MathHelper.sqrt_double(var6 * var6 + var8 * var8);
                    var12 = var6 / var10 * var4;
                    var14 = var8 / var10 * var4;
                    var16 = this.posX + var12;
                    var18 = this.posZ + var14;
                    if (!worldObj.isRemote) this.tryRip((int)var16, (int)this.posY - 1, (int)var18);
                    this.tryKnockback((int)var16, (int)var18);
                }

                for (var4 = 0.0D; var4 <= 3.0D; var4 += 0.5D)
                {
                    for (var20 = this.rotationYaw - 90.0F; this.rotationYaw >= 180.0F; this.rotationYaw -= 360.0F)
                    {
                        ;
                    }

                    while (this.rotationYaw < -180.0F)
                    {
                        this.rotationYaw += 360.0F;
                    }

                    var6 = (double)(-MathHelper.sin(var20 / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
                    var8 = (double)(MathHelper.cos(var20 / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
                    var10 = (double)MathHelper.sqrt_double(var6 * var6 + var8 * var8);
                    var12 = var6 / var10 * var4;
                    var14 = var8 / var10 * var4;
                    var16 = this.posX + var12;
                    var18 = this.posZ + var14;
                    if (!worldObj.isRemote) this.tryRip((int)var16, (int)this.posY - 1, (int)var18);
                    this.tryKnockback((int)var16, (int)var18);
                }

                double var55 = (double)((float)this.posX + this.rand.nextFloat() * 0.25F);
                double var22 = (double)((float)this.posY + this.height + 0.125F);
                double var24 = (double)((float)this.posZ + this.rand.nextFloat() * 0.25F);
                float var26 = this.rand.nextFloat() * 360.0F;
                this.worldObj.spawnParticle("explode", -Math.sin((double)(0.01745329F * var26)) * 0.75D, var22 - 0.25D, Math.cos((double)(0.01745329F * var26)) * 0.75D, var55, 0.125D, var24);
                float var27 = 2.0F;
                float var10000 = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * var27;
                float var28 = 0.0F;
                float var29 = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * var27;
                double var30 = 1.8D;
                double var32 = 0.05D;
                double var34 = this.prevPosX + (this.posX - this.prevPosX) * (double)var27;
                double var36 = this.prevPosY + (this.posY - this.prevPosY) * (double)var27 + 1.62D - (double)this.yOffset;
                double var38 = this.prevPosZ + (this.posZ - this.prevPosZ) * (double)var27;
                float var40 = MathHelper.cos(-var29 * 0.01745329F - (float)Math.PI);
                float var41 = MathHelper.sin(-var29 * 0.01745329F - (float)Math.PI);
                float var42 = -MathHelper.cos(-var28 * 0.01745329F - ((float)Math.PI / 4F));
                float var43 = MathHelper.sin(-var28 * 0.01745329F - ((float)Math.PI / 4F));
                float var44 = var41 * var42;
                float var46 = var40 * var42;
                double var47 = (double)var44 * var32;
                double var49 = (double)var43 * var32;
                double var51 = (double)var46 * var32;
                this.worldObj.spawnParticle("explode", var34 + (double)var44 * var30, var36 + (double)var43 * var30, var38 + (double)var46 * var30, var47 / 2.0D, var49 / 2.0D, var51 / 2.0D);

                if (!var3)
                {
                    ++this.noSeeTicks;

                    if (this.noSeeTicks > 150)
                    {
                        this.setEntityDead();
                    }
                }
                else
                {
                    this.noSeeTicks = 0;
                }

                if (var2)
                {
                    this.noSeeTicks = 0;
                }

                ++this.age;
            }
            else
            {
                this.setEntityDead();
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;

            if (this.handleWaterMovement())
            {
                for (int var53 = 0; var53 < 4; ++var53)
                {
                    float var54 = 0.25F;
                    this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double)var54, this.posY - this.motionY * (double)var54, this.posZ - this.motionZ * (double)var54, this.motionX, this.motionY, this.motionZ);
                }
            }

            this.motionX *= 1.0D;
            this.motionY *= 1.0D;
            this.motionZ *= 1.0D;
            this.setPosition(this.posX, this.posY, this.posZ);
        }
    }

    public void writeEntityToNBT(NBTTagCompound var1)
    {
        var1.setShort("age", (short)this.age);
        var1.setShort("type", (short)this.type);
    }

    public void readEntityFromNBT(NBTTagCompound var1)
    {
        this.age = var1.getShort("age");
        this.type = var1.getShort("type");
    }
}
