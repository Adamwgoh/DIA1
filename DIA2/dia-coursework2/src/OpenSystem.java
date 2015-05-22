package src;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import tank.*;

/**
 * <p><b>MULTI-AGENT SYSTEM</b> : An open system where tankers are able to work towards a common 
 * goal by sharing resources and exchanging knowledge, etc.</p>
 * <p>Tanker is freely to enter or leave the open system as agents.</p>
 * <p>To maintain the integrity of the open systems, tankers are only able join the system by accepting an open role.
 *  Only agents in the systems are allowed to request for open roles and shared resources such as wells.</p>
 * 
 * <p>However, agents that left the open system still holds information about the tasks and
 * wells that it holds.
 * This means the open system can be exploited by joining and leaving the System.</p>
 * <p>To prevent this, any agent that joins the system must adhere to its <i>Organisational Rules</i></p>
 * 
 * <p>The organisation structure is distributive, that is, anyone are allowed to receive any roles that are available,
 * However, relationship between roles can be hierachical, such as in FORAGE Role. </p>
 * 
 * <p>By joining the OpenSystem, the agent is complied to fulfill a certain role. That means agent must obey the tasks given
 * for the role, not acting based on self interest.</p>
 * <p>For more information, see {@link Role}, {@link Agent}, {@link ControlSystem#OpenSysCheck}</p>
 * 
 * 
 * 
 * @author awg04u
 *
 */
public class OpenSystem {
	 public final static int
	    NORTH       =   0, //(0, +1)
	    SOUTH       =   1, //(0, -1)
	    EAST        =   2, //(+1,  0)
	    WEST        =   3, //(-1, +1)
	    NORTHEAST   =   4, //(+1, +1)
	    NORTHWEST   =   5, //(-1, +1)
	    SOUTHEAST   =   6, //(-1, +1)
	    SOUTHWEST   =   7; //(-1, -1)
	 	private ArrayList<Agent> agent_list = new ArrayList<Agent>();
	 	private ArrayList<dWell> shared_wells = new ArrayList<dWell>();
	 	private ArrayList<Role>	open_roles = new ArrayList<Role>();
		InputStreamReader in = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(in);
		
		/**
		 * Print open roles in the system
		 */
	 	public void printRoles(){
	 		System.out.println("Roles in Open System");
	 		for(Role role : open_roles){
	 			System.out.printf("%s", role.toString());
	 		}
	 		System.out.println(" ");
	 	}
	 	
	 	/**
	 	 * Called when the system is started. An open role will be added immediately
	 	 */
	 	public void initOS(){
	 		Forage head = new Forage();
	 		open_roles.add(head);
	 	}
	 	
	 	/**
	 	 * Check if there are any HARVEST Role in the Open System
	 	 * @return	true if there are any HARVEST Role
	 	 */
	 	public boolean hasOpenHarvestRole(){
	 		for(Role role : open_roles){
	 			if(role instanceof Harvest){
	 				return true;
	 			}
	 		}
	 		
			return false;
	 	}
	 	
	 	/**
	 	 * Get the first HARVEST Role in the Open System.
	 	 * @return	A HARVEST Object 
	 	 */
	 	public Role getHarvestRole(){
	 			for(Role role : open_roles){
	 				if(role instanceof Harvest){
	 					return role;
	 				}
	 			}
	 			return null;
	 	}
	 	
	 	/**
	 	 * Changes Role of the agent. Agent's role in the Open System is updated too
	 	 * @param agent	agent's role to be changed
	 	 * @param role	target role to change to
	 	 */
	 	public void changeRole(Agent agent, Role role){
	 		//look for the agent in the agent list and changes it to that role
	 		for(Agent ag : agent_list){
	 			if(ag.equals(agent)){
	 				ag.setRole(role);
	 			}
	 		}
	 		
	 		//checks if role is from open_roles, if yes, remove it from there
	 		for(int i = 0; i < open_roles.size(); i++){
	 			if(open_roles.get(i).equals(role)){
	 				if(test.DebugMode)	System.out.println("Removed role from open_role. No repeats now");
	 				open_roles.remove(i);
	 			}
	 		}
	 		
	 	}
	 	
