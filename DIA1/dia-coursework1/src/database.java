import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import uk.ac.nott.cs.g54dia.library.*;

final class posXY{
	int x;
	int y;
	//posXY prevpos; do you need this?
	
	posXY(int x, int y){
		this.x = x;
		this.y = y;
	}
}

//data types
/**
 * A custom Task class with calculated absolute position, relative to tank position, as well as functions that
 * update its relative tank position.
 * @author awg04u
 *
 */
final class dTask{
	protected posXY relTank_pos;//position relative to Tanker
	protected posXY relPump_pos;
	protected int pump_dist;
	protected int tank_dist;
	protected int water_req;
	
	protected Task task;
	
	public dTask(Task t, int indx, int indy, SuperTanker tanker){
			//safe net for fun
			if(t instanceof Task){
				task = (Task) t;
				water_req = task.getWaterDemand();
			}
			
		calculatePos(indx, indy, tanker);
		
	}//end constructoe
	
	/**
	 * Sets position based on given Cell array coord
	 */
	private void calculatePos(int indx, int indy, SuperTanker tanker ){
		
		int i2, j2;
		int i3, j3;
		
		//get origin coord
		i3 = (indx - SuperTanker.VIEW_RANGE + tanker.getTankerPosition().x);
		j3 = - (indy - SuperTanker.VIEW_RANGE - tanker.getTankerPosition().y);
		
		//get tanker_relative coord
		i2 = (indx - 12);
		j2 = - (indy - 12);
		//System.out.printf("in addTask. tanker posX : %d, posY : %d\n", tanker.getTankerPosition().x, tanker.getTankerPosition().y);
		//System.out.printf("indxy : (%d,%d), i2j2 : (%d,%d), i3j3 : (%d,%d)\n", indx, indy, i2, j2, i3, j3);
		relPump_pos = new posXY(i3,j3);
		relTank_pos = new posXY(i2,j2);
		tank_dist = Math.max(Math.abs(relTank_pos.x), Math.abs(relTank_pos.x));
		//System.out.printf("tank_dist : %d", tank_dist);
		pump_dist = Math.max(Math.abs(relPump_pos.x), Math.abs(relPump_pos.y));

	}
	
	/**
	 * Updates its relative to tanker position by receiving the most recent tanker position
	 * @param tankerdPos	Tanker's most recent position
	 */
	public void updatePosition(posXY tankerdPos){
		setRelTankPos(new posXY(relTank_pos.x + tankerdPos.x, relTank_pos.y + tankerdPos.y));
		tank_dist = Math.max(Math.abs(relTank_pos.x), Math.abs(relTank_pos.x));
	}
	
	public posXY getRelTankPos(){	return relTank_pos; }
	public posXY getRelPumpRos(){	return relPump_pos; }
	public int getRelTankDist(){	return tank_dist;	}
	public int getRelPumpDist(){	return pump_dist;	}
	public int getWaterReq(){	 	return water_req;	}
	
	public void setRelTankPos(posXY pos){
		relTank_pos = pos;
		
		tank_dist = Math.max(Math.abs(relTank_pos.x), Math.abs(relTank_pos.x));
	}
	
	public String toString(){	return task.getStationPosition().toString();}
	


}

/**
 * A custom Station class with calculated absolute position, relative to tank position, as well as functions that
 * update its relative tank position.
 * @author awg04u
 *
 */
final class dStation{
	protected posXY relTank_pos;//tanker relative coord
	protected posXY relPump_pos;//origin coord
	protected int pump_dist; //distance to pump
	protected int tank_dist;
	protected Station station;
//	protected int well_dist; //distance to nearest well, 
	// may include the above if your planner goes through this on the way
	
