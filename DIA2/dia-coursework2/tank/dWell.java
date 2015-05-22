package tank;

import uk.ac.nott.cs.g54dia.multilibrary.Cell;
import uk.ac.nott.cs.g54dia.multilibrary.Well;


/**
 * A custom Station class with calculated absolute position, relative to tank position, as well as functions that
 * update its relative tank position.
 * @author awg04u
 *
 */
public final class dWell{
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
	 * Set the given position from the tanker 
	 * @param indx	position x
	 * @param indy	position y
	 * @param tanker	tanker to be set
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
	 * Set coordinate relative to the tanker
	 * @param pos Position to be set
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
	 * Set coordinate relative to the pump/ origin
	 * @param pos	Position to be set
	 */
	private void setRelPumpPos(posXY pos){
		
		relPump_pos.x = pos.x;
		relPump_pos.y = pos.y;
		
		pump_dist = Math.max(relPump_pos.x, relPump_pos.y);
	}
	
}