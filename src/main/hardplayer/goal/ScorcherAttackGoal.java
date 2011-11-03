package hardplayer.goal;

import battlecode.common.PowerNode;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

import hardplayer.Static;

public class ScorcherAttackGoal extends Static implements Goal {
	
	public int maxPriority() { return ATTACK; }

	public int priority() {
		RobotInfo info;
		int i;
		for(i=enemies.size;i>=0;i--) {
			info = enemyInfos[i];
			if(!(info.robot instanceof PowerNode) || myRC.senseConnected((PowerNode)info.robot))
				return ATTACK;
		}
		return 0;
	}

	public void execute() {
		int i, soldiersNow=0, soldiersBack=0;
		int d, dmax=0;
		RobotInfo info;
		RobotInfo farthest = null;
		for(i=enemies.size;i>=0;i--) {
			info = enemyInfos[i];
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
				if(soldiersNow-soldiersBack<2&&myRC.canMove(myDir.opposite())) {
					myRC.moveBackward();
				}
				else if(farthest!=null) {
					myRC.setDirection(myLoc.directionTo(farthest.location));
				}
			} else {
				if(farthest!=null) {
					if(dmax>2)
						myNav.moveToForward(farthest.location);
					else
						myRC.setDirection(myLoc.directionTo(farthest.location));
				}
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}

	}

}
