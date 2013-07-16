package weather.waves;

import net.minecraft.world.World;

public class EntitySurfboard extends EntityBuoyant
{
    public EntitySurfboard(World par1World)
    {
        super(par1World);
    }

    public EntitySurfboard(World par1World, double par2, double par4, double par6)
    {
        super(par1World, par2, par4, par6);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();
    }
}
