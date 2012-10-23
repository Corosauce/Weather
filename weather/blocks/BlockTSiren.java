package weather.blocks;

import java.util.Random;

import net.minecraft.src.BlockContainer;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class BlockTSiren extends BlockContainer
{
    public BlockTSiren(int var1, int var2)
    {
        super(var1, var2, Material.clay);
    }

    public int tickRate()
    {
        return 90;
    }

    public void updateTick(World var1, int var2, int var3, int var4, Random var5) {}

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        // TODO Auto-generated method stub
        return new TileEntityTSiren();
    }
}
