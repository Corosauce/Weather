package weather.storm;

import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import weather.WeatherMod;
import weather.c_CoroWeatherUtil;
import weather.config.ConfigTornado;
import cpw.mods.fml.relauncher.Side;

public class StormManager
{
	public int dimension;
	
    public int maxStage;

    public long thunderingTime;

    public boolean stormDying = false;
    public boolean stormDeathStage = false;

    public int stormTime = 0;
    public int stormStartTime = 0;
    public float stormStrength = 0F;
    public float stormIntensity = 0F;
    public boolean stormActive = false;

    public float baseWaveHeight = 1F;

    public int activeStormID;
    public int stage;

    private int syncDelay;

    public World world;

    private long lastWorldTime;
    
    public float smoothStrength;

    public StormManager(int dim)
    {
    	this();
    	dimension = dim;
    	if (dim == 0 || dim == c_CoroWeatherUtil.tropiDimID) {
    		maxStage = WeatherMod.weatherEntTypes.size();
    	}
    }
    
    public StormManager()
    {
    	dimension = 0;
    }

    public void tick(Side side, World parWorld)
    {
        if (side == Side.SERVER)
        {
            world = parWorld;//WeatherMod.proxy.getServerWorld();
            manageStage();
            syncDelay--;

            if (syncDelay <= 0 && MinecraftServer.getServer() != null)
            {
                syncDelay = 5;
                ByteArrayOutputStream bos = new ByteArrayOutputStream(Integer.SIZE + (Float.SIZE * 2));
                DataOutputStream dos = new DataOutputStream(bos);

                try
                {
                    dos.writeInt(stage);
                    dos.writeFloat(stormIntensity);
                    dos.writeFloat(this.baseWaveHeight);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }

                Packet250CustomPayload pkt = new Packet250CustomPayload();
                pkt.channel = "StormData";
                pkt.data = bos.toByteArray();
                pkt.length = bos.size();
                MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayersInDimension(pkt, world.provider.dimensionId);
            }
        }
        else
        {
            //if world is even needed at all for client side....
            world = WeatherMod.proxy.getClientWorld();

            if (world != null)
            {
                if (world.getWorldInfo().isRaining() && stage >= 1)
                {
                	//System.out.println(world.isRaining());
                    //if (world.getRainStrength(1F) >= 0.01F)
                    //{
                        float rStr = stage * 0.3F;

                        if (rStr > 1.0F)
                        {
                            rStr = 1.0F;
                        }

                        float tStr = 0.0F + (stage * 0.3F);

                        if (tStr > 2.0F)
                        {
                            tStr = 2.0F;
                        }
                        
                        if (smoothStrength < tStr) {
                        	smoothStrength += 0.003F;
                        	if (smoothStrength > tStr) smoothStrength = tStr;
                        } else if (smoothStrength > tStr) {
                        	smoothStrength -= 0.01F;
                        	if (smoothStrength < tStr) smoothStrength = tStr;
                        } 

                        //world.setRainStrength(smoothStrength);
                        c_CoroWeatherUtil.setThunderStr(world, smoothStrength);
                        
                        //System.out.println("boom");
                    //}
                } else {
                	smoothStrength = 0;
                }
            }
        }
    }

