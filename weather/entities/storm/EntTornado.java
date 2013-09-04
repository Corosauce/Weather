package weather.entities.storm;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ModLoader;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import weather.WeatherEntityConfig;
import weather.WeatherMod;
import weather.config.ConfigTornado;
import weather.config.ConfigWavesMisc;
import weather.entities.MovingBlock;
import weather.system.wind.WindHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
//@SideOnly(Side.CLIENT)
public class EntTornado extends Entity implements WindHandler
{
    public WeatherEntityConfig entConf;
    public int entConfID;

    public int type = 0;
    public boolean isUsed = false;
    public float strength;
    public int age;

    public EffectRenderer effR;

    public int noSeeTicks = 0;
    public long blockTrimDelay = 0;

    public int maxYHeight = 90;
    public double lastPosX;
    public double lastPosY;
    public double lastPosZ;

    public double lastMotionX;
    public double lastMotionZ;

    public List funnelEffects;
    public List activeBlocks;

    public long directionChangeDelay;

    public Map entToAge = new HashMap();

    public byte direction;

    public float realYaw;

    public String snd_dmg_close[] = new String[3];
    public String snd_wind_close[] = new String[3];
    public String snd_wind_far[] = new String[3];

    public int snd_rand[] = new int[3];
    public long soundTimer[] = new long[3];
    public int soundID[] = new int[3];

    /*public int snd_dmg_rand;
    public int snd_wind_close_rand;
    public int snd_wind_far_rand;*/

    public static Map blockIDToUseMapping = new HashMap();
    public static Map soundToLength = new HashMap();

    public boolean lastTickPlayerClose;

    public long lastSoundPositionUpdate;
    public long lastTickRunTime;

    public boolean spawning;
    public boolean dying;

    public int ripCount = 0;

    public long lastGrabTime;
    public int tickGrabCount;
    public int removeCount;
    public int tryRipCount;

    public float speedAdjust;
    public boolean hitMaxTriesLastTick;

    public float spawnYOffset = 0F;

    //public List funnelEffects;

    //public World worldRef;
    
    public float scale = 1F;

    public EntTornado(World var1)
    {
        this(var1, (WeatherEntityConfig)WeatherMod.weatherEntTypes.get(1), 1);
        this.effR = ModLoader.getMinecraftInstance().effectRenderer;
        this.funnelEffects = new ArrayList();
        this.activeBlocks = new ArrayList();
    }

    public EntTornado(World var1, WeatherEntityConfig config, int type)
    {
        super(var1);
        
        entConf = config;
        entConfID = type;
        spawning = true;
        dying = false;
        worldObj = var1;
        WeatherMod.activeTornado = this;
        this.isImmuneToFire = true;
        this.setSize(1.1F, 1.1F);
        this.strength = 0;
        this.age = 0;
        this.funnelEffects = new ArrayList();
        this.activeBlocks = new ArrayList();
        direction = 0;
        realYaw = rotationYaw;
        speedAdjust = 1.0F;
        snd_dmg_close[0] = "destruction_0_";
        snd_dmg_close[1] = "destruction_1_";
        snd_dmg_close[2] = "destruction_2_";
        snd_wind_close[0] = "wind_close_0_";
        snd_wind_close[1] = "wind_close_1_";
        snd_wind_close[2] = "wind_close_2_";
        snd_wind_far[0] = "wind_far_0_";
        snd_wind_far[1] = "wind_far_1_";
        snd_wind_far[2] = "wind_far_2_";
        snd_rand[0] = rand.nextInt(3);
        snd_rand[1] = rand.nextInt(3);
        snd_rand[2] = rand.nextInt(3);
        soundID[0] = -1;
        soundID[1] = -1;
        soundID[2] = -1;
        soundToLength.put(snd_dmg_close[0], 2515);
        soundToLength.put(snd_dmg_close[1], 2580);
        soundToLength.put(snd_dmg_close[2], 2741);
        soundToLength.put(snd_wind_close[0], 4698);
        soundToLength.put(snd_wind_close[1], 7324);
        soundToLength.put(snd_wind_close[2], 6426);
        soundToLength.put(snd_wind_far[0], 12892);
        soundToLength.put(snd_wind_far[1], 9653);
        soundToLength.put(snd_wind_far[2], 12003);
        lastTickPlayerClose = false;
        spawnYOffset = 128;
    }

    @Override
    public void setDead()
    {
        //if (worldObj.isRemote) return;
    	if (ConfigWavesMisc.debug)
        {
            System.out.println("setdead, remote: " + worldObj.isRemote);
        }

        if (worldObj.isRemote) {
        	WeatherMod.particleCount = 0;
        } else {
        	WeatherMod.blockCount = 0;
        }
        /*if (mod_EntMover.activeTornadoes > 0) */
        //WeatherMod.activeTornadoes--;
        super.setDead();
    }

    @Override
    protected boolean canTriggerWalking()
    {
        return false;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return false;
    }

    @Override
    public boolean canBePushed()
    {
        return false;
    }

    @Override
    public boolean isInRangeToRenderDist(double var1)
    {
        return true;
    }

    @Override
    public float getEyeHeight()
    {
        return 20F;
    }

    @Override
    public void entityInit()
    {
        //Dissipating
        this.dataWatcher.addObject(10, Byte.valueOf((byte) 0));
    }
    
    public boolean isNoDigCoord(int x, int y, int z) {

        // MCPC start
          /*org.bukkit.entity.Entity bukkitentity = this.getBukkitEntity();
          if ((bukkitentity instanceof Player)) {
            Player player = (Player)bukkitentity;
            BlockBreakEvent breakev = new BlockBreakEvent(player.getWorld().getBlockAt(x, y, z), player);
            Bukkit.getPluginManager().callEvent(breakev);
            if (breakev.isCancelled()) {
                return true;
            }
          }*/
          // MCPC end
          
          return false;
    }

