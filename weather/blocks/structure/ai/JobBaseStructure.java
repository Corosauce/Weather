package weather.blocks.structure.ai;

import java.util.List;

import weather.blocks.structure.Structure;

import CoroAI.*;
import CoroAI.entity.*;

import net.minecraft.src.*;

public class JobBaseStructure {
	
	public JobManager jm = null;
	public Structure ent = null;
	
	public EnumJobState state;
	
	//Shared job vars
	public int hitAndRunDelay = 0;
	public int tradeTimeout = 0;
	public int walkingTimeout;
	
	public int fleeDelay = 0;
	
	public JobBaseStructure() {
		
	}
	
	public JobBaseStructure(JobManager jm) {
		this.jm = jm;
		//this.ent = jm.ent;
		setJobState(EnumJobState.IDLE);
	}
	
	public void setJobState(EnumJobState ekos) {
		state = ekos;
		//System.out.println("jobState: " + occupationState);
	}

	public void tick() {
		if (hitAndRunDelay > 0) hitAndRunDelay--;
		if (tradeTimeout > 0) tradeTimeout--;
	}
	
	public boolean shouldExecute() {
		return true;
	}
	
	public boolean shouldContinue() {
		return true;
	}
	
	public void onLowHealth() {
		
	}
	
	public void onIdleTick() {
		
		
	}
	
	public void onJobRemove() {
		//Job cleanup stuff - 
		//this.ent.setCurrentSlot(0);
	}
	
	public void setJobItems() {
		
	}
	
	// Blank functions \\
		
	public boolean sanityCheck(Entity target) {
		return false;
	}
	
	public boolean sanityCheckHelp(Entity caller, Entity target) {
		return false;
	}
	
	public void koaTrade(EntityPlayer ep) {
		
	}
	
	public void hitHook(DamageSource ds, int damage) {
		
	}
	
	// Blank functions //
	
	// Job shared functions \\
	
	public boolean checkHunger() {
		
		return false;
	}
	
	// Job shared functions //
	
}
