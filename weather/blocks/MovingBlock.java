package weather.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import weather.WeatherMod;
import weather.config.ConfigTornado;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class MovingBlock extends Entity implements IEntityAdditionalSpawnData
{
    public int tile;
    public static final int falling = 0;
    public static final int grabbed = 1;
    public int mode;
    public static final float slowdown = 0.98F;
    public static final float curvature = 0.05F;
    public int metadata;
    public TileEntity tileentity;
    public Material material;
    public int age;
    public int type;
    public boolean noCollision;
    public boolean collideFalling = false;
    public double vecX;
    public double vecY;
    public double vecZ;
    public double lastPosX;
    public double lastPosZ;
    public Entity controller;
    public int gravityDelay;

    public MovingBlock(World var1)
    {
        super(var1);
        this.mode = 1;
        this.age = 0;
        this.tile = 0;
        this.noCollision = true;
        this.gravityDelay = 60;
    }

    public MovingBlock(World var1, int var2, int var3, int var4, int var5)
    {
        super(var1);
        this.mode = 1;
        this.age = 0;
        this.type = 0;
        this.noCollision = false;
        this.gravityDelay = 60;
        this.noCollision = true;
        this.tile = var5;
        this.setSize(0.9F, 0.9F);
        this.yOffset = this.height / 2.0F;
        this.setPosition((double)var2 + 0.5D, (double)var3 + 0.5D, (double)var4 + 0.5D);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = (double)((float)var2 + 0.5F);
        this.prevPosY = (double)((float)var3 + 0.5F);
        this.prevPosZ = (double)((float)var4 + 0.5F);
        this.material = Block.blocksList[this.tile].blockMaterial;
        this.tileentity = var1.getBlockTileEntity(var2, var3, var4);
        this.metadata = var1.getBlockMetadata(var2, var3, var4);

        if (this.tileentity != null)
        {
            var1.setBlockTileEntity(var2, var3, var4, ((BlockContainer)Block.blocksList[this.tile]).createNewTileEntity(var1));
            var1.setBlock(var2, var3, var4, 0, 0, 2);
        }
    }

    public boolean isInRangeToRenderDist(double var1)
    {
        return true;
    }

    public boolean canTriggerWalking()
    {
        return false;
    }

    public void entityInit() {}

    public boolean canBePushed()
    {
        return !this.isDead;
    }

    public boolean canBeCollidedWith()
    {
        return !this.isDead && !this.noCollision;
    }

    public void onUpdate()
    {
        if (this.tile == 0)
        {
            this.setDead();
        }
        else
        {
            ++this.age;

            if (this.age > this.gravityDelay && this.type == 0)
            {
                this.mode = 0;

                if (this.tileentity == null && ConfigTornado.Storm_Tornado_rarityOfDisintegrate != -1 && this.rand.nextInt((ConfigTornado.Storm_Tornado_rarityOfDisintegrate + 1) * 20) == 0)
                {
                    this.setDead();
                }

                if (this.tileentity == null && ConfigTornado.Storm_Tornado_rarityOfFirenado != -1 && this.rand.nextInt((ConfigTornado.Storm_Tornado_rarityOfFirenado + 1) * 20) == 0)
                {
                    this.tile = Block.fire.blockID;
                }
            }

            if (this.type == 0)
            {
            	if (this.controller != null) {
	                this.vecX = this.controller.posX - this.posX;
	                this.vecY = this.controller.boundingBox.minY + (double)(this.controller.height / 2.0F) - (this.posY + (double)(this.height / 2.0F));
	                this.vecZ = this.controller.posZ - this.posZ;
            	} else {
            		this.vecX++;
	                this.vecY++;
	                this.vecZ++;
            	}
            }

            if (this.mode == 1)
            {
                this.fallDistance = 0.0F;
                this.isCollidedHorizontally = false;
            }

            /*Field fire = null;
            int fireInt = 0;
            try {
             fire = Entity.class.getDeclaredField("c");
             fire.setAccessible(true);
             fireInt = (int)fire.get(ent);
            } catch (Exception ex) {
             try {
              fire = Entity.class.getDeclaredField("fire");
                 fire.setAccessible(true);
                 fireInt = (int)fire.get(ent);
             } catch (Exception ex2) {
             }
            }*/
            /*if(this.fire > 0) {
               --this.fire;
            }*/
            Vec3 var1 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            Vec3 var2 = Vec3.createVectorHelper(this.posX + this.motionX * 1.3D, this.posY + this.motionY * 1.3D, this.posZ + this.motionZ * 1.3D);
            MovingObjectPosition var3 = this.worldObj.rayTraceBlocks(var1, var2);
            var2 = Vec3.createVectorHelper(this.posX + this.motionX * 1.3D, this.posY + this.motionY * 1.3D, this.posZ + this.motionZ * 1.3D);

            if (var3 != null)
            {
                var2 = Vec3.createVectorHelper(var3.hitVec.xCoord, var3.hitVec.yCoord, var3.hitVec.zCoord);
            }

            Entity var4 = null;
            List var5 = null;

            if (this.age > this.gravityDelay / 4)
            {
                var5 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ));
            }

            double var6 = 0.0D;
            int var8;
            int var9;
            int var11;

            for (var8 = 0; var5 != null && var8 < var5.size() && var8 < 5; ++var8)
            {
                Entity var10 = (Entity)var5.get(var8);

                if (!(var10 instanceof MovingBlock) && var10.canBeCollidedWith() && this.canEntityBeSeen(var10))
                {
                	if (!(var10 instanceof EntityPlayer) || !((EntityPlayer)var10).capabilities.isCreativeMode) {
	                    var10.motionX = this.motionX / 1.5D;
	                    var10.motionY = this.motionY / 1.5D;
	                    var10.motionZ = this.motionZ / 1.5D;
                	}
                    
                    if (ConfigTornado.Storm_FlyingBlocksHurt && Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ) > 0.4F) {
                    	//System.out.println("damaging with block: " + var10);
                    	
                    	DamageSource ds = DamageSource.causeThrownDamage(this, this);
                		ds.damageType = "wm.movingblock";
                		var10.attackEntityFrom(ds, 4);
                    }
                }

                if (var10.canBeCollidedWith() && !this.noCollision)
                {
                    if (var10.canBePushed())
                    {
                        var10.getDistanceSqToEntity(this);

                        if (this.isBurning())
                        {
                            var10.setFire(15);
                        }

                        if (this.tile == 81)
                        {
                            var10.attackEntityFrom(DamageSource.causeThrownDamage(this, this), 1);
                        }
                        else if (this.tile != 88 && this.tile != 70 && this.tile != 72)
                        {
                            if (this.material == Material.lava)
                            {
                                var10.setFire(15);
                            }
                        }
                        else
                        {
                            var9 = MathHelper.floor_double(this.posX);
                            var11 = MathHelper.floor_double(this.posY);
                            int var12 = MathHelper.floor_double(this.posZ);
                            Block.blocksList[this.tile].onEntityCollidedWithBlock(this.worldObj, var9, var11, var12, var10);
                        }
                    }

                    float var16 = 0.3F;
                    AxisAlignedBB var19 = var10.boundingBox.expand((double)var16, (double)var16, (double)var16);
                    MovingObjectPosition var13 = var19.calculateIntercept(var1, var2);

                    if (var13 != null)
                    {
                        double var14 = var1.distanceTo(var13.hitVec);

                        if (var14 < var6 || var6 == 0.0D)
                        {
                            var4 = var10;
                            var6 = var14;
                        }
                    }
                }
            }

            if (var4 != null)
            {
                var3 = new MovingObjectPosition(var4);
            }

            if (var3 != null && var3.entityHit == null && this.mode == 0)
            {
                var8 = var3.blockX;
                int var17 = var3.blockY;
                var9 = var3.blockZ;

                if (var3.sideHit == 0)
                {
                    --var17;
                }

                if (var3.sideHit == 1)
                {
                    ++var17;
                }

                if (var3.sideHit == 2)
                {
                    --var9;
                }

                if (var3.sideHit == 3)
                {
                    ++var9;
                }

                if (var3.sideHit == 4)
                {
                    --var8;
                }

                if (var3.sideHit == 5)
                {
                    ++var8;
                }

                if (this.type == 0)
                {
                    if (var3.sideHit != 0 && !this.collideFalling)
                    {
                        if (!this.collideFalling)
                        {
                            this.collideFalling = true;
                            this.posX = MathHelper.floor_double(posX);
                            this.posZ = MathHelper.floor_double(posZ);
                            //this.posZ = (double)((int)(this.posZ + 0.0D));
                            this.setPosition(this.posX, this.posY, this.posZ);
                            this.motionX = 0.0D;
                            this.motionZ = 0.0D;
                        }
                    }
                    else
                    {
                        this.blockify(var8, var17, var9, var3.sideHit);
                    }

                    this.lastPosX = this.posX;
                    this.lastPosZ = this.posZ;
                }
                else
                {
                    this.blockify(var8, var17, var9, var3.sideHit);
                }

                return;
            }

            float var18 = 0.98F;

            if (this.type == 1)
            {
                var18 = (float)((double)var18 * 0.92D);

                if (this.mode == 0)
                {
                    this.motionY -= 0.05000000074505806D;
                }
            }
            else
            {
                this.motionY -= 0.05000000074505806D;
            }

            this.motionX *= (double)var18;
            this.motionY *= (double)var18;
            this.motionZ *= (double)var18;
            var11 = (int)(this.posX + this.motionX * 5.0D);
            byte var20 = 50;
            int var21 = (int)(this.posZ + this.motionZ * 5.0D);

            if (!this.worldObj.checkChunksExist(var11, var20, var21, var11, var20, var21))
            {
                this.setDead();
            }

            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;

            if (this.mode == 1)
            {
                //this.moveEntity(this.motionX, this.motionY, this.motionZ);
                this.posX += this.motionX;
                this.posY += this.motionY;
                this.posZ += this.motionZ;
            }
            else if (this.mode == 0)
            {
                this.posX += this.motionX;
                this.posY += this.motionY;
                this.posZ += this.motionZ;
            }

            this.setPosition(this.posX, this.posY, this.posZ);
        }
    }
    
    public boolean canEntityBeSeen(Entity par1Entity)
    {
        return this.worldObj.rayTraceBlocks(this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ), this.worldObj.getWorldVec3Pool().getVecFromPool(par1Entity.posX, par1Entity.posY + (double)par1Entity.getEyeHeight(), par1Entity.posZ)) == null;
    }

    private void blockify(int var1, int var2, int var3, int var4)
    {
        this.setDead();
        int var5 = this.worldObj.getBlockId(var1, var2, var3);

        if (var5 != 0)
        {
            ;
        }

        if (this.tileentity != null || this.type != 0 || ConfigTornado.Storm_Tornado_rarityOfBreakOnFall > 0 && this.rand.nextInt(ConfigTornado.Storm_Tornado_rarityOfBreakOnFall + 1) != 0)
        {
            if (!WeatherMod.shouldRemoveBlock(var5) && !WeatherMod.isOceanBlock(var5) && var2 < 255)
            {
                this.worldObj.setBlock(var1, var2 + 1, var3, this.tile, this.metadata, 3);
            }

            boolean var6 = false;

            if (!WeatherMod.isOceanBlock(var5))
            {
                if (this.worldObj.setBlock(var1, var2, var3, this.tile, this.metadata, 3))
                {
                    var6 = true;
                }
            }
            else
            {
                this.worldObj.setBlock(var1, var2, var3, WeatherMod.finiteWaterId, this.metadata, 3);

                if (var2 < 255)
                {
                    this.worldObj.setBlock(var1, var2 + 1, var3, WeatherMod.finiteWaterId, this.metadata, 3);
                }
            }

            if (var6)
            {
                //Block.blocksList[this.tile].onBlockPlacedBy(this.worldObj, var1, var2, var3, var4, this);
                if (this.tileentity != null)
                {
                    this.worldObj.setBlockTileEntity(var1, var2, var3, this.tileentity);
                }
            }
        }
    }

    public boolean attackEntityFrom(Entity var1, int var2)
    {
        return false;
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound var1)
    {
        var1.setByte("Tile", (byte)this.tile);
        var1.setByte("Metadata", (byte)this.metadata);
        var1.setInteger("blocktype", type);
        NBTTagCompound var2 = new NBTTagCompound();

        if (this.tileentity != null)
        {
            this.tileentity.writeToNBT(var2);
        }

        var1.setCompoundTag("TileEntity", var2);
        
        
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound var1)
    {
        this.tile = var1.getByte("Tile") & 255;
        this.metadata = var1.getByte("Metadata") & 15;
        this.type = var1.getInteger("blocktype");
        this.tileentity = null;

        if (Block.blocksList[this.tile] instanceof BlockContainer)
        {
            this.tileentity = ((BlockContainer)Block.blocksList[this.tile]).createNewTileEntity(this.worldObj);
            NBTTagCompound var2 = var1.getCompoundTag("TileEntity");
            this.tileentity.readFromNBT(var2);
        }
        
        if (type == 0) setDead(); //kill flying block on reload for tornado spazing fix
    }

    public float getShadowSize()
    {
        return 0.0F;
    }

    public boolean isInRangeToRenderVec3D(Vec3 asd)
    {
        return true;
    }

    public World func_22685_k()
    {
        return this.worldObj;
    }

    @Override
    public void setDead()
    {
    	if (!worldObj.isRemote) {
	        --WeatherMod.blockCount;
	
	        if (WeatherMod.blockCount < 0)
	        {
	            WeatherMod.blockCount = 0;
	        }
    	}

        super.setDead();
    }

    @Override
    public void writeSpawnData(ByteArrayDataOutput data)
    {
        data.writeInt(tile);
        data.writeInt(metadata);
    }

    @Override
    public void readSpawnData(ByteArrayDataInput data)
    {
        tile = data.readInt();
        metadata = data.readInt();
    }
}
