package src;

import java.util.ArrayList;

import tank.*;
/**
 * <p><b>MULTI-AGENT SYSTEM</b> : General Role class to be extended. Handles common and fundamental message passing
 * and knowledge exchange functions</p>
 * 
 * <p>A Role is a responsibility that any agent in the OpenSystem must execute and sees it to completion.</p>
 * <p>As of current implementation, a Role may be plain searching(FORAGE), or Completing certain tasks(HARVEST).</p>
 * 
 * <p>{@link Agent} that requests for help on the OpenSystem offers its tasks as a Role.</p>
 * <p>An Role available in the Open System can be picked up by any agent in the system.</p>
 * <p>This is an example of organisational rules, where agent in the system must obey(completing its role).</p>
 *
 * <p>Roles may not be abandoned halfway through, and agent must sees it to completion.</p>
 * @author awg04u
 *
 */
public class Role {
	protected String name = "";
	protected SuperTanker tanker;
	protected ArrayList<dTask> tasks = new ArrayList<dTask>();
	protected Role leader;
	protected boolean complete_role;//some roles cannot be abandoned halfway through, such as harvest

	
	public Role getLeader(){				return this.leader;		}
	public void setLeader(Role leader){		this.leader = leader; 	}
	public String toString(){				return name; 			}
	public ArrayList<dTask> getTasks(){		return tasks;			}
	
	/**
	 * Updates tasks that are completed in this Role
	 */
	public void updateTasks(){
		if(tasks == null)	return;
		for(int i = 0; i < tasks.size(); i++){
			if(tasks.get(i).isTaskComplete()){
				if(test.DebugMode)	System.out.printf("removing role task %s\n", tasks.get(i));
				tasks.remove(tasks.get(i));
			}
		}
	}
	
	public boolean isRoleCompleted(){
		return (tasks.isEmpty());
	}

}
