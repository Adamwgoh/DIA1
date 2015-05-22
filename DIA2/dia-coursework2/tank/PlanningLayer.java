package tank;

import java.util.ArrayList;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import uk.ac.nott.cs.g54dia.multilibrary.*;


/**
 * A Schema is a class that serves as a template for executing the Backward Chaining procedure.
 * Each Schema has :
 * 1) Preconditions, Goals that must be satisfied before executing this schema,
 * 2) PostConditions, Goals that will be satisfied by executing the current Schema
 * 3) Action, an Action that can be performed when picked up to satisfy the PostConditions	
 * @author awg04u
 *
 */
final class Schema{
	
	public Schema(SuperTanker tanker){
		this.tanker = tanker;

	};
	
	private SuperTanker tanker;
	private String name;
	private int scheme_number = 0;
	boolean DebugMode = false;
	//effect
	private ArrayList<Goal2> effects = new ArrayList<Goal2>();
	//action
	private Action goalAction;
	//precond
	private ArrayList<Goal2> preconds = new ArrayList<Goal2>();
	public int dPos = 8;
	public void setSchemaName(String n){	name = n;}
	public String getSchemaName(){	return name;}
	public void setscheme_number(int num){	scheme_number  = num;	}
	public int getscheme_number(){			return scheme_number;	}
	
	public Point MoveTowardsPoint;
	public dTask taskinvolved;
	public Action getAction(){	return goalAction;	}
	public ArrayList<Goal2> getEffects(){	return effects;	}
	public ArrayList<Goal2> getPreconds(){	return preconds;}
	public void addEffect(Goal2 effect){	if(effect != null) effects.add(effect);	 }
	public void addPreCond(Goal2 precond){	if(precond != null) preconds.add(precond);}
	
	private int getDirection(int x, int y){
		int direction = 8;
		
		//look for the direction of the movement
		if(x < 0 && y < 0){	direction = SuperTanker.NORTHEAST;}
		if(x < 0 && y > 0){ direction = SuperTanker.SOUTHEAST;}
		if(x < 0 && y == 0){direction = SuperTanker.EAST;}
				
		if(x > 0 && y < 0){	direction = SuperTanker.NORTHWEST;}
		if(x > 0 && y > 0){	direction = SuperTanker.SOUTHWEST;}
		if(x > 0 && y == 0){direction= SuperTanker.WEST;}
		
		if(x == 0 && y < 0){direction = SuperTanker.NORTH;}
		if(x == 0 && y > 0){direction = SuperTanker.SOUTH;}
		
		return direction;
	}
	
