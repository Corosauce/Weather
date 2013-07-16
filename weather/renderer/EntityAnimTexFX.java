package weather.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.src.ModLoader;
import net.minecraft.world.World;

import java.awt.Color;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import extendedrenderer.particle.entity.EntityRotFX;
@SideOnly(Side.CLIENT)
public class EntityAnimTexFX extends EntityRotFX
{
    public int age;
    public float brightness;

    //dont use, or separate this class from the layer 4 stuff
    public int textureIDs[];

    //Types, for diff physics rules in wind code
    //Leaves = 0
    //Sand = 1

    //or not
    public int type = 0;
    public int frames = 10;
    public int frame = 0;

    public EntityAnimTexFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, int texIDs[], int var16)
    {
        this(var1, var2, var4, var6, var8, var10, var12, var14, texIDs);
        Color var17 = null;

        frame = var1.rand.nextInt(frames);
        
        if (var16 == 0)
        {
            this.particleRed = this.particleGreen = this.particleBlue = this.rand.nextFloat() * 0.3F;
        }
        else if (var16 == 1)
        {
            var17 = new Color(7951674);
        }
        else if (var16 == 2)
        {
            var17 = new Color(14077848);
        }
        else if (var16 == 3)
        {
            var17 = new Color(10973);
        }
        else if (var16 == 4)
        {
            var17 = new Color(15663103);
        }
        else if (var16 == 5)
        {
            var17 = new Color(0xFFFFFF);
        }
        else if (var16 == 6)
        {
            var17 = Color.GREEN;
        }

        //this.brightness = 2.0F;

        if (var16 != 0)
        {
            this.particleRed = (float)var17.getRed() / 255.0F;
            this.particleGreen = (float)var17.getGreen() / 255.0F;
            this.particleBlue = (float)var17.getBlue() / 255.0F;
        }
        else
        {
            this.particleRed = this.particleGreen = this.particleBlue = 0.7F;
        }
    }

    public EntityAnimTexFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, int texIDs[])
    {
        super(var1, var2, var4, var6, var8, var10, var12, var14, texIDs);
        textureIDs = texIDs;
        this.motionX = var8 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.05F);
        this.motionY = var10 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.05F);
        this.motionZ = var12 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.05F);
        //BRIGHTNESS OVERRIDE! for textures
        this.particleRed = this.particleGreen = this.particleBlue = 0.7F;
        brightness = 2F;
        /*if (colorIndex != 0) {
            this.particleRed = color.getRed() / 255F;
            this.particleGreen = color.getGreen() / 255F;
            this.particleBlue = color.getBlue() / 255F;
        }*/
        this.particleScale = 30F;//this.rand.nextFloat() * this.rand.nextFloat() * 20.0F + 1.0F;
        this.particleMaxAge = (int)(16.0D/* / ((double)this.rand.nextFloat() * 0.8D + 0.2D)*/) + 2;
        this.particleMaxAge = (int)((float)this.particleMaxAge * var14);
        this.particleGravity = 0.03F;
        //this.particleScale = 5F;
        this.setParticleTextureIndex(0);
        renderDistanceWeight = 100.0D;
        setSize(1.0F, 1.0F);
        noClip = false;
        particleAge = 1;
    }

    public void setSize(float var1, float var2)
    {
        this.width = var1;
        this.height = var2;
    }

    public void renderParticle(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7)
    {
        //frames = 16;
        //this.rotationYaw = ModLoader.getMinecraftInstance().thePlayer.rotationYaw;
        //this.rotationPitch += this.rand.nextInt(4) - 2;//ModLoader.getMinecraftInstance().thePlayer.rotationPitch;
        /*if (System.currentTimeMillis() % 50 == 0) {
        	particleAge++;
        }*/
        if (particleAge < 5)
        {
            return;
        }

        if (posX < 30 || posZ < 30)
        {
            //System.out.println((int)posX + " - " + (int)posZ);
        }

        if (type != 1) {
        	this.setParticleTextureIndex((((int)particleAge / 3) + 0) % (frames));
        } else {
        	this.setParticleTextureIndex(frame);
        }
        int xCount = 2;
        int yCount = 4;
        float resSizeScaled = 0.0624375F * 4F;
        float dist = this.getDistanceToEntity(ModLoader.getMinecraftInstance().thePlayer);
        float scale;

        if (dist > 20F)
        {
            scale = 1F;
        }
        else
        {
            scale = dist / 20F;
        }

        float var8 = (float)(this.getParticleTextureIndex() % xCount) / 2.0F;
        float var9 = var8 + (resSizeScaled * 2F);
        float var10 = (float)(this.getParticleTextureIndex() / yCount) / 1.0F;
        float var11 = var10 + (resSizeScaled * 1F);
        float var12 = 0F;

        if (type == 0)
        {
            var12 = 0.1F * this.particleScale * 1F / (this.particleMaxAge / this.particleAge * 1) * (1 * 3);
        }
        else if (type == 1)
        {
            var12 = 0.6F * this.particleScale;
        }

        float var13 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)var2 - interpPosX);
        float var14 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)var2 - interpPosY) + 0.0F;
        float var15 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)var2 - interpPosZ);
        //var13 += i;//rand.nextInt(6)-3;
        //var14 += j;
        //var15 += k;
        //System.out.println("!!!");
        float var16 = /*this.getEntityBrightness(var2) * */brightness;
        //var16 = (Minecraft.getMinecraft().gameSettings.gammaSetting) - (this.worldObj.calculateSkylightSubtracted(var2) * 0.F);
        float adjSubtracted = (this.worldObj.calculateSkylightSubtracted(var2) / 15F) * 0.5F;
        var16 = 0.4F - adjSubtracted + (ModLoader.getMinecraftInstance().gameSettings.gammaSetting * 0.7F);
        
        //var16 -= 0.5F;
        //var16 += 0.3F;
        
        //var16 = 2F;
        
        //var1.setColorOpaque_F(this.particleRed * var16, this.particleGreen * var16, this.particleBlue * var16);
        
        float gamma = 0.7F;
        
        if (particleMaxAge - particleAge < 70) {
        	int count = (particleMaxAge - particleAge);
        	gamma = (float)count * 0.01F;
        	//gamma += ((float)Math.abs(particleMaxAge - particleAge) * 0.02F);
        	//System.out.println("gamma: " + gamma);
        }
        
        var1.setColorRGBA_F(this.particleRed * var16, this.particleGreen * var16, this.particleBlue * var16, Math.max(0F, gamma));
        var1.addVertexWithUV((double)(var13 - var3 * var12 - var6 * var12), (double)(var14 - var4 * var12), (double)(var15 - var5 * var12 - var7 * var12 - 0F), (double)var9, (double)var11);
        var1.addVertexWithUV((double)(var13 - var3 * var12 + var6 * var12), (double)(var14 + var4 * var12), (double)(var15 - var5 * var12 + var7 * var12 - 0F), (double)var9, (double)var10);
        var1.addVertexWithUV((double)(var13 + var3 * var12 + var6 * var12), (double)(var14 + var4 * var12), (double)(var15 + var5 * var12 + var7 * var12 + 0F), (double)var8, (double)var10);
        var1.addVertexWithUV((double)(var13 + var3 * var12 - var6 * var12), (double)(var14 - var4 * var12), (double)(var15 + var5 * var12 - var7 * var12 + 0F), (double)var8, (double)var11);
        /*}
        }
        }*/
    }

    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        //If clouds that are locked in Y position
        if (type == 1)
        {
            this.setPosition(this.posX, this.spawnY/*var30.posY+0.4*/, this.posZ);
            /*if (rand.nextInt(5) == 0) {
            	int size = 5;

            	EntityRotFX ent = new EntityFallingRainFX(this.worldObj, (double)this.posX+worldObj.rand.nextInt(size)-(size/2), (double)this.posY+10, (double)this.posZ+worldObj.rand.nextInt(size)-(size/2), 0D, -5D-(worldObj.rand.nextInt(5)*-1D), 0D, 1.5D, 3);

                ent.renderDistanceWeight = 1.0D;
                ent.setSize(1.2F, 1.2F);
                ent.rotationYaw = ent.rand.nextInt(360) - 180F;
                ent.particleGravity = 0.00001F;
                ent.spawnAsWeatherEffect();
            }*/
        }

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
        }

        this.setParticleTextureIndex((((int)particleAge) + 0) % frames);
        //this.particleTextureIndex = 7 - this.particleAge * 8 / this.particleMaxAge;
        //this.particleTextureIndex = 7 - this.particleAge * 8 / this.particleMaxAge;
        //this.func_40099_c(textureIDs[0]);//mod_EntMover.effWindID;
        //this.motionY += 0.0040D;
        this.motionY -= 0.04D * (double)this.particleGravity;
        //this.motionY -= 0.05000000074505806D;
        float var20 = 0.98F;
        this.motionX *= (double)var20;
        this.motionY *= (double)var20;
        this.motionZ *= (double)var20;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        /*this.motionX *= 0.8999999761581421D;
        this.motionY *= 0.8999999761581421D;
        this.motionZ *= 0.8999999761581421D;
        if(this.onGround) {
           this.motionX *= 0.699999988079071D;
           this.motionZ *= 0.699999988079071D;
        }*/
    }

    public int getFXLayer()
    {
        return 4;
    }
    
    public float maxRenderRange() {
    	return 512F;
    }
}