    public boolean tryRip(int tryX, int tryY, int tryZ, boolean notify)
    {
        if (!ConfigTornado.Storm_Tornado_grabBlocks) return true;
        
        if (isNoDigCoord(tryX, tryY, tryZ)) return true;
        
        if (worldObj.isRemote)
        {
            int what = 0;
        }

        boolean seesLight = false;
        int blockID = this.worldObj.getBlockId(tryX, tryY, tryZ);

        //System.out.println(worldObj.getHeightValue(tryX, tryZ));
        if (( /*(canGrab(blockID)) &&blockID != 0 ||*/
                ((worldObj.getHeightValue(tryX, tryZ) - 1 == tryY) ||
                        worldObj.getHeightValue(tryX + 1, tryZ) - 1 < tryY ||
                        worldObj.getHeightValue(tryX, tryZ + 1) - 1 < tryY ||
                        worldObj.getHeightValue(tryX - 1, tryZ) - 1 < tryY ||
                        worldObj.getHeightValue(tryX, tryZ - 1) - 1 < tryY))
                /*(worldObj.getBlockId(tryX,tryY+1,tryZ) == 0 ||
                 worldObj.getBlockId(tryX+1,tryY,tryZ) == 0 ||
                 worldObj.getBlockId(tryX,tryY,tryZ+1) == 0 ||
                 worldObj.getBlockId(tryX-1,tryY,tryZ) == 0 ||
                 worldObj.getBlockId(tryX,tryY,tryZ-1) == 0 ||
                 worldObj.getBlockId(tryX+1,tryY+1,tryZ) == 0 ||
                 worldObj.getBlockId(tryX,tryY+1,tryZ+1) == 0 ||
                 worldObj.getBlockId(tryX-1,tryY+1,tryZ) == 0 ||
                 worldObj.getBlockId(tryX,tryY+1,tryZ-1) == 0)*/
           )
        {
            if (WeatherMod.shouldRemoveBlock(blockID))
            {
                removeCount++;

                if (notify)
                {
                    worldObj.setBlock(tryX, tryY, tryZ, 0, 0, 3);
                }
                else
                {
                    worldObj.setBlock(tryX, tryY, tryZ, 0, 0, 0);
                }
            }

            if (worldObj.getChunkProvider().chunkExists((int)posX / 16, (int)posZ / 16) && /*mod_EntMover.getFPS() > mod_EntMover.safetyCutOffFPS && */WeatherMod.blockCount <= ConfigTornado.Storm_Tornado_maxBlocks && lastGrabTime < System.currentTimeMillis() && tickGrabCount < 10)
            {
                lastGrabTime = System.currentTimeMillis() - 5;
                //int blockMeta = this.worldObj.getBlockMetadata(tryX,tryY,tryZ);
                //rip noise, nm, forces particles
                //worldObj.playAuxSFX(2001, tryX, tryY, tryZ, blockID + blockMeta * 256);

                if (blockID != Block.snow.blockID && blockID != Block.glass.blockID)
                {
                    MovingBlock mBlock;

                    if (blockID == Block.grass.blockID)
                    {
                        mBlock = new MovingBlock(worldObj, tryX, tryY, tryZ, Block.dirt.blockID);
                    }
                    else
                    {
                        mBlock = new MovingBlock(worldObj, tryX, tryY, tryZ, blockID);
                    }

                    WeatherMod.blockCount++;
                    
                    //if (WeatherMod.debug && worldObj.getWorldTime() % 60 == 0) System.out.println("ripping, count: " + WeatherMod.blockCount);

                    //if (!worldObj.isRemote)
                    if (!worldObj.isRemote)
                    {
                        worldObj.spawnEntityInWorld(mBlock);

                        //worldObj.addWeatherEffect(mBlock);
                        if (worldObj instanceof WorldServer)
                        {
                            //((WorldServer) worldObj).getEntityTracker().addEntityToTracker(mBlock);
                        }
                    }

                    mBlock.setPosition(tryX, tryY, tryZ);
                    //this.activeBlocks.add(mBlock);
                    tickGrabCount++;
                    ripCount++;

                    if (ripCount % 10 == 0)
                    {
                        //System.out.println(ripCount);
                    }
                    else
                    {
                        //System.out.print(ripCount + " - ");
                    }

                    mBlock.controller = this;
                    mBlock.type = 0;
                    seesLight = true;
                }
                else
                {
                    //depreciated - OR NOT!
                    if (blockID == Block.glass.blockID)
                    {
                        worldObj.playSoundEffect(tryX, tryY, tryZ, "random.glass", 5.0F, 1.0F);
                    }

                    //break snow effect goes here
                    //ModLoader.getMinecraftInstance().effectRenderer.addBlockDestroyEffects(tryX,tryY,tryZ, blockID, 0);
                }
            }
        }

        return seesLight;
    }

    public boolean canGrab(int blockID)
    {
        if (blockID != 0 && WeatherMod.shouldGrabBlock(worldObj, blockID))
        {
            return true;
        }

        //if (true) return false;
        if (WeatherMod.grabFiniteWater && WeatherMod.tryFinite)
        {
            try
            {
                if (Class.forName("BlockNWater").isInstance(Block.blocksList[blockID]))
                {
                    return true;
                }

                if (Class.forName("BlockNWater_Ocean").isInstance(Block.blocksList[blockID]))
                {
                    return true;
                }

                if (Class.forName("BlockNWater_Pressure").isInstance(Block.blocksList[blockID]))
                {
                    return true;
                }

                if (Class.forName("BlockNWater_Still").isInstance(Block.blocksList[blockID]))
                {
                    return true;
                }
            }
            catch (Exception exception)
            {
                WeatherMod.tryFinite = false;
                return false;
            }
        }

        return false;
    }

    public void startDissipate()
    {
        //if (worldObj.isRemote) return;
        //this.dataWatcher.updateObject(10, Byte.valueOf((byte) 1));
    	
    	if (!worldObj.isRemote) {
	        syncDelay--;
	
	        if (syncDelay <= 0 && MinecraftServer.getServer() != null)
	        {
	            syncDelay = 5;
	            ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
	            DataOutputStream dos = new DataOutputStream(bos);
	
	            try
	            {
	            	dos.writeInt(2);
	            	dos.writeInt(this.entityId);
	                
	            }
	            catch (Exception ex)
	            {
	                ex.printStackTrace();
	            }
	
	            Packet250CustomPayload pkt = new Packet250CustomPayload();
	            pkt.channel = "Tornado";
	            pkt.data = bos.toByteArray();
	            pkt.length = bos.size();
	            //pkt.isChunkDataPacket = true;
	            MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(pkt);
	        }
        }

        if (ConfigWavesMisc.debug)
        {
            //System.out.println("dissipate");
        }

        dying = true;
    }

    public boolean isWeakBlock(int id)
    {
        return (id == 0 ||
                id == Block.leaves.blockID ||
                id == Block.wood.blockID ||
                id == Block.dirt.blockID ||
                id == Block.grass.blockID ||
                id == Block.snow.blockID ||
                id == Block.gravel.blockID);
    }

    public boolean isSolidBlock(int id)
    {
        return (id == Block.stone.blockID ||
                id == Block.cobblestone.blockID ||
                id == Block.sandStone.blockID);
    }

    public boolean isAvoidBlock(int id)
    {
        if (getStorm().type != getStorm().TYPE_SPOUT)
        {
            return isSolidBlock(id);
        }
        else
        {
            return isSolidBlock(id);
            //derp, avoid water? what?
            /*if (Block.blocksList[id] != null && Block.blocksList[id].blockMaterial == Material.water) {
            	return true;
            }*/
            //return false;
        }
    }

    public void getNewYPos()
    {
        //System.out.println("check height");
        int tryY = 255;

        while (isWeakBlock(this.worldObj.getBlockId((int)posX, tryY, (int)posZ))/* && !isSolidBlock(this.worldObj.getBlockId((int)posX, tryY, (int)posZ)) && this.worldObj.getBlockId((int)posX, tryY, (int)posZ) != 7 && this.worldObj.getBlockId((int)posX, tryY, (int)posZ) != 8*/)
        {
            tryY--;

            if (tryY < 0)
            {
                tryY = 5;
                break;
            }
        }

        if (tryY - posY > ConfigTornado.Storm_Tornado_maxYChange)
        {
            //System.out.println("tornadoMaxYChange trigger - " + (tryY - posY));
            //startDissipate();
        }

        if (getStorm().type == getStorm().TYPE_SPOUT)
        {
            int id = worldObj.getBlockId((int)posX, tryY - 1, (int)posZ);

            if (Block.blocksList[id] != null && Block.blocksList[id].blockMaterial != Material.water)
            {
                startDissipate();
            }
        }

        /*if (tryY > this.maxYHeight) {
           setEntityDead();
        }*/
        lastPosX = posX;
        lastPosZ = posZ;
        //if (Math.abs(this.posY - tryY) > 2) {
        this.posY = tryY + 1;
        setLocationAndAngles(posX, posY, posZ, this.rotationYaw, 0F);
        //}
    }

