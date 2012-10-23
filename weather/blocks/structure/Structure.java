package weather.blocks.structure;

import java.util.List;
import java.util.LinkedList;
import java.lang.reflect.Field;

import org.lwjgl.opengl.GL11;

import weather.WeatherMod;

import CoroAI.PathEntityEx;
import CoroAI.c_IEnhAI;
import CoroAI.entity.EnumActState;
import CoroAI.entity.EnumJob;
import CoroAI.entity.JobManager;

import cpw.mods.fml.client.FMLClientHandler;

import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Vec3;
import net.minecraft.src.World;

public class Structure extends EntityLiving
{
    public int age;
    public int type;
    public int state;

    public boolean noCollision;
    public boolean collideFalling = false;

    public double lockX;
    public double lockY;
    public double lockZ;
    public double lastPosX;
    public double lastPosZ;

    public List<StructurePiece> pieces;
    
    public float sizeScale = 1F;
    
    public boolean doTickInit = true;
    
    //AI based stuff
	public Entity entityToAttack;
	public PathEntityEx pathEntity;
	public float maxPFRange = 512;
	public JobManager job;
	private EnumActState currentAction;

    //public Entity controller;
    //public int gravityDelay;

    public Structure(World var1)
    {
        super(var1);
        this.state = 0;
        this.noCollision = true;
        sizeScale = 1.00F;
        pieces = new LinkedList();
        
        this.job = new JobManager(null);
        
        construct();
        
    }
    
    public void construct() {
    	for (int i = 0; i < pieces.size(); i++) {
    		pieces.get(i).nodes.clear();
    	}
    	pieces.clear();
    	
    	//BUILD CODE
    	StructurePiece sp;
    	
    	/*sp = new StructurePiece(worldObj);
        StructureTemplates.fillTest(sp.nodes, 4, 0);
        pieces.add(sp);*/
    	
    	sp = new StructurePiece(worldObj);
        StructureTemplates.fillTower(sp.nodes, 4, 0);
        //StructureTemplates.fillTree(sp.nodes, 4, 0);
        pieces.add(sp);
    	
    	/*sp = new StructurePiece(worldObj);
        StructureTemplates.fillBody(sp.nodes, 4, 0);
        pieces.add(sp);
        
        sp = new StructurePiece(worldObj);
        sp.relX = 2;
        sp.temp = 1;
        StructureTemplates.fillArm(sp.nodes, 8, 0);
        pieces.add(sp);
        
        sp = new StructurePiece(worldObj);
        sp.relX = -2;
        sp.temp = 2;
        StructureTemplates.fillArm(sp.nodes, 10, 0);
        pieces.add(sp);*/
    }

    public boolean isInRangeToRenderDist(double var1)
    {
        return true;
    }

    public boolean canTriggerWalking()
    {
        return false;
    }

    public void entityInit()
    {
        this.dataWatcher.addObject(22, Byte.valueOf((byte)0));
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
    	this.state = this.dataWatcher.getWatchableObjectByte(22);
    	
    	posX = ((int)lockX)+0.5F;
		posY = ((int)lockY)+0.5F;
		posZ = ((int)lockZ)+0.5F;
    }

    public void onUpdate()
    {
    	
    	if (doTickInit) {
    		doTickInit = false;
    		
    		lockX = posX;
        	lockY = posY;
        	lockZ = posZ;
    	}
    	
    	if (worldObj.isRemote) {
    		clientUpdate();
    	}
    	
    	//construct();
    	//setDead();
    	
    	if (state == 0) { //construction
    		
        	posX = ((int)lockX)+0.5F;
    		posY = ((int)lockY)+0.5F;
    		posZ = ((int)lockZ)+0.5F;
    		
    		if (!worldObj.isRemote) {
    		
	    		buildTick();
	    		
	    		boolean buildComplete = true;
	    		
	    		for (int i = 0; i < pieces.size(); i++) {
	            	StructurePiece sp = pieces.get(i);
	            	for (int j = 0; j < sp.nodes.size(); j++) {
	    	        	StructureNode sn = sp.nodes.get(j);
	    	        	
	    	        	int xx = (int)(((int)posX) + sp.relX + sn.relX);
	    	        	int yy = (int)(((int)posY) + sp.relY + sn.relY);
	    	        	int zz = (int)(((int)posZ) + sp.relZ + sn.relZ);
	    	        	
	    	        	int id = worldObj.getBlockId(xx, yy, zz);
	    	        	
	    	        	if (id == 0) {
	    	        		sn.render = true;
	    	        		buildComplete = false;
	    	        	} else {
	    	        		sn.render = false;
	    	        	}
	            	}
	    		}
	    		
	    		if (buildComplete) {
	    			transform();
	    		}
    		}
    		
    	} else if (state == 1) {
    		//vecY += 0.05F;
    		this.rotationYaw += 0.05F;
        	for (int i = 0; i < pieces.size(); i++) {
        		pieces.get(i).onUpdate();
        	}
        	ignoreFrustumCheck = true;
    	}
    	
    	this.dataWatcher.updateObject(22, Byte.valueOf((byte)this.state));
    }
    