	/**
	 * As each schema are empty class with only a scheme label and name, Action performed, preconditions and effects. using this function
	 * An action can be used to performed on a certain object of any type. This function will take care of data types
	 * by checking if the inherited classes are the valid and related to the environment.
	 * @param X	Object that will be assigned to Schema
	 */
	public void setTarget(Object X){
		dPos = 8;
		if(goalAction instanceof MoveTowardsAction){
			if(X instanceof dStation){
				
				goalAction = new MoveTowardsAction(((dStation) X).station.getPoint() );

					int x,y;
					x = this.tanker.getTankerPosition().x - ((dStation) X).relPump_pos.x;
					y = this.tanker.getTankerPosition().y - ((dStation) X).relPump_pos.y;
					if(DebugMode) System.out.printf("in gotostat schema, tankerPosX : %d. tankerPosy : %d\n",
							this.tanker.getTankerPosition().x, this.tanker.getTankerPosition().y);
					dPos = getDirection(x, y);
				
			}else if(X instanceof dWell){
				
				//goalAction = new MoveTowardsAction(((dWell) X).well.getPoint() );
				goalAction = new MoveTowardsAction(this.tanker.ControlSys.getNearestWell().well.getPoint());
				if(DebugMode) System.out.printf("well pos is %s\n", this.tanker.ControlSys.getNearestWell().well.getPoint());
					int x,y;
					x = this.tanker.getTankerPosition().x - this.tanker.ControlSys.getNearestWell().relPump_pos.x;
					y = this.tanker.getTankerPosition().y - this.tanker.ControlSys.getNearestWell().relPump_pos.y;
					if(DebugMode) System.out.printf("in gotowell schema, tankerPosX : %d. tankerPosy : %d\n", this.tanker.getTankerPosition().x, this.tanker.getTankerPosition().y);
					
					//look for the direction of the movement
					dPos = getDirection(x, y);
					
				
			}else if(X instanceof dTask){
				
				goalAction = new MoveTowardsAction(((dTask) X).task.getStationPosition() );
				
					int x,y;
					x = this.tanker.getTankerPosition().x - ((dTask) X).relPump_pos.x;
					y = this.tanker.getTankerPosition().y - ((dTask) X).relPump_pos.y;
					if(DebugMode) System.out.printf("in gototask schema, tankerPosX : %d. tankerPosy : %d\n", this.tanker.getTankerPosition().x, this.tanker.getTankerPosition().y);

					//look for the direction of the movement
					dPos = getDirection(x, y);

			}else if(X instanceof FuelPump){
				
				goalAction = new MoveTowardsAction(((FuelPump) X).getPoint() );
				
				int x,y;
				x = this.tanker.getTankerPosition().x;
				y = this.tanker.getTankerPosition().y;
				if(DebugMode) System.out.printf("in gotofuel schema, tankerPosX : %d. tankerPosy : %d\n", this.tanker.getTankerPosition().x, this.tanker.getTankerPosition().y);
				
				//look for the direction of the movement
				dPos = getDirection(x, y);
			}
			
			
		}else if(goalAction instanceof LoadWaterAction){
			
				goalAction = new LoadWaterAction();
		}else if(goalAction instanceof RefuelAction){
			
				goalAction = new RefuelAction();
		}else{
			if(X instanceof dTask){
				if(DebugMode) 	System.out.printf("delivering. X is at %s, and is completed? %s\n",
									((dTask) X).task.getStationPosition().toString(), ((dTask) X).task.isComplete() );
	
				goalAction = new DeliverWaterAction(((dTask) X).task);	
					int x,y;
					if(this.tanker == null){
						System.out.println("tanker is null");
					}
					x = this.tanker.getTankerPosition().x - ((dTask) X).relPump_pos.x;
					y = this.tanker.getTankerPosition().y - ((dTask) X).relPump_pos.y;
					if(DebugMode) System.out.printf("in schema, tankerPosX : %d. tankerPosy : %d\n", this.tanker.getTankerPosition().x, this.tanker.getTankerPosition().y);
					
					//look for the direction of the movement
					dPos = getDirection(x, y);
			}else{
				if(DebugMode) System.out.println("none of the above X instance in L153");
				goalAction = null;
			}
		
		
		}//end if
	}//end func
	
	/**
	 * SetAction is a placeholder function that sets the goalAction into the appropriate action
	 * @param i	flag to choose appropriate Action
	 */
	public void setAction(int i){	
		switch(i){
			//tanker position is a placeholder item, will be edited out during the backchaining procedure
			case 1 : goalAction = new MoveTowardsAction(this.tanker.getPosition());break;
			case 2 : goalAction = new RefuelAction();break;
			//null is a placeholder item. Unable to have the right well in here as planing layer does not see the well yet at this time.
			case 3 : goalAction = null;break;
			case 4 : goalAction = new LoadWaterAction();break;
		}
	}
	
	
}

/**
 * Task class to do backward chaining. variables in it determines what type of goal it is.
 * 
 * You can check the type of class by calling its askGoalType() method, which returns the object of interest.
 * You can also use getPreconds() to get the preconditions of the goal.
 * If you need to know the action that results in this goal, you can call getAction() to return the action;
 * 
 * Each goal can only have at most one requirements. Two targets in a goal is not allowed. Have two goals instead
 * if that is what was intended to represent.
 * 
 * NOTE : To check Fuel availability, it is required to throw an action of ReFuelAction even if planning layer doesnt support it.
 * This is only for reference and not for execution, despite refuel being reactive layer's job.
 *  
 * @author dolphin
 *
 */
final class Goal2{
	public Goal2(int goaltype, SuperTanker tanker){
		this.tanker = tanker;
		goalType = goaltype;
	}
	
