package weather.worldObjects;

import java.util.List;

import weather.WeatherMod;
import weather.blocks.MovingBlock;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.MathHelper;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;

public class EntDrill extends Entity
{
    public int type = 0;
    public boolean isUsed = false;
    public int strength;
    public int age;
    public int noSeeTicks = 0;

    public EntDrill(World var1)
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

        if (var5 != 0 && this.worldObj.getBlockTileEntity(var1, var2, var3) == null)
        {
            this.worldObj.setBlockWithNotify(var1, var2, var3, 0);

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
                var6.type = 0;
                var6.controller = this;
                var4 = true;
            }
        }

        return var4;
    }

    public void tryKnockback(int var1, int var2)
    {
        List var3 = this.worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox((double)var1 - 1.0D, this.posY - 1.0D, (double)var2 - 1.0D, (double)var1 + 1.0D, this.posY + 1.0D, (double)var2 + 1.0D).expand(1.0D, 1.0D, 1.0D));

        if (var3 != null)
        {
            for (int var4 = 0; var4 < var3.size(); ++var4)
            {
                Entity var5 = (Entity)var3.get(var4);

                if (!(var5 instanceof EntityPlayer))
                {
                    float var12;

                    for (var12 = this.rotationYaw - 45.0F; this.rotationYaw >= 180.0F; this.rotationYaw -= 360.0F)
                    {
                        ;
                    }

                    while (this.rotationYaw < -180.0F)
                    {
                        this.rotationYaw += 360.0F;
                    }

                    float var13;

                    for (var13 = this.rotationPitch; this.rotationPitch >= 180.0F; this.rotationPitch -= 360.0F)
                    {
                        ;
                    }

                    while (this.rotationPitch < -180.0F)
                    {
                        this.rotationPitch += 360.0F;
                    }

                    double var6 = (double)(-MathHelper.sin(var12 / 180.0F * (float)Math.PI) * MathHelper.cos(var13 / 180.0F * (float)Math.PI));
                    double var8 = (double)(MathHelper.cos(var12 / 180.0F * (float)Math.PI) * MathHelper.cos(var13 / 180.0F * (float)Math.PI));
                    double var10 = (double)(-MathHelper.sin(var13 / 180.0F * (float)Math.PI));

                    if (var5 instanceof MovingBlock)
                    {
                        var5.motionX = this.motionX * -15.5D;
                        var5.motionY = this.motionY * -15.5D;
                        var5.motionZ = this.motionZ * -15.5D;
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

    public void knockBack2(Entity var1, double var2, double var4, double var6, float var8)
    {
        float var9 = MathHelper.sqrt_double(var2 * var2 + var4 * var4 + var6 * var6);
        var1.motionX = var2 / (double)var9 * (double)var8;
        var1.motionY = var4 / (double)var9 * (double)var8;
        var1.motionZ = var6 / (double)var9 * (double)var8;
    }

    public void onUpdate()
    {
        for (int var1 = 0; var1 < 2; ++var1)
        {
            if (this.age < 900)
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
                double var20;
                double var22;
                double var24;
                float var26;

                for (var4 = 0.0D; var4 <= 3.0D; var4 += 0.5D)
                {
                    for (var26 = this.rotationYaw + 90.0F; this.rotationYaw >= 180.0F; this.rotationYaw -= 360.0F)
                    {
                        ;
                    }

                    while (this.rotationYaw < -180.0F)
                    {
                        this.rotationYaw += 360.0F;
                    }

                    var6 = (double)(-MathHelper.sin(var26 / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
                    var8 = (double)(-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI));
                    var10 = (double)(MathHelper.cos(var26 / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
                    var12 = (double)MathHelper.sqrt_double(var6 * var6 + var8 * var8 + var10 * var10);
                    var14 = var6 / var12 * var4;
                    var16 = var8 / var12 * var4;
                    var18 = var10 / var12 * var4;
                    var20 = this.posX + var14;
                    var22 = this.posY + var16;
                    var24 = this.posZ + var18;
                    if (!worldObj.isRemote) this.tryRip((int)var20, (int)var22, (int)var24);
                    this.tryKnockback((int)var20, (int)var24);
                }

                for (var4 = 0.0D; var4 <= 3.0D; var4 += 0.5D)
                {
                    for (var26 = this.rotationYaw - 90.0F; this.rotationYaw >= 180.0F; this.rotationYaw -= 360.0F)
                    {
                        ;
                    }

                    while (this.rotationYaw < -180.0F)
                    {
                        this.rotationYaw += 360.0F;
                    }

                    var6 = (double)(-MathHelper.sin(var26 / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
                    var8 = (double)(-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI));
                    var10 = (double)(MathHelper.cos(var26 / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
                    var12 = (double)MathHelper.sqrt_double(var6 * var6 + var8 * var8 + var10 * var10);
                    var14 = var6 / var12 * var4;
                    var16 = var8 / var12 * var4;
                    var18 = var10 / var12 * var4;
                    var20 = this.posX + var14;
                    var22 = this.posY + var16;
                    var24 = this.posZ + var18;
                    if (!worldObj.isRemote) this.tryRip((int)var20, (int)var22, (int)var24);
                    this.tryKnockback((int)var20, (int)var24);
                }

                double var65 = 4.0D;
                double var28 = var65 / 2.0D;
                double var30;
                double var34;
                double var32;
                float var38;
                float var39;
                float var36;
                float var37;

                for (var30 = this.posX - var28; var30 < this.posX + var28; ++var30)
                {
                    for (var32 = this.posY - var28; var32 < this.posY + var28; ++var32)
                    {
                        for (var34 = this.posZ - var28; var34 < this.posZ + var28; ++var34)
                        {
                            var36 = (float)(this.posX - var30);
                            var37 = (float)(this.posY - var32);
                            var38 = (float)(this.posZ - var34);
                            var39 = MathHelper.sqrt_float(var36 * var36 + var37 * var37 + var38 * var38);

                            if ((double)var39 <= var65)
                            {
                            	if (!worldObj.isRemote) this.tryRip((int)var30, (int)var32, (int)var34);
                                this.tryKnockback((int)var30, (int)var34);
                            }
                        }
                    }
                }

                var30 = (double)((float)this.posX + this.rand.nextFloat() * 0.25F);
                var32 = (double)((float)this.posY + this.height + 0.125F);
                var34 = (double)((float)this.posZ + this.rand.nextFloat() * 0.25F);
                var36 = this.rand.nextFloat() * 360.0F;
                this.worldObj.spawnParticle("explode", -Math.sin((double)(0.01745329F * var36)) * 0.75D, var32 - 0.25D, Math.cos((double)(0.01745329F * var36)) * 0.75D, var30, 0.125D, var34);
                var37 = 2.0F;
                float var10000 = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * var37;
                var38 = 0.0F;
                var39 = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * var37;
                double var40 = 1.8D;
                double var42 = 0.05D;
                double var44 = this.prevPosX + (this.posX - this.prevPosX) * (double)var37;
                double var46 = this.prevPosY + (this.posY - this.prevPosY) * (double)var37 + 1.62D - (double)this.yOffset;
                double var48 = this.prevPosZ + (this.posZ - this.prevPosZ) * (double)var37;
                float var50 = MathHelper.cos(-var39 * 0.01745329F - (float)Math.PI);
                float var51 = MathHelper.sin(-var39 * 0.01745329F - (float)Math.PI);
                float var52 = -MathHelper.cos(-var38 * 0.01745329F - ((float)Math.PI / 4F));
                float var53 = MathHelper.sin(-var38 * 0.01745329F - ((float)Math.PI / 4F));
                float var54 = var51 * var52;
                float var56 = var50 * var52;
                double var57 = (double)var54 * var42;
                double var59 = (double)var53 * var42;
                double var61 = (double)var56 * var42;
                this.worldObj.spawnParticle("explode", var44 + (double)var54 * var40, var46 + (double)var53 * var40, var48 + (double)var56 * var40, var57 / 2.0D, var59 / 2.0D, var61 / 2.0D);

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
                for (int var63 = 0; var63 < 4; ++var63)
                {
                    float var64 = 0.25F;
                    this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double)var64, this.posY - this.motionY * (double)var64, this.posZ - this.motionZ * (double)var64, this.motionX, this.motionY, this.motionZ);
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
