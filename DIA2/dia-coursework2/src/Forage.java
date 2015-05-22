package src;
/**
 * <p><b> MULTI-AGENT SYSTEM</b> : Forage is a simple extended Role class that determines the direction of Forage
 * <p>Forage action is neither executed nor implemented here, but by the Agent's tanker itself.</p>
 * <p>The relationship between A child FORAGE Role and leader FORAGE Role is important. The child FORAGE
 * changes its flap value according to the leader FORAGE's flap value. Flap value is the direction of
 * forage in the "Fan" pattern.</p>
 * 
 * <p>For more information, see {@link tank.ReactiveLayer#Forage2}</p>
 * @author awg04u
 *
 */
public class Forage extends Role {
	private int flap;
	
	public Forage(){
		if(leader != null ){
			flap = (((Forage) leader).getFlap() +1) % 4;
		}else{
			flap = 0;
		}
		name = "(Forage)";
	}
	
	public Forage(Role leader){
		this.leader = leader;
		this.flap = (((Forage) leader).getFlap() +1) % 4;
		name = "(Forage)";
	}
	
	public int getFlap(){
		
		return flap;
	}
}
