package weather;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import CoroAI.PFQueue;
import CoroAI.c_CoroAIUtil;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.DimensionManager;

public class CommandWeather extends CommandBase {

	@Override
	public String getCommandName() {
		return "wm";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		
		try {
			if (var2.length < 2)
	        {
				//exception throws dont seem to always get sent to player, do it manually
				var1.sendChatToPlayer("Invalid usage, example: '/wm spawn tornado");
	            throw new WrongUsageException("Invalid usage");
	        }
	        else
	        {
	        	
	        	if (var2[0].equalsIgnoreCase("spawn")) {
	        		if (var2[1].equalsIgnoreCase("tornado")) {
	        			int type = 1;
	        			if (var2.length >= 3) {
	        				type = Integer.valueOf(var2[2]);
	        			}
	        			if (type > 5) {
	        				var1.sendChatToPlayer("use type 5 or less");
	        			}
	        			EntityPlayer entP = (EntityPlayer)var1;
	        			WeatherMod.tryTornadoSpawn(entP.dimension, true, entP.username, type);
	        			var1.sendChatToPlayer("Spawning tornado type: " + type);
	        		}
	        	}
	        	
	        }
		} catch (Exception ex) {
			System.out.println("Caught WeatherMod command crash!!!");
			ex.printStackTrace();
		}
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender)
    {
        return par1ICommandSender.canCommandSenderUseCommand(this.getRequiredPermissionLevel(), this.getCommandName());
    }

}
