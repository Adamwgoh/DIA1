import uk.ac.nott.cs.g54dia.library.*;

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
	private final int CMD_NONE = 1;
	private final int CMD_FORAGE = 2;
	private final int CMD_FUELPUMP = 3;
	private final int CMD_WATERPUMP = 4;
	private final int CMD_REFUEL = 5;
	private final int CMD_FETCHWATER = 6;
	//private final int CMD_COMPLETETASK = 7;
	
	protected int layer_state = CMD_FORAGE;
	//protected database db;
	//protected Environment env;
	//protected Cell[][] view;
	protected Action finalAction = null;
	protected int finalDir;
//	private int forage_val   = 12;
//	private int forage_count = 1;
	private int forage_dir = 7;
	int steps = 18;
	int flap = 0;
	int dir = 0;
	int round = 0;
	
	private InputStreamReader in = new InputStreamReader(System.in);
	private BufferedReader br = new BufferedReader(in);
	
	//constructor
	public ReactiveLayer(){

	}
	
	public void resetForage(){
		round = 0;
		flap = 0;
		dir = 0;
		forage_dir = 8;
	}
	//TODO: third flap has a bug of going wrong direction
	/**
	 * Forage.(ver2). The forage implementede here uses the "Fan" pattern. directions shows the four diamonds that the tanker will go around. These are
	 * predefined directions. The directions and areas are chosen using a modulo function. 
	 */
	public void Forage2(){
		//System.out.println("Foraging2...");
		forage_dir = 8;
		int directions[][] = 	{{SuperTanker.SOUTHWEST, SuperTanker.NORTHWEST, SuperTanker.NORTHEAST, SuperTanker.SOUTHEAST},
								 {SuperTanker.NORTHWEST, SuperTanker.NORTHEAST, SuperTanker.SOUTHEAST, SuperTanker.SOUTHWEST},
								 {SuperTanker.NORTHEAST,SuperTanker.SOUTHEAST, SuperTanker.SOUTHWEST, SuperTanker.NORTHWEST},
								 {SuperTanker.SOUTHEAST, SuperTanker.SOUTHWEST, SuperTanker.NORTHWEST, SuperTanker.NORTHEAST}};
		
		//haven't reach the number of steps to turn
		if(round % steps != 0){

			//System.out.printf("aaa.. flap : %d, dir : %d\n", flap, dir);
			forage_dir = directions[dir][flap];
			finalDir = forage_dir;			
			//System.out.printf("finalDir aaa :%d", forage_dir);
			finalAction = new MoveAction(finalDir);
			round++;
		}else{
			//turn
			if(round == 0){
				
				//starting of forage
				forage_dir = directions[dir][flap];
				finalDir = forage_dir;			
				//System.out.printf("finalDir aaa :%d", forage_dir);
				finalAction = new MoveAction(finalDir);
				round++;
				return;
			}

			if(flap != 3){
				flap++;
			}else{
				flap = 0;
				if(dir != 3){
					dir++;
				}else{
					dir = 0;
				}
			}
			System.out.printf("dir(x) : %d, flap(y) : %d\n", dir, flap);
			round = 1;

			forage_dir = directions[dir][flap];
			//System.out.printf("forage_dir : %d", forage_dir);
			finalDir = forage_dir;			 
			finalAction = new MoveAction(finalDir);
		} 
								 
	}
	
	public int getLayerState(){

		return layer_state;
	}
	
	public void ReturnToFuelPump(){
		//reset forage first
		resetForage();
		
			int x,y;
			x = test.tanker.getTankerPosition().x;
			y = test.tanker.getTankerPosition().y;
			int dPos = 8;//TODO: CAREFUL
			
			if(x < 0 && y < 0){	dPos = SuperTanker.NORTHEAST;}
			if(x < 0 && y > 0){ dPos = SuperTanker.SOUTHEAST;}
			if(x < 0 && y == 0){dPos = SuperTanker.EAST;}
					
			if(x > 0 && y < 0){dPos = SuperTanker.NORTHWEST;}
			if(x > 0 && y > 0){dPos = SuperTanker.SOUTHWEST;}
			if(x > 0 && y == 0){dPos= SuperTanker.WEST;}
			
			if(x == 0 && y < 0){dPos = SuperTanker.NORTH;}
			if(x == 0 && y > 0){dPos = SuperTanker.SOUTH;}
			
			
			//Action a = new MoveAction(dPos);
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
		
		if(test.ControlSys.getNearestWell() != null){
			
				int x,y;
				x = test.tanker.getTankerPosition().x - test.ControlSys.getNearestWell().relPump_pos.x;
				y = test.tanker.getTankerPosition().y - test.ControlSys.getNearestWell().relPump_pos.y;
				
				
				if(x < 0 && y < 0){	dPos = SuperTanker.NORTHEAST;}
				if(x < 0 && y > 0){ dPos = SuperTanker.SOUTHEAST;}
				if(x < 0 && y == 0){dPos = SuperTanker.EAST;}
						
				if(x > 0 && y < 0){dPos = SuperTanker.NORTHWEST;}
				if(x > 0 && y > 0){dPos = SuperTanker.SOUTHWEST;}
				if(x > 0 && y == 0){dPos= SuperTanker.WEST;}
				
				if(x == 0 && y < 0){dPos = SuperTanker.NORTH;}
				if(x == 0 && y > 0){dPos = SuperTanker.SOUTH;}
			
			Action a = new MoveTowardsAction(test.ControlSys.getNearestWell().well.getPoint());
			finalAction =  a; 
			//System.out.printf("%d", dPos);
			finalDir = dPos; 
				
		
		}
	}
	
	public int getDirection(){
		return finalDir;
	}
	
	public posXY getTargetCoord(){
		posXY tanker_coord = test.tanker.getTankerPosition();
		
		switch(finalDir){
			case SuperTanker.NORTH 		: tanker_coord.y++; break;
			case SuperTanker.NORTHEAST  : tanker_coord.x++; tanker_coord.y++;break;
			case SuperTanker.NORTHWEST  : tanker_coord.x--; tanker_coord.y++;break;
			case SuperTanker.SOUTH 		: tanker_coord.y--; break;
			case SuperTanker.SOUTHEAST  : tanker_coord.x++; tanker_coord.y--;break;
			case SuperTanker.SOUTHWEST  : tanker_coord.x--; tanker_coord.y--;break;
			case SuperTanker.WEST  		: tanker_coord.x--; break;
			case SuperTanker.EAST  		: tanker_coord.x++; break;
			default : tanker_coord = test.tanker.getTankerPosition();
		}
		
		return tanker_coord;
		
	}
	
	/**
	 * Refuels the tanker
	 * @return	returns a refuel action
	 */
	public Action reFuel(){
		finalAction =  new RefuelAction();
		
		return finalAction;
	}
	
	public Action reFillWater(){
		finalAction = new LoadWaterAction();
		
		return finalAction;
	}
	
	/**
	 * Completes a given task. For now it chooses a nearest task to be completed
	 * @return	returns a DeliverWaterAction with the given task
	 */
