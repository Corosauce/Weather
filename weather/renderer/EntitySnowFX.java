package weather.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import extendedrenderer.particle.entity.EntityRotFX;

@SideOnly(Side.CLIENT)
public class EntitySnowFX extends EntityRotFX
{
    float smokeParticleScale;

    public EntitySnowFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12)
    {
        this(par1World, par2, par4, par6, par8, par10, par12, 1.0F);
    }

    public EntitySnowFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12, float par14)
    {
        super(par1World, par2, par4, par6, 0.0D, 0.0D, 0.0D);
        this.motionX *= 0.10000000149011612D;
        this.motionY *= 0.10000000149011612D;
        this.motionZ *= 0.10000000149011612D;
        this.motionX += par8;
        this.motionY += par10;
        this.motionZ += par12;
        this.particleRed = this.particleGreen = this.particleBlue = 255;//(float)(Math.random() * 0.30000001192092896D);
        this.particleScale *= 0.75F;
        this.particleScale *= par14;
        this.smokeParticleScale = this.particleScale;
        this.particleMaxAge = (int)(150.0D / (Math.random() * 0.8D + 0.2D));
        this.particleMaxAge = (int)((float)this.particleMaxAge * par14);
        this.noClip = false;
    }

    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        float var8 = ((float)this.particleAge + par2) / (float)this.particleMaxAge * 32.0F;

        if (var8 < 0.0F)
        {
            var8 = 0.0F;
        }

        if (var8 > 1.0F)
        {
            var8 = 1.0F;
        }

        this.particleScale = this.smokeParticleScale/* * var8*/;
        super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
        }

        this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
        this.motionY -= 0.004D;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        if (this.posY == this.prevPosY)
        {
            this.motionX *= 1.1D;
            this.motionZ *= 1.1D;
        }

        this.motionX *= 0.9599999785423279D;
        this.motionY *= 0.9599999785423279D;
        this.motionZ *= 0.9599999785423279D;

        int id = worldObj.getBlockId((int)posX, (int)posY, (int)posZ);
        Block block = Block.blocksList[id];
        
        
        //STUPID, WHY ARE YOU SETTING BLOCK IDS IN A PARTICLE FILE
        //make some server side entities that are snowfall, make less of them, dont sync them, use them to determine where snow should get placed
        if (block != null && block.blockMaterial != Material.plants && block.blockMaterial != Material.water && rand.nextInt(1) == 0)
        {
        	int meta = worldObj.getBlockMetadata((int)posX, (int)posY, (int)posZ);
        	if (id == Block.snow.blockID && meta < 7) {
        		worldObj.setBlockMetadataWithNotify((int)posX, (int)posY, (int)posZ, meta+1, 2);
        	} else {
        		if (id != Block.snow.blockID) {
        			if (worldObj.getBlockId((int)posX, (int)posY+1, (int)posZ) == 0) {
        				worldObj.setBlock((int)posX, (int)posY+1, (int)posZ, Block.snow.blockID, 0, 2);
        			}
        		}
        	}
            this.setDead();
        }
        
        if (block != null && (block.blockMaterial == Material.plants || block.blockMaterial != Material.water)) {
        	this.setDead();
        }
        
    }
}
