package hardplayer;

import battlecode.common.*;

import hardplayer.goal.Goal;
import hardplayer.component.ComponentAI;

import java.util.ArrayList;

abstract class BasePlayer extends Static {

	protected Goal [] goals;
	protected ArrayList<ComponentAI> ais = new ArrayList<ComponentAI>();

	public BasePlayer(RobotController rc) {
		init(rc);
	}

	public void pollComponent(ComponentController c) {
		if(c instanceof SensorController)
			sensor = (SensorController)c;
		else if(c instanceof MovementController)
			motor = (MovementController)c;
	}

	public void tryBestGoal() {
		int bestPriority = 0;
		Goal best = null;
		int priority = 0;
		for(Goal g : goals) {
			if(g.maxPriority() <= bestPriority)
				break;
			priority = g.priority();
			if(priority > bestPriority) {
				best = g;
				bestPriority = priority;
			}
		}
		if(best!=null)
			best.execute();
	}

	public void runloop() {
		for(ComponentController c : myRC.newComponents())
			pollComponent(c);
		myLoc = myRC.getLocation();
		sensorAI.sense();
		if(!motor.isActive())
			tryBestGoal();
	}

	public boolean repurpose() { return false; }

	public void run() {
		// flush newComponents
		myRC.newComponents();
		for(ComponentController c : myRC.components())
			pollComponent(c);
		while(true) {
			try {
				runloop();
				if(repurpose())
					return;
			} catch(Exception e) {
				debug_stackTrace(e);
			}
			myRC.yield();
		}
	}

}
