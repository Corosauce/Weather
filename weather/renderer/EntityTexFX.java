package weather.renderer;

import java.awt.Color;

import weather.ExtendedRenderer;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ModLoader;
import net.minecraft.src.Tessellator;
import net.minecraft.src.World;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.Side;
@SideOnly(Side.CLIENT)
public class EntityTexFX extends EntityRotFX
{
    public int age;
    public float brightness;
    public int textureID;

    //Types, for diff physics rules in wind code
    //Leaves = 0
    //Sand = 1
    public int type = 0;

    public EntityTexFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, int colorIndex, int texID)
    {
        super(var1, var2, var4, var6, var8, var10, var12);
        textureID = texID;
        this.motionX = var8 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.05F);
        this.motionY = var10 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.05F);
        this.motionZ = var12 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.05F);
        //Color IDS
        //0 = black/regular/default
        //1 = dirt
        //2 = sand
        //3 = water
        //4 = snow
        //5 = stone
        Color color = null;

        if (colorIndex == 0)
        {
            this.particleRed = this.particleGreen = this.particleBlue = this.rand.nextFloat() * 0.3F/* + 0.7F*/;
        }
        else if (colorIndex == 1)
        {
            color = new Color(0x79553a);
        }
        else if (colorIndex == 2)
        {
            color = new Color(0xd6cf98);
        }
        else if (colorIndex == 3)
        {
            color = new Color(0x002aDD);
        }
        else if (colorIndex == 4)
        {
            color = new Color(0xeeffff);
        }
        else if (colorIndex == 5)
        {
            color = new Color(0x79553a);
        }

        //BRIGHTNESS OVERRIDE! for textures
        this.particleRed = this.particleGreen = this.particleBlue = 0.7F;
        brightness = 0.5F;

        if (colorIndex != 0)
        {
            this.particleRed = color.getRed() / 255F;
            this.particleGreen = color.getGreen() / 255F;
            this.particleBlue = color.getBlue() / 255F;
        }

        this.particleScale = this.rand.nextFloat() * this.rand.nextFloat() * 2.0F + 1.0F;
        this.particleMaxAge = (int)(16.0D/* / ((double)this.rand.nextFloat() * 0.8D + 0.2D)*/) + 2;
        this.particleMaxAge = (int)((float)this.particleMaxAge * var14);
        this.particleGravity = 1.0F;
        //this.particleScale = 5F;
        this.setParticleTextureIndex(textureID);
        renderDistanceWeight = 10.0D;
        setSize(1.0F, 1.0F);
        noClip = false;
    }

    public void renderParticle(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7)
    {
        //this.rotationYaw = ModLoader.getMinecraftInstance().thePlayer.rotationYaw;
        //this.rotationPitch += this.rand.nextInt(4) - 2;//ModLoader.getMinecraftInstance().thePlayer.rotationPitch;
        float var8 = (float)(this.getParticleTextureIndex() % 16) / 16.0F;
        float var9 = var8 + 0.0624375F;
        float var10 = (float)(this.getParticleTextureIndex() / 16) / 16.0F;
        float var11 = var10 + 0.0624375F;
        float var12 = 0.1F * this.particleScale;
        //this is no way to code this!
        /*
         * | UltraMoogleMan | Corosus: You need to use a VBO if you're going to use particles, really
         * | UltraMoogleMan | Just bang the vertices into the buffer and then render it in one command
         * | UltraMoogleMan | Corosus: The problem, I think, you'll run into (if you haven't already) is that immediate mode is -slow-
         * | UltraMoogleMan | This is partly the reason why I was going on last night about how ideally, notch would eventually just eject most of the orientation code he has and replace it with a simple vertex shader that does single-weighted boned anims
         * | UltraMoogleMan | Corosus: To boil it down simply, ideally you wouldn't *have* to manually transform every box's vertices by the relevant transform matrix before rendering it
         * | UltraMoogleMan | This is done on the CPU and is slow
         * | UltraMoogleMan | Corosus: Ideally you'd just have a matrix array with one entry for the entire cube
         * | UltraMoogleMan | Corosus: Then you'd have a vertex array with the coords for the cube
         * or better -> | UltraMoogleMan | You'd have a matrix array with all of the transform matrices
         * | UltraMoogleMan | Corosus: Then have an array with all the vertices of all the cubes
         * | UltraMoogleMan | Un-transformed
         * | UltraMoogleMan | Then you just do two glDrawArrays
         * | UltraMoogleMan | Of course, this is predicated on using GLSL
         * | UltraMoogleMan | So you'd need to crack that nut
         * | UltraMoogleMan | But it shouldn't be too hard
         *
         *
         *
         *
         *
        */
        /*for (int i = 0; i < 0; i++) {
          for (int j = 0; j < 3; j++) {
        	  for (int k = 0; k < 3; k++) {*/
        float var13 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)var2 - interpPosX);
        float var14 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)var2 - interpPosY) + 0.0F;
        float var15 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)var2 - interpPosZ);
        //var13 += i;//rand.nextInt(6)-3;
        //var14 += j;
        //var15 += k;
        //System.out.println("!!!");
        float var16 = /*this.getEntityBrightness(var2) * */brightness;
        var16 = ExtendedRenderer.plBrightness * 0.1F;
        /*this.getBrightness(var2) * */
        var16 = (1F + ModLoader.getMinecraftInstance().gameSettings.gammaSetting) - (this.worldObj.calculateSkylightSubtracted(var2) * 0.13F);
        //var16 = 0f;
        EntityPlayer pl = ExtendedRenderer.player;
        //System.out.println(this.getBrightness(var2));
        //System.out.println(this.worldObj.calculateSkylightSubtracted(var2) * 0.12F);
        var1.setColorOpaque_F(this.particleRed * var16, this.particleGreen * var16, this.particleBlue * var16);
        var1.addVertexWithUV((double)(var13 - var3 * var12 - var6 * var12), (double)(var14 - var4 * var12), (double)(var15 - var5 * var12 - var7 * var12), (double)var9, (double)var11);
        var1.addVertexWithUV((double)(var13 - var3 * var12 + var6 * var12), (double)(var14 + var4 * var12), (double)(var15 - var5 * var12 + var7 * var12), (double)var9, (double)var10);
        var1.addVertexWithUV((double)(var13 + var3 * var12 + var6 * var12), (double)(var14 + var4 * var12), (double)(var15 + var5 * var12 + var7 * var12), (double)var8, (double)var10);
        var1.addVertexWithUV((double)(var13 + var3 * var12 - var6 * var12), (double)(var14 - var4 * var12), (double)(var15 + var5 * var12 - var7 * var12), (double)var8, (double)var11);
        /*}
        }
        }*/
    }

    public void onUpdate2()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
        }

        //this.particleTextureIndex = 7 - this.particleAge * 8 / this.particleMaxAge;
        //this.particleTextureIndex = 7 - this.particleAge * 8 / this.particleMaxAge;
        this.setParticleTextureIndex(textureID);//mod_EntMover.effWindID;
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

    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
        }

        if (this.motionX > 0.01F && this.motionZ > 0.01F)
        {
            this.rotationPitch += this.rand.nextInt(6) - 3;
        }

        //this.particleTextureIndex = 7 - this.particleAge * 8 / this.particleMaxAge;
        //this.particleTextureIndex = 7 - this.particleAge * 8 / this.particleMaxAge;
        setParticleTextureIndex(textureID);//mod_EntMover.effWindID;
        //this.motionY += 0.0040D;
        this.motionY -= (0.04D * this.rand.nextFloat()) * (double)this.particleGravity;
        //this.motionY -= 0.05000000074505806D;
        float var20 = 1F - (0.08F * this.rand.nextFloat());
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
        return 2;
    }
}
