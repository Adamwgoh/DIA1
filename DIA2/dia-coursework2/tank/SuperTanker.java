package tank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import src.InteractProtocol;
import src.OpenSystem;
import src.test;
import uk.ac.nott.cs.g54dia.multilibrary.*;



public class SuperTanker extends Tanker implements InteractProtocol{
	public ControlSystem ControlSys;
	public final static int
    NORTH       =   0, //(0, +1)
    SOUTH       =   1, //(0, -1)
    EAST        =   2, //(+1,  0)
    WEST        =   3, //(-1,  0)
    NORTHEAST   =   4, //(+1, +1)
    NORTHWEST   =   5, //(-1, +1)
    SOUTHEAST   =   6, //(-1, +1)
    SOUTHWEST   =   7; //(-1, -1)
    protected posXY position = new posXY(0,0);
    protected Action finalAction;
    protected int	finalDir;
    protected int	pump_dist;
    protected int fleet_number;
    protected FuelPump pump;
	InputStreamReader in = new InputStreamReader(System.in);
	BufferedReader br = new BufferedReader(in);
	
	public SuperTanker(){
		//RLayer = test.RLayer;
		//constructor
		position.x = 0;
		position.y = 0;
	}
	
	public int setFleetNumber(){
		
		ArrayList<SuperTanker> fleet = getFleetMates();
		for(int i = 0; i < fleet.size(); i++){
			if(((SuperTanker) fleet.get(i)).equals(this)){
				return i;
			}
		}
		
		return 404;
	}
	
	//acknowledging that the tanker is in a fleet
	public void AckFleet(){
		InteractProtocol.fleet_tankers.add(this);
		fleet_number = setFleetNumber();

	}
	
	public ArrayList<SuperTanker> getFleetMates(){
		
		return InteractProtocol.fleet_tankers;
	}
	
	public void startCtrlSys(){
		
		ControlSys = new ControlSystem(this);	
	}
	//State queries
	/**
	 * State clauses/atoms. To check if tanker is at the given position
	 * @param pos the queried position
	 * @return	True if the tanker is at the position
	 */
	public boolean At(posXY pos){
		
		return pos==position;
	}
	
	public boolean AtPump(){
		
		return(position.x == 0 && position.y == 0);
	}
	
	/**
	 * State clauses/atoms. To check if tanker has the given fuel amount
	 * @param amount the queried fuelAmount
	 * @return True if the tanker has that amount of fuel
	 */
	public boolean haveFuel(int amount){
		return (amount <= this.getFuelLevel());
	}
	
	/**
	 * State clauses/atoms. To check if tanker has the given water amount
	 * @param amount The queried WaterAmount
	 * @return True if the tanker has that amount of water
	 */
	public boolean haveWater(int amount){
		return (amount <= this.getWaterLevel());
	}
	
	public int getFleetNumber(){
		return fleet_number;
	}
	
	public void RequestHarvest(){
		this.ControlSys.RequestHarvest();
	}
	
	public int getTankerDirection(){	return finalDir;}
	public posXY getTankerPosition(){	return position; }
	public int getPumpDist(){	return pump_dist;	}
	public Action senseAndAct(Cell[][] view, long timestep){
		
		System.out.println("========================================================================");
		
		System.out.printf("fleet tanker %d, position (%s)\n", this.getFleetNumber(), this.getPosition().toString());
		final Pattern pattern = Pattern.compile("(?<=\\[)[^\\]]+|\\w+|-?\\d+"); // the regex
		final Matcher matcher = pattern.matcher(this.getPosition().toString()); // your string
		for(int i = 0; i < getFleetMates().size(); i++){
			
			System.out.printf("fleet %d pos : %s\n", i, ((SuperTanker) getFleetMates().get(i)).getPosition());
		}
		final ArrayList<Integer> ints = new ArrayList<Integer>(); // results

		while (matcher.find()) { // for each match
		    ints.add(Integer.parseInt(matcher.group())); // convert to int
		}
		
		System.out.printf("belief pos (%d,%d)\n", this.getTankerPosition().x, this.getTankerPosition().y);
		if(ints.get(0) != this.getTankerPosition().x || ints.get(1) != this.getTankerPosition().y){
			System.out.printf("mismatch.\n");
			try {
				br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		ControlSys.printCurrentRole();

		lookAround(view, timestep);
		InteractProtocol.OpenSys.printRoles();
		ControlSys.printTasks();
		ControlSys.printAgentTasks();
		finalAction = this.ControlSys.getOutput();
		finalDir = this.ControlSys.getDirection();
		System.out.printf("final action : %s, finalDir : %d\n", finalAction.toString(), finalDir);
		updatePosition(finalDir);
		
		return finalAction;		
	}
	
	/**
	 * The agent passes any objects that it saws into the Model through Control System. The control system only acts
	 * as a wrapper for a more tidy architecture, but does not involve any censoring in this part.
	 * @param view	visible cells
	 * @param timestep	timestep
	 */
	private void lookAround(Cell[][] view, long timestep){
		for(int i = 0; i < view.length; i++){
			for(int j = 0; j < view[i].length; j++){
				if(view[i][j] instanceof Well){
					
					this.ControlSys.addWell(view[i][j],  i,  j,  this);
				}else if(view[i][j] instanceof Station){
					
					this.ControlSys.addStation(view[i][j],  i,  j,  this);
					Station station = (Station) view[i][j];
					if(station.getTask() != null){
						this.ControlSys.addTask(station.getTask(),  i,  j,  this);
					
					}
				}else if(view[i][j] instanceof FuelPump){
					
					this.ControlSys.addFuelPump(view[i][j]);
				}
				
			}
		}
		
		//check for conflict
		this.ControlSys.RemoveConflictTask();

		
	}//end of func
	
	/**
	 * Update the tanker's current perceived position. This keeps track of where the tanker is in the environment from the fuel pump(0,0)
	 * @param dir	Direction of the tanker Action
	 */
	private void updatePosition(int dir){
		switch(dir){
			case NORTH : position.y++;	break;
			case SOUTH : position.y--;	break;
			case EAST : position.x++;	break;
			case WEST : position.x--;	break;
			case NORTHEAST : position.x++;position.y++;	break;
			case NORTHWEST : position.x--;position.y++;	break;
			case SOUTHEAST : position.x++;position.y--;	break;
			case SOUTHWEST : position.x--;position.y--;	break;
		
		}
		
		pump_dist = Math.max(Math.abs(position.x), (Math.abs(position.y)));
	}
	
}
