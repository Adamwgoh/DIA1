package tank;

import src.*;
import uk.ac.nott.cs.g54dia.multilibrary.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
/**
 * The Reactive Layer
 * the reactive layer reacts to simple behaviours such as low on fuels, low on water, foraging.
 * The reactive layer gets its information from the Model Layer, which is controlled by the ControlSystem.
 * The ControlSystem has the ability to shut down some of the Reactive Layer's percepts, turning off some of the behaviours' conditions.
 * @author awg04u
 *
 */
public class ReactiveLayer {
	private final int
		CMD_NONE		= 1,
		CMD_FORAGE		= 2,
		CMD_FUELPUMP	= 3,
		CMD_WATERPUMP	= 4,
		CMD_REFUEL		= 5,
		CMD_FETCHWATER	= 6;
	private SuperTanker tanker;
	private Agent agent;
	private boolean startedForage = false;
	protected int layer_state = CMD_FORAGE;
	protected Action finalAction = null;
	protected int finalDir;
	private int forage_dir = 7;
	int steps = 22;
	int flap = 0;
	int dir = 0;
	int round = 0;
	
	private InputStreamReader in = new InputStreamReader(System.in);
	private BufferedReader br = new BufferedReader(in);
	
	//constructor
	public ReactiveLayer(SuperTanker tanker){
		this.tanker = tanker;
	}
	
	/**
	 * agent needs to be added to the reactive layer to coordinate between other tankers
	 * for forage direction
	 * @param agent	Agent given by the OpenSystem to the Tanker
	 */
	public void addAgent(Agent agent){	this.agent = agent;	}
	public int getDirection(){	return finalDir; }
	public Action reFuel(){	finalAction =  new RefuelAction();	return finalAction;	}
	public Action reFillWater(){	finalAction = new LoadWaterAction(); return finalAction; }
	
	public void resetForage(){
		round = 0;
		if(agent != null && agent.getRole() != null){
			//coordinates with other tankers to get the right direction
			if(agent.getRole() instanceof Forage){
				dir = ((Forage) agent.getRole()).getFlap();
			}else{	dir = 0; }
			
		}else{	dir = 0; }
		flap = 0;
		forage_dir = 8;
		startedForage = false;
	}

	/**
	 * Forage.(ver2). The forage implemented here uses the "Fan" pattern. directions shows the four diamonds that the tanker will go around. These are
	 * predefined directions. The directions and areas are chosen using a modulo function. 
	 * 
	 * <b>MULTI-AGENT SYSTEM</b> : If the tanker had an agent, it checks if its role is of Forage. If yes, the direction
	 * of the forage may differ depending on the Role's leader, if there is any. 
	 */
	public void Forage2(){
		if(!startedForage){
			if(agent != null && agent.getRole() != null){
				if(agent.getRole() instanceof Forage){
						dir = ((Forage) agent.getRole()).getFlap();
						startedForage = true;
				}
			}else{	dir = 0; }
		}
		forage_dir = 8;
		//predefined forage patterns
		int directions[][] = 	{{SuperTanker.SOUTHWEST, SuperTanker.NORTHWEST, SuperTanker.NORTHEAST, SuperTanker.SOUTHEAST},
								 {SuperTanker.NORTHWEST, SuperTanker.NORTHEAST, SuperTanker.SOUTHEAST, SuperTanker.SOUTHWEST},
								 {SuperTanker.NORTHEAST,SuperTanker.SOUTHEAST, SuperTanker.SOUTHWEST, SuperTanker.NORTHWEST},
								 {SuperTanker.SOUTHEAST, SuperTanker.SOUTHWEST, SuperTanker.NORTHWEST, SuperTanker.NORTHEAST}};
		
		//haven't reach the number of steps to turn
		if(round % steps != 0){

			forage_dir = directions[dir][flap];
			finalDir = forage_dir;			
			finalAction = new MoveAction(finalDir);
			round++;
		}else{
			//turn
			if(round == 0){
				forage_dir = directions[dir][flap];
				finalDir = forage_dir;			
				finalAction = new MoveAction(finalDir);
				round++;
				return;
			}

			if(flap != 3){
				flap++;
			}else{
				flap = 0;
				if(dir != 3){	dir++; }else{	dir = 0; }
			}
			
			round = 1;
			forage_dir = directions[dir][flap];
			finalDir = forage_dir;			 
			finalAction = new MoveAction(finalDir);
		} 			 
	}//endof Forage()
	