	//represents what type of goal it is. At(x)? HaveFuel(x)? HaveWater(x)? Complete(x)?
	public final static int 
		NONE 		 = 0,
		ATSTAT 		 = 1,
		ATWELL 		 = 2,
		ATTASK 	 	 = 3,
		ATFUEL		 = 4,
		HAVEFUEL 	 = 5,
		HAVEWATER 	 = 6,
		COMPLETETASK = 7,
		REFUEL		 = 8;
		
	protected SuperTanker tanker;
	private int goalType = NONE;
	private Action goalAction = null;
	boolean DebugMode = false;
	public int getGoalType(){	return goalType; }
	
	public Action getAction(){	return goalAction; }
	public void setGoalType(int goal_type){
		switch(goal_type){
			case NONE : 		goalType = 0; break;
			case ATSTAT : 		goalType = 1; break;
			case ATWELL : 		goalType = 2; break;
			case ATTASK : 		goalType = 3; break;
			case ATFUEL : 		goalType = 4; break;
			case HAVEFUEL : 	goalType = 5; break;
			case HAVEWATER : 	goalType = 6; break;
			case COMPLETETASK : goalType = 7; break;
			case REFUEL		  : goalType = 8; break;
		}
	}
	public boolean checkGoalForX(Object X){
		switch(goalType){
			case ATSTAT : if(DebugMode){ System.out.println("checking if tanker is at stationX.");}return this.tanker.ControlSys.At((dStation) X);
			case ATWELL : if(DebugMode){ System.out.println("checking if tanker is at wellX..");}return this.tanker.ControlSys.At(this.tanker.ControlSys.getNearestWell());
			case ATTASK : if(DebugMode){ System.out.println("checking if tanker is at taskX..");}return this.tanker.ControlSys.At((dTask) X);
			case ATFUEL : if(DebugMode){ System.out.println("checking if X is at fuel..");}return this.tanker.ControlSys.AtPump();
			case HAVEFUEL :  if(X instanceof dStation){
				
								if(DebugMode) System.out.println("check if X has fuel to station");
								//have enough fuel to travel to station and have enough to travel back to pump + buffer fuel
								int tank_dist = Math.max(Math.abs(((dStation) X).getRelPumpRos().x -this.tanker.getTankerPosition().x),
										Math.abs(((dStation) X).getRelPumpRos().y -this.tanker.getTankerPosition().y));
								
								return this.tanker.ControlSys.haveFuel(tank_dist + ((dStation) X).pump_dist);
							 }else if(X instanceof dWell){
								 
								 if(DebugMode) System.out.println("check if X has fuel to well");
									//have enough fuel to travel to station and have enough to travel back to pump + buffer fuel
								 int tank_dist = Math.max(Math.abs(((dWell) X).getRelPumpRos().x -this.tanker.getTankerPosition().x), 
										 Math.abs(((dWell) X).getRelPumpRos().y -this.tanker.getTankerPosition().y));
								 
								 return this.tanker.ControlSys.haveFuel(tank_dist + ((dWell) X).pump_dist );
							 }else if(X instanceof dTask){
								 
								 if(DebugMode) System.out.println("check if X has fuel to task");
									//have enough fuel to travel to station and have enough to travel back to pump + buffer fuel
								 if(DebugMode) System.out.printf("(L263) tank dist : %d, pump_dist : %d\n", ((dTask) X).tank_dist, ((dTask) X).pump_dist);
									 int tank_dist = Math.max(Math.abs(((dTask) X).getRelPumpRos().x -this.tanker.getTankerPosition().x), 
											 Math.abs(((dTask) X).getRelPumpRos().y -this.tanker.getTankerPosition().y));
									 
								 	return this.tanker.ControlSys.haveFuel(tank_dist + ((dTask) X).pump_dist);
							 }else if(X instanceof Integer){
								 
								 return this.tanker.ControlSys.haveFuel((Integer) X);
							 }
			case HAVEWATER : if(X instanceof dTask){
				
								if(DebugMode) System.out.println("X is dTask, checking if have water requirement");
								return this.tanker.ControlSys.haveWater(((dTask) X).getWaterReq());
							 }else if(X instanceof Integer){
								 
								 return this.tanker.ControlSys.haveWater((Integer) X);
							 }
			case COMPLETETASK : if(X instanceof dTask){
				
									if(DebugMode) System.out.println("X is dTask, checking if task is completed");
									return ((dTask) X).task.isComplete();
								}
			case REFUEL : if(X instanceof FuelPump){
				
									if(DebugMode) System.out.println("X is fuelPump, checking if refueled");
								return (this.tanker.getFuelLevel() == Tanker.MAX_FUEL);
							}
			
			default : if(DebugMode) System.out.println("check goal fail(line162). Default returning false..");return false;
		}
	
	}
}

