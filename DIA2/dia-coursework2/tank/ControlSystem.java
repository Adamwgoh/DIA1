package tank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import src.Forage;
import src.test;
import uk.ac.nott.cs.g54dia.multilibrary.*;
import src.*;

public class ControlSystem {
	InputStreamReader in = new InputStreamReader(System.in);
	BufferedReader br = new BufferedReader(in);
	 public final static int
	    NORTH       =   0, //(0, +1)
	    SOUTH       =   1, //(0, -1)
	    EAST        =   2, //(+1,  0)
	    WEST        =   3, //(-1, +1)
	    NORTHEAST   =   4, //(+1, +1)
	    NORTHWEST   =   5, //(-1, +1)
	    SOUTHEAST   =   6, //(-1, +1)
	    SOUTHWEST   =   7; //(-1, -1)
	private  ReactiveLayer RLayer;
	private  PlanningLayer PLayer;
	private database Model;//only one having access to it.
	private SuperTanker tanker;
	private Agent agent;
	private Action final_action = null;
	private Action final_Paction = null;
	private Action final_Raction = null;
	private int final_dir = 8;

	//Boolean variables that control if Planing sees certain information. This is call censoring.
	protected boolean PlanningSeesWell = true;
	protected boolean PlanningSeesBTask = true;
	protected boolean PlanningSeesNTask = true;
	
	//constructor
	public ControlSystem(SuperTanker tanker){
		this.tanker = tanker;
		RLayer = new ReactiveLayer(tanker);
		PLayer = new PlanningLayer(tanker);
		Model  = new database(tanker);
	}

	public boolean haveWater(int amount){				return this.tanker.haveWater(amount); 	}
	public boolean haveFuel(int amount){				return this.tanker.haveFuel(amount);	}
	public boolean removeTanker(SuperTanker tanker){	return Model.removeTanker(tanker);		}
	public int getDirection(){							return final_dir;				}
	public FuelPump getFuelPump(){						return Model.getFuelPump();		}
	public boolean AtPump(){							return this.tanker.AtPump();	}
	public boolean At(posXY pos){						return this.tanker.At(pos);		}
	public boolean addTanker(SuperTanker tanker){		return Model.addTanker(tanker);	}
	public ArrayList<SuperTanker> getTankers(){			return Model.getTankers();		}
	public void printTasks(){							Model.printTasks();					}
	public void removeConflict(dTask task){				Model.removeConflict(task);			}
	public void addWell(Cell well,int x, int y, SuperTanker tanker){	Model.addWell(well, x, y, tanker);	}
	public void addStation(Cell st,int x, int y, SuperTanker tanker){	Model.addStation(st, x, y, tanker);	}
	public void addTask(Task task,int x, int y, SuperTanker tanker){	Model.addTask(task, x, y, tanker);	}

	public void addFuelPump(Cell pump){
		if(pump instanceof FuelPump) 	Model.addFuelPump((FuelPump) pump);
	}
	
	/**
	 * Enquire tanker if any tasks are in conflict with the tanker's belief state
	 * @param other_tasks	the list of tasks that is checked against for conflicts
	 * @return	A list of conflicting tasks
	 */
	public ArrayList<dTask> checkConflictTasks(ArrayList<dTask> other_tasks){
		ArrayList<dTask> conflicts = new ArrayList<dTask>();
		ArrayList<dTask> myTasks = Model.getTasks();
		if(other_tasks.isEmpty())	return null;
		if(myTasks.isEmpty())	return null;
		
		for(dTask task : other_tasks){
			for(dTask  mytask : myTasks){
				if(mytask.task.equals(task.task) ){
					conflicts.add(task);
				}
			}
		}
		
		return conflicts;
	}
	
	/**
	 * Remove conflicting task from the belief state
	 * @param tasks	the list of task that will be removed conflict
	 */
	public void removeConflicts(ArrayList<dTask> tasks){
		if(!tasks.isEmpty()){
			for(dTask task : tasks){
				if(haveTask(task)){
					removeConflict(task);
					if(test.DebugMode)	System.out.printf("removed task at %s\n", task.toString());
				}
			}
		}else{
			if(test.DebugMode)	System.out.println("tasks are empty");
		}
		
	}
	