	 	/**
	 	 * Check if there are any equivalent well in the Open System. Utlity function
	 	 * @param well	target well
	 	 * @return	True if there is an equivalent well in the Open System.
	 	 */
	 	private boolean hasSharedWell(dWell well){
	 		for(dWell w : shared_wells){
	 			if(well.equals(w)){
	 				return false;
	 			}
	 		}
	 		
	 		return true;
	 	}
	 	
	 	/**
	 	 * <p>Tanker joining the system. When joined, the tanker will be assigned with an Agent Object.</p>
	 	 * <p>The Agent will contain a Role that the Tanker must complete. However, if there are no available1 role, the tanker
	 	 * cannot join the system. Current implementation ensures there is always a Role in the Open System, may be change otherwise</p>
	 	 * @param tanker	Tanker requesting to join System
	 	 * @return	Agent object with an assigned role
	 	 */
	 	public Agent joinSystem(SuperTanker tanker){
	 		//reject entry to open system as there are no roles.
	 		if(open_roles.isEmpty()){
	 			System.out.println("no roles, Failed to join OS.");
	 			return null;
	 		}
	 		
	 		//share your wells!
	 		for(dWell well : tanker.ControlSys.getWells()){
	 			if(!hasSharedWell(well)){

		 			shared_wells.add(well);
	 			}
	 		}
	 		
	 		//get job of Harvest first, then Forage
	 		Role job = null;

	 		if(job == null){
	 			for(int i = 0; i < open_roles.size(); i++){
	 				//HARVEST ROLE is assigned with highest priority
		 			if(open_roles.get(i) instanceof Harvest){
		 				
		 				if(test.DebugMode)	System.out.println("picked up harvest");
		 				job = open_roles.get(i);
		 				open_roles.remove(i);
		 				break;
		 			}

		 			if(open_roles.get(i) instanceof Forage){
		 				job = open_roles.get(i);
		 				open_roles.remove(i);
		 				if(open_roles.isEmpty()){
		 					Forage work = new Forage(job);
		 					open_roles.add(work);
		 				}
		 			}
	 			}//end for
	 		}
	 		
	 		System.out.printf("Joined system. Current role is %s\n", job);
	 		Agent agent = new Agent(tanker.getTankerPosition(), tanker.getWaterLevel(), tanker.getFuelLevel(), job);
	 		agent_list.add(agent);
	 		
	 		//this agent represents an ID that the tanker has to identify with the OpenSystem
	 		return agent;
	 	}
	 	
	 	/**
	 	 * <p>Roles are updated to ensure there are no roles in the Open System with Completed tasks or empty Roles.</p>
	 	 * <p>Empty roles are deleted from the OpenSystem.</p>
	 	 * 
	 	 * <p>For more information, see {@link Role}</p>
	 	 */
	 	public void updateRoles(){
	 		for(int i = 0; i < open_roles.size(); i++){
	 			//check for each role if any tasks are completed. If yes, remove it.
	 			for(int j = 0; j < open_roles.get(i).getTasks().size(); j++){
	 				if(open_roles.get(i).getTasks().get(j).isTaskComplete()){
	 					
	 					open_roles.get(i).getTasks().remove(j);
	 				}
	 			}
	 			
	 			//after removing the completed tasks, if there are no more tasks left, consider removing it
	 			if(open_roles.get(i).getTasks().isEmpty() && !(open_roles.get(i) instanceof Forage)){
	 				if(test.DebugMode)	System.out.printf("Removed %s\n", open_roles.get(i).toString());
	 				open_roles.remove(i);
	 			}
	 			
	 		}
	 	}
	 		 	
	 	/**
	 	 * <p>Checks if the agent belongs in the OpenSystem. Matches the system's agent list for authentication.</p>
	 	 * This implementation allows Agents that can join more than one System.
	 	 * @param agent	Agent to be matched with
	 	 * @return	True if agent belongs to the System
	 	 */
	 	public boolean inSystem(Agent agent){	
	 		
	 		if(agent == null)	return false;
	 		return agent_list.contains(agent);
	 	}
	 	