	public void ReturnToFuelPump(){
			//reset forage first
			resetForage();
		
			int x,y;
			x = this.tanker.getTankerPosition().x;
			y = this.tanker.getTankerPosition().y;
			int dPos = 8;
			dPos = getDirection(x,y);

			Action a = new MoveTowardsAction(SuperTanker.FUEL_PUMP_LOCATION);
			finalAction =  a; 
			finalDir = dPos; 
	}//end of func
	
	/**
	 * Goes to the nearest well that is in the database. If no well is found, goes back to forage.
	 * The loop will bring the component back to fetchwater again when there is a well
	 */
	public void GotoWell(){
		//reset forage first
		resetForage();
		int dPos = 8;
		
		if(this.tanker.ControlSys.getNearestWell() != null){
			
			int x,y;
			x = this.tanker.getTankerPosition().x - this.tanker.ControlSys.getNearestWell().relPump_pos.x;
			y = this.tanker.getTankerPosition().y - this.tanker.ControlSys.getNearestWell().relPump_pos.y;
			dPos = getDirection(x,y);
			
			Action a = new MoveTowardsAction(this.tanker.ControlSys.getNearestWell().well.getPoint());
			finalAction =  a; 
			finalDir = dPos; 
		}
	}//end GotoWelL()
	
	/**
	 * Reactive layer Hierachical control.
	 * Behaviours are first prioritised and only allowed as output if conditions are met
	 * @return	chosen Action of the reactive layer to be used by the tanker
	 */
	public Action getOutput(){
		posXY tank_pos = this.tanker.getTankerPosition();
		int tank_fuel = this.tanker.getFuelLevel();
		int water_level = this.tanker.getWaterLevel();

		layer_state = CMD_FORAGE;
		//Priority ranking. The highest are least important and the bottom the most.
		if(water_level < Tanker.MAX_WATER && this.tanker.ControlSys.getNearestWell() != null){
			
			if(this.tanker.getPosition().equals(this.tanker.ControlSys.getNearestWell().well.getPoint())){
				
				System.out.println("getting water\n");
				layer_state = CMD_FETCHWATER;
			}else{
			
				System.out.println("Going to Well..\n");
				layer_state = CMD_WATERPUMP;
			}
		}
		
		if(Math.max(Math.abs(tank_pos.x), Math.abs(tank_pos.y) )+1 >= tank_fuel && tank_fuel != Tanker.MAX_FUEL){
			if(tank_pos.x != 0 || tank_pos.y != 0){
				System.out.println("finish! go back \n");
				layer_state = CMD_FUELPUMP;
			}else{
				if(tank_fuel < Tanker.MAX_FUEL){			
						System.out.println("refuelling");
						layer_state = CMD_REFUEL;
				}
			}
		}
		
		if(tank_fuel < Tanker.MAX_FUEL){
			if(tank_pos.x == 0 && tank_pos.y == 0){
				System.out.println("asking to refuel");
				layer_state = CMD_REFUEL;
			}
		}
	
		//change layer's state. Similar to Subsumption Control's arbitrary system
		switch(layer_state){
		
			case CMD_NONE 		: finalAction = null;	 	 break;
			case CMD_FORAGE		: Forage2();				 break;
			case CMD_FUELPUMP 	: ReturnToFuelPump();		 break;
			case CMD_REFUEL 	: finalDir = 8;reFuel();	 break;
			case CMD_WATERPUMP 	: GotoWell();				 break;
			case CMD_FETCHWATER : finalDir = 8;reFillWater();break;
		}
		return finalAction;
	}
	
///////////////////////////		START OF UTILITY METHODS	////////////////////////////
	
	//usual utility methods for converting between tanker direction and tanker coordinate;
	public posXY getTargetCoord(){
		posXY tanker_coord = this.tanker.getTankerPosition();
		
		switch(finalDir){
			case SuperTanker.NORTH 		: tanker_coord.y++; break;
			case SuperTanker.NORTHEAST  : tanker_coord.x++; tanker_coord.y++;break;
			case SuperTanker.NORTHWEST  : tanker_coord.x--; tanker_coord.y++;break;
			case SuperTanker.SOUTH 		: tanker_coord.y--; break;
			case SuperTanker.SOUTHEAST  : tanker_coord.x++; tanker_coord.y--;break;
			case SuperTanker.SOUTHWEST  : tanker_coord.x--; tanker_coord.y--;break;
			case SuperTanker.WEST  		: tanker_coord.x--; break;
			case SuperTanker.EAST  		: tanker_coord.x++; break;
			default : tanker_coord = this.tanker.getTankerPosition();
		}
		
		return tanker_coord;	
	}
	
	
	private int getDirection(int x, int y){
		int direction = 0;
		
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
///////////////////////////		END OF UTILITY METHODS	////////////////////////////
	
}//end of class
