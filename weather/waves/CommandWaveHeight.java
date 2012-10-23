package weather.waves;

import weather.ServerTickHandler;
import weather.storm.StormManager;
import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;

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
