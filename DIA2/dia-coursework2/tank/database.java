package tank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import src.test;
import uk.ac.nott.cs.g54dia.multilibrary.*;

/**
 * Database class stores information gathered from the GUI.
 * This is the modelling layer of the hybrid architecture
 * Raw information / percepts taken from the Tanker are modelled here into each their own classes without extension
 * Without extension ensures there could be no direct access to any of the environment's information, except through the Modelling Layer
 * 
 * The Model Layer receives from the environment the tanker's coordinate, the object (Well/Station) coordinates, and calculates its absolute
 * position to the fuelpump, as well as distances to the fuel pump and the tank
 * 
 * The Model Layer keeps a list of the objects it had modelled and able to be ping from timestep to timestep for Tasks if 
 * there are no more tasks available/ seen in the map.
 * 
 * The Model Layer is controlled by the Control System in a hierachical approach(maintained by the control system),
 * but are mainly used by the Planning Layer and Reactive Layer
 * 
 * @author awg04u
 *
 */
public class database {
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
	 
	/**
	 * An array of task list.
	 */
	private ArrayList<dTask> task_list = new ArrayList<dTask>();
	private ArrayList<dStation> station_list = new ArrayList<dStation>();
	private ArrayList<dWell> well_list = new ArrayList<dWell>();
	private ArrayList<SuperTanker> tanker_list = new ArrayList<SuperTanker>();
	private SuperTanker tanker;
	private FuelPump pump;

	//constructor
	public database(SuperTanker t){

		tanker = t;
	}
	
	public ArrayList<dWell> getWells(){					return well_list;	}
	public ArrayList<dTask> getTasks(){					return task_list;	}
	public ArrayList<dStation> getStations(){			return station_list;	}
	public boolean adddWell(dWell well){				return well_list.add(well);}
	public boolean adddTask(dTask task){				return task_list.add(task);}
	public boolean addTanker(SuperTanker tanker){		return tanker_list.add(tanker);	}
	public boolean removeTanker(SuperTanker tanker){	return tanker_list.remove(tanker);	}
	public ArrayList<SuperTanker> getTankers(){			return tanker_list;		}
	public FuelPump getFuelPump(){						return pump;	}
	public void addFuelPump(FuelPump pump){				this.pump = pump;	}
	
	public void updateTasks(){
		for(int i = 0; i < task_list.size(); i++){
			if(task_list.get(i).isTaskComplete()){
				task_list.remove(i);
			}
		}
	}
	
	
	/**
	 * Get nearest task to the tanker.
	 * @return	shortest distance task.
	 */
	public dTask getNearestTask(){
		
		dTask n_task = null;
		int dist = 50;
		if(task_list.isEmpty()){
			return null;
		}
		
		//find for the task nearest to the tanker now and return it
		for(dTask t : task_list){
			posXY task_coord = t.relPump_pos;
			if(dist > Math.max(Math.abs(task_coord.x), Math.abs(task_coord.y)) ){
				
				dist = Math.max(Math.abs(task_coord.x), Math.abs(task_coord.y));
				n_task = t;
			}
		}
		
		return n_task;
	}
	
	/**
	 * Get nearest task to the tanker under the given water requirement
	 * @param waterlevel maximum water required for the task
	 * @return	shortest dist task with a maximum water requirement level
	 */
	public dTask getNearestTask(int waterlevel){
		
		dTask n_task = null;
		int dist = 0;
		
		//find for the task nearest to the tanker now and return it
		for(dTask t : task_list){
			posXY task_coord = t.relPump_pos;
			if(dist > Math.max(Math.abs(task_coord.x), Math.abs(task_coord.y)) ){
				if(t.getWaterReq() <= waterlevel){
					n_task = t;
					dist = Math.max(Math.abs(task_coord.x), Math.abs(task_coord.y));
				}
			}
		}//end task_list loop
		
		return n_task;
	}

	public void printTasks(){
		
		System.out.println("tanker tasks : ");
		if(task_list.isEmpty()){	System.out.println("(Empty)"); 	}
		for(dTask task : task_list){
			System.out.printf("%s", task.toString());
		}
		System.out.println(" ");
	}
	
	/**
	 * Removes any task that conflicts with the state's task list.
	 * This is used in Multi-agent system to prevent agents from having conflicting tasks
	 * @param task	Task to be checked for conflict in the tanker's Model Layer
	 */
	public void removeConflict(dTask task){
		for(int i = 0; i < task_list.size(); i++){
			if(task_list.get(i).task.equals(task.task)){
				task_list.remove(task_list.get(i));
			}
		}

	}
	
	/**
	 * gets the nearest well from the tanker's current position
	 * @return	A custom class well that is nearest to the tanker's current position. Null if it doesnt exist.
	 */
	public dWell getNearestWell(){
		
		dWell n_well = null;
		int dist = 10000000;
		
		//find for the task nearest to the tanker now and return it
		for(dWell w : well_list){
			posXY well_coord = new posXY(this.tanker.getTankerPosition().x-w.getRelPumpRos().x, 
					this.tanker.getTankerPosition().y-w.getRelPumpRos().y);
			
			if(dist > Math.max(Math.abs(well_coord.x), Math.abs(well_coord.y)) ){
				n_well = w;
				dist = Math.max(Math.abs(well_coord.x), Math.abs(well_coord.y));
			}
		}
		
		return n_well;
	}
	
