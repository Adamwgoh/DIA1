import java.beans.FeatureDescriptor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import uk.ac.nott.cs.g54dia.library.*;


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
	 
	private static ReactiveLayer RLayer;
	private static PlanningLayer PLayer;
	private static database Model;//only one having access to it.
	private Action final_action = null;
	private Action final_Paction = null;
	private Action final_Raction = null;
	private int final_dir = 8;
	
	//Reactive censoring booleans. Separated out for Arbitration
	protected boolean ReactiveSeesWater = true;
	protected boolean ReactiveSeesFuel  = true;
	protected boolean ReactiveSeesPosition = true;
	protected boolean ReactiveSeesWell = true;
	protected boolean ReactiveSeesBTask = true;
	
	protected boolean PlanningSeesWater = true;
	protected boolean PlanningSeesFuel  = true;
	protected boolean PlanningSeesPosition = true;
	protected boolean PlanningSeesWell = true;
	protected boolean PlanningSeesBTask = true;
	protected boolean PlanningSeesNTask = true;
	protected boolean PlanningSeesFuelPump = true;
	protected boolean canReactivePlanningAdd = false;
	
	//constructor
	public ControlSystem(){
		RLayer = new ReactiveLayer();
		PLayer = new PlanningLayer();
		Model  = new database(test.tanker);
	}
	
	//Lots of lots of lots of percept-censoring wraps
	//Because I've privatized the Model Layer, now I have to wrap the public methods out.
	//Not all percepts are censored, some adding are needed to by the tanker to modify the Model
	public boolean haveWater(int amount){
		if(!ReactiveSeesWater){
			return false;
		}else{
			return test.tanker.haveWater(amount);
		}
	}
	
	public boolean haveFuel(int amount){
		if(!ReactiveSeesFuel){
			
			return false;
		}else{
			
			return test.tanker.haveFuel(amount);
		}
	}
	
	public boolean At(FuelPump pump){
		
		if(test.tanker.getTankerPosition().x == 0 && test.tanker.getTankerPosition().y == 0){
			
			return true;
		}
		
		return false;
	}
	public boolean AtPump(){
		
		return test.tanker.AtPump();
	}
	public boolean At(posXY pos){
		
		return test.tanker.At(pos);
	}
	
	public boolean At(dWell well){
		if(well != null){
		
			posXY w = well.relPump_pos;
			int dx = w.x - test.tanker.getTankerPosition().x;
			int dy = w.y - test.tanker.getTankerPosition().y;
			//System.out.printf("well reltankdist : %d\n", Math.max(Math.abs(dx), Math.abs(dy)));	
			if(Math.max(Math.abs(dx), Math.abs(dy)) == 0)	return true;
		}
		
		return false;
	}
	
	public boolean At(dStation station){
		
		if(station != null){
			posXY st = station.relPump_pos;
			int dx = st.x - test.tanker.getTankerPosition().x;
			int dy = st.y - test.tanker.getTankerPosition().y;
			
			if(Math.max(Math.abs(dx), Math.abs(dy)) == 0)	return true;
		}
		
		return false;
	}
	
	public boolean At(dTask task){
		
		if(task != null){
			posXY t = task.relPump_pos;
			//System.out.printf("tasK pos X : %d, Y : %d\n", t.x, t.y);
			int dx = t.x - test.tanker.getTankerPosition().x;
			int dy = t.y - test.tanker.getTankerPosition().y;
			
			if(Math.max(Math.abs(dx), Math.abs(dy)) == 0)	return true;
		}
		
		return false;
	}
	
	public ArrayList<dWell> getWells(){	return Model.getWells();	}
	
	public ArrayList<dTask> getTasks(){	
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
	
	/**
	 * gets nearest task from a specific position within reach from pump.
	 * Dont think this was used in the final implementation
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
	
	public ArrayList<dStation> getStation(){	return Model.getStations();	}
	public boolean AtWell(){	return Model.AtWell();	}
	public int getDirection(){	return final_dir;	}

	public posXY getTankerPosition(){
		if(!PlanningSeesPosition){	return null;	}
		if(!ReactiveSeesPosition){
			return null;
		}else{
			return test.tanker.getTankerPosition();
		}
	}
	
	/**
	 * returns the biggest task. I think this was never used in the final implementation.
	 * A clear example of censoring through the Control System. Tasks beyond the range of the tanker
	 * and task more than the current water level is censored.
	 * @return	the biggest task explored
	 */
	public dTask getBiggestTask(){
		dTask task = Model.getBiggestTask();
		//if do not have enough water to fufill task, censor it
		if(task != null){
			if(test.tanker.getWaterLevel() < task.water_req || task.pump_dist > 49){

				PlanningSeesBTask = false;
			}else{
				System.out.println("restart");
				PlanningSeesBTask = true;
			}
		}
		
		if(!PlanningSeesBTask){	
			System.out.println("biggest task blinded from planning");
			return null;
			
		}
		
		if(!ReactiveSeesBTask){

			System.err.printf("Not allowed access!\n");
			return null;
		}else{			
			
			return task;
		}
	}	
	
	public dTask getBiggestTask(int waterlevel){
		
		if(!PlanningSeesBTask){	return null;	}
		if(!ReactiveSeesBTask){

			System.err.printf("Not allowed access!\n");
			
			return null;
		}else{			
			
			return Model.getBiggestTask(waterlevel);
		}
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
			if(test.tanker.getWaterLevel() < task.water_req || task.pump_dist > 49){
				System.out.println("nearest task blinded from planning");
				PlanningSeesNTask = false;
			}else{
				PlanningSeesNTask = true;
			}
		}else{

			System.out.println("no nearest task found.\n");
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
			System.out.println("no see well (CS:269)");
			
			PlanningSeesWell = false;
			if(Model.getNearestPWell() != null){
				
				if(well.pump_dist < 49){

					System.out.println("found another well");
//					try {
//						br.readLine();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					return Model.getNearestPWell();
				}else{
					
					System.out.println("no alternative. Doomed");
//					try {
//						br.readLine();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				}
			}
		}else{	
			PlanningSeesWell = true;
		}
			
		if(!PlanningSeesWell){
			return null;
		}else{
			
			if(!ReactiveSeesWell){
				
				System.err.printf("Not allowed access!\n");
				return null;
			}
			return well;
		}

	}
	
	public void addWell(Cell well,int x, int y, SuperTanker tanker){
	
	//	if(!canReactivePlanningAdd){ }else{	this doesn't make sense
			
			Model.addWell(well, x, y, tanker);
	//	}
	}
	
	public void addStation(Cell st,int x, int y, SuperTanker tanker){
		
	//	if(!canReactivePlanningAdd){ }else{
			
			Model.addStation(st, x, y, tanker);
	//	}
	}
	
	public void addTask(Task task,int x, int y, SuperTanker tanker){
		
	//	if(!canReactivePlanningAdd){ }else{
			
			Model.addTask(task, x, y, tanker);
	//	}
	}
	
	public void addFuelPump(Cell pump){
		if(pump instanceof FuelPump) 	Model.addFuelPump((FuelPump) pump);
	}
	
	public FuelPump getFuelPump(){		return Model.getFuelPump();	}
	
	//
	//	END OF PERCEPT-CENSORING WRAPS
	//
	/**
	 * Control System chooses output between reactive layer and deliberative layer here. This is where Control System is able
	 * to choose which action between Reactive Layer and Deliberative Layer as the final output. Once chosen, the Model Layer
	 * will be updated with the action's movement direction. 
	 * @return
	 */
	public Action getOutput(){
		
		//surpressors goes here
		final_Paction = ControlSystem.PLayer.getOutput();
		final_Raction = ControlSystem.RLayer.getOutput();
		
		//Control rule where reactive is prioritized is implemented here. This is to make sure tanker has enough fuel to return to fuel pump
		if(Math.max(Math.abs(test.tanker.getTankerPosition().x), Math.abs(test.tanker.getTankerPosition().y) )+1 >= test.tanker.getFuelLevel() 
				&& test.tanker.getFuelLevel() != test.tanker.MAX_FUEL){
			
			System.out.printf("only %d left. ",Math.max(Math.abs(test.tanker.getTankerPosition().x), Math.abs(test.tanker.getTankerPosition().y) ));
			System.out.println("control rule take over. choosing reactive over planning");
			final_action = final_Raction;
			if(final_action instanceof MoveTowardsAction){
				
				posXY target = ControlSystem.RLayer.getTargetCoord();
				final_dir = ConvertPosToDir(target);
				//System.out.println("movetowardsaction dbtick");
				Model.dbTick(target);		
		
			}else if(final_action instanceof MoveAction){
				
				//System.out.println("moveaction dbtick");
				final_dir = ControlSystem.RLayer.getDirection();
				System.out.printf("final_dir : %d\n", final_dir);
				Model.dbTick(final_dir);
			}
		}else if(final_Paction != null){
			
			posXY target = ControlSystem.PLayer.getTargetCoord();
			System.out.printf("target x : %d, target y : %d\n", target.x, target.y );
			final_dir = ConvertPosToDir(target);
			Model.dbTick(target);
			final_action = final_Paction;
			
		}else if(final_Raction != null){
			
			if(final_Raction instanceof MoveTowardsAction){
				
				posXY target = ControlSystem.RLayer.getTargetCoord();
				final_dir = ConvertPosToDir(target);
				System.out.println("movetowardsaction dbtick");
				Model.dbTick(target);		
		
			}else if(final_Raction instanceof MoveAction){
				//System.out.println("moveaction dbtick");
				final_dir = ControlSystem.RLayer.getDirection();
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
		if( (final_action instanceof RefuelAction && test.tanker.getFuelLevel() == test.tanker.MAX_FUEL) || 
				(final_action instanceof LoadWaterAction && test.tanker.getWaterLevel() == test.tanker.MAX_WATER)){
			final_action = null;
		}
		
		return final_action;
	}
	
	
	/**
	 * Conversion between position and direction for ease of use
	 */
	private int ConvertPosToDir(posXY pos){
		int dir = 8;
		int dx = pos.x - test.tanker.getTankerPosition().x;
		int dy = pos.y - test.tanker.getTankerPosition().y;
		
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
			else if(dy == 0) dir = 8;//INFINININININININTY
		}
	
		return dir;
	}
}