	/**
	 * <p>MULTI-AGENT SYSTEM : Addresses conflicts between agents in a multi-agent environment</p>
	 * <p>This prevents any two tankers from having the same tasks in its belief state.</p>
	 * <p>This is possible as all tankers are connected by an interface call InteractProtocol.</p>
	 * 
	 * <p>For more information about it see {@link InteractProtocol}</p>
	 */
	public void RemoveConflictTask(){
		//this addresses conflicts between agents that did not join the open system.
		if(this.tanker.getFleetMates().size() != 0){
			ArrayList<SuperTanker> othertanks = this.tanker.getFleetMates();
			//check for each tanker for conflicts with current task list
			for(SuperTanker tankz : othertanks){
				
				if(test.DebugMode)	System.out.printf("checking for conflict with tank %d\n", tankz.getFleetNumber());
				if(!(tankz.getFleetNumber() == this.tanker.getFleetNumber()) ){
					
					ArrayList<dTask> conflicts = tankz.ControlSys.checkConflictTasks(Model.getTasks());
					if(conflicts == null){
						return;
					}
					if(!conflicts.isEmpty()){
						for(dTask task : conflicts){
							if(test.DebugMode)	System.out.printf("conflicting task : %s\n", task.toString());
						}
						
						removeConflicts(conflicts);
					}
				}
			}//endof tanker loop
		}
	}//endof RemoveConflictTask()
	
	/**
	 * <p>MULTI-AGENT SYSTEM. Prints out the tasks that the tanker was assigned to from a Role</p>
	 * <p>For more information, see {@link Role}</p>
	 */
	public void printAgentTasks(){
		if(this.agent == null)	return;
		if(this.agent.getRole() == null)	return;
		ArrayList<dTask> role_tasks = this.agent.getRole().getTasks();
		System.out.println("Agent Tasks :");
		if(role_tasks.isEmpty()){	System.out.println("(Empty)");	}
		for(dTask task : role_tasks){
			System.out.printf("%s, ", task.toString());
		}
		System.out.println(" ");
		
	}
	
	/**
	 * Check the Tanker is at Object X, either a FuelPump, Well, Station, or a Task
	 * @param X	Object
	 * @return	True if Tanker is at Object X
	 */
	public boolean At(Object X){
		if(X instanceof FuelPump){
			if(this.tanker.getTankerPosition().x == 0 && this.tanker.getTankerPosition().y == 0){
				return true;
			}
			
			return false;
		}else if(X instanceof dWell){
			dWell well = (dWell) X;
			if(well != null){
				posXY w = well.relPump_pos;
				int dx = w.x - this.tanker.getTankerPosition().x;
				int dy = w.y - this.tanker.getTankerPosition().y;
				
				if(Math.max(Math.abs(dx), Math.abs(dy)) == 0)	return true;
			}
			
			return false;
		}else if(X instanceof dStation){
			dStation station = (dStation) X;
			if(station != null){
				posXY st = station.relPump_pos;
				int dx = st.x - this.tanker.getTankerPosition().x;
				int dy = st.y - this.tanker.getTankerPosition().y;
				
				if(Math.max(Math.abs(dx), Math.abs(dy)) == 0)	return true;
			}
			
			return false;
		}else if(X instanceof dTask){
			dTask task = (dTask) X;
			if(task != null){
				posXY t = task.relPump_pos;
				int dx = t.x - this.tanker.getTankerPosition().x;
				int dy = t.y - this.tanker.getTankerPosition().y;
				
				if(Math.max(Math.abs(dx), Math.abs(dy)) == 0)	return true;
			}
			
			return false;
		}
			
		return false;
	}
	
	public ArrayList<dWell> getWells(){
		ArrayList<dWell> out_wells = new ArrayList<dWell>();
		for(dWell well : Model.getWells()){
			if(well.pump_dist < 49){
				out_wells.add(well);
			}
		}
		
		return out_wells;
	}
	