/**
 * The Planning layer takes care of delibrative actions of the system. It derives legal/right plans that can be executed.
 * If there are no plans that could be executed, it passes back to the reactive layer by returning null action to the control system.
 * 
 * The Planning Layer has the backward planning ability that derive an optimal plan. A plan consist of set of Actions to be executed to reach
 * a goal state. Each timestep the planner feeds an action from the action_list(the plan that stores actions in order) to the control system.
 * If no actions are in the action_list, then the reactive layer will take over. However, control system still maintains the final decision to
 * choose actions from reactive layers or planning layer based on the control rules.
 * @author dolphin
 *
 */
public class PlanningLayer {
//  NORTH       =   0, //(0, +1)
//  SOUTH       =   1, //(0, -1)
//  EAST        =   2, //(+1,  0)
//  WEST        =   3, //(-1,  0)
//  NORTHEAST   =   4, //(+1, +1)
//  NORTHWEST   =   5, //(-1, +1)
//  SOUTHEAST   =   6, //(-1, +1)
//  SOUTHWEST   =   7; //(-1, -1)
	
	//constructor
	public PlanningLayer(SuperTanker tanker){
		this.tanker = tanker;
		GOTOSTAT = new Schema(this.tanker);
		GOTOWELL = new Schema(this.tanker);
		GOTOTASK = new Schema(this.tanker);
		GOTOFUEL = new Schema(this.tanker);
		DELIVER = new Schema(this.tanker);
		FILLUP = new Schema(this.tanker);
		REFUEL = new Schema(this.tanker);
		GOTOSTAT.setSchemaName("GOTOSTAT");
		GOTOWELL.setSchemaName("GOTOWELL");
		GOTOTASK.setSchemaName("GOTOTASK");
		GOTOFUEL.setSchemaName("GOTOFUEL");
		DELIVER.setSchemaName("DELIVER");
		FILLUP.setSchemaName("FILLUP");
		REFUEL.setSchemaName("REFUEL");
		
		SetupSchemas();
		schemas.add(GOTOSTAT);
		schemas.add(GOTOWELL);
		schemas.add(GOTOTASK);
		schemas.add(DELIVER);
		schemas.add(FILLUP);
		schemas.add(REFUEL);
		schemas.add(GOTOFUEL);
	}
	
	protected SuperTanker tanker;
	protected Action finalAction = null;
	protected int finalDir = 8;
	protected LinkedList<Action> action_list = new LinkedList<Action>();

	protected ArrayList<Schema> schemas = new ArrayList<Schema>();
	private Schema GOTOSTAT;
	private Schema GOTOWELL;
	private Schema GOTOTASK;
	private Schema GOTOFUEL;
	private Schema DELIVER;
	private Schema FILLUP;
	private Schema REFUEL;
	private int curr_fuellevel;
	private int curr_pumpdist; 
	private int curr_waterlevel;
	private Plan plan;
	private boolean DebugMode = false;
	private	InputStreamReader in = new InputStreamReader(System.in);
	private BufferedReader br = new BufferedReader(in);

