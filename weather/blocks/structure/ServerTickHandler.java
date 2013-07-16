package weather.blocks.structure;

import net.minecraft.world.World;

import java.util.EnumSet;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;

public class ServerTickHandler implements ITickHandler {

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        if (type.equals(EnumSet.of(TickType.SERVER)))
        {
            onTickInGame();
        }
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.SERVER);
    }

    @Override
    public String getLabel()
    {
        return null;
    }
    
    public static boolean addCommands = true;

    public void onTickInGame()
    {
        if (FMLCommonHandler.instance() == null || FMLCommonHandler.instance().getMinecraftServerInstance() == null)
        {
            return;
        }
        
        if (addCommands) {
        	//((ServerCommandManager)FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager()).registerCommand(new CommandWaveHeight());
        	addCommands = false;
        }

        World world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(0);

        if (world != null)
        {
            AIBlock.tick(Side.SERVER, world);
        }

        world = null;//FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(127);

        if (world != null)
        {
        	AIBlock.tick(Side.SERVER, world);
        }

        //System.out.println("onTickInGame");
        //TODO: Your Code Here
    }
}
