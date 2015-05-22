package tank;

import java.util.ArrayList;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import src.test;
import uk.ac.nott.cs.g54dia.multilibrary.*;

/**
 * A Plan class is a wrapper than keeps a list of goals, perceived fuel, water level, and distance to fuel pump.
 * The generated plan is sorted with a greedy search algorithm, meaning each action is chosen with the least total distance.
 * You are able to print out the plan, compare plans, generate a new plan with its constructor, as well as calculate a tasks required total distance
 * @author awg04u
 *
 */
public class Plan {

	protected SuperTanker tanker;
	protected int EstimatedtotalDist = 0;
	protected int curr_fuel, curr_pumpdist, curr_waterlvl = 0;
	protected ArrayList<dTask> tasks;
	protected LinkedList<dTask> task_pipeline;
	protected boolean DebugMode = false;
	protected InputStreamReader in = new InputStreamReader(System.in);
	protected BufferedReader br = new BufferedReader(in);
	
	public Plan(){
		//empty constructor
	}
	
	public Plan(ArrayList<dTask> ts, int fuel, int pump_dist, int waterlevel, SuperTanker tanker){
		//feed in the number of tasks, able to generate a plan. If need well to calculate,
		//it gets automatically from controlsystem
		this.tanker = tanker;
		tasks = ts;
		curr_fuel = fuel;
		curr_pumpdist = pump_dist;
		curr_waterlvl = waterlevel;
		task_pipeline = makePlan(tasks);
	}
	
	//generated plan is in sequence, with the first as first goal to be backchained
	/**
	 * Makes a plan with a given ArrayList of Tasks. uses calcATask to calculate each tasks' total distance required
	 * @param tasks	An ArrayList of tasks. This should be received from the control system and not raw from Model Layer
	 * @return	returns a LinkedList of Tasks
	 */
	public LinkedList<dTask> makePlan(ArrayList<dTask> tasks){
		LinkedList<dTask> rawtasks = new LinkedList<dTask>();
		for(dTask t : tasks){
			
			rawtasks.add(t);
		}
		
		LinkedList<dTask> plan = new LinkedList<dTask>();
		int t_fuel = curr_fuel;
		int t_pumpdist = curr_pumpdist;
		int t_waterlvl = curr_waterlvl;
		int total_dist = 0;
		int temp_dist = 10000000;
		dTask shortTask = null;
		
		if(DebugMode) System.out.printf("list of rawtasks %d\n", rawtasks.size());
		int initial_size = rawtasks.size();
		
		//this part calculates the total distance for completing a task and tries to find for the shortest distance task
		//The found task will be added into the plan and excluded in the next round
		for(int j = 0; j < initial_size; j++){
			for(int i = 0; i < rawtasks.size(); i++){
				
				if(temp_dist > calcATask(rawtasks.get(i), t_waterlvl, t_fuel, t_pumpdist, 0) ){
					temp_dist = calcATask(rawtasks.get(i), t_waterlvl, t_fuel, t_pumpdist, 0);
	
					shortTask = rawtasks.get(i);
				}
			}
			
			//found shortest task. update values
			int fuel = t_fuel;
			int waterlvl = t_waterlvl;
			int pumpdist = t_pumpdist;
			t_fuel = calcATask(shortTask, waterlvl, fuel, pumpdist, 1);
			t_pumpdist = calcATask(shortTask, waterlvl, fuel, pumpdist, 2);
			t_waterlvl = calcATask(shortTask, waterlvl, fuel, pumpdist, 3);
			total_dist += calcATask(shortTask, waterlvl, fuel, pumpdist, 0);
			temp_dist = 100000000;//reset for next round
			plan.addLast(shortTask);
			
			if(rawtasks.remove(shortTask))	if(DebugMode) System.out.println("removed choice");
			if(DebugMode) System.out.printf("added %s \n",  shortTask.toString());
		}
		EstimatedtotalDist = total_dist;
		
		return plan;
	}
	
	/**
	 * Gets the plan's total score with the given formula
	 * @return	the plan's estimated total score
	 */
	public int getPlanScore(){
		LinkedList<dTask> tasks = task_pipeline;
		int score = 0;
		int water_delivered = 0;
		
		for(dTask t : tasks){
			water_delivered += t.getWaterReq();
		}
		score = water_delivered*tasks.size();
		
		return score;
	}
	