	private final int 
		NONE = 0,
		ATSTAT = 1,
		ATWELL = 2,
		ATTASK = 3,
		ATFUEL = 4,
		HAVEFUEL = 5,
		COMPLETETASK = 7,
		rEFUEL = 8;
	
	
	/**
	 * Setsup the predefined schemas
	 * The schemas that will be used are
	 *  GOTOSTAT, GOTOWELL, GOTOFUEL, DELIVER, FILLUP, and REFUEL
	 */
	private void SetupSchemas(){
		final int
			ATSTAT 	  = 1,
			ATWELL 	  = 2,
			ATTASK 	  = 3,
			ATFUEL	  = 4,
			HAVEFUEL  = 5,
			HAVEWATER = 6,
			COMPLETETASK = 7;
			
		//effects
		GOTOSTAT.addEffect(new Goal2(ATSTAT, this.tanker));
		GOTOWELL.addEffect(new Goal2(ATWELL, this.tanker));
		GOTOTASK.addEffect(new Goal2(ATTASK, this.tanker));
		GOTOFUEL.addEffect(new Goal2(ATFUEL, this.tanker));
		DELIVER.addEffect(new Goal2(COMPLETETASK, this.tanker));
		FILLUP.addEffect(new Goal2(HAVEWATER, this.tanker));
		REFUEL.addEffect(new Goal2(HAVEFUEL, this.tanker));
		
		//preconds
		GOTOSTAT.addPreCond(new Goal2(HAVEFUEL, this.tanker));//usually backward chaining will end at havefuel as true. If false, then refuel.
		GOTOWELL.addPreCond(new Goal2(HAVEFUEL, this.tanker));
		GOTOTASK.addPreCond(new Goal2(HAVEFUEL, this.tanker));
		//GOTOFUEL.addPreCond(new Goal2(HAVEFUEL)); z
		DELIVER.addPreCond(new Goal2(HAVEWATER, this.tanker));
		DELIVER.addPreCond(new Goal2(ATTASK, this.tanker));
		FILLUP.addPreCond(new Goal2(ATWELL, this.tanker));
		REFUEL.addPreCond(new Goal2(ATFUEL, this.tanker));
		
		//actions
		//	1 : MoveTowardsAction(this.tanker.getPosition());
		//	2 : RefuelAction();
		//	3 : DeliverWaterAction(atTask.task);
		//	4 : LoadWaterAction();
		GOTOSTAT.setAction(1);
		GOTOWELL.setAction(1);
		GOTOTASK.setAction(1);
		GOTOFUEL.setAction(1);
		DELIVER.setAction(3);
		FILLUP.setAction(4);
		REFUEL.setAction(2);
		
		//set scheme number
		// 0 : NONE
		// 1 : GOTOSTAT
		// 2 : GOTOWELL
		// 3 : GOTOTASK
		// 4 : GOTOFUEL
		// 4 : DELIVER
		// 5 : FILLUP
		// 6 : REFUEL
		// 7 : GOTOFUEL
		GOTOSTAT.setscheme_number(1);
		GOTOWELL.setscheme_number(2);
		GOTOTASK.setscheme_number(3);
		GOTOFUEL.setscheme_number(4);
		DELIVER.setscheme_number(5);
		FILLUP.setscheme_number(6);
		REFUEL.setscheme_number(7);
		GOTOFUEL.setscheme_number(8);
		
	}

	
	public posXY getTargetCoord(){
		
		posXY tanker_coord = this.tanker.getTankerPosition();
		System.out.printf("tanker coord is x : %d, y : %d, finalDir : %d\n", tanker_coord.x, tanker_coord.y, finalDir);
		switch(finalDir){
			case SuperTanker.NORTH 		: tanker_coord.y++; break;
			case SuperTanker.NORTHEAST  : tanker_coord.x++; tanker_coord.y++;break;
			case SuperTanker.NORTHWEST  : tanker_coord.x--; tanker_coord.y++;break;
			case SuperTanker.SOUTH 		: tanker_coord.y--; break;
			case SuperTanker.SOUTHEAST  : tanker_coord.x++; tanker_coord.y--;break;
			case SuperTanker.SOUTHWEST  : tanker_coord.x--; tanker_coord.y--;break;
			case SuperTanker.WEST  		: tanker_coord.x--; break;
			case SuperTanker.EAST  		: tanker_coord.x++; break;
			
		}
		
		return tanker_coord;
		
	}

