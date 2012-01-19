package hardplayer.goal;

import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.PowerNode;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;

import hardplayer.Static;
import hardplayer.message.MessageHandler;
import hardplayer.message.MessageSender;

public class DisrupterAttackGoal extends AttackGoal {

	public int priority() {
		//debug_setIndicatorStringFormat(2,"OH LOOK");
		RobotInfo info;
		int i;
		for(i=enemies.size;i>=0;i--) {
			info = enemyInfos[i];
			if(info.type==RobotType.TOWER&&!senseConnected(info.location))
				continue;
			message = null;
			return ATTACK;
		}
		if(message!=null)
			return ATTACK;
		return 0;
	}

	public void attackFromMessage() {
		int i, best = 0;
		int d, dmin=99999;
		MapLocation [] locs = message.locations;
		for(i=locs.length-1;i>0;i--) {
			d = myLoc.distanceSquaredTo(locs[i]);
			if(d<dmin) {
				dmin = d;
				best = i;
			}
		}
		try {
			if(dmin>RobotType.DISRUPTER.sensorRadiusSquared)
				myNav.moveToForward(locs[best]);
			else
				myRC.setDirection(myLoc.directionTo(locs[best]));
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

	public void execute() {
		if(message!=null) {
			attackFromMessage();
			message = null;
			return;
		}
		int i;
		int d, dmin=99999;
		RobotInfo info;
		RobotInfo closest = null;
		for(i=enemies.size;i>=0;i--) {
			info = enemyInfos[i];
			d=myLoc.distanceSquaredTo(info.location);
			if(info.type==RobotType.TOWER&&!senseConnected(info.location))
				continue;
			if(d<dmin) {
				dmin = d;
				closest = info;
			}
		}
		myRC.setIndicatorString(1,"HEY I'M SETTING AN INDICATOR STRING!");
		debug_setIndicatorStringFormat(2,"%s %s",myLoc,closest.location);
		try {
			if(closest.type==RobotType.SOLDIER) {
				if(myRC.canMove(myDir.opposite())&&myRC.canAttackSquare(closest.location.add(myDir)))
					myRC.moveBackward();
				else if(dmin>0)
					myRC.setDirection(myLoc.directionTo(closest.location));
			}
			else {
				if(myRC.canAttackSquare(closest.location))
					if(myRC.canMove(myDir)&&!myRC.canMove(myDir.opposite()))
						myRC.moveForward();
					else if(dmin>0)
						myRC.setDirection(myLoc.directionTo(closest.location));
				else if(dmin<=RobotType.DISRUPTER.attackRadiusMaxSquared) {
					if(dmin>0)
						myRC.setDirection(myLoc.directionTo(closest.location));
				}
				else
					myNav.moveToForward(closest.location);
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}

	}

}