	public ArrayList<dTask> getTasks(){	

		//Example of how Joining OpenSystem changes tanker's belief state, hence reducing its self-interest
		//or control over what it wants to do
		if(agent != null && InteractProtocol.OpenSys.inSystem(agent)){

			if(!agent.getRole().getTasks().isEmpty()){
				if(test.DebugMode)	System.out.print("taking tasks from role. Tasks are :");
				for(int i = 0; i < agent.getRole().getTasks().size(); i++){
					if(test.DebugMode)	System.out.printf("%s",agent.getRole().getTasks().get(i).toString() );
				}
				if(test.DebugMode)	System.out.println("");
				
				return agent.getRole().getTasks();
			}
		}
		
		//censors tasks that are out of reach. Its in the Model but not shown to Planning Layer.
		//does not need to give reactive layer as it does not involve going to stations.
		ArrayList<dTask> gottentasks = Model.getTasks();
		ArrayList<dTask> tasks = new ArrayList<dTask>();
		
		for(int i = 0; i < gottentasks.size(); i++){
			if(gottentasks.get(i).pump_dist < 49){
				tasks.add(gottentasks.get(i) );
			}
		}
		
		return tasks;	
	}
	
	public boolean haveTask(dTask task){
		for(dTask model_task : Model.getTasks()){
			if(model_task.task.equals(task.task)){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * gets nearest task from a specific position within reach from pump.
	 * Dont think this was used in the final implementation
	 * @param pos	Reference position to get the nearest task
	 * @return	Nearest Task to the position given
	 */
	public dTask getNearestTask(posXY pos){//pos value is rel_pump_pos
		
		ArrayList<dTask> tasks = getTasks();
		dTask n_task = null;
		int dist = 50;
		if(tasks.isEmpty()){
			return null;
		}
		
		//find for the task nearest to the tanker now and return it
		for(dTask t : tasks){
			
			posXY task_coord = t.relPump_pos;
			if(dist > Math.max(Math.abs(task_coord.x - pos.x), Math.abs(task_coord.y - pos.x)) ){
				
				dist = Math.max(Math.abs(task_coord.x - pos.x), Math.abs(task_coord.y - pos.x));
				n_task = t;
			}
		}
		
		return n_task;
	}
	

	public posXY getTankerPosition(){

			return this.tanker.getTankerPosition();
	}
	
	/**
	 * returns the nearest task. 
	 * A clear example of censoring through the Control System. Tasks beyond the range of the tanker
	 * and task more than the current water level is censored.
	 * @return	the nearest task explored
	 */
	public dTask getNearestTask(){
		//if do not have enough water to fufill task, censor it
		dTask task = Model.getNearestTask();
		if(task != null){
			if(this.tanker.getWaterLevel() < task.water_req || task.pump_dist > 49){
				if(test.DebugMode)	System.out.println("nearest task blinded from planning");
				PlanningSeesNTask = false;
			}else{
				PlanningSeesNTask = true;
			}
		}else{

			if(test.DebugMode)	System.out.println("no nearest task found.\n");
			return null;
		}
		
		if(!PlanningSeesNTask){	return null;	}
		return task;	}
	
	/**
	 * returns the nearest well from the tanker.
	 * A clear example of censoring through the Control System. Wells beyond the range of the tanker
	 * is censored. This lead to a bug where the tanker does not see any wells that are within its reach.
	 * The alternative will be trying to get the nearest well from the fuel pump, hence leading the tanker 
	 * back to the fuel pump and to the well. Else there's need to do mnore exploration
	 * @return	the nearest well explored
	 */
	public dWell getNearestWell(){
		dWell well = Model.getNearestWell();
		if(well == null){
			
			return null;
		}
		if(well.pump_dist > 49){	
			if(test.DebugMode)	System.out.println("no see well (CS:269)");
			
			PlanningSeesWell = false;
			if(Model.getNearestPWell() != null){	
				if(Model.getNearestPWell().pump_dist < 49){
					if(test.DebugMode)	System.out.println("found another well");
					
					return Model.getNearestPWell();
				}else{
					if(test.DebugMode)	System.out.println("no alternative. Doomed");
				}
			}
		}else{	
			PlanningSeesWell = true;
		}
		
		if(!PlanningSeesWell){	return null;	}else{	return well; }
	}//endof getNearestWell
	
	public void printCurrentRole(){
		if(this.agent != null && InteractProtocol.OpenSys.inSystem(this.agent)){

			System.out.printf("In OS, current Role : %s\n", this.agent.getRole().toString());
		}else{
			System.out.println("Tanker is not in OS.");
		}
	}
	
	/**
	 * <b>MULTI-AGENT SYSTEM</b> : The tanker checks if it needs help from other agent. To do so, the tanker joins the OpenSystem
	 * and posts a Harvest request to complete half of the available tasks.
	 * As the cost of requesting for help, It assigns the other half of those tasks to itself as a Role to be completed
	 * 
	 * For more information, see {@link Harvest}, {@link OpenSystem}, and {@link Role}
	 */
	public void RequestHarvest(){
		//if you are in a role that is not completed(unless Forage), you are not allowed to request!
		if(this.agent != null){
			if(!this.agent.getRole().isRoleCompleted() && !(this.agent.getRole() instanceof Forage)){
				return;
			}
		}
		
		if(test.DebugMode)	System.out.printf("Belief state tasks available : %d\n", getTasks().size());
		if( getTasks().size() >= 3 ){
			System.out.println("Requesting for help to harvest");
			//join system and offer role
			if(agent == null){
				if(test.DebugMode)	System.out.println("null agent");

				agent = InteractProtocol.OpenSys.joinSystem(this.tanker);
				
				//successfully joined
				if(agent != null){	
					/*
					 * offer the half the tasks further from this tanker as a recruit role,
					 * the nearer for you as harvest role
					 * this can be done recursively that keeps recruiting until harvest size is 2 for each role
					 */
					ArrayList<dTask> task1,task2;
					task1 = new ArrayList<dTask>();
					task2 = new ArrayList<dTask>();
					if(test.DebugMode)	System.out.println("Harvest split");
					
					//notice that its taking directly from model, no censoring or redirects.
					//censoring is manually done here again. Can be expanded to make it more complicated
					for(int i = 0; i < Model.getTasks().size(); i++){
						if(i < (int) (Model.getTasks().size())/2){
							
							//if its out of reach, do not add
							if(Model.getTasks().get(i).pump_dist < 49){
								task1.add(Model.getTasks().get(i) );
							}
						}else{
							
							//if its out of reach, do not add
							if(Model.getTasks().get(i).pump_dist < 49){
								task2.add(Model.getTasks().get(i) );
							}
						}
					}
					
					Harvest harvester = new Harvest(this.tanker, task1);
					Harvest recruit = new Harvest(this.tanker,harvester, task2);
					InteractProtocol.OpenSys.requestHelp(this.agent, harvester, recruit);
					System.out.println("Offered Up these tasks as harvest role :");
					for(dTask task : recruit.getTasks()){
						System.out.printf("%s ", task.toString());
					}
					System.out.println(" ");
				}else{
					System.out.println("Failed to Join System! ControlSystem L558");
				}
			}else{
				if(InteractProtocol.OpenSys.inSystem(this.agent))	if(test.DebugMode)	System.out.println("Already in OS!");
			}
		}else{
			if(test.DebugMode)	System.out.println("Too little. Do not need help from system");
		}
	}
	
	/**
	 * <p><b>MULTI-AGENT SYSTEM</b> : Checks if the agent needs to join the Open System or leaves the Open System.</p>
	 * <p>The requirements here determines what are the conditions that the tanker should join the Open System or the condition
	 * if the tanker should leave the Open System. </p>
	 * 
	 * <p>To prevent conflict between roles in Open System and tanker's belief state, both must be updated to prevent clashing
	 * or attempting to complete a completed task.</p>
	 * <p>Joining the OpenSystem returns an Agent object. Agent acts as a "key" to retrieve information from the Open System.</p>
	 * <p>A tanker is only considered to be in the Open System iff it has an Agent object distributed by the Open System.</p>
	 * 
	 * <p>For more information, see {@link OpenSystem}, {@link Agent}, and {@link ControlSystem#RequestHarvest}</p>
	 * 
	 */
	private void OpenSysCheck(){
		//=/////////////////		START OF OPEN SYSTEM CHECK		/////////////////////////=//
		//remove any completed tasks from the open system and the agent's Model
		InteractProtocol.OpenSys.updateRoles();
		this.Model.updateTasks();
		
		if(InteractProtocol.OpenSys.inSystem(this.agent)){
			this.agent.updateAgent(this.tanker.getTankerPosition(), tanker.getWaterLevel(), tanker.getFuelLevel(), tanker.getTankerDirection());
			InteractProtocol.OpenSys.updateAgent(this.agent, this.tanker);
			ArrayList<dWell> open_wells = InteractProtocol.OpenSys.getSharedWell(agent);
			for(dWell well : open_wells){	
				if(well.pump_dist < 49){

					Model.adddWell(well);	
				}
			}
			
			if(this.agent.getRole().isRoleCompleted()){
					System.out.printf("UPDATE : role %s completed. Leaving system\n", this.agent.getRole().toString());
					InteractProtocol.OpenSys.leaveSystem(this.agent);
					this.agent = null;
					
					//check if any of the agent's belief state tasks are completed
					//remove them if completed
					for(int i = 0; i < Model.getTasks().size(); i++){
						if(Model.getTasks().get(i).isTaskComplete()){
							if(test.DebugMode)	System.out.println("Removed finished task in Model");
							Model.getTasks().remove(i);
						}
					}
			}
		}
		
		if(test.DebugMode)	System.out.print("Agent task after update : ");
		if(this.agent != null){
			this.printAgentTasks();
		}else{
			if(test.DebugMode)	System.out.println("no agent!");
		}
		
		//most basic requirement of a tanker not met.
		//Dependant on the open system to see if other agents have wells or tasks
		if( (Model.getWells().isEmpty() || getTasks().isEmpty()) ){
			
			if(this.agent == null){
				System.out.println("join system. Well empty or Task empty");
				this.agent = InteractProtocol.OpenSys.joinSystem(this.tanker);
				
			}else{
				if(this.agent.getRole() instanceof Forage){
					//check if there are other roles to do. If yes, pick them up 
					if(InteractProtocol.OpenSys.hasOpenHarvestRole()){
						
						System.out.println("From Forage. Picked up harvest role");					
						this.agent.setRole(InteractProtocol.OpenSys.getHarvestRole());
						InteractProtocol.OpenSys.changeRole(this.agent, InteractProtocol.OpenSys.getHarvestRole());
					}
				}
			}//end middle-if
		}//end outer if
		//check if agent should request help from OpenSystem to solve all tasks in belief state
		RequestHarvest();
		
		//if tanker joined system successfully
		if(agent != null){
			
			//Reactive Layer needs agent information to coordinate the Forage with other tankers
			RLayer.addAgent(agent);
				
			//get shared well and task lists.
			if(InteractProtocol.OpenSys.inSystem(agent)){
				ArrayList<dWell> open_well = InteractProtocol.OpenSys.getSharedWell(agent);
					
				for(dWell well : open_well){
					if(well.pump_dist < 49){
						Model.adddWell(well);
					}
				}
			}
		}//endof outer-if
		
		//if Agent has tasks that do not need help from the OpenSystem, try to leave as long as Role is completed
		if(!Model.getTasks().isEmpty() && Model.getTasks().size() >=1
				&& Model.getTasks().size() < 3 && InteractProtocol.OpenSys.inSystem(this.agent)){
			
			if(test.DebugMode)	System.out.println("has >= 2 tasks and in system");
			
			if(!(this.agent.getRole() instanceof Forage)){
				if(this.agent.getRole().isRoleCompleted()){
					
					System.out.println("Role Completed. Leaving system.");
					InteractProtocol.OpenSys.leaveSystem(this.agent);
					this.agent = null;				
				}else{
					System.out.printf("Role not completed. Role is %s\n", this.agent.getRole().toString());
				}
				
			}else{
				
				System.out.println("Quitting Forage to collect tasks. Leaving System");
				InteractProtocol.OpenSys.leaveSystem(this.agent);
				this.agent = null;	
			}
		}
	
	//=/////////////////		END OF OPEN SYSTEM CHECK		/////////////////////////=//
		
	}
	

	/**
	 * Control System chooses output between reactive layer and deliberative layer here. This is where Control System is able
	 * to choose which action between Reactive Layer and Deliberative Layer as the final output. Once chosen, the Model Layer
	 * will be updated with the action's movement direction. 
	 * @return	Output Action
	 */
	public Action getOutput(){
		OpenSysCheck();
		final_Paction = PLayer.getOutput();
		final_Raction = RLayer.getOutput();
		
		//Control rule where reactive is prioritized is implemented here. This is to make sure tanker has enough fuel to return to fuel pump
		if(Math.max(Math.abs(this.tanker.getTankerPosition().x), Math.abs(this.tanker.getTankerPosition().y) )+1 >= this.tanker.getFuelLevel() 
				&& this.tanker.getFuelLevel() != Tanker.MAX_FUEL){
			
			if(test.DebugMode)	System.out.printf("only %d left. ",Math.max(Math.abs(this.tanker.getTankerPosition().x), Math.abs(this.tanker.getTankerPosition().y) ));
			System.out.println("control rule take over. choosing reactive over planning");
			final_action = final_Raction;
			if(final_action instanceof MoveTowardsAction){
				
				posXY target = RLayer.getTargetCoord();
				final_dir = ConvertPosToDir(target);
				//System.out.println("movetowardsaction dbtick");
				Model.dbTick(target);		
		
			}else if(final_action instanceof MoveAction){
				
				//System.out.println("moveaction dbtick");
				final_dir = RLayer.getDirection();
				System.out.printf("final_dir : %d\n", final_dir);
				Model.dbTick(final_dir);
			}
			
		}else if(agent != null ){//this is where open system has contorl over the tanker for its designated role

			if(InteractProtocol.OpenSys.inSystem(agent) && agent.getRole() != null){
				//check role and select output
				if(agent.getRole() instanceof Forage){
					
					final_action = RLayer.getOutput();
					final_dir = RLayer.getDirection();
					Model.dbTick(final_dir);
					//recruit more for foraging
					if(InteractProtocol.OpenSys.getForageOpen() < 1){
						Forage recruit = new Forage(this.agent.getRole());
						InteractProtocol.OpenSys.requestHelp(this.agent, this.agent.getRole(), recruit);
					}
				}else if(agent.getRole() instanceof Harvest){
					//check if harvest is done
					final_action = PLayer.getOutput();
					final_dir = ConvertPosToDir(PLayer.getTargetCoord());
					Model.dbTick(final_dir);
					if(final_action == null){
						System.out.println("no action from harvest get role");
						
					}
				}
			}
		}else if(final_Paction != null){
			
			posXY target = PLayer.getTargetCoord();
			System.out.printf("target x : %d, target y : %d\n", target.x, target.y );
			final_dir = ConvertPosToDir(target);
			Model.dbTick(target);
			final_action = final_Paction;
			
		}else if(final_Raction != null){
			
			if(final_Raction instanceof MoveTowardsAction){
				
				posXY target = RLayer.getTargetCoord();
				final_dir = ConvertPosToDir(target);
				Model.dbTick(target);		
		
			}else if(final_Raction instanceof MoveAction){
				
				final_dir = RLayer.getDirection();
				System.out.printf("final_dir : %d\n\n", final_dir);
				Model.dbTick(final_dir);
			}else{
				
				final_dir = 8;
				Model.dbTick(final_dir);
			}
			
			System.out.printf("Raction is %s\n", final_Raction.toString());
			final_action = final_Raction;
		}
		
		//final check
		if( (final_action instanceof RefuelAction && this.tanker.getFuelLevel() == Tanker.MAX_FUEL) || 
				(final_action instanceof LoadWaterAction && this.tanker.getWaterLevel() == Tanker.MAX_WATER)){
			final_action = null;
		}
		if(this.agent != null){
			if(InteractProtocol.OpenSys.inSystem(agent)){
				if(test.DebugMode)	System.out.printf("In Open System. Current Role : %s\n", agent.getRole().toString());
			}
		}
		
		return final_action;
	}//endof GetOutput()
	
	
	/**
	 * Conversion between position and direction for ease of use
	 * @param pos position to be converted for direction
	 * @return Converted direction from given position
	 */
	private int ConvertPosToDir(posXY pos){
		int dir = 8;
		int dx = pos.x - this.tanker.getTankerPosition().x;
		int dy = pos.y - this.tanker.getTankerPosition().y;
		
		if(dx < 0){//WEST
			
			if(dy < 0)	dir = SOUTHWEST;//SOUTHWEST
			else if(dy > 0)	dir = NORTHWEST;//NORTHWEST
			else if(dy == 0) dir = WEST;//WEST
		}else if(dx > 0){//EAST
			
			if(dy < 0)	dir = SOUTHEAST;//SOUTHEAST
			else if(dy > 0)	dir = NORTHEAST;//NORTHEAST
			else if(dy == 0) dir = EAST;//EAST
		}else if(dx == 0){
			
			if(dy < 0)	dir = SOUTH;//SOUTH
			else if(dy > 0)	dir = NORTH;//NORTHWEST
			else if(dy == 0) dir = 8;
		}
		if(test.DebugMode)	System.out.printf("dir is %d\n", dir);
		return dir;
	}
}