	/**
	 * One of the most important part of the Deliberative Layer. Backwards Chaining fixes a certain goal to an object, and
	 * attempt to look for actions= in schemas that satisfies the goal. If preconditions are needed to be satisfied first before
	 * the action can be executed, the precondtions will be recursively backward chained to generate an action. 
	 * Backwards chaining only returns true if the goal is satisfied. This allows control over what action to be made in regards to the
	 * goal satisfiabiity
	 * @param goal	The goal to be achieved
	 * @param X	The item to be asserted with thhe goal
	 * @return	returns true if the goal is satisfied with the current state
	 */
	private boolean backwardsChaining(Goal2 goal, Object X){
		
			if(DebugMode) System.out.println("backward chaining");
			//check if goal had already been satisfied for X
			if(X == null){
				
				if(DebugMode)	System.out.println("X is null!\n");
				return false;
			}
			
			if(goal.checkGoalForX(X)){
				
				if(DebugMode) System.out.println("goal satisfied by current state\n");
				return true;
			}else{
				
				if(DebugMode) System.out.println("goal not satisfied by X for current state");
			}
			
			//find schema that matches the goal as effect
			Schema schema = null;
			for(Schema s : schemas){
				if(s.getEffects() != null){
					for(Goal2 g : s.getEffects()){
						
						if(g.getGoalType() == goal.getGoalType() && schema == null){//if goal is an effect of a certain scheme.
							
							//found target goal with object in schema
							if(DebugMode) System.out.printf("Goal found. Schema is %s : \n\n", s.getSchemaName());
							schema = s;
							
						}
					}
				}
				
			}
			
			if(schema == null){
				if(DebugMode)	System.err.println("goal not found in any schema!");
				
				return false;//can't find an action that leads to target goal in schemas.
			}
				
			schema.setTarget(X);//sets X onto the action
			if(X instanceof dTask){
				if(((dTask)X).task.isComplete()){
					if(DebugMode) System.out.printf("X is completed task, coord : %s\n", ((dTask)X).task.getStationPosition().toString());
				}else{
					if(DebugMode) System.out.printf("X is not completed task, coord : %s\n", ((dTask)X).task.getStationPosition().toString());
				}
			}
		
			if(DebugMode) System.out.printf("dpos value %d\n", schema.dPos);

			Action act = null;	
			finalDir = schema.dPos;
			act = schema.getAction();
			
			finalAction = act;
			ArrayList<Goal2> preconds = schema.getPreconds();
			if(preconds.size() == 0){
				return false;
			}
			
			for(Goal2 g : preconds){
				
				if(g.getGoalType() == ATWELL){

					if(this.tanker.ControlSys.getNearestWell() == null){
						
						if(DebugMode) System.out.println("no valid well");
						finalAction = null;
						return false;
					}
					if(!backwardsChaining(g, this.tanker.ControlSys.getNearestWell())){
						return false;
					}
					
				}else if(g.getGoalType() == ATFUEL){
					if(!backwardsChaining(g, this.tanker.ControlSys.getFuelPump())){
						return false;
					}
				}else{
				
					if(!backwardsChaining(g, X)){
						return false;
					}
				}
			}

			return false;
	}
	
	/**
	 * Generate Plan generates a plan by creating a new Plan object by the given paramters. A wrapper to the Plan class
	 * @param tasks	tasks received from the Model Layer
	 * @param curr_fuellevel	current fuel level
	 * @param curr_pumpdist		current distance to pump
	 * @param curr_waterlevel	current water level
	 * @return	A Plan with the given percepts and tasks to be executed
	 */
	private Plan GeneratePlan(ArrayList<dTask> tasks, int curr_fuellevel, int curr_pumpdist, int curr_waterlevel){
		Plan plan = new Plan(tasks, curr_fuellevel, curr_pumpdist, curr_waterlevel,  this.tanker);

		if(!(plan.getPlan() == null)){
			if(!plan.getPlan().isEmpty()){
				System.out.printf("generated plan : %s, pipeline size : %d\n", plan.printPipeLine(), plan.task_pipeline.size());
				return plan;
			}
		}
		return null;
	}
	
	/**
	 * Compare the score of two plans and returns the difference in scores
	 * @param new_plan	the new plan that was generated
	 * @param old_plan	the current plan that is being executed
	 * @return	score differences between two plans
	 */
	private float scoreDiff(Plan new_plan, Plan old_plan){
		int plan1 = new_plan.getPlanScore();
		int plan2 = old_plan.getPlanScore();
		
		float result = 0;
		result = ((float) plan1 - (float) plan2)/((float) plan1+(float) plan2);
		if(DebugMode)	System.out.printf("scorediff : %f\n", result);
		if(DebugMode)	System.out.printf("old score : %d, new score : %d\n", plan2, plan1);
		
		return result;
	}
	
