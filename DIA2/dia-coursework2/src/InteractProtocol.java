package src;

import tank.*;
import java.util.ArrayList;

/**
 * <p><b>MULTI-AGENT SYSTEM</b> : A Communication interface that can be used between tankers.</p>
 * <p>This allows tankers to share knowledge between each other for necessary needs such as 
 * avoiding conflicting tasks and sharing tanker locations.</p>
 * 
 * <p>The interface contains the {@link OpenSystem}, A distributive multi-agent system that shares
 * tasks among any agents that are in it. A tanker is free to join or leave the OpenSystem anytime
 * it wants.</p>
 * 
 * <p>For more information, see {@link OpenSystem}</p>
 * @author awg04u
 *
 */
public interface InteractProtocol {
	public static ArrayList<SuperTanker> fleet_tankers = new ArrayList<SuperTanker>();
	public static OpenSystem OpenSys = new OpenSystem();
	
	public void AckFleet();
	public int setFleetNumber();
	public ArrayList<SuperTanker> getFleetMates();
	public void RequestHarvest();
}
