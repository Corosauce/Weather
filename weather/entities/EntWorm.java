package weather.entities;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;


public class EntWorm extends Entity
{
    public int type = 0;
    public boolean isUsed = false;
    public int strength;
    public int age;
    public int noSeeTicks = 0;
    public WormNode head;
    public WormNode tail;

    public EntWorm(World var1)
    {
        super(var1);
        this.worldObj = var1;
        this.isImmuneToFire = true;
        this.setSize(0.1F, 0.1F);
        this.strength = 100;
        this.age = 0;
        if (!worldObj.isRemote) {
        	this.head = this.newPiece(this.newPiece(this.newPiece(this.newPiece(this.newPiece(this.newPiece(this.newPiece(this.newPiece(this.newPiece(this.newPiece(this.newPiece(this.newPiece(this.newPiece(this.newPiece(this.newPiece(this.newPiece(this.newPiece(this.newPiece(this.newPiece(this.newPiece(this.newPiece(this.newPiece((WormNode)null))))))))))))))))))))));
        	this.tail = this.getTail();
        	this.head.bodyPiece.noCollision = false;
        }
        
        
    }

    public WormNode destroy()
    {
        WormNode var1;

        for (var1 = this.head; var1.prevNode != null; var1 = var1.prevNode)
        {
            var1.bodyPiece.type = 0;
            var1.bodyPiece.mode = 0;
            var1.bodyPiece.noCollision = false;
        }

        var1.bodyPiece.type = 0;
        var1.bodyPiece.mode = 0;
        var1.bodyPiece.noCollision = false;
        return var1;
    }

    public void addToTail(int var1)
    {
        this.tail.prevNode = this.newPiece((WormNode)null);
        this.tail.prevNode.nextNode = this.tail;
        this.tail = this.tail.prevNode;
    }

    public WormNode getTail()
    {
        WormNode var1;

        for (var1 = this.head; var1.prevNode != null; var1 = var1.prevNode)
        {
            ;
        }

        return var1;
    }

    public WormNode newPiece(WormNode var1)
    {
        WormNode var2 = new WormNode();
        var2.bodyPiece = this.newBlock();
        var2.prevNode = var1;

        if (var1 != null)
        {
            var1.nextNode = var2;
        }

        var2.nextNode = null;
        return var2;
    }

    public MovingBlock newBlock(int var1, int var2, int var3)
    {
        this.worldObj.setBlock(var1, var2, var3, 0, 0, 2);
        MovingBlock var4 = new MovingBlock(this.worldObj, var1, var2, var3, 51);
        this.worldObj.spawnEntityInWorld(var4);
        return var4;
    }

    public MovingBlock newBlock()
    {
        int var1 = 0;
        int var2 = 0;
        int var3 = 0;

        if (this.tail != null)
        {
            var1 = (int)this.tail.bodyPiece.posX;
            var2 = (int)this.tail.bodyPiece.posX;
            var3 = (int)this.tail.bodyPiece.posX;
        }

        MovingBlock var4 = new MovingBlock(this.worldObj, var1, var2, var3, 51);
        var4.type = 1;
        var4.noCollision = true;
        var4.controller = this;
        this.worldObj.spawnEntityInWorld(var4);
        return var4;
    }

    @Override
    public void setDead()
    {
        if (!worldObj.isRemote) this.destroy();
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

    public void doTailPhysics()
    {
        WormNode var1 = this.head;
        this.setPosition(var1.bodyPiece.posX, var1.bodyPiece.posY, var1.bodyPiece.posZ);
        int var2 = 0;

        while (var1.prevNode != null)
        {
            var1 = var1.prevNode;
            ++var2;
            float var3 = var1.bodyPiece.getDistanceToEntity(var1.nextNode.bodyPiece);

            if (var3 > 45.0F)
            {
                var1.bodyPiece.setPosition(var1.nextNode.bodyPiece.posX, var1.nextNode.bodyPiece.posY, var1.nextNode.bodyPiece.posZ);
            }

            if (var3 > 0.5F)
            {
                double var4 = var1.nextNode.bodyPiece.posX - var1.bodyPiece.posX;
                double var6 = var1.nextNode.bodyPiece.boundingBox.minY + (double)(var1.nextNode.bodyPiece.height / 2.0F) - (var1.bodyPiece.posY + (double)(var1.bodyPiece.height / 2.0F));
                double var8 = var1.nextNode.bodyPiece.posZ - var1.bodyPiece.posZ;
                var1.bodyPiece.vecX = var4;
                var1.bodyPiece.vecY = var6;
                var1.bodyPiece.vecZ = var8;
                double var10 = (double)MathHelper.sqrt_double(var4 * var4 + var6 * var6 + var8 * var8);
                double var12 = 0.1D * (double)(var3 / 6.0F);
                var1.bodyPiece.motionX += var4 / var10 * var12;
                var1.bodyPiece.motionY += var6 / var10 * var12;
                var1.bodyPiece.motionZ += var8 / var10 * var12;
                double maxSpeed = 0.4D;

                while (MathHelper.sqrt_double(var1.bodyPiece.motionX * var1.bodyPiece.motionX + var1.bodyPiece.motionY * var1.bodyPiece.motionY + var1.bodyPiece.motionZ * var1.bodyPiece.motionZ) > maxSpeed)
                {
                    var1.bodyPiece.motionX *= 0.9D;
                    var1.bodyPiece.motionY *= 0.9D;
                    var1.bodyPiece.motionZ *= 0.9D;
                    //System.out.println(MathHelper.sqrt_double(var1.bodyPiece.motionX * var1.bodyPiece.motionX + var1.bodyPiece.motionY * var1.bodyPiece.motionY + var1.bodyPiece.motionZ * var1.bodyPiece.motionZ));
                    //break;
                }
            }
        }
    }

    public void onUpdate()
    {
        super.onUpdate();

        if (this.age < 450)
        {
            ++this.age;
        }
        else
        {
            this.setDead();
        }

        if (this.handleWaterMovement())
        {
            for (int var1 = 0; var1 < 4; ++var1)
            {
                float var2 = 0.25F;
                this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double)var2, this.posY - this.motionY * (double)var2, this.posZ - this.motionZ * (double)var2, this.motionX, this.motionY, this.motionZ);
            }
        }
        
        if (worldObj.isRemote) return;

        double var11 = this.head.bodyPiece.motionX + this.rand.nextGaussian() * 0.4D - 0.2D;
        double var3 = this.head.bodyPiece.motionY + this.rand.nextGaussian() * 0.4D;
        double var5 = this.head.bodyPiece.motionZ + this.rand.nextGaussian() * 0.4D - 0.2D;
        double var7 = (double)MathHelper.sqrt_double(var11 * var11 + var3 * var3 + var5 * var5);
        double var9 = 0.02D;
        this.head.bodyPiece.motionX += var11 / var7 * var9;
        this.head.bodyPiece.motionY += var3 / var7 * var9;
        this.head.bodyPiece.motionZ += var5 / var7 * var9;
        this.doTailPhysics();
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
