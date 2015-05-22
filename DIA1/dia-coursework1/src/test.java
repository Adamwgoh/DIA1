import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.nott.cs.g54dia.library.*;
import uk.ac.nott.cs.g54dia.demo.*;

public class test {
	//public static database db;
	//public static ReactiveLayer RLayer;
	public static Environment e;
	//TODO Refactor this so only controlsystems sees this Not good if all sees it, although makes life easier
	public static SuperTanker tanker;
	public static ControlSystem ControlSys;
	public test() {
		// TODO Auto-generated constructor stub
	}


	
	
	public static void main(String[] args) throws IOException{
			InputStreamReader in = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(in);
			e = new Environment((Tanker.MAX_FUEL/2) - 5);
			tanker = new SuperTanker();
			TankerViewer tanker_tv = new TankerViewer(tanker);
			int DURATION = 10 * 10000;
			int SPEED = 0;
			tanker_tv.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);


			ControlSys = new ControlSystem();
			
			while(e.getTimestep() < DURATION){	
				e.tick();
				//updates GUI
				tanker_tv.tick(e);

				System.out.printf("\n============================================\nTimestep %d, Actual pos %s, calc_pos (%d,%d)\n", e.getTimestep(), 
						tanker.getPosition().toString(), ((SuperTanker)tanker).getTankerPosition().x, ((SuperTanker) tanker).getTankerPosition().y);
				System.out.printf("current fuel level : %d, dist to pump : %d\n", tanker.getFuelLevel(), tanker.getPumpDist());
				final Pattern pattern = Pattern.compile("(?<=\\[)[^\\]]+|\\w+|-?\\d+"); // the regex
				final Matcher matcher = pattern.matcher(tanker.getPosition().toString()); // your string

				final ArrayList<Integer> ints = new ArrayList<Integer>(); // results

				while (matcher.find()) { // for each match
				    ints.add(Integer.parseInt(matcher.group())); // convert to int#
				}
				
				//System.out.printf("intsX : %d, intsY : %d\n", ints.get(0), ints.get(1));
				
				if(ints.get(0) != ((SuperTanker) tanker).getTankerPosition().x || ints.get(1) != ((SuperTanker) tanker).getTankerPosition().y){
					System.out.printf("mismatch.\n");
					br.readLine();
					
				}
				//gets current view of tanker
				Cell[][] view = e.getView(tanker.getPosition(), Tanker.VIEW_RANGE);
				
				//YOU SEE YOU DO (Sense&Act)
				Action a = tanker.senseAndAct(view, e.getTimestep());
				
				try {
					if(a != null)	a.execute(e, tanker);
					//System.out.printf("%s", a.toString());
					//br.readLine();
					///System.in.read();
					Thread.sleep(SPEED);
				} 
				catch (ActionFailedException e1) { 
					System.out.printf("%s", e1.toString());
					br.readLine(); 
				}
				//catch (IOException e1){}
				catch (Exception e1){}
				
				
			}
	}
}
