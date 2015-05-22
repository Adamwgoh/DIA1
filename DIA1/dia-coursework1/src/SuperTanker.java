import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import uk.ac.nott.cs.g54dia.library.*;



public class SuperTanker extends Tanker{
	
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
    protected FuelPump pump;
    
	public SuperTanker(){
		//RLayer = test.RLayer;
		//constructor
		
		position.x = 0;
		position.y = 0;
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
	
	public posXY getTankerPosition(){	return position; }
	public int getPumpDist(){	return pump_dist;	}
	public Action senseAndAct(Cell[][] view, long timestep){
		
		lookAround(view, timestep);
		//System.out.println("end of search\n");
		finalAction = test.ControlSys.getOutput();
		finalDir = test.ControlSys.getDirection();
		
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
					//System.out.printf("found well\n");
					test.ControlSys.addWell(view[i][j],  i,  j,  this);
					//test.db.addWell(view[i][j], i, j, this);
					
					
				}else if(view[i][j] instanceof Station){
					//System.out.println("found station\n");
					test.ControlSys.addStation(view[i][j],  i,  j,  this);
					//test.db.addStation(view[i][j], i, j, this);
					
					Station station = (Station) view[i][j];
					
					if(station.getTask() != null){
						//System.out.printf("supertanker L99, i : %d, j %d\n",i,j);
						test.ControlSys.addTask(station.getTask(),  i,  j,  this);
						//test.db.addTask(station.getTask(), i, j, this);
					
					}
				}else if(view[i][j] instanceof FuelPump){
					test.ControlSys.addFuelPump(view[i][j]);
				}
				
			}
		}
	}//end of func
	
	/**
	 * Update the tanker's current perceived position. This keeps track of where the tanker is in the environment from the fuel pump(0,0)
	 * @param dir
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