	 	/**
	 	 * Agent is removed from the OpenSystem
	 	 * @param agent Agent that wants to leave
	 	 * @return	True if removed from agent_list. If false, agent does not belong in this system
	 	 */
	 	public boolean leaveSystem(Agent agent){
	 		if(agent == null)	return false;
	 		return agent_list.remove(agent);
	 	}
	 	
	 	/**
	 	 * <p>Request for help for a certain job. Harvest and Forage can be called with this function.</p>
	 	 * Current Implementation only supports one child Role and one leader Role
	 	 * @param agent	Agent requesting for help
	 	 * @param lead	If the Role has a leader, assign it to the child Role
	 	 * @param recruit	Child role. 
	 	 */
	 	public void requestHelp(Agent agent, Role lead, Role recruit){
	 		
	 		agent.setRole(lead);
	 		for(Agent agnt : agent_list){
	 			if(agnt.equals(agent)){
	 				agnt.setRole(lead);
	 			}
	 		}
	 		if(recruit != null){
		 		for(Role role : open_roles){
		 			if(role.equals(recruit)){
		 				//already exist. returning
		 				return;
		 			}
		 		}
		 		if(test.DebugMode)	System.out.println("added role recruit in open role");
		 		open_roles.add(recruit);
	 		}
	 	}
	 	
	 	/**
	 	 * <p>Request for help for a certain job. Harvest and Forage can be called with this function.</p>
	 	 * Current Implementation only supports one child Role.
	 	 * @param agent	Agent requesting for help
	 	 * @param solo	Role without any leader
	 	 */
	 	public void requestHelp(Agent agent, Role solo){
	 		
	 		if(solo != null){
		 		for(Role role : open_roles){
		 			if(role.equals(solo)){
		 				//already exist. returning
		 				return;
		 			}
		 		}
		 		
	 			open_roles.add(solo);
	 		}
	 	}
	 	
	 	/**
	 	 * Book-keeping function. Updates agent in the Open System with the given tanker's information
	 	 * @param agent	Agent to be updated
	 	 * @param tanker	information taken from this tanker will be used to update Agent
	 	 */
	 	public void updateAgent(Agent agent, SuperTanker tanker){
	 		
	 		agent_list.get(agent_list.indexOf(agent)).updateAgent(tanker.getTankerPosition(), tanker.getWaterLevel(), 
	 				tanker.getFuelLevel(), tanker.getTankerDirection());;
	 	}
	 	
	 	/**
	 	 * <p>Returns the Open System's list of wells. Wells can be openly shared to provide maximum options when completing Roles.</p>
	 	 * @param agent Agent that requests for well
	 	 * @return	A list of dWell
	 	 */
	 	public ArrayList<dWell> getSharedWell(Agent agent){
	 		if(agent_list.contains(agent)){
	 			if(shared_wells == null)	System.err.println("shared wells is null!");
	 			return shared_wells;
	 		}
	 		
	 		if(test.DebugMode)	System.err.println("not allowed to access open wells, agent is not found!");
	 		return null;
	 	}
	 	
	 	/**
	 	 * Returns How many available HARVEST roles are there in the Open System.
	 	 * @return	number of available HARVEST Role.
	 	 * 
	 	 * For more information, See {@link Harvest}
	 	 */
	 	public int getOpenHarvests(){
	 		//return number of open Harvest roles
	 		int count = 0;
	 		for(Role role : open_roles){
	 			if(role instanceof Harvest){
	 				count++;
	 			}
	 		}
	 		return count;
	 	}
	 	
	 	/**
	 	 * Returns How many available FORAGE£ roles are there in the Open System.
	 	 * @return	number of available FORAGE Role.
	 	 * 
	 	 * For more information, See {@link Forage}
	 	 */
		public int getForageOpen(){
	 		int count = 0;
	 		for(Role role : open_roles){
	 			if(role instanceof Forage){
	 				count++;
	 			}
	 		}
	 		return count;
		}
}
