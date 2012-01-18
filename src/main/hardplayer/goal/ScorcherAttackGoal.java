package hardplayer.goal;

import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.PowerNode;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;

public class ScorcherAttackGoal extends AttackGoal {

	public int priority() {
		RobotInfo info;
		int i;
		for(i=enemies.size;i>=0;i--) {
			info = enemyInfos[i];
			switch(info.type) {
				case SCOUT:
					continue;
				case TOWER:
					if(!senseConnected(info.location))
						continue;
				default:
					message = null;
					return ATTACK;
			}
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
			if(dmin>20)
				myNav.moveToForward(locs[best]);
			else if(dmin>0)
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
		int i, soldiersNow=0, soldiersBack=0;
		int d, dmax=0;
		RobotInfo info;
		RobotInfo farthest = null;
		for(i=enemies.size;i>=0;i--) {
			info = enemyInfos[i];
			if(info.type==RobotType.SCOUT)
				continue;
			if(info.type==RobotType.SOLDIER) {
				soldiersNow++;
				if(myRC.canAttackSquare(info.location.add(myDir)))
					soldiersBack++;
			}
			else {
				d=myLoc.distanceSquaredTo(info.location);
				if(d>dmax) {
					dmax = d;
					farthest = info;
				}
			}
		}
		debug_setIndicatorStringFormat(2,"%s %d %d",farthest,dmax,soldiersNow);
		try {
			if(soldiersNow>0) {
				if(soldiersBack>0&&soldiersNow-soldiersBack<2&&myRC.canMove(myDir.opposite())) {
					myRC.moveBackward();
				}
				else if(farthest!=null) {
					myRC.setDirection(myLoc.directionTo(farthest.location));
				}
			} else {
				if(farthest!=null) {
					if(farthest.type==RobotType.TOWER) {
						if(dmax<=4&&myRC.canMove(myDir.opposite()))
							myRC.moveBackward();
					}
					else {
						if(dmax>2)
							myNav.moveToForward(farthest.location);
						else
							myRC.setDirection(myLoc.directionTo(farthest.location));
					}
				}
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}

	}

}
