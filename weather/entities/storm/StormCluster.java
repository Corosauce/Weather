package weather.entities.storm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import weather.WeatherEntityConfig;
import weather.WeatherMod;
import weather.config.ConfigTornado;
import weather.entities.particles.EntityAnimTexFX;
import weather.system.wind.WindHandler;
import CoroUtil.util.CoroUtilEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import extendedrenderer.ExtendedRenderer;
import extendedrenderer.particle.ParticleRegistry;
import extendedrenderer.particle.behavior.ParticleBehaviorFog;
import extendedrenderer.particle.entity.EntityRotFX;
@SideOnly(Side.CLIENT)
public class StormCluster extends Entity implements WindHandler
{
    public World worldRef;
    public List cloudEffects;
    public int age;
    public int maxAge = 5000;

    public static int updateLCG;
    
    public int type = 0;
    
	public ParticleBehaviorFog particleBehaviorFog;

    public StormCluster()
    {
        super(WeatherMod.worldRef);
        cloudEffects = new ArrayList();
        worldRef = WeatherMod.worldRef;
        this.updateLCG = (new Random()).nextInt();
        age = 0;
    }
    
    //rubes smooth texture render gl calls

/*
   GL11.glDisable(3553  GL_TEXTURE_2D );
   		GL11.glShadeModel(7425  GL_SMOOTH );
   		GL11.glEnable(3042  GL_BLEND );
   		GL11.glBlendFunc(770, 1);
   		GL11.glDisable(3008  GL_ALPHA_TEST );
   		GL11.glDisable(2884  GL_CULL_FACE );
   		GL11.glDepthMask(false);

   GL11.glDepthMask(true);
   		GL11.glEnable(2884  GL_CULL_FACE );
   		GL11.glDisable(3042  GL_BLEND );
   		GL11.glShadeModel(7424  GL_FLAT );
   		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   		GL11.glEnable(3553  GL_TEXTURE_2D );
   		GL11.glEnable(3008  GL_ALPHA_TEST );
*/

