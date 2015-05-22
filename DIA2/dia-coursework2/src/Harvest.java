package src;


import java.util.ArrayList;

import tank.*;

/**
 * <p><b> MULTI-AGENT SYSTEM</b>: Harvest is a extended Role that holds simple information about tasks to be completed
 * and the leader. Leader is only for reference as to who requested for the Harvest Role.</p>
 * <p> Although the relationship to a leader Harest Role is hierachical, there are no strict control by the leader Role yet.</p>
 * 
 * 
 * @author awg04u
 *
 */
public class Harvest extends Role {
	
	public Harvest(SuperTanker tanker, Role leader, dTask task){
		this.tanker = tanker;
		this.leader = leader;
		tasks.add(task);
		name = "(Harvest)";
	}
	
	public Harvest(SuperTanker tanker, Role leader, ArrayList<dTask> tasks){
		this.tanker = tanker;
		this.leader = leader;
		
		for(dTask task : tasks){
			this.tasks.add(task);
		}
		name = "(Harvest)";
	}
	
	public Harvest(SuperTanker tanker, dTask task){
		this.tanker = tanker;
		tasks.add(task);
		name = "(Harvest)";
	}
	
	
	
	public Harvest(SuperTanker tanker, ArrayList<dTask> tasks){
		for(dTask task : tasks){
			this.tasks.add(task);
		}
		
		name = "(Harvest)";
	}
}
