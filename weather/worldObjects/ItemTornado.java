package weather.worldObjects;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.settings.EnumOptions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import weather.WeatherEntityConfig;
import weather.WeatherMod;
import weather.blocks.structure.Structure;
import weather.storm.EntTornado;

public class ItemTornado extends Item
{
    public int type;

    public ItemTornado(int var1, int var2)
    {
        super(var1);
        this.maxStackSize = 64;
        this.type = var2;
        this.setMaxDamage(190);
    }
    
    public Icon getIconFromDamage(int par1) {
    	return Item.silk.getIconFromDamage(0);
    }

    public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3)
    {
        var2.playSoundAtEntity(var3, "random.pop", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F));
        var2.playSoundAtEntity(var3, "random.wood click", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F));
        Object var4 = null;

        if (var2.isRemote)
        {
            return var1;
        }

        if (this.type == 0)
        {
            //if (true) return var1;
            var4 = new EntTornado(var2, (WeatherEntityConfig)WeatherMod.weatherEntTypes.get(1), 1);
        }
        else if (this.type == 1)
        {
            var4 = new EntWorm(var2);
            //((Structure)var4).setPosition(var3.posX, var3.posY + 5, var3.posZ);
            ((EntWorm)var4).head.bodyPiece.setPosition(var3.posX, var3.posY + 3, var3.posZ);
        }
        else if (this.type == 2)
        {
            var4 = new EntShockWave(var2);
        }
        else if (this.type == 3)
        {
            var4 = new EntDrill(var2);
        }
        else if (this.type == 4)
        {
        	var4 = new Structure(var2);
            ((Structure)var4).setPosition(var3.posX, var3.posY + 5, var3.posZ);
            
            //Zombie zombie = new Zombie(var2);
			
			//zombie.setPosition(var3.posX, var3.posY + 5, var3.posZ);
			
			var2.spawnEntityInWorld((Structure)var4);
			
			return var1;
            
            //((c_w_EntWorm)var4).head.bodyPiece.setPosition(var3.posX, var3.posY + 3, var3.posZ);
        }

        double var5 = var3.posY - (double)var3.yOffset + 0.0D;
        ((Entity)var4).setLocationAndAngles(var3.posX, var5, var3.posZ, var3.rotationYaw, var3.rotationPitch);
        
        
        World worldObj = var2;
        
        if (!worldObj.isRemote) {
        	if (this.type == 0) {
	
		        if (MinecraftServer.getServer() != null)
		        {
		            ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
		            DataOutputStream dos = new DataOutputStream(bos);
		
		            try
		            {
		            	dos.writeInt(0);
		            	dos.writeInt(((Entity)var4).entityId);
		                dos.writeFloat((float)((Entity)var4).posX);
		                dos.writeFloat((float)((Entity)var4).posY);
		                dos.writeFloat((float)((Entity)var4).posZ);
		                dos.writeInt(1);
		                
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
		            MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayersInDimension(pkt, worldObj.provider.dimensionId);
		            
		            var2.weatherEffects.add(((Entity)var4));
		        }
        	} else {
        		var2.spawnEntityInWorld((Entity)var4);
        	}
        }
        
        
        double var7 = (double)((WeatherEntityConfig)WeatherMod.weatherEntTypes.get(WeatherMod.stormMan.activeStormID)).tornadoInitialSpeed;
        ((Entity)var4).motionX = var7 * (double)(-MathHelper.sin(((Entity)var4).rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(((Entity)var4).rotationPitch / 180.0F * (float)Math.PI));
        ((Entity)var4).motionZ = var7 * (double)(MathHelper.cos(((Entity)var4).rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(((Entity)var4).rotationPitch / 180.0F * (float)Math.PI));
        ((Entity)var4).motionY = var7 * (double)(-MathHelper.sin(((Entity)var4).rotationPitch / 180.0F * (float)Math.PI));

        if (this.type == 2)
        {
            ((Entity)var4).motionX = 1.0D * (double)(-MathHelper.sin(((Entity)var4).rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(((Entity)var4).rotationPitch / 180.0F * (float)Math.PI));
            ((Entity)var4).motionZ = 1.0D * (double)(MathHelper.cos(((Entity)var4).rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(((Entity)var4).rotationPitch / 180.0F * (float)Math.PI));
            ((Entity)var4).motionY = 0.0D;
            ((Entity)var4).posX += ((Entity)var4).motionX * 2.5D;
            ((Entity)var4).posZ += ((Entity)var4).motionZ * 2.5D;
            var3.swingItem();
        }
        else if (this.type == 0)
        {
            ((EntTornado)var4).lastMotionX = ((Entity)var4).motionX;
            ((EntTornado)var4).lastMotionZ = ((Entity)var4).motionZ;
            ((EntTornado)var4).realYaw = ((Entity)var4).rotationYaw;
            ((Entity)var4).posX += (double)(-MathHelper.sin(((Entity)var4).rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(((Entity)var4).rotationPitch / 180.0F * (float)Math.PI)) * 20.5D;
            ((Entity)var4).posZ += (double)(MathHelper.cos(((Entity)var4).rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(((Entity)var4).rotationPitch / 180.0F * (float)Math.PI)) * 20.5D;
            ((Entity)var4).setPosition(((Entity)var4).posX, ((Entity)var4).posY, ((Entity)var4).posZ);
            ((EntTornado)var4).getNewYPos();
        }
        else if (this.type == 3 || this.type == 4)
        {
            ((Entity)var4).setLocationAndAngles(var3.posX, var5 + 1.0D, var3.posZ, var3.rotationYaw, var3.rotationPitch);
            ((Entity)var4).motionX = 0.15D * (double)(-MathHelper.sin(((Entity)var4).rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(((Entity)var4).rotationPitch / 180.0F * (float)Math.PI));
            ((Entity)var4).motionZ = 0.15D * (double)(MathHelper.cos(((Entity)var4).rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(((Entity)var4).rotationPitch / 180.0F * (float)Math.PI));
            ((Entity)var4).motionY = 0.15D * (double)(-MathHelper.sin(((Entity)var4).rotationPitch / 180.0F * (float)Math.PI));
            ((Entity)var4).posX += ((Entity)var4).motionX * 8.5D;
            ((Entity)var4).posY += ((Entity)var4).motionY * 8.5D;
            ((Entity)var4).posZ += ((Entity)var4).motionZ * 8.5D;
            ((Entity)var4).setPosition(((Entity)var4).posX, ((Entity)var4).posY, ((Entity)var4).posZ);
            var3.swingItem();
        } else {
        	((Entity)var4).setLocationAndAngles(var3.posX, var5 + 0.1F, var3.posZ, var3.rotationYaw, var3.rotationPitch);
        }
        
        

        WeatherMod.particleCount = 0;
        //mod_EntMover.stormTime = 0;
        //mod_EntMover.stormActive = false;
        //mod_EntMover.setStage(mod_EntMover.stage + 1L);
        //var2.worldInfo.setIsRaining(true);
        //var2.worldInfo.setIsThundering(true);
        return var1;
    }

    public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7)
    {
        return false;
    }

    public int func_100011_a(NBTTagByte var1)
    {
        return 1;
    }

    public float func_100012_a(ItemStack var1, Frustrum var2)
    {
        return 1.0F;
    }

    public void hitEntity(ItemStack var1, EntityLiving var2) {}

    public void func_100010_b(EnumOptions var1, EntityLiving var2)
    {
        if (this.type != 0)
        {
            ;
        }
    }
}