    public void onUpdate()
    {
    	
    	if (!worldObj.isRemote) return;
    	
    	if (particleBehaviorFog == null) {
			particleBehaviorFog = new ParticleBehaviorFog(Vec3.createVectorHelper(posX, posY, posZ));
			particleBehaviorFog.sourceEntity = this;
		} else {
			if (!Minecraft.getMinecraft().isSingleplayer() || !(Minecraft.getMinecraft().currentScreen instanceof GuiIngameMenu)) {
				particleBehaviorFog.tickUpdateList();
			}
		}
    	
        //Entity target = null;//mod_EntMover.activeTornado;
        byte radius = 3;
        byte baseRadius = 9;
        //System.out.println("dadasdasd!");
        int var3;
        int var4;
        int var6;
        int var7;
        int var8;
        int var9;
        //var3 = MathHelper.floor_double(target.posX / 16.0D);
        //var4 = MathHelper.floor_double(target.posZ / 16.0D);
        var3 = MathHelper.floor_double(posX / 16.0D);
        var4 = MathHelper.floor_double(posZ / 16.0D);
        byte var5 = radius;

        /*for (int var66 = -var5; var66 <= var5; ++var66)
        {
            for (int var77 = -var5; var77 <= var5; ++var77)
            {
                //worldRef.positionsToUpdate.add(new ChunkCoordIntPair(var6 + var3, var7 + var4));
                int var33 = (var66 + var3) * 16;
                int var44 = (var77 + var4) * 16;

                if (worldRef.rand.nextInt(1 + (int)(((float)1000 * ((float)radius / (float)baseRadius)))) == 0)
                {
                    //System.out.println("blam!");
                    updateLCG = updateLCG * 3 + 1013904223;
                    var6 = updateLCG >> 2;
                    var7 = var33 + (var6 & 15);
                    var8 = var44 + (var6 >> 8 & 15);
                    var9 = 100;//worldRef.findTopSolidBlock(var7, var8);
                    //if(worldRef.canBlockBeRainedOn(var7, var9, var8)) {
                    EntityAnimTexFX var31 = new EntityAnimTexFX(worldRef, posX, posY, posZ, worldRef.rand.nextGaussian() * 0.8D, worldRef.rand.nextGaussian() * 0.8D, worldRef.rand.nextGaussian() * 0.8D, 20D, WeatherMod.effWindAnimID);
                    var31.spawnY = ((int)128 - 5) + rand.nextFloat() * 5;
                    //this.effR.addEffect(var31);
                    //this.funnelEffects.add(var31);
                    //mod_EntMover.particleCount++;
                    var31.rotationPitch = 90F;
                    var31.renderDistanceWeight = 10.0D;
                    var31.noClip = true;
                    var31.setSize(1.25F, 1.25F);
                    //var31.posY = var6 + 0D;
                    var31.setPosition(posX, var31.spawnY, posZ);
                    var31.type = 1;
                    ExtendedRenderer.rotEffRenderer.addEffect(var31);
                    //System.out.println(var31.posY);
                    cloudEffects.add(var31);
                    WeatherMod.particleCount2++;
                    //EntityCloud ent = new EntityCloud(worldRef);
                    //ent.setPosition(var7, var9, var8);
                    ent.motionX = ent.rand.nextFloat()*2-1;
                    ent.motionY = ent.rand.nextFloat()*2-1;
                    ent.motionZ = ent.rand.nextFloat()*2-1;
                    //ent.setPosition((double)var7, (double)var9, (double)var8);
                    //worldRef.addWeatherEffect(ent);
                    //}
                }
            }
        }*/
        
        int xMax = 100;
        int zMax = 100;
        int inc = 10;
        Random rand = new Random();
        
        if (cloudEffects.size() < 500) {
        	for (int xx = 0; xx < xMax; xx+=inc) {
        		for (int zz = 0; zz < zMax; zz+=inc) {
        			
        			if (rand.nextInt(150) != 0) continue;
        			
        			//old way
        			if (!ConfigTornado.Storm_Tornado_hdParticles) {
				        EntityAnimTexFX var31 = new EntityAnimTexFX(worldRef, posX, posY, posZ, worldRef.rand.nextGaussian() * 0.8D, worldRef.rand.nextGaussian() * 0.8D, worldRef.rand.nextGaussian() * 0.8D, 20D, WeatherMod.effWindAnimID);
				        var31.spawnY = ((int)200 - 5) + rand.nextFloat() * 5;
				        var31.rotationPitch = -90F;
				        var31.renderDistanceWeight = 10.0D;
				        var31.noClip = true;
				        var31.setSize(1.25F, 1.25F);
				        var31.setPosition(posX + xx - xMax/2, var31.spawnY, posZ + zz - zMax/2);
				        var31.type = 1;
				        
				        ExtendedRenderer.rotEffRenderer.addEffect(var31);
				        cloudEffects.add(var31);
				        WeatherMod.particleCount2++;
        			}
        			
        			
        			//new way - no
        			/*float speed = 1F;
        			EntityRotFX var31 = new EntityIconFX(worldObj, posX + rand.nextDouble(), posY + 0.0D + rand.nextDouble() * 1.5D, posZ + rand.nextDouble(), (rand.nextDouble() - rand.nextDouble()) * speed, 0.03D(rand.nextDouble() - rand.nextDouble()) * speed, (rand.nextDouble() - rand.nextDouble()) * speed, ParticleRegistry.cloud);
        			var31.spawnY = ((int)128 - 5) + rand.nextFloat() * 5;
        			var31.rotationPitch = 90F;
			        var31.renderDistanceWeight = 10.0D;
			        var31.noClip = true;
			        var31.setSize(5.25F, 5.25F);
			        var31.particleScale = 75F;*/

        			//real new way
        			/*int topY = 180;
        			int rangeY = 30;
        			float speed = 1F;
        			float spawnY = ((int)topY - rangeY) + rand.nextFloat() * rangeY;
        			EntityRotFX var31 = WeatherMod.pm.spawnNewParticleIconFX(worldObj, ParticleRegistry.cloud, posX, spawnY, posZ, (rand.nextDouble() - rand.nextDouble()) * speed, (rand.nextDouble() - rand.nextDouble()) * speed, (rand.nextDouble() - rand.nextDouble()) * speed);
        			WeatherMod.pm.setParticleCloud(var31, spawnY);
        			var31.setPosition(posX + xx - xMax/2, var31.spawnY, posZ + zz - zMax/2);
        			//EntityRotFX var31 = new EntityIconFX(worldObj, posX + rand.nextDouble(), posY + 0.0D + rand.nextDouble() * 1.5D, posZ + rand.nextDouble(), (rand.nextDouble() - rand.nextDouble()) * speed, 0.03D(rand.nextDouble() - rand.nextDouble()) * speed, (rand.nextDouble() - rand.nextDouble()) * speed, ParticleRegistry.cloud);
			        
			        
			        */
			        /**/
        		}
        	}
        }

    	//new new new way
    	//not using cloudEffects for now
    	if (ConfigTornado.Storm_Tornado_hdParticles) {
    		double createRange = 500;
    		double createRangeY = 30;
    		double spawnX = posX + rand.nextDouble() * createRange - rand.nextDouble() * createRange;
    		double spawnZ = posZ + rand.nextDouble() * createRange - rand.nextDouble() * createRange;
    		EntityRotFX particle = spawnFogParticle(spawnX, posY, spawnZ);
    		WeatherMod.particleCount2++;
    	}
        
        //System.out.println("active clouds: " + cloudEffects.size());
        
        //Localized rain!
        for (int i = 0; i < 10; i++) {
        	
            //FMLClientHandler.instance().getClient().effectRenderer.addEffect(ent);
            //ent.spawnAsWeatherEffect();
        }

        if (cloudEffects.size() > 0)
        {
            for (int var99 = 0; var99 < cloudEffects.size(); ++var99)
            {
                EntityRotFX var30 = (EntityRotFX)cloudEffects.get(var99);

                //rotations!
                /*double var16 = this.posX - var30.posX;
                double var18 = this.posZ - var30.posZ;
                float var20 = this.rotationYaw;
                var30.rotationYaw = (float)(Math.atan2(var18, var16) * 180.0D / Math.PI) - 90.0F;
                var30.rotationPitch = -30F;*/

                if (var30.getAge() >= var30.getMaxAge())
                {
                    var30.setDead();
                    //mod_ExtendedRenderer.rotEffRenderer.fxLayers[4].remove(var30);
                }

                if (var30.isDead)
                {
                    cloudEffects.remove(var30);
                    WeatherMod.particleCount2--;
                }
                else if (var30 != null)
                {
                	
                	//var30.pb.tickUpdateCloud(var30);
                	
                    WeatherMod.spin(this, (WeatherEntityConfig)WeatherMod.weatherEntTypes.get(type != 1 ? type : 2), var30);
                    
                    
                    //var30.motionX *= 0.995F;
                    //var30.motionZ *= 0.995F;
                }
                
                float distCur = var30.getDistanceToEntity(this);
                float distMax = cloudEffects.size() * 0.7F;
                
                if (distCur > distMax) {
                	CoroUtilEntity.moveTowards(var30, this, 0.03F);
                }
            }
        }
        
        //setDead();
    }
    