    public void manageStage()
    {
    	
    	//System.out.println(world.getWorldInfo().getRainTime());
    	
    	if (world.getWorldInfo().getRainTime() > 400) {
    		//world.getWorldInfo().setRainTime(200);
    	}
    	
        if (stormActive)
        {
            if (stormStrength < 0.5F)
            {
                stormStrength = c_CoroWeatherUtil.getThunderStr(world) + 0.25F;
            }
            else
            {
                stormStrength = 0.5F + (0.5F * stormTime / stormStartTime);
            }

            stormIntensity = (0.25F - Math.abs(stormStrength - 0.75F)) / 0.25F;

            if (stormIntensity < 0F)
            {
                stormIntensity = 0.0001F;
            }
            
            if (world.getWorldTime() % 40 == 0) {
            	//System.out.println("stormIntensity: " + stormIntensity + " - " + "stage: " + stage + "stormDying: " + stormDying);
            }

            if (stormIntensity > 0.8F && stage >= 2 && !stormDying)
            {
                WeatherMod.tryTornadoSpawn(this.dimension);
            }
            
        } else {
            //WeatherMod.t_trySpawnTornado = false;
            //WeatherMod.t_SpawnTornado = false;
        }

        if (stormTime < 0 && stage >= 2)
        {
            prevStage();
        }

        if (WeatherMod.Storm_noRain)
        {
            world.getWorldInfo().setRaining(false);
            //world.getWorldInfo().setThunderTime(500);
            world.getWorldInfo().setRainTime(500);
            world.setRainStrength(0.0F);
        }

        /*world.getWorldInfo().setThunderTime(500);
        world.getWorldInfo().setThundering(true);*/

        if (stage >= 2)
        {
            if (!world.getWorldInfo().isRaining())
            {
                prevStage();
            }

            if (lastWorldTime != world.getWorldInfo().getWorldTime())
            {
                stormTime--;
            }

            if (stormIntensity > 0.8F && !stormDying)
            {
            	int chance = (int)(ConfigTornado.Storm_rarityOfIncrease * (stage - 1)) + 1;
            	if (chance > 0) {
	                if (world.rand.nextInt(chance) == 0)
	                {
	                    nextStage();
	                }
            	}
            }

            if (!stormDying && stormIntensity < 0.8F && stormTime < stormStartTime / 2)
            {
                stormDying = true;
            }

            //give chance to evolve to higher stages
        }
        else if (world.getWorldInfo().isRaining() && world.getWorldInfo().isThundering() && stage < 2 && !stormDying)
        {
            stormActive = true;
            nextStage();
        }
        else if (world.getWorldInfo().isRaining() && !stormDying)
        {
            setStage(0);
            nextStage();
            //setStage(1);
            resetStorm();
        }
        else if (stage < 2)
        {
            setStage(0);
            resetStorm();
        }
        else
        {
        }
    }

    public void resetStorm()
    {
        stormActive = false;
        stormDying = false;
        stormDeathStage = false;
        //t_SpawnTornado = false;
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

        //if (WeatherMod.debug && stage > 1) System.out.println("Storm Stage: " + stage);
        activeStormID = (int)stage - 1;

        if (activeStormID < 0)
        {
            activeStormID = 0;
        }
    }
    public void prevStage()
    {
        if (stormDeathStage)
        {
            stage = 2;
        }
        else
        {
            stormDeathStage = true;
        }

        setStage(stage - 1);
        int newTime = world.rand.nextInt(12000) + 3600;

        if (stage <= 2)
        {
            setStage(0);
            resetStorm();
            world.getWorldInfo().setThundering(false);
            world.getWorldInfo().setRaining(false);
            newTime = world.rand.nextInt(120000) + 36000;
            world.getWorldInfo().setRainTime(newTime);
            world.getWorldInfo().setThunderTime(newTime);
        }
        else
        {
            world.getWorldInfo().setThundering(true);
            world.getWorldInfo().setRaining(true);
            stormTime = newTime;
            stormStartTime = newTime;
            world.getWorldInfo().setRainTime(newTime);
            world.getWorldInfo().setThunderTime(newTime);
        }

        //if (WeatherMod.debug && stage > 1) System.out.println("Prev Stage: " + stage);
    }
    public void nextStage()
    {
        if (stage == maxStage - 1)
        {
            return;
        }

        setStage(stage + 1);
        int newTime = world.rand.nextInt(12000) + 3600;
        stormTime = newTime;
        stormStartTime = newTime;

        if (stage > 1)
        {
            world.getWorldInfo().setRainTime(newTime);
            world.getWorldInfo().setThunderTime(newTime);
        }

        //if (WeatherMod.debug && stage > 1) System.out.println("Next Stage: " + stage);
    }
}