	public dWell getNearestPWell(){
		dWell n_well = null;
		int dist = 1000000000;
			//no nearest tanker well, look for for nearest well to pump and try going there	
			for(dWell w : well_list){
				if(dist > w.getRelPumpDist()){
					n_well = w;
					dist = w.getRelPumpDist();
				}
			}
			
		return n_well;
	}
	
	
	/**
	 * Database update. Database must be updated before the local tanker's position is updated, or there will be mismatched.
	 * For each timestep it sees which direction does it go, and updates the database accordingly. MoveAction or MoveTargetAction
	 * does not matter as dbTick accepts both direction and target position
	 * 
	 * @param dir	the direction of the movement step
	 */
	public void dbTick(int dir){
		//System.out.println("updating database..... \n");
		posXY tanker_dpos;
		switch(dir){
			case NORTH 		: tanker_dpos = new posXY(0,  1);break;
			case NORTHEAST  : tanker_dpos = new posXY(1,  1);break;
			case NORTHWEST  : tanker_dpos = new posXY(-1, 1);break;
			case SOUTH 		: tanker_dpos = new posXY(0, -1);break;
			case SOUTHEAST  : tanker_dpos = new posXY(1, -1);break;
			case SOUTHWEST  : tanker_dpos = new posXY(-1,-1);break;
			case WEST  		: tanker_dpos = new posXY(-1, 0);break;
			case EAST  		: tanker_dpos = new posXY(1,  0);break;
			default : tanker_dpos = new posXY(0,0);
		
		}
		
		for(int k = 0; k < task_list.size(); k++){
			dTask task = task_list.get(k);
			task.water_req = task.task.getRequired();
			
			if(task.task.isComplete()){
				task_list.remove(k);		
			}
			task.updatePosition(tanker_dpos);
		}
		
		for(int k = 0; k < station_list.size(); k++){
			dStation station = station_list.get(k);
			station.updatePosition(tanker_dpos);
		}
		
		for(int k = 0; k < well_list.size(); k++){
			dWell well = well_list.get(k);
			well.updatePosition(tanker_dpos);
		}
		
		for(int k = 0; k < task_list.size(); k++){
			dTask task = task_list.get(k);
			task.updatePosition(tanker_dpos);
		}
	}//end dbTick(int dir)
	
	/**
	 * Database update. Database must be updated before the local tanker's position is updated, or there will be mismatched.
	 * For each timestep it sees which direction does it go, and updates the database accordingly. MoveAction or MoveTargetAction
	 * does not matter as dbTick accepts both direction and target position
	 * 
	 * @param pos	the target location
	 */
	public void dbTick(posXY pos){
		posXY tanker_dpos = new posXY(0,0);
		
		int dx = pos.x - this.tanker.getTankerPosition().x;
		int dy = pos.y - this.tanker.getTankerPosition().y;
		
		if(dx < 0){
			tanker_dpos.x = -1;
		}else if(dx > 0){
			tanker_dpos.x = 1;
		}
		if(dy < 0){
			tanker_dpos.y = -1;
		}else if(dy > 0){
			tanker_dpos.y = +1;
		}
		
		//update task_list
		for(int k = 0; k < task_list.size(); k++){
			dTask task = task_list.get(k);
			task.water_req = task.task.getRequired();
			if(task.task.isComplete()){		task_list.remove(k);	}
			
			task.updatePosition(tanker_dpos);
		}
		
		//update station_list
		for(int k = 0; k < station_list.size(); k++){
			dStation station = station_list.get(k);
			station.updatePosition(tanker_dpos);
		}
		
		//update_well_list
		for(int k = 0; k < well_list.size(); k++){
			dWell well = well_list.get(k);
			well.updatePosition(tanker_dpos);
		}
		
		//update tasks position in task_list
		for(int k = 0; k < task_list.size(); k++){
			dTask task = task_list.get(k);
			task.updatePosition(tanker_dpos);
		}

	}//end dbTick(posXY target)
	
	/**
	 * Adds station to tanker's Model Layer
	 * @param st	Cell object
	 * @param x		Coordinate X
	 * @param y		Coordinate Y
	 * @param tanker Tanker the Model Layer belongs to
	 */
	public void addStation(Cell st,int x, int y, SuperTanker tanker){
		dStation temp = new dStation(st, x, y, tanker);

		if(station_list.isEmpty()){
			
			station_list.add(temp);
		}else{
			
			for(dStation stat : station_list) {
				if(stat.station.equals((Station) st)){	return;	}
			}
			station_list.add(temp);	
		}
		
	}
	
	/**
	 * Adds Task to the tanker's Model Layer
	 * @param task	task to be added
	 * @param x		Coordinate X
	 * @param y		Coordinate Y
	 * @param tanker Tanker the Model Layer belongs to
	 */
	public void addTask(Task task,int x, int y, SuperTanker tanker){
		dTask temp = new dTask(task, x, y, tanker);
		
		if(task_list.isEmpty()){
			
			task_list.add(temp);
		}else{
			for(dTask stat : task_list) {
				if(stat.task.equals((Task) task)){	return;	}
			}
			task_list.add(temp);
		}
	}//end addTask()
	
	/**
	 * Adds Well to the tanker's Model Layer
	 * @param well	well to be added
	 * @param x		Coordinate X
	 * @param y		Coordinate Y
	 * @param tanker Tanker the Model Layer belongs to
	 */
	public void addWell(Cell well,int x, int y, SuperTanker tanker){
		dWell temp = new dWell(well, x, y, tanker);
		
		if(well_list.isEmpty()){
			
			well_list.add(temp);
		}else{
			
			for(dWell stat : well_list) {
				if(stat.well.equals((Well) well)){	return;	}
			}
			well_list.add(temp);		
		}
	}//end addWell
	
}//end database class
