package weather;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import weather.waves.BlockDataGrid;
import weather.waves.BlockDataPoint;
import weather.waves.EntityBuoyant;
import weather.waves.EntitySurfboard;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.server.FMLServerHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class WeatherManager
{
	public int dimension;
	
    public Wind wind;

    public BlockDataGrid windGrid;
    public BlockDataGrid waterGrid;

    public int windEventTime;
    public int windGustEventTime;

    public int windEventTimeCurMax;
    public int windGustEventTimeCurMax;

    public float randWindFactor = 1F;
    public float nextStageRandFactor = 1F;

    public int stage;
    public boolean dying;
    public int windTimer;

    public List<WeatherConfig> weatherTypes;

    public Random rand = new Random();

    public boolean stormDying;

    public int rarityOfStormIncrease;

    public int maxStage;

    public boolean stormDeathStage;

    public int syncDelay = 0;

    public int stormTime = 0;
    public int stormStartTime = 0;
    public float stormStrength = 0F;
    public float stormIntensity = 0F;
    public boolean stormActive = false;

    public WeatherManager(int dim)
    {
    	this();
    	dimension = dim;
    }
    
    public WeatherManager()
    {
    	dimension = 0;
        //when chanceOfWindEvent is triggered, wind climbs up from 0 to min > random variation < max
        //regardless of chanceOfWindEvent,
        wind = new Wind();
        windGrid = new BlockDataGrid();
        waterGrid = new BlockDataGrid();
        dying = false;
        //General weather state/stage configurations
        weatherTypes = new LinkedList();
        WeatherConfig wConfig;
        //Calm
        //wConfig = new WeatherConfig();
        //weatherTypes.add(wConfig);
        //Chance of weak wind
        wConfig = new WeatherConfig();
        wConfig.chanceOfWindEvent = 0.2F;
        wConfig.minWind = 0.1F;
        wConfig.maxWind = 0.3F;
        wConfig.windEventTimeRand = 100;
        //twice, for 0 and 1
        weatherTypes.add(wConfig);
        weatherTypes.add(wConfig);
        //Regular weak wind
        wConfig = new WeatherConfig();
        wConfig.chanceOfWindEvent = 0.5F;
        wConfig.minWind = 0.2F;
        wConfig.maxWind = 0.3F;
        weatherTypes.add(wConfig);
        //Regular medium winds with low gusts
        wConfig = new WeatherConfig();
        wConfig.chanceOfWindEvent = 0.7F;
        wConfig.chanceOfWindGustEvent = 0.2F;
        wConfig.minWind = 0.2F;
        wConfig.alwaysMinWind = true;
        wConfig.maxWind = 0.5F;
        weatherTypes.add(wConfig);
        //Regular strong winds with gusts
        wConfig = new WeatherConfig();
        wConfig.chanceOfWindEvent = 0.8F;
        wConfig.chanceOfWindGustEvent = 0.4F;
        wConfig.minWind = 0.4F;
        wConfig.alwaysMinWind = true;
        wConfig.maxWind = 0.7F;
        weatherTypes.add(wConfig);
        //Regular intense winds with high gusts
        wConfig = new WeatherConfig();
        wConfig.chanceOfWindEvent = 1.0F;
        wConfig.chanceOfWindGustEvent = 0.6F;
        wConfig.minWind = 0.6F;
        wConfig.alwaysMinWind = true;
        wConfig.maxWind = 0.9F;
        weatherTypes.add(wConfig);
        //Tropical storm winds with high gusts
        wConfig = new WeatherConfig();
        wConfig.chanceOfWindEvent = 1.0F;
        wConfig.chanceOfWindGustEvent = 0.6F;
        wConfig.minWind = 0.8F;
        wConfig.alwaysMinWind = true;
        wConfig.maxWind = 1.2F;
        weatherTypes.add(wConfig);
        //Hurricane winds with high gusts
        wConfig = new WeatherConfig();
        wConfig.chanceOfWindEvent = 1.0F;
        wConfig.chanceOfWindGustEvent = 0.6F;
        wConfig.minWind = 0.8F;
        wConfig.alwaysMinWind = true;
        wConfig.maxWind = 1.2F;
        weatherTypes.add(wConfig);
        maxStage = weatherTypes.size();
        //needs its own max! and stage!
    }

    public void updateWater(World world)
    {
        int dist = 60;
        //if (true) return;
        //MinecraftServer mcs = ModLoader.getMinecraftServerInstance();
        //if (mcs == null) return;
        World worldRef = world;//mcs.worldServerForDimension(0);

        for (int ii = 0; ii < worldRef.loadedEntityList.size(); ii++)
        {
            Entity entity1 = (Entity)worldRef.loadedEntityList.get(ii);

            //if (player.isDead) continue;

            //List list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.expand(dist, 80, dist));

            //Chunk Entities
            //if(list != null) {
            //for(int i = 0; i < list.size(); i++) {
            //Entity entity1 = (Entity)list.get(i);

            if (isBoyant(entity1))
            {
                applyBoyancy(entity1);

                if (entity1 instanceof EntitySurfboard)
                {
                    applyFlow(entity1);
                }
            }

            //}
            //}
            /*if (isBoyant(player)) {
            	applyBoyancy(player);
            }*/
            //applyFlow(player);
        }
    }

    public boolean isBoyant(Entity ent)
    {
        if (ent instanceof EntityLiving)
        {
            if (c_CoroWeatherUtil.getIsJumping((EntityLiving)ent))
            {
                return true;
            }
        }

        if (ent instanceof EntityItem || ent instanceof EntityBoat || ent instanceof EntityBuoyant)
        {
            return true;
        }

        return false;
    }

    public float boyancy(Entity ent)
    {
        return 1F;
    }

    public void applyBoyancy(Entity ent)
    {
        int oceanLevel = 63;
        int xx = (int)(ent.posX + 0.5F);
        int zz = (int)(ent.posZ + 0.5F);
        xx = (int)Math.floor(ent.posX + 0.5F);
        zz = (int)Math.floor(ent.posZ + 0.5F);
        int yy = (int)Math.floor(ent.posY + 0.5F);
        //int yy = ent.worldObj.getHeightValue(xx, zz)-1;
        BlockDataPoint bdp = waterGrid.getPoint(xx, oceanLevel - 1, zz);

        if (!(ent.worldObj.isRemote))
        {
            bdp.height = WeatherMod.getHeight(ent.worldObj, xx, oceanLevel - 1, zz);
        }

        //if (true) return;
        float adj = 0.5F;

        if (ent instanceof EntityItem)
        {
            adj = 0F;
        }

        if (ent.posY < (oceanLevel - 0.5F)/* && ent.posY > (oceanLevel - 1 + bdp.height)*/)
        {
            //if (ent instanceof EntityPlayer)
            //{
                return;
            //}
        }

        if ((ent instanceof EntityBuoyant && !(ent.worldObj.isRemote)))
        {
            //ent.setDead();
            int hmm = 0;
        }

        if (bdp.isWater)
        {
            if (bdp.height > 0F)
            {
                int bleh = 0;
            }

            float top = oceanLevel + (bdp.height);
            float diff = top - ((float)ent.posY - adj);

            if ((ent instanceof EntityBuoyant && !(ent.worldObj.isRemote)))
            {
                int uhm = 0;
            }

            if (ent.posY - 1.0F <= top)
            {
                if (!ent.isInWater())
                {
                    ent.handleWaterMovement();
                }

                if (ent.motionY < -0.2F)
                {
                    ent.motionY *= 0.3F;
                }

                //ent.posY += (bdp.height);
                //ent.setPosition(ent.posX, ent.posY+(bdp.height)-0.2F, ent.posZ);
                float ySpeed = 0.02F + (diff * 0.1F);

                if (ySpeed > 0.07F)
                {
                    ySpeed = 0.07F;
                }

                if (diff > 0F)
                {
                    if (ent instanceof EntitySurfboard)
                    {
                        int herp = 34;
                        //System.out.println(ent.motionY);
                        //ent.setDead();
                    }

                    if (ent.worldObj.isRemote)
                    {
                        //if (ent.riddenByEntity != null) {
                        //f (ent instanceof EntityBuoyant || ent instanceof EntityBoat) {
                        //System.out.println(diff);
                        ent.motionY += ySpeed;//0.045F;
                        //ent.setVelocity(ent.motionX, ent.motionY, ent.motionZ);
                        //ent.setPosition(ent.posX, ent.posY, ent.posZ);
                        //}
                        //}
                    }
                    else
                    {
                        //ent.setDead();
                        //if ((ent instanceof EntityBuoyant)) {
                        ent.motionY += ySpeed;//0.045F;
                        //ent.setVelocity(ent.motionX, ent.motionY, ent.motionZ);
                        //ent.setPosition(ent.posX, ent.posY, ent.posZ);
                        //}
                    }
                }

                //ent.motionY = 0F;
            }
        }
    }

    public void applyFlow(Entity ent)
    {
        int oceanLevel = 63;
        int xx = (int)(ent.posX + 0.5F);
        int zz = (int)(ent.posZ + 0.5F);
        xx = (int)Math.floor(ent.posX + 0.5F);
        zz = (int)Math.floor(ent.posZ + 0.5F);
        int yy = ent.worldObj.getHeightValue(xx, zz) - 1;
        BlockDataPoint bdp = waterGrid.getPoint(xx, oceanLevel - 1, zz);

        if (!(ent.worldObj.isRemote))
        {
            bdp.height = WeatherMod.getHeight(ent.worldObj, xx, oceanLevel - 1, zz);
        }

        if (bdp.isWater)
        {
            float top = yy + (bdp.height);
            float diff = top - ((float)ent.posY - 1);

            if (ent.posY - 1 <= top)
            {
                float angle = /*(wind.directionSmooth) * (float)Math.PI / 180.0F;*/bdp.angle;
                angle = (wind.directionSmoothWaves + 90F) * (float)Math.PI / 180.0F;
                double xm = -Math.cos(angle);
                double zm = Math.sin(angle);
                float speed = (bdp.height * 0.001F);

                if (ent instanceof EntityPlayer)
                {
                    //System.out.println(bdp.angle);
                }

                if (ent instanceof EntityBuoyant && ent.riddenByEntity != null)
                {
                    //System.out.println(Math.sqrt(ent.motionX * ent.motionX + ent.motionZ * ent.motionZ));
                    //System.out.println(diff);
                }

                //add is river check maybe?
                //if (Math.sqrt(ent.motionX * ent.motionX + ent.motionZ * ent.motionZ) <= bdp.speed / 2) {
                if (ent.worldObj.isRemote)
                {
                    if (ent.riddenByEntity != null)
                    {
                        if (ent instanceof EntityBuoyant || ent instanceof EntityBoat)
                        {
                        }
                    }
                }
                else
                {
                    //ent.motionX += xm * speed;
                    //ent.motionZ += zm * speed;
                    //ent.setVelocity(ent.motionX, ent.motionY, ent.motionZ);
                    //ent.setPosition(ent.posX, ent.posY, ent.posZ);
                }
            }
        }
    }

    public void tick(Side side, World world)
    {
        //ACTIVATE SEPARATE STAGE MANAGEMENT ONCE READY!
        boolean remote = world.isRemote;

        boolean windOn = ((WeatherMod.Wind_active && world.provider.dimensionId == c_CoroWeatherUtil.mainDimID) || (WeatherMod.TropicraftRealm_Wind_active && world.provider.dimensionId == c_CoroWeatherUtil.tropiDimID));
        
        if (side == Side.SERVER)
        {
        	if (windOn) {
        		manageStage();
        	}
        }
        else
        {
        }

        //
        //stage = mod_EntMover.stage;
        if (WeatherMod.waveRenderRange > 0) updateWater(world);

        if (side == Side.SERVER)
        {
        	
        	if (!windOn) {
        		wind.strengthTarget = 0F;
        		wind.strength = 0F;
        	}
        	
            if (windEventTime > 0)
            {
                windEventTime--;
            }

            if (windGustEventTime > 0)
            {
                windGustEventTime--;
            }

            //wind.windStrength = 0.2F;
            wind.yDirection = 0.0F;
            wind.yStrength = 0.0F;
            //chance of wind, once in a while (5 seconds) roll dice and see if wind should fade into range or fade to none
            //chance of gust, sudden wind speed about 0.4 faster than current wind, TEMPORARILY swing the wind direction to some random angle then back
            //chance of rain, turn on for a little bit if chance hits, modify chance higher if in right biome
            //chance of hail, turn on for a little bit if chance hits, use intensity levels...
            //NEEEEEEEED TO STOP WIND WHEN UNDERGROUND! - copied
            randWindFactor = 5F;
            float randGustWindFactor = 1F;
            //System.out.println(rand.nextInt((int)((100-this.weatherTypes.get(stage).chanceOfWindEvent) * randWindFactor)));
            //mod_EntMover.prevStage();
            //System.out.println(windEventTime);
            int windTime = 200;

            if (windOn && this.windEventTime == 0)
            {
                if (rand.nextInt((int)((100 - this.weatherTypes.get(stage).chanceOfWindEvent) * randWindFactor)) == 0)
                {
                    //if (rand.nextInt(1+(int)(this.weatherTypes.get(stage).chanceOfWindEvent * randWindFactor)) == 0) {
                    windEventTime = windTime + rand.nextInt(this.weatherTypes.get(stage).windEventTimeRand);
                    //if (WeatherMod.debug) System.out.println("Wind event");
                }
            }

            //Dont let gusts occur when already gusting, prevents wind strength stacking
            if (windOn && this.windGustEventTime == 0)
            {
                if (this.weatherTypes.get(stage).chanceOfWindGustEvent > 0F)
                {
                    if (rand.nextInt((int)((100 - this.weatherTypes.get(stage).chanceOfWindGustEvent) * randGustWindFactor)) == 0)
                    {
                        wind.strengthTarget += rand.nextFloat() * 0.6F;
                        windGustEventTime = rand.nextInt(this.weatherTypes.get(stage).windGustEventTimeRand);
                        windEventTime += windTime;
                        wind.directionBeforeGust = wind.direction;
                        wind.directionGust = rand.nextInt(360) - 180;
                        //if (WeatherMod.debug) System.out.println("Wind gust event");
                    }
                }
            }

            if (rand.nextInt(1 + (int)(this.weatherTypes.get(stage).chanceOfWindGustEvent * randWindFactor)) == 0)
            {
            }

            if (this.windGustEventTime == 0)
            {
                wind.direction += ((new Random()).nextInt(5) - 2) * 0.5;
            }
            else
            {
                //last decrementing tick reset
                if (this.windGustEventTime == 1)
                {
                    wind.direction = wind.directionBeforeGust;
                }
                else
                {
                    wind.direction = wind.directionGust;
                }
            }

            if (windEventTime > 0)
            {
                if (windTimer-- <= 0)
                {
                    wind.strengthTarget += (rand.nextDouble() * wind.strengthAdjSpeed) - (wind.strengthAdjSpeed / 2);
                    windTimer = 10;
                }

                if (windGustEventTime <= 0)
                {
                    if (wind.strengthTarget < weatherTypes.get(stage).minWind)
                    {
                        wind.strengthTarget = weatherTypes.get(stage).minWind;
                    }

                    if (wind.strengthTarget > weatherTypes.get(stage).maxWind)
                    {
                        wind.strengthTarget = weatherTypes.get(stage).maxWind;
                    }
                }
                else
                {
                    if (wind.strengthTarget < weatherTypes.get(stage).minWind)
                    {
                        wind.strengthTarget = weatherTypes.get(stage).minWind;
                    }

                    if (wind.strengthTarget > 1.5F)
                    {
                        wind.strengthTarget = 1.5F;
                    }
                }
            }
            else
            {
                if (this.weatherTypes.get(stage).alwaysMinWind)
                {
                    wind.strengthTarget = weatherTypes.get(stage).minWind;
                }
                else
                {
                    wind.strengthTarget = 0.02F;
                }
            }

            if (wind.strengthTarget > wind.strength)
            {
                wind.strength += 0.01F;
            }
            else if (wind.strengthTarget < wind.strength)
            {
                wind.strength -= 0.01F;
            }

            if (wind.strength < 0)
            {
                wind.strength = 0F;
            }

            if (wind.direction < -180)
            {
                wind.direction += 360;
            }

            if (wind.direction > 180)
            {
                wind.direction -= 360;
            }

            //TEST
            //WeatherMod.weatherMan.wind.strength = 0.1F;
            syncDelay--;

            if (syncDelay <= 0 && MinecraftServer.getServer() != null)
            {
                syncDelay = 5;
                ByteArrayOutputStream bos = new ByteArrayOutputStream(Float.SIZE * 2);
                DataOutputStream dos = new DataOutputStream(bos);

                try
                {
                    dos.writeFloat(wind.strength);
                    dos.writeFloat(wind.direction);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }

                Packet250CustomPayload pkt = new Packet250CustomPayload();
                pkt.channel = "WindData";
                pkt.data = bos.toByteArray();
                pkt.length = bos.size();
                //pkt.isChunkDataPacket = true;
                MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayersInDimension(pkt, world.provider.dimensionId);
            }
        }

        //modify for gusts?
        //float adj = 0.0003F;
        if (wind.strength > wind.strengthSmooth)
        {
            wind.strengthSmooth += 0.0003F;
        }
        else if (wind.strength < wind.strengthSmooth)
        {
            wind.strengthSmooth -= 0.0003F;
        }

        if (wind.directionSmooth < -180)
        {
            wind.directionSmooth += 360;
        }

        if (wind.directionSmooth > 180)
        {
            wind.directionSmooth -= 360;
        }

        if (wind.direction > wind.directionSmooth)
        {
            wind.directionSmooth += 0.01F;
        }
        else if (wind.direction < wind.directionSmooth)
        {
            wind.directionSmooth -= 0.01F;
        }

        if (wind.directionSmoothWaves < -180)
        {
            wind.directionSmoothWaves += 360;
        }

        if (wind.directionSmoothWaves > 180)
        {
            wind.directionSmoothWaves -= 360;
        }

        if (wind.direction > wind.directionSmoothWaves)
        {
            wind.directionSmoothWaves += 0.001F;
        }
        else if (wind.direction < wind.directionSmoothWaves)
        {
            wind.directionSmoothWaves -= 0.001F;
        }

        //System.out.println(wind.windDirection + " - " + wind.windTargetDirection);
        //wind.windTargetDirection = wind.windDirection;
        //temp
        //wind.windStrength = 0.2F;
        //debug = false;
    }

    public void manageStage()
    {
        rarityOfStormIncrease = WeatherMod.Wind_rarityOfIncrease;

        //TEMP!
        //rarityOfStormIncrease = 20;

        if (stormActive)
        {
            if (stormStrength < 0.5F/* + (0.5F * stormTime / stormStartTime)*/)
            {
                stormStrength = 0.5F;
            }
            else
            {
                stormStrength = 0.5F + (0.5F * stormTime / stormStartTime);
                //world.thunderingStrength = stormStrength;
            }

            //if (stormStrength > 0.75F) {
            stormIntensity = (0.25F - Math.abs(stormStrength - 0.75F)) / 0.25F;

            if (stormIntensity < 0F)
            {
                stormIntensity = 0.0001F;
            }

            //}
        }
        else
        {
            //make active!
            /*stormActive = true;
            stage = 0;
            nextStage();*/
        }

        if (true)
        {
            stormTime--;

            if (stormTime < 0)
            {
                if (stage == 0)
                {
                    stormActive = true;
                    stage = 0;
                    nextStage();
                }
                else
                {
                    prevStage();
                }
            }

            if (stormIntensity > 0.8F && !stormDying)
            {
                if (rand.nextInt((int)(rarityOfStormIncrease * (stage - 1)) + 1) == 0)
                {
                    nextStage();
                }
            }

            if (stage >= 1 && !stormDying && stormIntensity < 0.8F && stormTime < stormStartTime / 2)
            {
                stormDying = true;
            }

            //give chance to evolve to higher stages
        }
    }

    public void setStage(int newStage)
    {
        stage = newStage;

        /*if (stage > 1) {
        	stage = stage;
        }*/
        if (stage > maxStage)
        {
            stage = maxStage;
        }

        //if (WeatherMod.debug) System.out.println("Wind Stage: " + stage);
        //activeStormID = (int)stage-1;
        //if (activeStormID < 0) activeStormID = 0;
    }

    public void prevStage()
    {
        if (stormDeathStage)
        {
            stage = 0;
            stormActive = false;
        }
        else
        {
            stormDeathStage = true;
        }

        setStage(stage - 1);
        int newTime = rand.nextInt(1200) + 720;

        if (stage <= 1)
        {
            setStage(0);
            resetStorm();
            //worldRef.worldInfo.setIsThundering(false);
            //worldRef.worldInfo.setIsRaining(false);
            //newTime = 1;
            stormTime = newTime;
            stormStartTime = newTime;
        }
        else
        {
            //mod_EntMover.worldRef.worldInfo.setThundering(true);
            //mod_EntMover.worldRef.worldInfo.setRaining(true);
            stormTime = newTime;
            stormStartTime = newTime;
            //mod_EntMover.worldRef.worldInfo.setRainTime(newTime);
            //mod_EntMover.worldRef.worldInfo.setThunderTime(newTime);
        }
    }

    public void nextStage()
    {
        if (stage == maxStage - 1)
        {
            return;
        }

        setStage(stage + 1);
        int newTime = rand.nextInt(1200) + 720;
        stormTime = newTime;
        stormStartTime = newTime;
        //mod_EntMover.worldRef.worldInfo.setRainTime(newTime);
        //mod_EntMover.worldRef.worldInfo.setThunderTime(newTime);
    }

    public void resetStorm()
    {
        stormActive = false;
        stormDying = false;
        stormDeathStage = false;
        //t_SpawnTornado = false;
    }

    //Weather Functions

}
