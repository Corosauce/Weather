package weather.blocks.structure.tree;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;



public class TileEntityVectorTree extends TileEntity
{
	
	public boolean hasInit = false;
	public GrowthManager growMan;
	
    public TileEntityVectorTree()
    {
    	Random rand = new Random();
    }
    
    public void init() {
    	hasInit = true;
    	
    	growMan = new GrowthManager(worldObj, new ChunkCoordinates(xCoord, yCoord, zCoord));
    }
    
    public void onClicked() {
    	
    }

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    public void updateEntity()
    {
    	if (!this.worldObj.isRemote) {
	    	
    		if (!hasInit) init();
    		
    		if (growMan != null) {
    			growMan.tickGrowth();
    		}
	    	
	    	watchVariables();
    	}

        super.updateEntity();
        
    }
    
    public void watchVariables() {
    	
    	
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.hasInit = par1NBTTagCompound.getBoolean("hasInit");
        //this.itemIndex = par1NBTTagCompound.getInteger("itemIndex");
        //this.cycleCurDelay = par1NBTTagCompound.getInteger("cycleCurDelay");
        //cycleItems = par1NBTTagCompound.getBoolean("cycleItems");
        
        //this.delay = par1NBTTagCompound.getShort("Delay");
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setBoolean("hasInit", hasInit);
        //par1NBTTagCompound.setInteger("itemIndex", this.itemIndex);
        //par1NBTTagCompound.setInteger("cycleCurDelay", this.cycleCurDelay);
        //par1NBTTagCompound.setBoolean("cycleItems", cycleItems);
        //par1NBTTagCompound.setShort("Delay", (short)this.delay);
    }
    
    @Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
    	this.readFromNBT(pkt.customParam1);
    }
    
    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound var1 = new NBTTagCompound();
        this.writeToNBT(var1);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 0, var1);
    }
}
