package tank;

import uk.ac.nott.cs.g54dia.multilibrary.Task;

/**
 * A custom Task class with calculated absolute position, relative to tank position, as well as functions that
 * update its relative tank position.
 * @author awg04u
 *
 */
public final class dTask{
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
		
	}//end constructor
	
	/**
	 * Calculate the distance of given position from the tanker and stores it in the tanker
	 * @param indx	position x
	 * @param indy	position y
	 * @param tanker	tanker to be updated
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
		relPump_pos = new posXY(i3,j3);
		relTank_pos = new posXY(i2,j2);
		tank_dist = Math.max(Math.abs(relTank_pos.x), Math.abs(relTank_pos.x));
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
	public boolean isTaskComplete(){	return this.task.isComplete();	}


}