	//constructor
	dStation(Cell vstation, int indx, int indy, SuperTanker tanker){
		
		//safe net for fun
		if(vstation instanceof Station){
			station = (Station) vstation;
			/**
			 * Gets origin coordinates(to the pump) and tanker coordinates(relative to Tanker position)
			 */
			int i2, j2;
			int i3, j3;

			//get origin coord
			i3 = (indx - SuperTanker.VIEW_RANGE + tanker.getTankerPosition().x);
			j3 = - (indy - SuperTanker.VIEW_RANGE - tanker.getTankerPosition().y);
			
			//get tanker_relative coord
			i2 = (indx - 12);
			j2 = - (indy - 12);
			relPump_pos = new posXY(i3,j3);
			relTank_pos = new posXY(i2,j2);
			
			//get distance to pump and tank
			//System.out.printf("%s, i3 %d,j3 %d \n", station.getPoint().toString(), i3, j3 );
			//System.out.printf("reltank_pos X : %d, Y : %d", Math.abs(relTank_pos.x), Math.abs(relTank_pos.y));
			tank_dist = Math.max(Math.abs(relTank_pos.x), Math.abs(relTank_pos.x));
			//System.out.printf("tank_dist : %d", tank_dist);
			pump_dist = Math.max(Math.abs(relPump_pos.x), Math.abs(relPump_pos.y));
		}
	}//end constructor
	
	
	/**
	 * Sets position based on given Cell array coord
	 */
	public void setPosition(int indx, int indy, SuperTanker tanker ){
		int i2, j2;
		int i3, j3;
		
		//get origin coord
		i3 = (indx - SuperTanker.VIEW_RANGE + tanker.getTankerPosition().x);
		j3 = - (indy - SuperTanker.VIEW_RANGE - tanker.getTankerPosition().y);
		
		//get tanker_relative coord
		i2 = (indx - 12);
		j2 = - (indy - 12);
		
		setRelTankPos(new posXY(i2,j2));	setRelPumpPos(new posXY(i3, j3));
	}
	
	public void updatePosition(posXY tankerdPos){
		setRelTankPos(new posXY(relTank_pos.x + tankerdPos.x, relTank_pos.y + tankerdPos.y));
		tank_dist = Math.max(Math.abs(relTank_pos.x), Math.abs(relTank_pos.x));
	}
	
	public posXY getRelTankPos(){	return relTank_pos; }
	public posXY getRelPumpRos(){	return relPump_pos; }
	public int getRelTankDist(){	return tank_dist;	}
	public int getRelPumpDist(){	return pump_dist;	}
	
	/**
	 * Set coord relative to the tanker
	 * @param pos
	 */
	public void setRelTankPos(posXY pos){
		relTank_pos = pos;
		
		tank_dist = Math.max(relTank_pos.x, relTank_pos.y);
	}
	
	/**
	 * Set coord relative to the pump/ origin
	 * @param pos
	 */
	public void setRelPumpPos(posXY pos){
		relPump_pos.x = pos.x;
		relPump_pos.y = pos.y;
		
		pump_dist = Math.max(relPump_pos.x, relPump_pos.y);
	}
	
	//HACK : May not be correct. Need to check
	public boolean HasTask(Task task){
		return station.getTask().equals(task);
	}
	
	
}

/**
 * A custom Station class with calculated absolute position, relative to tank position, as well as functions that
 * update its relative tank position.
 * @author awg04u
 *
 */
final class dWell{
	protected posXY relTank_pos;//tanker relative coord
	protected posXY relPump_pos;//origin coord
	protected int pump_dist; //distance to pump
	protected int tank_dist;
	protected Well well;
	
	//constructor
	dWell(Cell vWell, int indx, int indy, SuperTanker tanker){
		//safe net for fun
		if(vWell instanceof Well){
			well = (Well) vWell;
			/**
			 * Gets origin coordinates(to the pump) and tanker coordinates(relative to Tanker position)
			 */
			int i2, j2;
			int i3, j3;

			//get origin coord
			i3 = (indx - SuperTanker.VIEW_RANGE + tanker.getTankerPosition().x);
			j3 = - (indy - SuperTanker.VIEW_RANGE - tanker.getTankerPosition().y);
			
			//get tanker_relative coord
			i2 = (indx - 12);
			j2 = - (indy - 12);
			relPump_pos = new posXY(i3,j3);
			relTank_pos = new posXY(i2,j2);
			//System.out.printf("%s, i3 %d,j3 %d \n", well.getPoint().toString(), i3, j3 );
			//get distance to pump and tank
			tank_dist = Math.max(Math.abs(relTank_pos.x), Math.abs(relTank_pos.x));
			pump_dist = Math.max(Math.abs(relPump_pos.x), Math.abs(relPump_pos.y));
		}
	}//end constructor
	