    @SideOnly(Side.CLIENT)
    public EntityRotFX spawnFogParticle(double x, double y, double z) {
    	double speed = 0D;
    	EntityRotFX entityfx = particleBehaviorFog.spawnNewParticleIconFX(Minecraft.getMinecraft().theWorld, ParticleRegistry.cloud256, x, y, z, (rand.nextDouble() - rand.nextDouble()) * speed, 0.0D/*(rand.nextDouble() - rand.nextDouble()) * speed*/, (rand.nextDouble() - rand.nextDouble()) * speed);
		particleBehaviorFog.initParticle(entityfx);
		//lock y
		//entityfx.spawnY = (float) entityfx.posY;
		entityfx.spawnY = ((int)200 - 5) + rand.nextFloat() * 5;
    	entityfx.callUpdatePB = false;
		//ExtendedRenderer.rotEffRenderer.addEffect(entityfx);
		entityfx.spawnAsWeatherEffect();
		particleBehaviorFog.particles.add(entityfx);
		return entityfx;
    }

    @Override
    public void setDead()
    {
        super.setDead();

        if (cloudEffects.size() > 0)
        {
            for (int var99 = 0; var99 < cloudEffects.size(); ++var99)
            {
                EntityRotFX var30 = (EntityRotFX)cloudEffects.get(var99);
                //var30.setAge(9999999);
                var30.setDead();
                //cloudEffects.remove(var30);
                //mod_ExtendedRenderer.rotEffRenderer.fxLayers[4].remove(var30);
            }
        }

        //mod_ExtendedRenderer.rotEffRenderer.clearEffects(worldRef);
    }

    @Override
    protected void entityInit()
    {
        // TODO Auto-generated method stub
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound var1)
    {
        // TODO Auto-generated method stub
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound var1)
    {
        // TODO Auto-generated method stub
    }

	@Override
	public float getWindWeight() {
		return 500;
	}

	@Override
	public int getParticleDecayExtra() {
		return 0;
	}
}