//	public Action completeTask(){
//		dTask d_task = test.ControlSys.getNearestTask(test.tanker.getWaterLevel());
//		Task task = d_task.task;
//		finalAction = new DeliverWaterAction(task);
//		
//		return finalAction;
//	}
	
	/**
	 * Reactive layer Hierachical control.
	 * Behaviours are first prioritized and only allowed as output if conditions are met
	 * @return	chosen Action of the reactive layer to be used by the tanker
	 */
	public Action getOutput(){
		posXY tank_pos = test.tanker.getTankerPosition();
		int tank_fuel = test.tanker.getFuelLevel();
		int water_level = test.tanker.getWaterLevel();
		
		//System.out.printf("tank_pos x : %d, y : %d\n", tank_pos.x, tank_pos.y);
		//System.out.printf("max dist to pump : %d,\n fuel left %d\n", Math.max(Math.abs(tank_pos.x), Math.abs(tank_pos.y)), tank_fuel);
		

		//System.out.println("back to forage");
		layer_state = CMD_FORAGE;
		
		if(water_level < Tanker.MAX_WATER && test.ControlSys.getNearestWell() != null){
			
			if(test.tanker.getPosition().equals(test.ControlSys.getNearestWell().well.getPoint())){
				
				System.out.println("getting water\n");
				layer_state = CMD_FETCHWATER;
			}else{
			
				System.out.println("Going to Well..\n");
				layer_state = CMD_WATERPUMP;
			}
		}
		
		//Priority ranking. The highest are least important and the bottom the most.
		if(Math.max(Math.abs(tank_pos.x), Math.abs(tank_pos.y) )+1 >= tank_fuel && tank_fuel != test.tanker.MAX_FUEL){
		//if(tank_fuel < test.tanker.MAX_FUEL){
			
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
		
		//TODO : I can put a <20% refuel if here. It might help but may complicate things
		
		//if(Math.max(Math.abs(tank_pos.x), Math.abs(tank_pos.y) ) >= tank_fuel && tank_fuel != test.tanker.MAX_FUEL){

		//if(layer_state == CMD_FORAGE)	System.out.println("forage state");
		switch(layer_state){
		
			case CMD_NONE 		: finalAction = null;	break;
			case CMD_FORAGE		: Forage2();			break;
			case CMD_FUELPUMP 	: ReturnToFuelPump();	break;
			case CMD_REFUEL 	: finalDir = 8;reFuel();break;
			case CMD_WATERPUMP 	: GotoWell();			break;
			case CMD_FETCHWATER : finalDir = 8;reFillWater();break;
		}
		return finalAction;
	}
	
	
}
