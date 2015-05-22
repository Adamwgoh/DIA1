import java.util.ArrayList;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import uk.ac.nott.cs.g54dia.library.*;


/**
 * A schema con
 * @author awg04u
 *
 */
final class Schema{
	
	public Schema(){};
	private String name;
	private int scheme_number = 0;
	boolean DebugMode = true;
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
	
	//sets X onto the action.
	/**
	 * As each schema are empty class with only a scheme label and name, Action performed, preconditions and effects. using this function
	 * An action can be used to performed on a certain object of any type. This function will take care of data types
	 * by checking if the inherited classes are the valid and related to the environment.
	 * @param X
	 */
	public void setTarget(Object X){
		dPos = 8;
		if(goalAction instanceof MoveTowardsAction){
			if(X instanceof dStation){
				
				goalAction = new MoveTowardsAction(((dStation) X).station.getPoint() );
				
					int x,y;
					x = test.tanker.getTankerPosition().x - ((dStation) X).relPump_pos.x;
					y = test.tanker.getTankerPosition().y - ((dStation) X).relPump_pos.y;
					if(DebugMode) System.out.printf("in gotostat schema, tankerPosX : %d. tankerPosy : %d\n",
							test.tanker.getTankerPosition().x, test.tanker.getTankerPosition().y);
					
					//look for the direction of the movement
					if(x < 0 && y < 0){	dPos = SuperTanker.NORTHEAST;}
					if(x < 0 && y > 0){ dPos = SuperTanker.SOUTHEAST;}
					if(x < 0 && y == 0){dPos = SuperTanker.EAST;}
							
					if(x > 0 && y < 0){dPos = SuperTanker.NORTHWEST;}
					if(x > 0 && y > 0){dPos = SuperTanker.SOUTHWEST;}
					if(x > 0 && y == 0){dPos= SuperTanker.WEST;}
					
					if(x == 0 && y < 0){dPos = SuperTanker.NORTH;}
					if(x == 0 && y > 0){dPos = SuperTanker.SOUTH;}
				
			}else if(X instanceof dWell){
				
				//goalAction = new MoveTowardsAction(((dWell) X).well.getPoint() );
				goalAction = new MoveTowardsAction(test.ControlSys.getNearestWell().well.getPoint());
				if(DebugMode) System.out.printf("well pos is %s\n", test.ControlSys.getNearestWell().well.getPoint());
					int x,y;
					x = test.tanker.getTankerPosition().x - test.ControlSys.getNearestWell().relPump_pos.x;
					y = test.tanker.getTankerPosition().y - test.ControlSys.getNearestWell().relPump_pos.y;
					if(DebugMode) System.out.printf("in gotowell schema, tankerPosX : %d. tankerPosy : %d\n", test.tanker.getTankerPosition().x, test.tanker.getTankerPosition().y);
					
					//look for the direction of the movement
					if(x < 0 && y < 0){	dPos = SuperTanker.NORTHEAST;}
					if(x < 0 && y > 0){ dPos = SuperTanker.SOUTHEAST;}
					if(x < 0 && y == 0){dPos = SuperTanker.EAST;}
							
					if(x > 0 && y < 0){dPos = SuperTanker.NORTHWEST;}
					if(x > 0 && y > 0){dPos = SuperTanker.SOUTHWEST;}
					if(x > 0 && y == 0){dPos= SuperTanker.WEST;}
					
					if(x == 0 && y < 0){dPos = SuperTanker.NORTH;}
					if(x == 0 && y > 0){dPos = SuperTanker.SOUTH;}
				
			}else if(X instanceof dTask){
				
				goalAction = new MoveTowardsAction(((dTask) X).task.getStationPosition() );
				
					int x,y;
					x = test.tanker.getTankerPosition().x - ((dTask) X).relPump_pos.x;
					y = test.tanker.getTankerPosition().y - ((dTask) X).relPump_pos.y;
					if(DebugMode) System.out.printf("in gototask schema, tankerPosX : %d. tankerPosy : %d\n", test.tanker.getTankerPosition().x, test.tanker.getTankerPosition().y);
					//System.out.printf("task.relpump_pos.x : %d, task.relpump_pos.y",((dTask) X).relPump_pos.x, ((dTask) X).relPump_pos.y );
					//System.out.printf("%s, task position, X : %d, Y : %d\n", ((dTask) X).task.getStationPosition().toString(), ((dTask) X).relPump_pos.x, ((dTask) X).relPump_pos.y);
					
					//look for the direction of the movement
					if(x < 0 && y < 0){	dPos = SuperTanker.NORTHEAST;}
					if(x < 0 && y > 0){ dPos = SuperTanker.SOUTHEAST;}
					if(x < 0 && y == 0){dPos = SuperTanker.EAST;}
							
					if(x > 0 && y < 0){dPos = SuperTanker.NORTHWEST;}
					if(x > 0 && y > 0){dPos = SuperTanker.SOUTHWEST;}
					if(x > 0 && y == 0){dPos= SuperTanker.WEST;}
					
					if(x == 0 && y < 0){dPos = SuperTanker.NORTH;}
					if(x == 0 && y > 0){dPos = SuperTanker.SOUTH;}
			}else if(X instanceof FuelPump){
				
				goalAction = new MoveTowardsAction(((FuelPump) X).getPoint() );
				
				int x,y;
				x = test.tanker.getTankerPosition().x;
				y = test.tanker.getTankerPosition().y;
				if(DebugMode) System.out.printf("in gotofuel schema, tankerPosX : %d. tankerPosy : %d\n", test.tanker.getTankerPosition().x, test.tanker.getTankerPosition().y);
				
				//look for the direction of the movement
				if(x < 0 && y < 0){	dPos = SuperTanker.NORTHEAST;}
				if(x < 0 && y > 0){ dPos = SuperTanker.SOUTHEAST;}
				if(x < 0 && y == 0){dPos = SuperTanker.EAST;}
						
				if(x > 0 && y < 0){dPos = SuperTanker.NORTHWEST;}
				if(x > 0 && y > 0){dPos = SuperTanker.SOUTHWEST;}
				if(x > 0 && y == 0){dPos= SuperTanker.WEST;}
				
				if(x == 0 && y < 0){dPos = SuperTanker.NORTH;}
				if(x == 0 && y > 0){dPos = SuperTanker.SOUTH;}
			}
			
			
		}else if(goalAction instanceof LoadWaterAction){
			
				goalAction = new LoadWaterAction();
		}else if(goalAction instanceof RefuelAction){
			
				goalAction = new RefuelAction();
		}else{
			if(X instanceof dTask){
				
				//System.out.println("settargetX's fault la");
				if(DebugMode) System.out.printf("delivering. X is at %s, and is completed? %s\n",
						((dTask) X).task.getStationPosition().toString(), ((dTask) X).task.isComplete() );
				goalAction = new DeliverWaterAction(((dTask) X).task);
				
					int x,y;
					x = test.tanker.getTankerPosition().x - ((dTask) X).relPump_pos.x;
					y = test.tanker.getTankerPosition().y - ((dTask) X).relPump_pos.y;
					if(DebugMode) System.out.printf("in schema, tankerPosX : %d. tankerPosy : %d\n", test.tanker.getTankerPosition().x, test.tanker.getTankerPosition().y);
					//System.out.printf("x : %d, y : %d\n", x, y);
					
					//look for the direction of the movement
					if(x < 0 && y < 0){	dPos = SuperTanker.NORTHEAST;}
					if(x < 0 && y > 0){ dPos = SuperTanker.SOUTHEAST;}
					if(x < 0 && y == 0){dPos = SuperTanker.EAST;}
							
					if(x > 0 && y < 0){dPos = SuperTanker.NORTHWEST;}
					if(x > 0 && y > 0){dPos = SuperTanker.SOUTHWEST;}
					if(x > 0 && y == 0){dPos= SuperTanker.WEST;}
					
					if(x == 0 && y < 0){dPos = SuperTanker.NORTH;}
					if(x == 0 && y > 0){dPos = SuperTanker.SOUTH;}
			}else{
				if(DebugMode) System.out.println("none of the above X instance in L153");
				goalAction = null;
			}
		
		
		}//end if
	}//end func
	
