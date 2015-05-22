package src;

import tank.*;

/**
 * <p><b>MULTI-AGENT SYSTEM</b> : An Agent is a placeholder class that retains a Tanker's information
 * in the Open System. Each Agent holds the tanker's water level, fuel level, and position. </p>
 * <p>An Agent also holds information such as Role, tasks which must be completed by the Tanker that is holding
 * this agent.</p>
 * 
 * <p>An agent also acts as an authentication object that checks if the tanker belongs to which System.</p>
 * <p>This allows the Multi-agent system to be expanded to include more Systems than the current single
 * Open System implementation. </p>
 * 
 * <p>For more information, see {@link OpenSystem} and {@link Role}</p>
 * @author awg04u
 *
 */
public final class Agent{
	Role role;
	posXY position;
	int waterlevel;
	int fuellevel;
	int final_dir = 8;
	Agent(posXY pos, int waterlvl, int fuellvl, Role rol){
		this.role = rol;
		position = pos;
		waterlevel = waterlvl;
		fuellevel = fuellvl;
	}
	
	public void setWaterlevel(int waterlvl){	waterlevel = waterlvl;	}
	public int getWaterlevel(){	return waterlevel;	}
	
	public int getFuellevel(){	return fuellevel;}
	public void setFuellevel(int fuellvl){	fuellevel = fuellvl;	}

	public void setPosition(posXY pos){	position = pos;}
	public posXY getPosition(){	return position;}
	public Role getRole(){	return role;}
	
	public void setRole(Role role){	
		
		this.role = role;
		if(this.role instanceof Harvest){
			if(this.role.getTasks().isEmpty()){
				if(test.DebugMode)	System.out.println("New Harvest has no task!");
				return;
			}
		}else{
			if(test.DebugMode)	System.out.println("Role not instance of Harvest");
		}
	}
	
	/**
	 * Updates the agent's holding information
	 * @param pos	Update the agent's perceived position
	 * @param waterlvl	Update the agent's perceived water level
	 * @param fuellvl	Update the agent's perceived fuel level
	 * @param dir		Update the agent's perceived direction
	 */
	public void updateAgent(posXY pos, int waterlvl, int fuellvl, int dir){
		
		if(this.getRole() != null)	this.getRole().updateTasks();
		setPosition(pos);
		setWaterlevel(waterlvl);
		setFuellevel(fuellvl);
		final_dir = dir;
	}
	
}