    public void checkFront()
    {
        float look = -10F;
        //int height = 10;
        double dist = 40F;
        double leftX = posX + (double)(-Math.sin((rotationYaw + look) / 180.0F * (float)Math.PI) * Math.cos(rotationPitch / 180.0F * (float)Math.PI)) * dist;
        double leftZ = posZ + (double)(Math.cos((rotationYaw + look) / 180.0F * (float)Math.PI) * Math.cos(rotationPitch / 180.0F * (float)Math.PI)) * dist;
        look += -10;
        double leftX2 = posX + (double)(-Math.sin((rotationYaw + look) / 180.0F * (float)Math.PI) * Math.cos(rotationPitch / 180.0F * (float)Math.PI)) * dist;
        double leftZ2 = posZ + (double)(Math.cos((rotationYaw + look) / 180.0F * (float)Math.PI) * Math.cos(rotationPitch / 180.0F * (float)Math.PI)) * dist;
        look += -10;
        double leftX3 = posX + (double)(-Math.sin((rotationYaw + look) / 180.0F * (float)Math.PI) * Math.cos(rotationPitch / 180.0F * (float)Math.PI)) * dist;
        double leftZ3 = posZ + (double)(Math.cos((rotationYaw + look) / 180.0F * (float)Math.PI) * Math.cos(rotationPitch / 180.0F * (float)Math.PI)) * dist;
        look = 10F;
        double rightX = posX + (double)(-Math.sin((rotationYaw + look) / 180.0F * (float)Math.PI) * Math.cos(rotationPitch / 180.0F * (float)Math.PI)) * dist;
        double rightZ = posZ + (double)(Math.cos((rotationYaw + look) / 180.0F * (float)Math.PI) * Math.cos(rotationPitch / 180.0F * (float)Math.PI)) * dist;
        look += 10;
        double rightX2 = posX + (double)(-Math.sin((rotationYaw + look) / 180.0F * (float)Math.PI) * Math.cos(rotationPitch / 180.0F * (float)Math.PI)) * dist;
        double rightZ2 = posZ + (double)(Math.cos((rotationYaw + look) / 180.0F * (float)Math.PI) * Math.cos(rotationPitch / 180.0F * (float)Math.PI)) * dist;
        look += 10;
        double rightX3 = posX + (double)(-Math.sin((rotationYaw + look) / 180.0F * (float)Math.PI) * Math.cos(rotationPitch / 180.0F * (float)Math.PI)) * dist;
        double rightZ3 = posZ + (double)(Math.cos((rotationYaw + look) / 180.0F * (float)Math.PI) * Math.cos(rotationPitch / 180.0F * (float)Math.PI)) * dist;
        direction = 0;

        for (int yy = 15; yy < 50; yy += 5)
        {
            int realY = (int)posY + yy;

            if (realY > 255)
            {
                realY = 255;
            }

            //might work, might need redesign for other look coords
            if (getStorm().type == getStorm().TYPE_SPOUT)
            {
                realY = this.worldObj.getHeightValue((int)leftX, (int)leftZ);
            }

            if (isAvoidBlock(this.worldObj.getBlockId((int)leftX, realY, (int)leftZ)) ||
                    isAvoidBlock(this.worldObj.getBlockId((int)leftX2, realY, (int)leftZ2)) ||
                    isAvoidBlock(this.worldObj.getBlockId((int)leftX3, realY, (int)leftZ3)) ||
                    !this.worldObj.checkChunksExist((int)leftX3, 0, (int)leftZ3, (int)leftX3, 128, (int)leftZ3))
            {
                direction = 1;
                break;
            }
            else if (isAvoidBlock(this.worldObj.getBlockId((int)rightX, realY, (int)rightZ)) ||
                    isAvoidBlock(this.worldObj.getBlockId((int)rightX2, realY, (int)rightZ2)) ||
                    isAvoidBlock(this.worldObj.getBlockId((int)rightX3, realY, (int)rightZ3)) ||
                    !this.worldObj.checkChunksExist((int)rightX3, 0, (int)rightZ3, (int)rightX3, 128, (int)rightZ3))
            {
                direction = -1;
                break;
            }
            else
            {
                direction = 0;
            }
        }

        //mod_MovePlus.displayMessage(new StringBuilder().append(direction).toString());
    }

    public void checkArea()
    {
        int scanSize = 40;
        int hScanSize = scanSize / 2;
        int scanResolution = 5;
        int height = 10;

        for (int xx = (int)posX - hScanSize; xx <= posX + hScanSize; xx += scanResolution)
        {
            for (int yy = (int)posY + height; yy <= posY + scanSize; yy += scanResolution)
            {
                for (int zz = (int)posZ - hScanSize; zz <= posZ + hScanSize; zz += scanResolution)
                {
                    if (isSolidBlock(this.worldObj.getBlockId(xx, (int)yy + height, zz)) || !this.worldObj.checkChunksExist(xx, 0, zz, xx, 128, zz))
                    {
                        //System.out.print("!");
                        if (changeDirection())
                        {
                            return;
                        }
                    }
                }
            }
        }

        //System.out.println("??? - " + this.rotationYaw + " - pl - " + ModLoader.getMinecraftInstance().thePlayer.rotationYaw);
    }