	/**
	 * SetAction is a placeholder function that sets the goalAction into the appropriate action
	 * @param i
	 */
	public void setAction(int i){	
		switch(i){
			//tanker position is a placeholder item, will be edited out during the backchaining procedure
			case 1 : goalAction = new MoveTowardsAction(test.tanker.getPosition());break;
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
	public Goal2(int goaltype){
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
		
	private int goalType = NONE;
	private dStation atStation = null;
	private dWell	 atWell = null;
	private dTask atTask = null;
	private Integer HaveFuel = 0;
	private Integer HaveWater = 0;
	private Action goalAction = null;
	boolean DebugMode = true;
	public int getGoalType(){	return goalType; }
	
	public Action getAction(){	return goalAction; }
	public void setGoalType(int goal_type){
		switch(goal_type){
			case NONE : goalType = 0;break;
			case ATSTAT : goalType = 1;break;
			case ATWELL : goalType = 2;break;
			case ATTASK : goalType = 3;break;
			case ATFUEL : goalType = 4;break;
			case HAVEFUEL : goalType = 5;break;
			case HAVEWATER : goalType = 6;break;
			case COMPLETETASK : goalType = 7;break;
			case REFUEL		  : goalType = 8;break;
		}
	}
	public boolean checkGoalForX(Object X){
		switch(goalType){
			case ATSTAT : if(DebugMode){ System.out.println("checking if tanker is at stationX.");}return test.ControlSys.At((dStation) X);
			case ATWELL : if(DebugMode){ System.out.println("checking if tanker is at wellX..");}return test.ControlSys.At(test.ControlSys.getNearestWell());
			case ATTASK : if(DebugMode){ System.out.println("checking if tanker is at taskX..");}return test.ControlSys.At((dTask) X);
			case ATFUEL : if(DebugMode){ System.out.println("checking if X is at fuel..");}return test.ControlSys.AtPump();
			case HAVEFUEL :  if(X instanceof dStation){
				
								if(DebugMode) System.out.println("check if X has fuel to station");
								//have enough fuel to travel to station and have enough to travel back to pump + buffer fuel
								int tank_dist = Math.max(Math.abs(((dStation) X).getRelPumpRos().x -test.tanker.getTankerPosition().x),
										Math.abs(((dStation) X).getRelPumpRos().y -test.tanker.getTankerPosition().y));
								return test.ControlSys.haveFuel(tank_dist + ((dStation) X).pump_dist);
							 }else if(X instanceof dWell){
								 
								 if(DebugMode) System.out.println("check if X has fuel to well");
									//have enough fuel to travel to station and have enough to travel back to pump + buffer fuel
								 int tank_dist = Math.max(Math.abs(((dWell) X).getRelPumpRos().x -test.tanker.getTankerPosition().x), 
										 Math.abs(((dWell) X).getRelPumpRos().y -test.tanker.getTankerPosition().y));	
								 return test.ControlSys.haveFuel(tank_dist + ((dWell) X).pump_dist );
							 }else if(X instanceof dTask){
								 
								 if(DebugMode) System.out.println("check if X has fuel to task");
									//have enough fuel to travel to station and have enough to travel back to pump + buffer fuel
								 	System.out.printf("(L263) tank dist : %d, pump_dist : %d\n", ((dTask) X).tank_dist, ((dTask) X).pump_dist);
									 int tank_dist = Math.max(Math.abs(((dTask) X).getRelPumpRos().x -test.tanker.getTankerPosition().x), 
											 Math.abs(((dTask) X).getRelPumpRos().y -test.tanker.getTankerPosition().y));	
								 	return test.ControlSys.haveFuel(tank_dist + ((dTask) X).pump_dist);
							 }else if(X instanceof Integer){
								 
								 return test.ControlSys.haveFuel((Integer) X);
							 }
			case HAVEWATER : if(X instanceof dTask){
								if(DebugMode) System.out.println("X is dTask, checking if have water requirement");
								return test.ControlSys.haveWater(((dTask) X).getWaterReq());
								
							 }else if(X instanceof Integer){
								 
								 return test.ControlSys.haveWater((Integer) X);
							 }
			case COMPLETETASK : if(X instanceof dTask){
									if(DebugMode) System.out.println("X is dTask, checking if task is completed");
									return ((dTask) X).task.isComplete();
								}
			case REFUEL : if(X instanceof FuelPump){
									if(DebugMode) System.out.println("X is fuelPump, checking if refueled");
								return (test.tanker.getFuelLevel() == test.tanker.MAX_FUEL);
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
	public PlanningLayer(){
		
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
	
	protected Action finalAction = null;
	protected int finalDir = 8;
	protected LinkedList<Action> action_list = new LinkedList<Action>();

	protected ArrayList<Schema> schemas = new ArrayList<Schema>();
	private Schema GOTOSTAT = new Schema();
	private Schema GOTOWELL = new Schema();
	private Schema GOTOTASK = new Schema();
	private Schema GOTOFUEL = new Schema();
	private Schema DELIVER = new Schema();
	private Schema FILLUP = new Schema();
	private Schema REFUEL = new Schema();
	private int curr_fuellevel;
	private int curr_pumpdist; 
	private int curr_waterlevel;
	private Plan plan;
	private boolean DebugMode = true;
	private	InputStreamReader in = new InputStreamReader(System.in);
	private BufferedReader br = new BufferedReader(in);
	
	/**
	 * Setsup the predefined schemas
	 * The schemas that will be used are
	 *  GOTOSTAT, GOTOWELL, GOTOFUEL, DELIVER, FILLUP, and REFUEL
	 */
	private void SetupSchemas(){
		final int
			NONE 	  = 0,
			ATSTAT 	  = 1,
			ATWELL 	  = 2,
			ATTASK 	  = 3,
			ATFUEL	  = 4,
			HAVEFUEL  = 5,
			HAVEWATER = 6,
			COMPLETETASK = 7;
			
		//effects
		GOTOSTAT.addEffect(new Goal2(ATSTAT));
		GOTOWELL.addEffect(new Goal2(ATWELL));
		GOTOTASK.addEffect(new Goal2(ATTASK));
		GOTOFUEL.addEffect(new Goal2(ATFUEL));
		DELIVER.addEffect(new Goal2(COMPLETETASK));
		FILLUP.addEffect(new Goal2(HAVEWATER));
		REFUEL.addEffect(new Goal2(HAVEFUEL));
		
		//preconds
		GOTOSTAT.addPreCond(new Goal2(HAVEFUEL));//usually backward chaining will end at havefuel as true. If false, then refuel.
		GOTOWELL.addPreCond(new Goal2(HAVEFUEL));
		GOTOTASK.addPreCond(new Goal2(HAVEFUEL));
		//GOTOFUEL.addPreCond(new Goal2(HAVEFUEL)); z
		DELIVER.addPreCond(new Goal2(HAVEWATER));
		DELIVER.addPreCond(new Goal2(ATTASK));
		FILLUP.addPreCond(new Goal2(ATWELL));
		REFUEL.addPreCond(new Goal2(ATFUEL));
		
		//actions
		//	1 : MoveTowardsAction(test.tanker.getPosition());
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
		
		posXY tanker_coord = test.tanker.getTankerPosition();
		if(DebugMode) System.out.printf("tanker coord is x : %d, y : %d, finalDir : %d\n", tanker_coord.x, tanker_coord.y, finalDir);
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
			//if(DebugMode) System.out.println("X is null!\n");
			System.out.println("X is null!\n");
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
					//System.out.println("loop");
					//System.out.printf("schema goal type : %d, goal type : %d\n", g.getGoalType(), goal.getGoalType() );
					if(g.getGoalType() == goal.getGoalType() && schema == null){//if goal is an effect of a certain scheme.
						
						//found target goal with object in schema
						if(DebugMode) System.out.printf("Goal found. Schema is %s : \n\n", s.getSchemaName());
						schema = s;
						
					}
				}
			}
			
		}
		//System.out.println("out");
		if(schema == null){
			
			System.err.println("goal not found in any schema!");
			return false;//can't find an action that leads to target goal in schemas.
		}
	
		
			Action act = null;
			
			schema.setTarget(X);//sets X onto the action
			if(X instanceof dTask){
				if(((dTask)X).task.isComplete()){
					if(DebugMode) System.out.printf("X is completed task, coord : %s\n", ((dTask)X).task.getStationPosition().toString());
				}else{
					if(DebugMode) System.out.printf("X is not completed task, coord : %s\n", ((dTask)X).task.getStationPosition().toString());
				}
			}
			
			if(DebugMode) System.out.printf("dpos value %d\n", schema.dPos);
			finalDir = schema.dPos;
			act = schema.getAction();
			
			if(act != null){
				
				if(DebugMode) System.out.printf("act %s\n", act.toString());
			}else{
				//System.out.println("act is null");
			}
			
			finalAction = act;
			//System.out.printf("finalAction %s\n", finalAction.toString());
			//System.out.printf("list :%s\n", action_list.toString());
			ArrayList<Goal2> preconds = schema.getPreconds();
			if(preconds.size() == 0){
				
				//System.out.println("no more preconds");
				return false;
			}else{
				
				//System.out.printf("schema %s has nof preconds %d\n", schema.getSchemaName(),preconds.size());
			}
			
			
			for(Goal2 g : preconds){
				if(DebugMode) System.out.printf("g : %d\n", g.getGoalType());
//				NONE 	  = 0,
//				ATSTAT 	  = 1,
//				ATWELL 	  = 2,
//				ATTASK 	  = 3,
//				ATFUEL	  = 4,
//				HAVEFUEL  = 5,
//				HAVEWATER = 6,
//				COMPLETETASK = 7,
//				rEFUEL		 = 8;
				if(g.getGoalType() == 2){
					//gotonearestwell, don't need X. X is only Task
					if(test.ControlSys.getNearestWell() == null){
						
						if(DebugMode) System.out.println("no valid well");
						finalAction = null;
						return false;
					}
					if(!backwardsChaining(g, test.ControlSys.getNearestWell())){
						//System.out.println("backward chaining nearest well stopped");
						return false;
					}else{
						//System.out.println("recursive backwardchaining true");
					}
				}else if(g.getGoalType() == 4){
					if(!backwardsChaining(g, test.ControlSys.getFuelPump())){
						//System.out.println("backward chaining fuel pump stopped");
						return false;
					}else{
						//System.out.println("recursive backwardchaining true");
					}
				}else{
				
					if(!backwardsChaining(g, X)){
						//System.out.println("backward chaining stopped");
						return false;
					}else{
						//System.out.println("recursive backwardchaining true");
					}
				}
			}

			
			return false;//false because first goal wasn't satisfied
			//all backwardchaining recursives returned true
	
	}
	
	/**
	 * Generate Plan generates a plan by creating a new Plan object by the given paramters. A wrapper to the Plan class
	 * @param tasks	tasks received from the Model Layer
	 * @param curr_fuellevel	current fuel level
	 * @param curr_pumpdist		current distance to pump
	 * @param curr_waterlevel	current water level
	 * @return
	 */
	private Plan GeneratePlan(ArrayList<dTask> tasks, int curr_fuellevel, int curr_pumpdist, int curr_waterlevel){
		Plan plan = new Plan(tasks, curr_fuellevel, curr_pumpdist, curr_waterlevel);

		if(!(plan.getPlan() == null)){
			if(!plan.getPlan().isEmpty()){
				System.out.printf("generated plan : %s, pipeline size : %d\n", plan.printPipeLine(), plan.task_pipeline.size());
				return plan;
			}
		}
		return null;
	}
	
	private float scoreDiff(Plan new_plan, Plan old_plan){
		int plan1 = new_plan.getPlanScore();
		int plan2 = old_plan.getPlanScore();
		System.out.printf("old score : %d, new score : %d\n", plan2, plan1);
		float result = 0;
		result = ((float) plan1 - (float) plan2)/((float) plan1+(float) plan2);
		if(DebugMode)	System.out.printf("scorediff : %f\n", result);
		
		return result;
	}
	/**
	 * @deprecated	no longer used as distance will not be measured in the final measure
	 * @param new_plan	The new plan generated
	 * @param old_plan	Current plan
	 * @return	A value that shows correlation between two plans in reagards to distance
	 */
	private float distDiff(Plan new_plan, Plan old_plan){
		int plan1 = new_plan.getTotalDist();
		int plan2 = old_plan.getTotalDist();
		System.out.printf("old dist : %d, new dist : %d\n", plan2, plan1);
		float result = 0;
		
		result = ((float) plan2 - (float) plan1)/((float) plan1+(float) plan2);
		if(DebugMode)	System.out.printf("distdiff : %f\n", result);
		
		return result;
	}
	
	/**
	 * "deprecated	no longer used as only Score measure is used for comparison
	 * @param scorediff
	 * @param distdiff
	 * @return
	 */
	private float measureScore(float scorediff, float distdiff){
		float result = 0;
		result = (scorediff*distdiff)/(scorediff+distdiff);
		
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
		ArrayList<dTask> tasks = test.ControlSys.getTasks();
		curr_fuellevel = test.tanker.getFuelLevel();
		curr_pumpdist = test.tanker.getPumpDist();
		curr_waterlevel = test.tanker.getWaterLevel();
		
		if(test.ControlSys.getNearestWell() != null){
			Plan temp_plan = GeneratePlan(tasks, curr_fuellevel, curr_pumpdist, curr_waterlevel);
			//System.out.println("found well. generating plan");
			
			if(plan != null){
				if(plan.CompareDist(temp_plan)){
					if(!plan.isSame(temp_plan)){
						System.out.printf("plan dist : %d, temp_plan dist : %d\n", plan.EstimatedtotalDist, temp_plan.EstimatedtotalDist);
						float score_diff = scoreDiff(temp_plan, this.plan);
						//TODO: cannot use this yet because calcATask is not accurate
						//float dist_diff = distDiff(temp_plan, this.plan);
						//float measure = measureScore(score_diff, dist_diff);
						//System.out.printf("scorediff : %f, distdiff : %f, measure : %f\n",score_diff, dist_diff, measure);
						if(score_diff > 0.3){
							System.out.printf("curr plan : %s", plan.printPipeLine() );
							System.out.printf("temp plan : %s", temp_plan.printPipeLine() );
							System.out.println("change of plan");
							plan = temp_plan;
						}
//						try {
//							br.readLine();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
					}

				}else{
					System.out.printf("plan dist : %d, temp_plan dist : %d\n", plan.EstimatedtotalDist, temp_plan.EstimatedtotalDist);
					System.out.println("remain same plan");

				}
				//System.out.println("has a plan");
			}else{
				//System.out.println("no plan exist");
				plan = temp_plan;
			}
		}else{
			System.out.println("no well thats why no generate plan");
		}
		
	}
	
	/**
	 * Executing a plan relates to backchaining each goal in the plan. If the goal is achieved, the goal will be removed from the plan
	 * @param plan
	 * @return	true when the plan is done
	 */
	 // generate goal to backchain with
	 private boolean executePlan(Plan plan){
	  		Goal2 task_goal = new Goal2(7);
	  		int step = 0;
	  
	  		//get objects
	  		if(plan == null)	return false;
	  		LinkedList<dTask>goals = plan.getPlan();
	  		System.out.printf("plan size : %d\n", goals.size());
	  		System.out.printf("chaining %s\n", goals.getFirst().toString());

	  		if(backwardsChaining(task_goal, goals.getFirst()) ){
	  			
	  			//System.out.println("finished one goal of the plan. moving to next");
	  			if(DebugMode) System.out.println("removing goal");
	  			plan.RemoveGoal(goals.getFirst(), test.tanker.getFuelLevel(), test.tanker.pump_dist, test.tanker.getWaterLevel());
	  			if(goals.size() == 0){
	  				//plan.updatePlan(dTask goal);
	  				//last goal in the plan completed, exiting
	  				System.out.println("finish plan. removing plan and action");
	  				finalAction = null;
	  				this.plan = null;
	  				return true;
	  			}
	 			//this is just to move on to the next goal, if next goal is completed, step up straight and move to next
	 			//have to break if not complete so controlsystem can run the set action
	  			while(goals.size() > 0){
	  				
	  				//System.out.println("bc twice");
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
	  			//shouldnt be reached.
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
		//suppose to do a plan compare
		
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
				
				System.out.println("no action from planning layer");
				return null;
			}
		
		return finalAction;
	}
}
