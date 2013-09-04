package weather.blocks.structure;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import weather.WeatherMod;

public class StructurePiece extends Entity
{
    //public int age;
    //public int type;
    //public int mode;

    public boolean noCollision;
    //public boolean collideFalling = false;

    public double vecX;
    public double vecY;
    public double vecZ;
    public double lastPosX;
    public double lastPosZ;
    
    public float relX;
	public float relY;
	public float relZ;

    public List<StructureNode> nodes;
    public List<StructurePiece> childPieces;
    
    public float sizeScale = 1F;
    
    public int temp = 0;

    //public Entity controller;
    //public int gravityDelay;

    public StructurePiece(World var1)
    {
        super(var1);
        //this.mode = 0;
        this.noCollision = true;
        nodes = new LinkedList();
        childPieces = new LinkedList();
        
        sizeScale = 1.00F;
        
        //StructureTemplates.fillTree(nodes, 88, 0);
        //childPieces.clear();
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
    	//Super temp
    	if (temp == 0) {
    		//vecY += worldObj.rand.nextFloat() * 0.15F;
    	} else if (temp == 1) {
    		vecX += 0.2F;
    	} else if (temp == 2) {
    		vecX -= 0.2F;
    	}
    	
    }

    public boolean attackEntityFrom(Entity var1, int var2)
    {
        return false;
    }

    protected void writeEntityToNBT(NBTTagCompound var1)
    {
        //NBTTagCompound var2 = new NBTTagCompound();
        //var1.setCompoundTag("TileEntity", var2);
    }

    protected void readEntityFromNBT(NBTTagCompound var1)
    {
    }

    public float getShadowSize()
    {
        return 0.0F;
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
}