    public boolean changeDirection()
    {
        if (directionChangeDelay > System.currentTimeMillis())
        {
            return false;
        }

        int scanSize = 120;
        int hScanSize = scanSize / 2;
        int scanResolution = 5;
        int height = 10;

        for (int xx = (int)posX - hScanSize; xx <= posX + hScanSize; xx += scanResolution)
        {
            for (int zz = (int)posZ - hScanSize; zz <= posZ + hScanSize; zz += scanResolution)
            {
                if (isWeakBlock(this.worldObj.getBlockId(xx, (int)posY, zz)))
                {
                    if (getDistance(xx, posY, zz) > 10)
                    {
                        if (canSeeArea(xx, (int)posY + height, zz) && this.worldObj.checkChunksExist(xx, 0, zz, xx, 128, zz))
                        {
                            if (withinAngle(xx, zz, 60))
                            {
                                setDirection(xx, posY + height, zz);
                                directionChangeDelay = System.currentTimeMillis() + 1500;
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean withinAngle(double x, double z, double maxAngle)
    {
        double var8 = x - this.posX;
        double var10 = z - this.posZ;
        //double var12 = var5.yCoord - (double)var21;
        float var14 = (float)(Math.atan2(var10, var8) * 180.0D / Math.PI);
        //var14*=-1;
        //System.out.println(var14);
        float var15 = 180 - var14 - this.rotationYaw;

        while (var15 >= 180.0F)
        {
            var15 -= 360.0F;
        }

        while (var15 < -180.0F)
        {
            var15 += 360.0F;
        }

        if (var15 < maxAngle && var15 > maxAngle * -1)
        {
            /*System.out.println("");
            System.out.println("change!");
            System.out.println(var15);*/
            return true;
        }

        /*if (var15 < maxAngle && var15 > maxAngle*-1) {
           System.out.println("change!");
           System.out.println(var15);
           return true;
        }*/
        /*System.out.print(" - fail! - ");
        System.out.print(var15);*/
        return false;
    }

    public void setDirection(double x, double y, double z)
    {
        double vecX = x - posX;
        //double vecY = tNode.nextNode.bodyPiece.boundingBox.minY + (double)(tNode.nextNode.bodyPiece.height / 2.0F) - (posY + (double)(height / 2.0F));
        double vecZ = z - posZ;
        //mod_MovePlus.displayMessage(new StringBuilder().append("vecX: " + vecX).toString());
        //vecX = vecX;//tNode.bodyPiece.posX - tNode.nextNode.bodyPiece.posX;
        //tNode.bodyPiece.vecY = vecY;//tNode.bodyPiece.posY - tNode.nextNode.bodyPiece.posY;
        //tNode.bodyPiece.vecZ = vecZ;//tNode.bodyPiece.posZ - tNode.nextNode.bodyPiece.posZ;
        double var9 = (double)MathHelper.sqrt_double(vecX * vecX + vecZ * vecZ);
        double speed = getStorm().tornadoInitialSpeed;
        lastMotionX = vecX / var9 * speed;
        lastMotionZ = vecZ / var9 * speed;
    }

    public WeatherEntityConfig getStorm()
    {
        return this.entConf;// ((WeatherEntityConfig)mod_EntMover.weatherEntTypes.get(mod_EntMover.activeStormID));
    }

    public boolean canSeeArea(int x, int y, int z)
    {
        return this.worldObj.clip(Vec3.createVectorHelper(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ), Vec3.createVectorHelper((double)x, (double)y, (double)z)) == null;
    }

    @SideOnly(Side.CLIENT)
    public void soundUpdates()
    {
        if (ModLoader.getMinecraftInstance().thePlayer == null)
        {
            return;
        }

        //close sounds
        int far = 200;
        int close = 120;
        float volScaleFar = (far - this.getDistanceToEntity(ModLoader.getMinecraftInstance().thePlayer)) / far;
        float volScaleClose = (close - this.getDistanceToEntity(ModLoader.getMinecraftInstance().thePlayer)) / close;

        if (volScaleFar < 0F)
        {
            volScaleFar = 0.0F;
        }

        if (volScaleClose < 0F)
        {
            volScaleClose = 0.0F;
        }

        if (this.getDistanceToEntity(ModLoader.getMinecraftInstance().thePlayer) < close)
        {
            if (!lastTickPlayerClose)
            {
                /*this.soundTimer[0] = System.currentTimeMillis();
                this.soundTimer[1] = System.currentTimeMillis();
                tryPlaySound(snd_dmg_close, 0, ModLoader.getMinecraftInstance().thePlayer, volScaleClose);
                tryPlaySound(snd_wind_close, 1, ModLoader.getMinecraftInstance().thePlayer, volScaleClose);*/
            }

            lastTickPlayerClose = true;
            //tryPlaySound(snd_dmg_close[0], 0);
            //tryPlaySound(snd_dmg_close[0], 0);
        }
        else
        {
            lastTickPlayerClose = false;
        }

        if (this.getDistanceToEntity(ModLoader.getMinecraftInstance().thePlayer) < far)
        {
            tryPlaySound(snd_wind_far, 2, ModLoader.getMinecraftInstance().thePlayer, volScaleFar);
            //tryPlaySound(snd_dmg_close[0], 0);
            //tryPlaySound(snd_dmg_close[0], 0);
            tryPlaySound(snd_wind_close, 1, ModLoader.getMinecraftInstance().thePlayer, volScaleClose);

            if (getStorm().type == getStorm().TYPE_TORNADO)
            {
                tryPlaySound(snd_dmg_close, 0, ModLoader.getMinecraftInstance().thePlayer, volScaleClose);
            }
        }

        if (this.getDistanceToEntity(ModLoader.getMinecraftInstance().thePlayer) < far && lastSoundPositionUpdate < System.currentTimeMillis())
        {
            //System.out.println(sndSys);
            //int j = (int)(field.getFloat(item)
            lastSoundPositionUpdate = System.currentTimeMillis() + 100;

            //float gameVol = ModLoader.getMinecraftInstance().gameSettings.soundVolume;
            if (soundID[0] > -1 && soundTimer[0] < System.currentTimeMillis())
            {
                WeatherMod.setVolume(new StringBuilder().append("sound_" + soundID[0]).toString(), volScaleClose);
            }

            if (soundID[1] > -1 && soundTimer[1] < System.currentTimeMillis())
            {
                WeatherMod.setVolume(new StringBuilder().append("sound_" + soundID[1]).toString(), volScaleClose);
            }

            if (soundID[2] > -1 && soundTimer[2] < System.currentTimeMillis())
            {
                WeatherMod.setVolume(new StringBuilder().append("sound_" + soundID[2]).toString(), volScaleFar);
            }
        }

        //System.out.println(volScaleClose);
        //System.out.println(this.getDistanceToEntity(ModLoader.getMinecraftInstance().thePlayer));
        //worldObj.playRecord("destruction2", (int)posX, (int)posY, (int)posZ);
        //worldObj.playSoundEffect(posX, posY, posZ, "tornado.destruction", 1F, 1.0F);
        //worldObj.playSoundAtEntity(ModLoader.getMinecraftInstance().thePlayer, "tornado.destruction", 1.0F, 1.0F);
        //worldObj.playRecord("tornado.destruction", (int)ModLoader.getMinecraftInstance().thePlayer.posX, (int)ModLoader.getMinecraftInstance().thePlayer.posY, (int)ModLoader.getMinecraftInstance().thePlayer.posZ);
        //ModLoader.getMinecraftInstance().ingameGUI.recordPlayingUpFor = 0;
    }

    public boolean tryPlaySound(String[] sound, int arrIndex, Entity source, float vol)
    {
        Entity soundTarget = source;

        // should i?
        //soundTarget = this;
        if (this.soundTimer[arrIndex] <= System.currentTimeMillis())
        {
            //worldObj.playSoundAtEntity(soundTarget, new StringBuilder().append("tornado."+sound).toString(), 1.0F, 1.0F);
            //((IWorldAccess)this.worldAccesses.get(var5)).playSound(var2, var1.posX, var1.posY - (double)var1.yOffset, var1.posZ, var3, var4);
            this.soundID[arrIndex] = WeatherMod.playMovingSound(new StringBuilder().append(WeatherMod.modID + ":tornado." + sound[snd_rand[arrIndex]]).toString(), (float)soundTarget.posX, (float)soundTarget.posY, (float)soundTarget.posZ, vol, 1.0F);
            //this.soundID[arrIndex] = mod_EntMover.getLastSoundID();
            //System.out.println(new StringBuilder().append("tornado."+sound[snd_rand[arrIndex]]).toString());
            //System.out.println(soundToLength.get(sound[snd_rand[arrIndex]]));
            int length = (Integer)soundToLength.get(sound[snd_rand[arrIndex]]);
            //-500L, for blending
            this.soundTimer[arrIndex] = System.currentTimeMillis() + length - 500L;
            this.snd_rand[arrIndex] = rand.nextInt(3);
        }

        return false;
    }

    public void onUpdate()
    {
        //if (true) return;
    	
    	

        //setDead();
        if (WeatherMod.activeTornado == null)
        {
            WeatherMod.activeTornado = this;
        }

        if (WeatherMod.weatherMan.wind.strength < 0.11F)
        {
            WeatherMod.weatherMan.wind.strength = 0.11F;//getStorm().tornadoPullRate * 10F;
        }

        //this.setEntityDead();
        //super.onUpdate();
        //this.startDissipate();
        
        //System.out.println("remote: " + worldObj.isRemote + " - " + entityId + " - " + age + " - " + getStorm().tornadoTime);
        
        //client side safety
        if (age - (worldObj.isRemote ? 80 : 0) > getStorm().tornadoTime)
        {
            //this.trimBlocksFromWorld();
            //System.out.println("time trigger");
            this.startDissipate();
        }

        int recedeY = 128;

        if (spawning && !dying)
        {
            strength += 0.5;

            if (strength < 40)
            {
                strength = 40;
            }

            if (strength >= 100)
            {
                strength = 100;
                //spawning = false;
            }
        }

        if (worldObj.isRemote)
        {
            if (ConfigTornado.Storm_Tornado_makeClouds && getStorm().type != getStorm().TYPE_SPOUT)
            {
            	//System.out.println(Math.max(1, 10 - (entConfID * 2)));
                if (rand.nextInt(Math.max(1, 10 - (entConfID * 3))) == 0)
                {
                    WeatherMod.spawnStorm((int)posX, (int)posY, (int)posZ, entConfID);
                }
            }
        }

        //slave dying check, unused since changed to weather entity
        int dyingByte = this.dataWatcher.getWatchableObjectByte(10);

        if (dyingByte == 1)
        {
            dying = true;
            //System.out.println(dyingByte);
        }

        if (dying)
        {
            strength -= 0.1;

            if (strength <= 0)
            {
                strength = 0;
            }

            if (spawnYOffset < recedeY)
            {
                spawnYOffset += 0.3F - this.rand.nextGaussian() * 2F; // 0.2F;
                posX += (rand.nextGaussian() * 2F - 0.5F) * 0.2F;
                posZ += (rand.nextGaussian() * 2F - 0.5F) * 0.2F;
            }
            else
            {
                spawnYOffset = (float)posY;
                dying = false;
                setDead();
            }

            setLocationAndAngles(posX, posY, posZ, this.rotationYaw, 0F);
        }
        else if (spawning)
        {
            if (spawnYOffset > posY)
            {
                spawnYOffset -= 0.3F - this.rand.nextGaussian() * 2F; // 0.2F;

                if (getStorm().type != getStorm().TYPE_SPOUT)
                {
                    posX += (rand.nextGaussian() * 2F - 0.5F) * 0.2F;
                    posZ += (rand.nextGaussian() * 2F - 0.5F) * 0.2F;
                }

                float yaw = 0;

                /*if (WeatherMod.player != null)
                {
                    double var11 = WeatherMod.player.posX - posX;
                    double var15 = WeatherMod.player.posZ - posZ;
                    yaw = -((float)Math.atan2(var11, var15)) * 180.0F / (float)Math.PI;
                    int size = 45;
                    yaw += rand.nextInt(size) - (size / 2);
                    realYaw = yaw;
                }*/

                setLocationAndAngles(posX, posY, posZ, yaw, 0F);
            }
            else
            {
                spawning = false;
                spawnYOffset = (float)posY;
            }
        } else {
        	if (!worldObj.isRemote) {
        		if (spawnYOffset > worldObj.getHeightValue((int)posX, (int)posZ)) spawnYOffset--;
        	}
        }

        if (dying)
        {
        }

        if (!spawning && !dying)
        {
            strength = (rand.nextInt(30) * 1.0F) + 100F;
        }

        //System.out.println(age + " - " + strength);
        lastTickRunTime = System.currentTimeMillis();
        /*for (int i = 0; i < worldRef.loadedEntityList.size(); i++) {
           Entity ent = (Entity)worldRef.loadedEntityList.get(i);
           if (ent instanceof EntityMob) {
        	   System.out.println("!!");*/
        //}
        //}
        int lockTime = (int)((float)WeatherMod.stormMan.stormTimeLength * 0.4F);
        //System.out.println(lockTime);
        //maintains storm while tornado active, keeps at minimum storm time till death
        //disabled, weather logic should maintain this now
        boolean rotatingEnt = false;
        //if (!worldObj.isRemote) {
        rotatingEnt = WeatherMod.forceRotate(this, getStorm());
        //}
        boolean seesLight = false;
        tickGrabCount = 0;
        removeCount = 0;
        tryRipCount = 0;
        int tryRipMax = 300;

        //startDissipate();

        if (!worldObj.isRemote && getStorm().grabsBlocks)
        {
            int yStart = 00;
            int yEnd = (int)posY + 72;
            int yInc = 1;

            if (getStorm().type == getStorm().TYPE_HURRICANE)
            {
                yStart = 10;
                yEnd = 40;
            }

            for (int i = yStart; i < yEnd; i += yInc)
            {
                int YRand = i;//rand.nextInt(126)+2;
                int ii = YRand / 4;

                if (i > 20 && rand.nextInt(2) != 0)
                {
                    continue;
                }

                if (tryRipCount > tryRipMax)
                {
                    break;
                }

                for (int k = 0; k < 5 + ii; k++)
                {
                    //for (int k = 0; k < mod_EntMover.tornadoBaseSize/2+(ii/2); k++) {
                    //for (int l = 0; l < mod_EntMover.tornadoBaseSize/2+(ii/2); l++) {
                    //if (rand.nextInt(3) != 0) { continue; }
                    if (tryRipCount > tryRipMax)
                    {
                        break;
                    }

                    int tryY = (int)(spawnYOffset + YRand - 1.5D); //mod_EntMover.tornadoBaseSize;

                    if (tryY > 255)
                    {
                        tryY = 255;
                    }

                    //System.out.println(posY);
                    //int tryX = (int)posX+k-((mod_EntMover.tornadoBaseSize/2)+(ii/2));
                    //int tryZ = (int)posZ+l-((mod_EntMover.tornadoBaseSize/2)+(ii/2));
                    int tryX = (int)posX + this.rand.nextInt(getStorm().tornadoBaseSize + (ii)) - ((getStorm().tornadoBaseSize / 2) + (ii / 2));
                    int tryZ = (int)posZ + this.rand.nextInt(getStorm().tornadoBaseSize + (ii)) - ((getStorm().tornadoBaseSize / 2) + (ii / 2));

                    if (tryRipCount < tryRipMax)
                    {
                        int blockID = this.worldObj.getBlockId(tryX, tryY, tryZ);

                        if (blockID != 0 && canGrab(blockID)/* && Block.blocksList[blockID].blockMaterial == Material.ground*//* && worldObj.getHeightValue(tryX, tryZ)-1 == tryY*/)
                        {
                            /*if (blockID != 0 && canGrab(blockID) && (worldObj.getBlockId(tryX,tryY+1,tryZ) == 0 ||
                                    worldObj.getBlockId(tryX+1,tryY,tryZ) == 0 ||
                                    worldObj.getBlockId(tryX,tryY,tryZ+1) == 0 ||
                                    worldObj.getBlockId(tryX-1,tryY,tryZ) == 0 ||
                                    worldObj.getBlockId(tryX,tryY,tryZ-1) == 0)) {*/
                            tryRipCount++;
                            seesLight = tryRip(tryX, tryY, tryZ, true);
                        }
                    }

                    /*tryX = (int)posX-k+((mod_EntMover.tornadoBaseSize/2)+(ii/2));
                    tryZ = (int)posZ-l+((mod_EntMover.tornadoBaseSize/2)+(ii/2));

                    if (tryRipCount < tryRipMax) {
                    	int blockID = this.worldObj.getBlockId(tryX,tryY,tryZ);
                    	if (blockID != 0 && canGrab(blockID)) {
                    		tryRipCount++;
                    		seesLight = tryRip(tryX,tryY,tryZ, true);
                    	}
                    }*/
                    //}
                    //int tryX = (int)posX+this.rand.nextInt(mod_EntMover.tornadoBaseSize+(ii))-((mod_EntMover.tornadoBaseSize/2)+(ii/2));
                    //int tryZ = (int)posZ+this.rand.nextInt(mod_EntMover.tornadoBaseSize+(ii))-((mod_EntMover.tornadoBaseSize/2)+(ii/2));
                }
            }

            if (getStorm().type == getStorm().TYPE_TORNADO)
            {
                for (int k = 0; k < 10; k++)
                {
                    int tryX = (int)posX + this.rand.nextInt(40) - 20;
                    int tryY = (int)spawnYOffset - 2 + this.rand.nextInt(8);
                    int tryZ = (int)posZ + this.rand.nextInt(40) - 20;

                    if (tryRipCount < tryRipMax)
                    {
                        int blockID = this.worldObj.getBlockId(tryX, tryY, tryZ);

                        if (blockID != 0 && canGrab(blockID))
                        {
                            tryRipCount++;
                            tryRip(tryX, tryY, tryZ, true);
                        }
                    }
                }
            }

            if (tryRipCount >= tryRipMax)
            {
                hitMaxTriesLastTick = true;
            }
            else
            {
                hitMaxTriesLastTick = false;
            }
        }
        else
        {
            seesLight = true;
        }

        if (Math.abs((spawnYOffset - posY)) > 5)
        {
            seesLight = true;
        }

        if (tryRipCount >= tryRipMax / 4 * 3)
        {
            //speed reduce
            speedAdjust -= 0.1F;

            if (speedAdjust < 0.2F)
            {
                speedAdjust = 0.2F;
            }
        }
        else
        {
            if (speedAdjust < 1.0F)
            {
                speedAdjust += 0.01F;

                if (speedAdjust > 1.0F)
                {
                    speedAdjust = 1.0F;
                }
            }
        }

        if (ConfigWavesMisc.debug)
        {
            WeatherMod.displayMessage(new StringBuilder().append(tryRipCount + " - " + removeCount + " - " + speedAdjust).toString());
        }

        //System.out.println(removeCount);

        //System.out.println(effR.getStatistics());
        //this.worldObj.spawnParticle("explode", -Math.sin((double)(0.01745329F * var10)) * 0.75D, var6 - 0.25D, Math.cos((double)(0.01745329F * var10)) * 0.75D, var4, 0.125D, var8);
        //for (int i = 0; i < 26; i++) {
        //}

        scale = 0.08F;
        
        //scale = 0.15F;
        
        scale = 1F;
        
        if (worldObj.isRemote) {
	        if (WeatherMod.particleCount < ConfigTornado.Storm_Tornado_maxParticles)
	        {
	            float f = 2.0F;
	            float f1 = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * f;
	            //float f2 = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * f;
	            f1 = 0.0F;
	            float f3 = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * f;
	            //double d3 = 1.0D;
	            double d4 = 1.8D;
	            double d5 = 0.050000000000000003D;
	            double d = this.prevPosX + (this.posX - this.prevPosX) * (double)f;
	            double d1 = 10D + (this.prevPosY + (this.posY - this.prevPosY) * (double)f + 1.6200000000000001D) - (double)this.yOffset;
	            double d2 = this.prevPosZ + (this.posZ - this.prevPosZ) * (double)f;
	            //Vec3D vec3d = Vec3D.createVector(d, d1, d2);
	            float f4 = (float)Math.cos(-f3 * 0.01745329F - (float)Math.PI);
	            float f5 = (float)Math.sin(-f3 * 0.01745329F - (float)Math.PI);
	            float f6 = (float) - Math.cos(-f1 * 0.01745329F - ((float)Math.PI / 4F));
	            float f7 = (float)Math.sin(-f1 * 0.01745329F - ((float)Math.PI / 4F));
	            float f8 = f5 * f6;
	            float f9 = f7;
	            float f10 = f4 * f6;
	            double d6 = (double)f8 * d5;
	            double d7 = (double)f9 * d5;
	            double d8 = (double)f10 * d5;
	            //worldObj.spawnParticle("explode", d + (double)f8 * d4, d1 + (double)f9 * d4, d2 + (double)f10 * d4, d6 / 2D, d7 / 2D, d8 / 2D);
	            //double var4 = (double)((float)this.posX + this.rand.nextFloat() * 0.25F);
	            double var6 = (double)((float)this.posY + this.height + 0.125F);
	            //double var8 = (double)((float)this.posZ + this.rand.nextFloat() * 0.25F);
	            //float var10 = this.rand.nextFloat() * 360.0F;
	            //EntityExplodeFX var31 = new EntityExplodeFX(this.worldObj, -Math.sin((double)(0.01745329F * var10)) * 0.75D, var6 - 0.25D, Math.cos((double)(0.01745329F * var10)) * 0.75D, var4, 0.125D, var8);
	            //EntityFX var31;
	            //mod_EntMover.displayMessage(new StringBuilder().append(funnelEffects.size()).toString());
	            int itCount = 6;
	
	            if (getStorm().type == getStorm().TYPE_SPOUT)
	            {
	                itCount = 4;
	            }
	
	            if (ConfigTornado.Storm_Tornado_oldParticles)
	            {
	                itCount = 35;
	            }
	
	            for (int i = 0; i < itCount; i++)
	            {
	                /*if (mod_EntMover.particleCount+5 >= mod_EntMover.tornadoMaxParticles && funnelEffects.size() >= 25) {
	                	EntityFX var30 = (EntityFX)this.funnelEffects.get(funnelEffects.size()-2);
	
	                	var30.setEntityDead();
	
	                    if(var30.isDead) {
	                        this.funnelEffects.remove(var30);
	                        mod_EntMover.particleCount--;
	                    }
	                }*/
	                double tryX2 = this.posX + (rand.nextDouble() * getStorm().tornadoBaseSize * 2) - getStorm().tornadoBaseSize;
	                double tryZ2 = this.posZ + (rand.nextDouble() * getStorm().tornadoBaseSize * 2) - getStorm().tornadoBaseSize;
	                int blockID = worldObj.getBlockId((int)tryX2, (int)posY - 1, (int)tryZ2);
	                int blockIDUp = worldObj.getBlockId((int)tryX2, (int)posY, (int)tryZ2);
	                int blockIDDown = worldObj.getBlockId((int)tryX2, (int)posY - 2, (int)tryZ2);
	                //if (blockID != 0) {
	                int colorID = 0;
	
	                if (getStorm().type == getStorm().TYPE_SPOUT)
	                {
	                    colorID = 3;
	                }
	                else
	                {
	                    if (blockID == Block.waterMoving.blockID || blockID == Block.waterStill.blockID ||
	                            blockIDUp == Block.waterMoving.blockID || blockIDUp == Block.waterStill.blockID ||
	                            blockIDDown == Block.waterMoving.blockID || blockIDDown == Block.waterStill.blockID)
	                    {
	                        colorID = 3;
	                    }
	                    else if (blockID == Block.sand.blockID || blockID == Block.sandStone.blockID)
	                    {
	                        colorID = 2;
	                    }
	                    else if (blockID == Block.dirt.blockID)
	                    {
	                        colorID = 1;
	                    }
	                    else if (blockID == Block.snow.blockID)
	                    {
	                        colorID = 4;
	                    }
	                    else if (blockID == Block.stone.blockID)
	                    {
	                        colorID = 0;
	                    }
	                    else if (blockID == Block.leaves.blockID || blockID == Block.grass.blockID)
	                    {
	                        colorID = 6;
	                    }
	                }
	
	                
	                
	                //HERE
	                if (ConfigTornado.Storm_Tornado_oldParticles)
	                {
	                    WeatherMod.proxy.newParticle("WindFX", worldObj, this, tryX2, posY, tryZ2, 0, 0, 0, 9.5D, colorID);
	                }
	                else
	                {
	                    WeatherMod.proxy.newAnimParticle("AnimTexFX", worldObj, this, tryX2, posY, tryZ2, 0, 0, 0, 9.5D, WeatherMod.effWindAnimID, colorID, scale);
	                    //WeatherMod.proxy.newParticle("WindFX", worldObj, this, tryX2, posY, tryZ2, 0, 0, 0, 9.5D, colorID);
	                    //derp
	                    /*c_w_EntityRotFX var32 = null;
	                    var32 = new c_w_EntityAnimTexFX(this.worldObj, 0, 0, 0, 0, 0, 0, 8D, WeatherMod.effWindAnimID, colorID);
	                    mod_ExtendedRenderer.rotEffRenderer.addEffect(var32);
	                    this.funnelEffects.add(var32);
	                    WeatherMod.particleCount++;
	                    var32.renderDistanceWeight = 1000.0D;
	                    var32.noClip = true;
	                    var32.setSize(1.25F, 1.25F);
	                    var32.posY = var6 + 0D;
	                    var32.setPosition(tryX2, spawnYOffset, tryZ2);*/
	                }
	
	                //}
	            }
	
	            for (int i = 0; i < 5; i++)
	            {
	                double tryX2 = this.posX + (rand.nextDouble() * 60) - 30D;
	                double tryZ2 = this.posZ + (rand.nextDouble() * 60) - 30D;
	
	                if (worldObj.getBlockId((int)tryX2, (int)posY - 1, (int)tryZ2) != 0)
	                {
	                    WeatherMod.proxy.newParticle("WindFX", worldObj, this, tryX2, this.posY, tryZ2, 0, 0, 0, 9.5D, 0);
	                    /*var31 = new c_w_EntityWindFX(this.worldObj, d + (double)f8 * d4, d1 + (double)f9 * d4, d2 + (double)f10 * d4, d6 / 2D, d7 / 2D, d8 / 2D, 9.5D, 0);
	                    this.effR.addEffect(var31);
	                    this.funnelEffects.add(var31);
	                    WeatherMod.particleCount++;
	                    var31.renderDistanceWeight = 10.0D;
	                    var31.noClip = true;
	                    var31.setSize(1.25F, 1.25F);
	                    var31.posY = var6 + 0D;
	                    var31.setPosition(tryX2, this.posY, tryZ2);*/
	                }
	            }
	        }
        }

        if (worldObj.isRemote)
        {
            soundUpdates();
        }

        if (!seesLight)
        {
            noSeeTicks++;

            if (noSeeTicks > 150)
            {
                //this.startDissipate();
            }
        }
        else
        {
            noSeeTicks = 0;
        }

        if (rotatingEnt)
        {
            noSeeTicks = 0;
        }

        //System.out.println(this.posY);
        age++;

        if (blockTrimDelay < System.currentTimeMillis())
        {
            blockTrimDelay = System.currentTimeMillis() + 1500L;
            //this.trimBlocksFromWorld();
        }

        /*while (funnelEffects.size() > mod_EntMover.tornadoMaxParticles-100) {
        	funnelEffects.remove(rand.nextInt(funnelEffects.size()));
        	mod_EntMover.particleCount--;
        }*/

        if (worldObj.isRemote)
        {
            if (this.funnelEffects.size() > 0)
            {
            	//System.out.println(this.funnelEffects.size() + " - " + WeatherMod.particleCount);
                for (int var9 = 0; var9 < this.funnelEffects.size(); ++var9)
                {
                    Entity var30 = (Entity)this.funnelEffects.get(var9);
                    //rotations!
                    double var16 = this.posX - var30.posX;
                    double var18 = this.posZ - var30.posZ;
                    float var20 = this.rotationYaw;
                    var30.rotationYaw = (float)(Math.atan2(var18, var16) * 180.0D / Math.PI) - 90.0F;
                    var30.rotationPitch = -30F;

                    /*if(var30.particleAge >= var30.particleMaxAge) {
                    	var30.setDead();
                    }*/
                    if (var30.isDead)
                    {
                        this.funnelEffects.remove(var30);
                        WeatherMod.particleCount--;
                    }
                    else
                    {
                        WeatherMod.spin(this, getStorm(), var30);
                    }
                }
            }
        }

        /*if(this.activeBlocks.size() > 0) {
            for(int var9 = 0; var9 < this.activeBlocks.size(); ++var9) {
            	MovingBlock var30 = (MovingBlock)this.activeBlocks.get(var9);

                if(var30.isDead) {
                    this.activeBlocks.remove(var30);
                    //mod_EntMover.particleCount--;
                } else {
                    mod_EntMover.spin(this, var30);
                }
            }
        }*/

        if (worldObj.weatherEffects.size() > 0)
        {
            for (int var9 = 0; var9 < worldObj.weatherEffects.size(); ++var9)
            {
                Entity var30 = (Entity)worldObj.weatherEffects.get(var9);

                /*if(var30.particleAge++ >= var30.particleMaxAge) {
                	var30.setEntityDead();
                }*/
                if (var30.isDead)
                {
                    //this.activeBlocks.remove(var30);
                    //mod_EntMover.particleCount--;
                }
                else
                {
                    /*if (var30 instanceof MovingBlock) {
                    	mod_EntMover.spin(this, getStorm(), var30);
                    }*/

                    //DISTANCE CHECK?!
                    if (!(var30 instanceof EntityLightningBolt))
                    {
                        WeatherMod.spin(this, getStorm(), var30);
                    }
                }
            }
        }

        //this.motionX = this.lastMotionX;
        //this.motionZ = this.lastMotionZ;
        //if (scale >= 1F) {
	        this.motionX = (double)(-Math.sin(rotationYaw / 180.0F * (float)Math.PI) * Math.cos(rotationPitch / 180.0F * (float)Math.PI)) * getStorm().tornadoInitialSpeed * speedAdjust;
	        this.motionZ = (double)(Math.cos(rotationYaw / 180.0F * (float)Math.PI) * Math.cos(rotationPitch / 180.0F * (float)Math.PI)) * getStorm().tornadoInitialSpeed * speedAdjust;
        //} else {
        	/*this.motionX = 0F;
        	this.motionZ = 0F;*/
        //}
        //no Y movement
        rotationPitch = 0;
        rotationYaw = realYaw;
        //mod_MovePlus.displayMessage(new StringBuilder().append(motionZ + " - " + rotationPitch).toString());
        posX += motionX;
        if (!worldObj.isRemote) posY = spawnYOffset; //server only, let client use packets
        posZ += motionZ;

        if (!this.worldObj.isRemote)
        {
            if (WeatherMod.getDistanceXZ(this, lastPosX, 0.0D, lastPosZ) > 5.0D)
            {
                checkFront();
                getNewYPos();
            }
        }

        float f = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
        realYaw += direction;
        //rotationYaw = (float)((Math.atan2(motionX, motionZ) * 180D) / Math.PI) * -1;

        for (rotationPitch = (float)((Math.atan2(motionY, f) * 180D) / Math.PI); rotationPitch - prevRotationPitch < -180F; prevRotationPitch -= 360F) { }

        for (; rotationPitch - prevRotationPitch >= 180F; prevRotationPitch += 360F) { }

        /*        for(; rotationYaw - prevRotationYaw < -180F; prevRotationYaw -= 360F) { }

                for(; rotationYaw - prevRotationYaw >= 180F; prevRotationYaw += 360F) { }*/
        rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch)/* * 0.2F*/;
        realYaw = prevRotationYaw + (realYaw - prevRotationYaw)/* * 0.2F*/;
        /*if(handleWaterMovement()) {
            for(int j = 0; j < 4; j++) {
                float f1 = 0.25F;
                worldObj.spawnParticle("bubble", posX - motionX * (double)f1, posY - motionY * (double)f1, posZ - motionZ * (double)f1, motionX, motionY, motionZ);
            }
        }*/
        
        motionX *= 1.0D;
        motionY = 0D;
        motionZ *= 1.0D;
        if (!worldObj.isRemote) setPosition(posX, posY, posZ);
        
        
        //Anti unload code - fail, oldest chunk unload still found it
    	/*this.addedToChunk = true;
    	int var66 = MathHelper.floor_double(posX / 16.0D);
        int var7 = MathHelper.floor_double(posY / 16.0D);
        int var8 = MathHelper.floor_double(posZ / 16.0D);
        
        chunkCoordX = var66;
        chunkCoordY = var7;
        chunkCoordZ = var8;
        
        worldObj.getChunkFromChunkCoords(var66, var8).removeEntity(this);
        worldObj.getChunkFromChunkCoords(var66, var8).removeEntityAtIndex(this, 16);
        
        worldObj.getChunkFromChunkCoords(var66-1, var8).removeEntity(this);
        worldObj.getChunkFromChunkCoords(var66-1, var8).removeEntityAtIndex(this, 16);
        
        worldObj.getChunkFromChunkCoords(var66+1, var8).removeEntity(this);
        worldObj.getChunkFromChunkCoords(var66+1, var8).removeEntityAtIndex(this, 16);
        
        worldObj.getChunkFromChunkCoords(var66, var8-1).removeEntity(this);
        worldObj.getChunkFromChunkCoords(var66, var8-1).removeEntityAtIndex(this, 16);
        
        worldObj.getChunkFromChunkCoords(var66, var8+1).removeEntity(this);
        worldObj.getChunkFromChunkCoords(var66, var8+1).removeEntityAtIndex(this, 16);
        
        worldObj.getChunkFromChunkCoords(var66-1, var8-1).removeEntity(this);
        worldObj.getChunkFromChunkCoords(var66-1, var8-1).removeEntityAtIndex(this, 16);
        
        worldObj.getChunkFromChunkCoords(var66-1, var8+1).removeEntity(this);
        worldObj.getChunkFromChunkCoords(var66-1, var8+1).removeEntityAtIndex(this, 16);
        
        worldObj.getChunkFromChunkCoords(var66+1, var8-1).removeEntity(this);
        worldObj.getChunkFromChunkCoords(var66+1, var8-1).removeEntityAtIndex(this, 16);
        
        worldObj.getChunkFromChunkCoords(var66+1, var8+1).removeEntity(this);
        worldObj.getChunkFromChunkCoords(var66+1, var8+1).removeEntityAtIndex(this, 16);*/
        
        if (!worldObj.isRemote) {
	        syncDelay--;
	
	        if (syncDelay <= 0 && MinecraftServer.getServer() != null)
	        {
	            syncDelay = 5;
	            ByteArrayOutputStream bos = new ByteArrayOutputStream((Integer.SIZE * 2) + (Float.SIZE * 6));
	            DataOutputStream dos = new DataOutputStream(bos);
	
	            try
	            {
	            	dos.writeInt(1);
	            	dos.writeInt(this.entityId);
	                dos.writeFloat((float)posX);
	                dos.writeFloat((float)posY);
	                dos.writeFloat((float)posZ);
	                dos.writeFloat((float)motionX);
	                dos.writeFloat((float)motionY);
	                dos.writeFloat((float)motionZ);
	                
	            }
	            catch (Exception ex)
	            {
	                ex.printStackTrace();
	            }
	
	            Packet250CustomPayload pkt = new Packet250CustomPayload();
	            pkt.channel = "Tornado";
	            pkt.data = bos.toByteArray();
	            pkt.length = bos.size();
	            //pkt.isChunkDataPacket = true;
	            MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(pkt);
	        }
        }
    }
    
    public int syncDelay = 0;

    public void writeEntityToNBT(NBTTagCompound var1)
    {
        var1.setShort("age", (short)age);
        var1.setShort("type", (short)type);
        //this.setDead();
        //var1.setShort("blockCount", (short)mod_EntMover.blockCount);
    }

    public void readEntityFromNBT(NBTTagCompound var1)
    {
        age = var1.getShort("age");
        type = var1.getShort("type");
        //mod_EntMover.blockCount = var1.getShort("blockCount");
        this.lastMotionX = motionX;
        this.lastMotionZ = motionZ;
        //fuck you endlessly reloading tornadoes
        this.setDead();
    }

	@Override
	public float getWindWeight() {
		// TODO Auto-generated method stub
		return 999999;
	}

	@Override
	public int getParticleDecayExtra() {
		// TODO Auto-generated method stub
		return 0;
	}
}
