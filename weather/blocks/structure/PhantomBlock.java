package weather.blocks.structure;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import weather.WeatherMod;
import weather.config.ConfigTornado;
import weather.system.wind.WindHandler;
import CoroUtil.pathfinding.PFQueue;
import CoroUtil.pathfinding.PathEntityEx;
import CoroUtil.pathfinding.c_IEnhPF;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class PhantomBlock extends Entity implements IEntityAdditionalSpawnData, c_IEnhPF, WindHandler
{
    public int tile;
    public final int falling = 0;
    public final int grabbed = 1;
    public int mode;
    public final float slowdown = 0.98F;
    public final float curvature = 0.05F;
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
    
    public PathEntityEx path;
    public Entity target;
    public int noMoveTicks;
    public boolean active;
    
    public int destX;
    public int destY;
    public int destZ;
    
    public boolean waitingForPath;

    public PhantomBlock(World var1)
    {
        super(var1);
        this.mode = 1;
        this.age = 0;
        this.tile = 0;
        this.noCollision = true;
        this.gravityDelay = 60;
    }

    public PhantomBlock(World var1, int var2, int var3, int var4, int var5)
    {
        super(var1);
        this.mode = 1;
        this.age = 0;
        this.type = 0;
        active = true;
        this.noCollision = false;
        this.gravityDelay = 1000;
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
        
        waitingForPath = true;

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

    public boolean canBePushed()
    {
        return !this.isDead;
    }

    public boolean canBeCollidedWith()
    {
        return !this.isDead && !this.noCollision;
    }
    
    public void clientUpdate() {
    	
    }

    public void onUpdate()
    {
    	
    	//setDead();
    	
    	if (worldObj.isRemote) {
    		clientUpdate();
    		return;
    	}
    	
        if (this.tile == 0)
        {
            this.setEntityDead();
        }
        else
        {
        	
        	if (notMoving(this, 0.05F)) {
                noMoveTicks++;
            } else {
                noMoveTicks = 0;
            }

            /*if (noMoveTicks > 20) {
                active = false;
                mode = 0;
            }*/

            if (target != null) {
                if (getDistanceToEntity(target) < 1) {
                    active = false;
                    mode = 0;
                }
            } else {
            }

            //path = null;
            
            if (!waitingForPath && !pathFollow()) {
                //System.out.println("D:");
                if (active) {
                    motionX = motionY = motionZ = 0F;
                }

                active = false;
                mode = 0;
                
                blockify(0,0,0,0);
            }

            if (age > 40) { //if still waiting, pf failed
            	waitingForPath = false;
            }
            
        	
            ++this.age;

            if (this.age > this.gravityDelay && this.type == 0)
            {
                this.mode = 0;

                if (this.tileentity == null && ConfigTornado.Storm_Tornado_rarityOfDisintegrate != -1 && this.rand.nextInt((ConfigTornado.Storm_Tornado_rarityOfDisintegrate + 1) * 20) == 0)
                {
                    //this.setEntityDead();
                }

                if (this.tileentity == null && ConfigTornado.Storm_Tornado_rarityOfFirenado != -1 && this.rand.nextInt((ConfigTornado.Storm_Tornado_rarityOfFirenado + 1) * 20) == 0)
                {
                    this.tile = Block.fire.blockID;
                }
            }

            if (this.type == 0 && this.controller != null)
            {
                this.vecX = this.controller.posX - this.posX;
                this.vecY = this.controller.boundingBox.minY + (double)(this.controller.height / 2.0F) - (this.posY + (double)(this.height / 2.0F));
                this.vecZ = this.controller.posZ - this.posZ;
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
            MovingObjectPosition var3 = this.worldObj.clip(var1, var2);
            var2 = Vec3.createVectorHelper(this.posX + this.motionX * 1.3D, this.posY + this.motionY * 1.3D, this.posZ + this.motionZ * 1.3D);

            if (var3 != null)
            {
                var2 = Vec3.createVectorHelper(var3.hitVec.xCoord, var3.hitVec.yCoord, var3.hitVec.zCoord);
            }

            Entity var4 = null;
            List var5 = null;

            if (!active)
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

                if (!(var10 instanceof PhantomBlock) && var10.canBeCollidedWith())
                {
                    var10.motionX = this.motionX / 2.0D;
                    var10.motionY = this.motionY / 2.0D;
                    var10.motionZ = this.motionZ / 2.0D;
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
                            this.posX = (double)((int)(this.posX + 0.0D));
                            this.posZ = (double)((int)(this.posZ + 0.0D));
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

            float var18 = 0.93F;

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
                //this.motionY -= 0.05000000074505806D;
            }
            
            if (!active) {
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
                this.setEntityDead();
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

    private void blockify(int var1, int var2, int var3, int var4)
    {
        this.setEntityDead();
        
        var1 = destX;
        var2 = destY;
        var3 = destZ;
        
        //System.out.println("trying place");
        
        if (this.worldObj.setBlock(destX, destY, destZ, this.tile, this.metadata, 2)) {
        	
        }

        /*if (this.tileentity != null || this.type != 0)
        {
            if (!WeatherMod.shouldRemoveBlock(var5) && !WeatherMod.isOceanBlock(var5) && var2 < 255)
            {
                this.worldObj.setBlock(var1, var2 + 1, var3, this.tile, this.metadata);
            }

            boolean var6 = false;

            if (!WeatherMod.isOceanBlock(var5))
            {
                if (this.worldObj.setBlock(var1, var2, var3, this.tile, this.metadata))
                {
                    var6 = true;
                }
            }
            else
            {
                this.worldObj.setBlock(var1, var2, var3, WeatherMod.finiteWaterId, this.metadata);

                if (var2 < 255)
                {
                    this.worldObj.setBlock(var1, var2 + 1, var3, WeatherMod.finiteWaterId, this.metadata);
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
        }*/
    }

    public boolean attackEntityFrom(Entity var1, int var2)
    {
        return false;
    }

    protected void writeEntityToNBT(NBTTagCompound var1)
    {
        var1.setByte("Tile", (byte)this.tile);
        var1.setByte("Metadata", (byte)this.metadata);
        NBTTagCompound var2 = new NBTTagCompound();

        if (this.tileentity != null)
        {
            this.tileentity.writeToNBT(var2);
        }

        var1.setCompoundTag("TileEntity", var2);
    }

    protected void readEntityFromNBT(NBTTagCompound var1)
    {
        this.tile = var1.getByte("Tile") & 255;
        this.metadata = var1.getByte("Metadata") & 15;
        this.tileentity = null;

        if (Block.blocksList[this.tile] instanceof BlockContainer)
        {
            this.tileentity = ((BlockContainer)Block.blocksList[this.tile]).createNewTileEntity(this.worldObj);
            NBTTagCompound var2 = var1.getCompoundTag("TileEntity");
            this.tileentity.readFromNBT(var2);
        }
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

    public void setEntityDead()
    {
        --WeatherMod.blockCount;

        if (WeatherMod.blockCount < 0)
        {
            WeatherMod.blockCount = 0;
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
    
    public void pathfind(Entity targ) {
    	
        //path = worldObj.getPathEntityToEntity(this, targ, 30F, false, false, false, false);    
        int what = 0;
    }
    
    public void pathfind(int x, int y, int z) {
    	PFQueue.getPath(this, x, y, z, 128);
    	waitingForPath = true;
    	//path = worldObj.getEntityPathToXYZ(this, x, y, z, 96F, false, false, false, false);
    	destX = x;
    	destY = y;
    	destZ = z;
    	//path = worldObj.getPathEntityToEntity(this, targ, 30F, false, false, false, false);
    }

    public boolean pathFollow() {
        
        boolean var3 = this.isInWater();
        boolean var4 = this.handleLavaMovement();
        this.rotationPitch = 0.0F;

        Vec3 moveTo = null;
        
        if(this.path != null/* && this.rand.nextInt(100) != 0*/) {
        	
        	/*if (this.getDistance(destX, destY, destZ) < 20F) {
        		path = null;
        		return true;
        	}*/
        	
        	//System.out.println("uhh");
            moveTo = this.path.getPosition(this);
            double var6 = (double)(this.width * 1.2); //override on path track distance

            while(moveTo != null && moveTo.squareDistanceTo(this.posX, moveTo.yCoord, this.posZ) < var6 * var6) {
                this.path.incrementPathIndex();

                if(this.path.isFinished()) {
                    moveTo = null;
                    this.path = null;
                    //System.out.println("path done");
                    //remove pf delay!
                    //this.pathfindDelay = 50;
                } else {
                    moveTo = this.path.getPosition(this);
                }
            }

            double oldSpeed = 0.2F;//motionX + motionY + motionZ + 0.01D;

            //System.out.println(oldSpeed);
            /*if (oldSpeed > 0.3F) {
             return;
            }*/
            if (Math.abs(motionX) > oldSpeed || Math.abs(motionY) > oldSpeed || Math.abs(motionZ) > oldSpeed) {
                return true;
            }

            
        } else if (this.getDistance(destX, destY, destZ) > 1.5F) {
        	moveTo = Vec3.createVectorHelper(destX, destY, destZ);
        } else {
        	return false;
        }
        
        //this.isJumping = false;
        if(moveTo != null) {
        	int var21 = MathHelper.floor_double(this.boundingBox.minY + 0.5D);
            double vecX = moveTo.xCoord - this.posX;//tNode.nextNode.bodyPiece.posX - tNode.bodyPiece.posX;
            double vecY = moveTo.yCoord - (double)var21;//tNode.nextNode.bodyPiece.boundingBox.minY + (double)(tNode.nextNode.bodyPiece.height / 2.0F) - (tNode.bodyPiece.posY + (double)(tNode.bodyPiece.height / 2.0F));
            double vecZ = moveTo.zCoord - this.posZ;//tNode.nextNode.bodyPiece.posZ - tNode.bodyPiece.posZ;
            
            if (path == null) {
            	
            	/*float adj = 1000 - this.age;//this.getDistance(moveTo.xCoord, moveTo.yCoord, moveTo.zCoord)
            	if (adj < 0) adj = 0;
            	vecX += (0.03F * adj);
            	vecZ += (0.03F * adj);
            	vecY += (0.03F * adj);*/
            }
            
            double var9 = (double)MathHelper.sqrt_double(vecX * vecX + vecY * vecY + vecZ * vecZ);
            double dist = 6.0F;
            double speed = 0.10D * (dist/6.0F);
            motionX += vecX / var9 * speed;
            motionY += vecY / var9 * speed;
            motionZ += vecZ / var9 * speed;
            /*double var8 = var5.xCoord - this.posX;
            double var10 = var5.zCoord - this.posZ;
            double var12 = var5.yCoord - (double)var21;
            float var14 = (float)(Math.atan2(var10, var8) * 180.0D / 3.1415927410125732D) - 90.0F;
            float var15 = var14 - this.rotationYaw;*/
        }
        
        return true;
    }

    public static boolean notMoving(Entity var0, float var1) {
        double var2 = var0.prevPosX - var0.posX;
        double var4 = var0.prevPosZ - var0.posZ;
        float var6 = (float)Math.sqrt(var2 * var2 + var4 * var4);
        return var6 < var1;
    }

	@Override
	public void setPathExToEntity(PathEntityEx pathentity) {
		// TODO Auto-generated method stub
		this.path = pathentity;
		waitingForPath = false;
		
	}

	@Override
	public void setPathToEntity(PathEntity pathentity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PathEntityEx getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasPath() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void faceCoord(int x, int y, int z, float f, float f1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noMoveTriggerCallback() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void entityInit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getWindWeight() {
		// TODO Auto-generated method stub
		return 9999;
	}

	@Override
	public int getParticleDecayExtra() {
		// TODO Auto-generated method stub
		return 0;
	}
}