	/**
	 * Equivalent method to compare if the goal is the same by first checking its size, then checking if each any task is not contained in the other task
	 * @param plan	A plan to be compared with this object
	 * @return	boolean value. If true, the plan is equivalent. False if inequivalent
	 */
	public boolean isSame(Plan plan){
		
		if(this.getPlan().size() == plan.getPlan().size()){
			for(dTask t : this.getPlan()){
				if(!plan.getPlan().contains(t)){
					
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Calculates total dist needed to backchain this task
	 * @param task	task to calculate
	 * 	0 : travel_dist
	 *  1 : belief_fuel
	 *  2 : belief_pumpdist
	 *  3 : belief_waterlvl
	 *  default : travel_dist
	 */
	/**
	 * <p>Calculates total distance needed to back-chain the given task</p>
	 * <p>returnVal reference</p>
	 * <p> 0 : travel_dist</p>
	 * <p> 1 : belief_fuel</p>
	 * <p> 2 : belief_pumpdist</p>
	 * <p> 3 : belief_waterlvl</p>
	 * <p>default : travel_dist</p>
	 * @param task	Task to be backchained
	 * @param curr_waterlvl	current water level
	 * @param curr_fuel		current fuel level
	 * @param curr_pumpdist	current distance to pump
	 * @param returnVal	switch to choose what value to return. For reference of returnVal, see above
	 * @return total distance to complete the task
	 */
	public int calcATask(dTask task, int curr_waterlvl, int curr_fuel, int curr_pumpdist, int returnVal){
		int travel_dist = 0;
		int temp_fuel;
		int temp_pumpdist;
		int temp_waterlvl;
		
		/**
		 * The following checks are to calculate the total distance to complete a task.
		 * The distance measured includes trip to water and fuel for refuel
		 */
		//Not enough water to Execute Task
		if(curr_waterlvl < task.task.getRequired()){
			if((curr_fuel-1-curr_pumpdist) <= (this.tanker.ControlSys.getNearestWell().getRelTankDist()) ){
				//go to pump to refuel before going to well
				travel_dist += (curr_pumpdist + this.tanker.ControlSys.getNearestWell().getRelPumpDist() );
				temp_fuel = 100 - this.tanker.ControlSys.getNearestWell().getRelPumpDist()-1;
				temp_pumpdist = this.tanker.ControlSys.getNearestWell().getRelPumpDist();
				temp_waterlvl = Tanker.MAX_WATER;
			}else{
				
				//Go to well directly
				temp_fuel = curr_fuel - this.tanker.ControlSys.getNearestWell().getRelTankDist()-1;
				travel_dist += this.tanker.ControlSys.getNearestWell().getRelTankDist();
				temp_pumpdist = this.tanker.ControlSys.getNearestWell().getRelPumpDist();
			}
			
			//Enough Water but not enough fuel to task
			if((temp_fuel - curr_pumpdist) < (task.getRelTankDist()) ){
				
				travel_dist += temp_pumpdist + task.getRelPumpDist();
				temp_fuel = 100 - task.getRelPumpDist()-1;
				temp_pumpdist = task.getRelPumpDist();
				temp_waterlvl = task.getWaterReq();
			}else{
				//Enough fuel and enough water. Goes to task.
				travel_dist += task.getRelTankDist();
				temp_fuel -= task.getRelTankDist()-1;
				temp_pumpdist = task.getRelPumpDist();
				temp_waterlvl = task.getWaterReq();
			}
			
		}else{
			//Have water but not enough fuel to task
			if((curr_fuel - curr_pumpdist) < (task.getRelTankDist()) ){
				travel_dist += curr_pumpdist + task.getRelPumpDist();
				temp_fuel = 100 - task.getRelPumpDist()-1;
				temp_pumpdist = task.getRelPumpDist();
				temp_waterlvl = task.getWaterReq();
			}else{
				//Enough fuel and enough water. Goes to task.
				travel_dist += task.getRelTankDist();
				temp_fuel = curr_fuel - task.getRelTankDist()-1;
				temp_pumpdist = task.getRelPumpDist();
				temp_waterlvl = curr_waterlvl - task.getWaterReq();
			}
		}
		
		switch(returnVal){
			//switch case helps to return the wanted estimated value
			case 0 :	return travel_dist;
			case 1 :	return temp_fuel;
			case 2 :	return temp_pumpdist;
			case 3 :	return temp_waterlvl;
			default : return travel_dist;
		}
	}

	/**
	 * Prints the tasks stored in the plan
	 * @return	String of tasks' position stored in the plan in order
	 */
	public String printPipeLine(){
		String result = "";
		
		for(int k = 0; k < task_pipeline.size(); k++){
			
			result = result.concat(task_pipeline.get(k).toString());
			
		}
		result = result.concat("\n");
		return result;
	}
	
	public boolean hasPlan(){
		if(task_pipeline.isEmpty())	return false;
		
		return true;
	}
	

	/**
	 * Remove goal removes a task from a goal when it is finished. then, the current plan is changed to a plan without the goal
	 * Only reason you should be removing goal is to update plan, hence the need to update the three variables
	 * @param goal	The Task to be removed
	 * @param fuel	Current fuel level
	 * @param pump_dist	current distance to fuel pump
	 * @param waterlevel	current water level
	 */
	public void RemoveGoal(dTask goal, int fuel, int pump_dist, int waterlevel){
		
		//look for goal
		if(task_pipeline.getLast().task.equals(goal.task)){
			
			if(DebugMode) System.out.println("removing goal from plan");
			
			task_pipeline.removeLast();
			ArrayList<dTask> temp = new ArrayList<dTask>();
			for(dTask t : task_pipeline){
				temp.add(t);
			}
			
			task_pipeline = makePlan(temp);
			
			if(DebugMode) System.out.printf("in remove goal : %s\n", printPipeLine());
			curr_fuel = fuel;
			curr_pumpdist = pump_dist;
			curr_waterlvl = waterlevel;
		}
	}
	
	
	public LinkedList<dTask> getPlan(){
		if(task_pipeline != null){
			if(!task_pipeline.isEmpty()){
				
				return task_pipeline;
			}
		}
		
		if(DebugMode) System.out.println("no plan generated!");
		return null;
	}
	
	/**
	 * Returns true if alternative has a shorter estimateDist
	 * @param alternative plan to be compared with
	 * @return	returns True if alternative distance is shorter
	 */
	public boolean CompareDist(Plan alternative){
		if(alternative == null)		return false;
		if(alternative.getTotalDist() < EstimatedtotalDist ){
			return true;
		}
		
		return false;
	}
	
	public int getTotalDist(){	return EstimatedtotalDist;	}
}