	public void updatePosition(posXY tankerdPos){
		setRelTankPos(new posXY(relTank_pos.x + tankerdPos.x, relTank_pos.y  + tankerdPos.y));
		tank_dist = Math.max(Math.abs(relTank_pos.x), Math.abs(relTank_pos.x));
	}
	
	/**
	 * Sets position based on given Cell array coord
	 */
	public void setPosition(int indx, int indy, SuperTanker tanker ){
		
		int i2, j2;
		int i3, j3;
		
		//get origin coord
		i3 = (indx - SuperTanker.VIEW_RANGE + tanker.getTankerPosition().x);
		j3 = - (indy - SuperTanker.VIEW_RANGE + tanker.getTankerPosition().y);
		
		//get tanker_relative coord
		i2 = (indx - 12);
		j2 = - (indy - 12);
		
		setRelTankPos(new posXY(i2,j2));	setRelPumpPos(new posXY(i3, j3));
	}
	
	/**
	 * Set coord relative to the tanker
	 * @param pos
	 */
	private void setRelTankPos(posXY pos){
		
		relTank_pos.x = pos.x;
		relTank_pos.y = pos.y;
		
		tank_dist = Math.max(relTank_pos.x, relTank_pos.y);
	}
	
	public posXY getRelTankPos(){	return relTank_pos; }
	public posXY getRelPumpRos(){	return relPump_pos; }
	public int getRelTankDist(){	return tank_dist;	}
	public int getRelPumpDist(){	return pump_dist;	}
	
	/**
	 * Set coord relative to the pump/ origin
	 * @param pos
	 */
	private void setRelPumpPos(posXY pos){
		
		relPump_pos.x = pos.x;
		relPump_pos.y = pos.y;
		
		pump_dist = Math.max(relPump_pos.x, relPump_pos.y);
	}
	
}




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
	private posXY tanker_pos;
	private SuperTanker tanker;
	private FuelPump pump;
