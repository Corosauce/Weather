package weather;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import net.minecraft.entity.Entity;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import weather.entities.storm.EntTornado;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class WeatherPacketHandler implements IPacketHandler
{
    public WeatherPacketHandler()
    {
    }

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet.data));

        if ("WindData".equals(packet.channel))
        {
            try
            {
                float val = dis.readFloat();
                float val2 = dis.readFloat();
                WeatherMod.weatherMan.wind.strength = val;
                WeatherMod.weatherMan.wind.direction = val2;
                //TEST
                //WeatherMod.weatherMan.wind.strength = 1.5F;
                //WeatherMod.weatherMan.wind.strengthSmooth = val;
                //System.out.println("packet: " + val + " - " + val2);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        else if ("StormData".equals(packet.channel))
        {
            try
            {
                int val = dis.readInt();
                float val2 = dis.readFloat();
                float waveheight = dis.readFloat();
                WeatherMod.stormMan.stage = val;
                WeatherMod.stormMan.stormIntensity = val2;
                WeatherMod.stormMan.baseWaveHeight = waveheight;
                //WeatherMod.weatherMan.wind.direction = val2;
                //WeatherMod.weatherMan.wind.strengthSmooth = val;
                //System.out.println("packet: " + val + " - " + val2);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        } else if ("Tornado".equals(packet.channel))
        {
            try
            {
                int state = dis.readInt();
                World world = WeatherMod.proxy.getClientWorld();
                if (state == 0) { //spawning: id, x, y, z, type  
                	EntTornado ent = new EntTornado(world);
                	ent.entityId = dis.readInt();
                	ent.posX = dis.readFloat();
                	ent.posY = dis.readFloat();
                	ent.posZ = dis.readFloat();
                	int type = dis.readInt();
                	ent.entConf = (WeatherEntityConfig)WeatherMod.weatherEntTypes.get(type);
                	ent.entConfID = type;
                	System.out.println("spawning client tornado, type: " + type);
                	world.addWeatherEffect(ent);
                } else if (state == 1) { //updating: id, posx, posy, posz, velx, vely, velz 
                	int id = dis.readInt();
                	Entity ent = null;// = WeatherMod.proxy.getEntByID(id);
                	
                	for (int i = 0; i < world.weatherEffects.size(); i++) {
                		Entity we = (Entity)world.weatherEffects.get(i);
                		if (we.entityId == id) {
                			ent = we;
                			break;
                		}
                	}
                	
                	//System.out.println("syncing client tornado: " + id);
                	if (ent instanceof EntTornado) {
                		ent.setPosition(dis.readFloat(), dis.readFloat(), dis.readFloat());
                		ent.setVelocity(dis.readFloat(), dis.readFloat(), dis.readFloat());
                	}
                } else if (state == 2) { //kill: id
                	int id = dis.readInt();
                	Entity ent = null;// = WeatherMod.proxy.getEntByID(id);
                	
                	for (int i = 0; i < world.weatherEffects.size(); i++) {
                		Entity we = (Entity)world.weatherEffects.get(i);
                		if (we.entityId == id) {
                			ent = we;
                			break;
                		}
                	}
                	
                	if (ent instanceof EntTornado) {
                		((EntTornado)ent).startDissipate();
                	}
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
