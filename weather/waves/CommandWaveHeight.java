package weather.waves;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import weather.ServerTickHandler;
import weather.storm.StormManager;

public class CommandWaveHeight extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "waveheight";
    }

    @Override
    public void processCommand(ICommandSender var1, String[] var2)
    {
        EntityPlayer player = getCommandSenderAsPlayer(var1);
        if (player instanceof EntityPlayerMP)
        {
        	for (int i = 0; i < ServerTickHandler.sMans.size(); i++) {
	        	StormManager sMan = ServerTickHandler.sMans.get(i);
	        	sMan.baseWaveHeight = Float.valueOf(var2[0]);
        	}
        }
        else
        {
            System.out.println("Not EntityPlayerMP");
        }
    }
}