	/**
	 * Gather percepts first gathers all necessary information from the Model (fileterd by ControlSystem) that are
	 * needed to produce a plan. If a current plan is being pursued, the plan will be compared with the newly generated
	 * plan based on the Score Difference. If the score difference is more than 50% than the new plan will be used
	 */
	private void gatherPercepts2(){
		System.out.println("gather percepts");
		//get set of tasks available
		ArrayList<dTask> tasks = this.tanker.ControlSys.getTasks();
		curr_fuellevel = this.tanker.getFuelLevel();
		curr_pumpdist = this.tanker.getPumpDist();
		curr_waterlevel = this.tanker.getWaterLevel();
		
		if(this.tanker.ControlSys.getNearestWell() != null){
			Plan temp_plan = GeneratePlan(tasks, curr_fuellevel, curr_pumpdist, curr_waterlevel);
			//System.out.println("found well. generating plan");
			
			if(plan != null){
				
				if(plan.CompareDist(temp_plan)){
					if(!plan.isSame(temp_plan)){
						System.out.printf("plan dist : %d, temp_plan dist : %d\n", plan.EstimatedtotalDist, temp_plan.EstimatedtotalDist);
						float score_diff = scoreDiff(temp_plan, this.plan);
						if(score_diff > 0.3){
							System.out.printf("curr plan : %s", plan.printPipeLine() );
							System.out.printf("temp plan : %s", temp_plan.printPipeLine() );
							System.out.println("change of plan");
							plan = temp_plan;
						}

					}

				}else{
					
					if(temp_plan != null)	System.out.printf("plan dist : %d, temp_plan dist : %d\n", plan.EstimatedtotalDist, temp_plan.EstimatedtotalDist);
					System.out.println("remain same plan");

				}
			}else{	plan = temp_plan;	}
		}else{
			if(DebugMode)	System.out.println("no well thats why no generate plan");
		}
		
	}
	
	/**
	 * Executing a plan relates to backchaining each goal in the plan. If the goal is achieved, the goal will be removed from the plan
	 * @param plan	Plan to be executed
	 * @return	Returns true when the plan is done
	 */
	 // generate goal to backchain with
	 private boolean executePlan(Plan plan){
	  		Goal2 task_goal = new Goal2(7, this.tanker);
	  
	  		//get objects
	  		if(plan == null)	return false;
	  		LinkedList<dTask>goals = plan.getPlan();
	  		System.out.printf("plan size : %d\n", goals.size());
	  		System.out.printf("chaining %s\n", goals.getFirst().toString());
	  		if(this.tanker == null)	System.out.println("in PLayer ln743 : tank is null");
	  		if(backwardsChaining(task_goal, goals.getFirst()) ){

	  			if(DebugMode) System.out.println("removing goal");
	  			plan.RemoveGoal(goals.getFirst(), this.tanker.getFuelLevel(), this.tanker.pump_dist, this.tanker.getWaterLevel());
	  		
	  			if(goals.size() == 0){
	  				
	  				System.out.println("finish plan. removing plan and action");
	  				finalAction = null;
	  				this.plan = null;
	  				return true;
	  			}
	 			//this is just to move on to the next goal, if next goal is completed, step up straight and move to next
	 			//have to break if not complete so controlsystem can run the set action
	  			while(goals.size() > 0){

	  				if(backwardsChaining(task_goal,goals.getFirst()) ){
	  					
	  					goals.removeFirst();
	  					if(DebugMode) System.out.println("recursive backwardchaining is true");	
	 				}else{
	 					return false;
	 				}
				}
	  			if(DebugMode) System.out.println("(L590) finish plan. removing plan and action");
  				finalAction = null;
  				this.plan = null;

	  			return true;
	  		}else{
	  			return false;
	  		}
	  		
	  }
	
	 /**
	  * Final destination of the Deliberative Layer.
	  * @return	the Action that the deliberative layer decided to execute. The action will then be filtered by the Control System.
	  */
	public Action getOutput(){
		gatherPercepts2();
		if(plan != null){
			
			if(executePlan(plan)){
				gatherPercepts2();
				executePlan(plan);
			}
		}else{
			System.out.println("no plan generated. not executing anything");
		}

		if(finalAction == null){		
				if(DebugMode)	System.out.println("no action from planning layer");
				return null;
		}
		
		return finalAction;
	}
}
