package hardplayer;

import battlecode.common.*;

import hardplayer.goal.Goal;

import java.util.ArrayList;

public abstract class BasePlayer extends Static {

	protected Goal [] goals;

	public BasePlayer(RobotController rc) {
		init(rc);
	}

	public void tryBestGoal() {
		if(myRC.isMovementActive()||myRC.getFlux()<myType.moveCost)
			return;
		if(queued!=null) {
			queued.doAction();
			queued = null;
			return;
		}
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
		debug_setIndicatorStringObject(0,best);	
		if(best!=null)
			best.execute();
	}

	public void shoot() {}

	public void transferFlux() {}

	public void broadcast() {}

	public void runloop() throws GameActionException {
		myLoc = myRC.getLocation();
		myDir = myRC.getDirection();
		senseNearbyRobots();
		//System.out.println("Allies: "+allies.size);
		shoot();
		transferFlux();
		tryBestGoal();
		broadcast();
	}

	public boolean repurpose() { return false; }

	public void senseNearbyRobots() {
		try {
			Robot [] robots = myRC.senseNearbyGameObjects(Robot.class);
			Robot robot;
			RobotInfo info;
			FastList fl;
			int i=robots.length;
			allies.size = -1;
			enemies.size = -1;
			debris.size = -1;
			while(--i>=0) {
				robot = robots[i];
				info = myRC.senseRobotInfo(robots[i]);
				fl = allUnits[info.team.ordinal()];
				fl.robotInfos[++fl.size] = info;
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

	public abstract void setGoals();

	public void run() {
		setGoals();
		while(true) {
			try {
				runloop();
			} catch(Exception e) {
				debug_stackTrace(e);
			}
			myRC.yield();
		}
	}

}

