package hardplayer;

import battlecode.common.*;

import hardplayer.goal.FindEnemyGoal;
import hardplayer.goal.Goal;
import hardplayer.message.MessageSender;

import java.util.ArrayList;

public abstract class BasePlayer extends Static {

	protected Goal [] goals;

	public BasePlayer(RobotController rc) {
		init(rc);
	}

	public static void debug_logGoal(Goal best) {
		debug_setIndicatorStringFormat(0,"%d %s",Clock.getRoundNum(),best);
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
		debug_logGoal(best);
		if(best!=null)
			best.execute();
	}

	public void shoot() {}

	public void transferFlux() {}

	public void broadcast() {}

	public void runloop() throws GameActionException {
		myLoc = myRC.getLocation();
		myDir = myRC.getDirection();
		FindEnemyGoal.decay();
		sortMessages();
		senseNearbyRobots();
		findEnemy();
		//System.out.println("Allies: "+allies.size);
		shoot();
		transferFlux();
		tryBestGoal();
		broadcast();
	}

	public void findEnemy() {
		FindEnemyGoal.read();
		checkAtWar();
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
			alliedArchons.size = -1;
			alliedSoldiers.size = -1;
			alliedScouts.size = -1;
			alliedDisrupters.size = -1;
			alliedScorchers.size = -1;
			alliedTowers.size = -1;
			enemyArchons.size = -1;
			enemySoldiers.size = -1;
			enemyScouts.size = -1;
			enemyDisrupters.size = -1;
			enemyScorchers.size = -1;
			enemyTowers.size = -1;
			while(--i>=0) {
				robot = robots[i];
				info = myRC.senseRobotInfo(robots[i]);
				fl = allUnits[info.team.ordinal()];
				fl.robotInfos[++fl.size] = info;
				fl = unitsByType[info.team.ordinal()][info.type.ordinal()];
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
				if(repurpose()) {
					myRC.yield();
					return;
				}
			} catch(Exception e) {
				debug_stackTrace(e);
			}
			myRC.yield();
		}
	}
	
	public void sortMessages() {
		mySender.updateRoundNum();
		// Using Clock.getRoundNum() could prove problematic if we ever go
		// over the bytecode limit
		Message [] newMessages=myRC.getAllMessages();
		int [] ints;
		int type;
		int i;
		for(i=0;i<newMessages.length;i++) {
			ints=newMessages[i].ints;
			if(ints==null||
			   ints.length<3||
			   (type=ints[0])<0||
			   type>=MessageSender.numTypes||
			   handlers[type]==null||
			   !mySender.isValid(newMessages[i])) continue;
			handlers[type].receivedMessage(newMessages[i]);
		}
	}

}

