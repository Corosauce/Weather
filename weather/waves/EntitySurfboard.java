package weather.waves;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.Iterator;
import java.util.List;

import net.minecraft.src.*;

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