    public void callInBlocks(int count) {
    	
    	for (int i = 0; i < pieces.size(); i++) {
        	StructurePiece sp = pieces.get(i);
        	for (int j = 0; j < sp.nodes.size(); j++) {
	        	StructureNode sn = sp.nodes.get(j);
	        	
	        	int xx = (int)(((int)posX) + sp.relX + sn.relX);
	        	int yy = (int)(((int)posY) + sp.relY + sn.relY);
	        	int zz = (int)(((int)posZ) + sp.relZ + sn.relZ);
	        	
	        	int id = worldObj.getBlockId(xx, yy, zz);
	        	
	        	if (/*id == 0 || */sn.needBuild) {
	        		
	        		int curCount = 0;
	        		boolean found = false;
	        		while (!found && curCount++ < count) {
		        		int tryX = (int)posX+worldObj.rand.nextInt(40)-20;
		                int tryY = (int)posY-40+worldObj.rand.nextInt(80);
		                int tryZ = (int)posZ+worldObj.rand.nextInt(40)-20;
		                
		                if (tryRip(worldObj, this, tryX, tryY, tryZ, xx, yy, zz, true)) {
		                	sn.needBuild = false;
		                	//System.out.println("calling out a block");
		                	found = true;
		                }
	        		}
	        		
	        		
	        		//return Vec3.createVectorHelper(xx, yy, zz);
	        		//sn.render = true;
	        		//buildComplete = false;
	        	} else {
	        		//sn.render = false;
	        	}
        	}
		}
    }
    
    public void buildTick() {
    	
    	callInBlocks(100);
    }
    
    public static boolean canGrab(int blockID) {
        if (blockID == Block.dirt.blockID || blockID == Block.grass.blockID || blockID == Block.stone.blockID/* || blockID == Block.sand.blockID*/) {
            return true;
        }

        return false;
    }
    
    public static boolean tryRip(World world, Entity player, int tryX, int tryY, int tryZ, int destX, int destY, int destZ, boolean notify) {

        int blockID = world.getBlockId(tryX,tryY,tryZ);

        if (!canGrab(blockID)) {
            return false;
        }

        if (blockID == 0 || world.getBlockId(tryX,tryY+1,tryZ) != 0 || !world.canBlockSeeTheSky(tryX,tryY+1,tryZ)) {
            return false;
        }

        if (player.getDistance(tryX, tryY, tryZ) < 5) {
            return false;
        }

        world.setBlockWithNotify(tryX,tryY,tryZ, 0);
        PhantomBlock mBlock;

        if (blockID == Block.grass.blockID) {
            mBlock = new PhantomBlock(world,tryX,tryY,tryZ, Block.dirt.blockID);
        } else {
            mBlock = new PhantomBlock(world,tryX,tryY,tryZ, blockID);
        }

        mBlock.controller = null;
        mBlock.type = 0;
        mBlock.motionY = 0.3;
        mBlock.target = player;
        mBlock.destX = destX;
        mBlock.destY = destY;
        mBlock.destZ = destZ;
        mBlock.pathfind(destX, destY, destZ);
        world.spawnEntityInWorld(mBlock);
        return true;
    }
    
    public void transform() {
    	
    	if (true) {
    		setDead();
    		System.out.println("killing Structure");
    		return;
    	}
    	
    	state = 1;
    	
    	for (int i = 0; i < pieces.size(); i++) {
        	StructurePiece sp = pieces.get(i);
        	for (int j = 0; j < sp.nodes.size(); j++) {
	        	StructureNode sn = sp.nodes.get(j);
	        	
	        	int xx = (int)(((int)posX) + sp.relX + sn.relX);
	        	int yy = (int)(((int)posY) + sp.relY + sn.relY);
	        	int zz = (int)(((int)posZ) + sp.relZ + sn.relZ);
	        	
	        	int id = worldObj.getBlockId(xx, yy, zz);
	        	
	        	worldObj.setBlock(xx, yy, zz, 0);
	        	
        	}
		}
    }

    public boolean attackEntityFrom(Entity var1, int var2)
    {
        return false;
    }

    public void writeEntityToNBT(NBTTagCompound var1)
    {
    	super.writeEntityToNBT(var1);
        //NBTTagCompound var2 = new NBTTagCompound();
        //var1.setCompoundTag("TileEntity", var2);
    }

    public void readEntityFromNBT(NBTTagCompound var1)
    {
    	super.readEntityFromNBT(var1);
    	
    	System.out.println("killing Structure on load");
    	setDead();
    	
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
    
    public void initJobAndStates(EnumJob job, boolean initItems) {
		
		/*if (this.rand.nextInt(2) == 0) {
			job = EnumJob.HUNTER;
		} else {
			job = EnumJob.FISHERMAN;
		}*/
		//System.out.println("init job " + job);
		
		this.job.setPrimaryJob(job);
		
		//this.job.swapJob(job);
		this.job.clearJobs();
		if (job == EnumJob.HUNTER) {
			addJob(EnumJob.FINDFOOD);
	        addJob(EnumJob.HUNTER);
		} else if (job == EnumJob.FISHERMAN) {
			addJob(EnumJob.FINDFOOD);
	        addJob(EnumJob.FISHERMAN);
		} else if (job == EnumJob.GATHERER) {
			addJob(EnumJob.GATHERER);
		} else {
			addJob(EnumJob.UNEMPLOYED);
			//addJob(EnumJob.FINDFOOD);
	        //addJob(EnumJob.HUNTER);
		}
		
		if (initItems) {
			
			//setOccupationItems();
			//if (entID == -1) entID = rand.nextInt(999999999);
		}
	}
    
    public void swapJob(EnumJob job) {
		setState(EnumActState.IDLE);
		this.job.swapJob(job);
	}
	
	public void addJob(EnumJob job) {
		//setState(EnumActState.IDLE);
		this.job.addJob(job);
	}
	
	public void setState(EnumActState eka) {
		currentAction = eka;
	}

	public boolean hasPath() {
		// TODO Auto-generated method stub
		return pathEntity != null && !pathEntity.isFinished();
	}

	public int getHealth() {
		// TODO Auto-generated method stub
		return this.health;
	}

	@Override
	public int getMaxHealth() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isEnemy(Entity entity1) {
		// TODO Auto-generated method stub
		return false;
	}

	public void huntTarget(Entity clEnt) {
		// TODO Auto-generated method stub
		
	}
}