//	private Iterator<dTask> taskIt = task_list.iterator();
//	private Iterator<dStation> stationIt = station_list.iterator();
//	private Iterator<dWell> wellIt = well_list.iterator();
	//constructor
	public database(SuperTanker t){

		tanker = t;
		tanker_pos = t.getTankerPosition();
	}
	
	public boolean AtWell(){
		for(dWell well : well_list){
			if(well.getRelTankDist() == 0){
				return true;
			}
		}
		return false;
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
		}
		
		return n_task;
	}
	
	/**
	 * Get the task with the biggest reward
	 * @return	task with the biggest reward
	 */
	public dTask getBiggestTask(){
		
		dTask n_task = null;
		int water_req = 100000000;
		
		//find for the task nearest to the tanker now and return it
		for(dTask t : task_list){
			if(water_req > t.water_req){

				water_req = t.water_req;
				n_task = t;
			}
		}
		
		return n_task;
	}
	
	/**
	 * Get the task with the biggest reward under a given water level
	 * @param waterlevel
	 * @return	returns a the biggest reward task that requires at maximum, the given water level
	 */
	public dTask getBiggestTask(int waterlevel){
		
		dTask n_task = null;
		int water_req = 100000000;
		
		//find for the task nearest to the tanker now and return it
		for(dTask t : task_list){
			if(water_req > t.water_req){
				if(t.getWaterReq() <= waterlevel){
					water_req = t.water_req;
					n_task = t;
				}
			}
		}
		
		return n_task;
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
			posXY well_coord = new posXY(test.tanker.getTankerPosition().x-w.getRelPumpRos().x, 
					test.tanker.getTankerPosition().y-w.getRelPumpRos().y);
			
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
	
	public ArrayList<dWell> getWells(){
		
		return well_list;
	}
	
	public ArrayList<dTask> getTasks(){
		
		return task_list;
	}
	
	public ArrayList<dStation> getStations(){
		
		return station_list;
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
		
		//System.out.printf("dcoods (%d,%d)\n", tanker_dpos.x, tanker_dpos.y);
		//int i = 1;
		
		for(int k = 0; k < task_list.size(); k++){
			dTask task = task_list.get(k);
			task.water_req = task.task.getRequired();
			System.out.printf("Task %d, coord : (%d, %d)\n", k, task.relPump_pos.x, task.relPump_pos.y);
			if(task.task.isComplete()){
				System.out.println("removing task");
				task_list.remove(k);
//				try {
//					br.readLine();
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
				
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
		
		int dx = pos.x - test.tanker.getTankerPosition().x;
		int dy = pos.y - test.tanker.getTankerPosition().y;
		
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
		
		for(int k = 0; k < task_list.size(); k++){
			dTask task = task_list.get(k);
			task.water_req = task.task.getRequired();
			if(task.task.isComplete()){
				System.out.println("removing task");
				task_list.remove(k);
//				try {
//					br.readLine();
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
				
			}
			System.out.printf("Task %d, coord : (%d, %d)\n", k, task.relPump_pos.x, task.relPump_pos.y);
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

	}//end dbTick(posXY target)
	
	
	public void addStation(Cell st,int x, int y, SuperTanker tanker){
		dStation temp = new dStation(st, x, y, tanker);

		if(station_list.isEmpty()){
			
			if(station_list.add(temp) ){
				//System.out.printf("Station added to db! coord : (%d, %d)\n",
				//	temp.getRelPumpRos().x, temp.getRelPumpRos().y);
			}
		} else {
			for(dStation stat : station_list) {
				//System.out.println("accessing statio_list");
				if(stat.station.equals((Station) st)){
					//System.out.println("Station not added, already exist!\n");
					return;
				}
			}
			
			if(station_list.add(temp) ){
				//System.out.printf("Station added to db! coord : (%d, %d)\n",
				//	temp.getRelPumpRos().x, temp.getRelPumpRos().y);
		}
			
		}
		
	}
	
	public void addTask(Task task,int x, int y, SuperTanker tanker){
		dTask temp = new dTask(task, x, y, tanker);
		
		if(task_list.isEmpty()){
			
			if(task_list.add(temp) ){
				//System.out.printf("Task added to db! coord : (%d, %d), Req : %d\n",
				//	temp.getRelPumpRos().x, temp.getRelPumpRos().y, temp.getWaterReq());
			}
		} else {
			for(dTask stat : task_list) {
			
				if(stat.task.equals((Task) task)){
					//System.out.println("Task not added, already exist!\n");
					return;
				}
			}
			
			if(task_list.add(temp) ){
				//System.out.printf("Task added to db! coord : (%d, %d), Req : %d\n",
				//	temp.getRelPumpRos().x, temp.getRelPumpRos().y, temp.getWaterReq());
			}
		}
	}
	
	public void addFuelPump(FuelPump pump){
		this.pump = pump;
	}
	
	public FuelPump getFuelPump(){	return pump;	}
	
	public void addWell(Cell well,int x, int y, SuperTanker tanker){
		dWell temp = new dWell(well, x, y, tanker);
		
		if(well_list.isEmpty()){
			if(well_list.add(temp) ){
				//System.out.printf("Well added to db! coord : (%d, %d)\n",
					//temp.getRelPumpRos().x, temp.getRelPumpRos().y);
			}
		} else {
			for(dWell stat : well_list) {
			
				if(stat.well.equals((Well) well)){
					//System.out.println("Well not added, already exist!\n");
					return;
				}
			}
			
			if(well_list.add(temp) ){
				//System.out.printf("Well added to db! coord : (%d, %d)\n",
				//	temp.getRelPumpRos().x, temp.getRelPumpRos().y);
		}
			
		}
	}
	
}
