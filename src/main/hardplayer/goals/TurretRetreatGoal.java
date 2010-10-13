package hardplayer.goals;

import hardplayer.BasePlayer;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;

public class TurretRetreatGoal extends Goal {

	MapLocation enemyLocation;

	public TurretRetreatGoal(BasePlayer bp) {
		super(bp);
	}

	public int getMaxPriority() {
		return ARMY_RETREAT;
	}

	public int getPriority() {
		// 16 is the smallest distance at which
		// we might not be able to shoot our enemy
		// after we back up
		int i, d, dmin=16;
		MapLocation myLoc=player.myLoc;
		RobotInfo info, closest=null;
		for(i=player.enemySoldiers.size-1;i>=0;i--) {
			info=player.enemySoldierInfos[i];
			if((d=myLoc.distanceSquaredTo(info.location))<dmin&&
			   info.roundsUntilAttackIdle<=5&&
			   info.energonLevel>0) {
				dmin=d;
				closest=info;
			}
		}
		for(i=player.enemyChainers.size-1;i>=0;i--) {
			info=player.enemyChainerInfos[i];
			if((d=myLoc.distanceSquaredTo(info.location))<dmin&&
			   info.roundsUntilAttackIdle<=5&&
			   info.energonLevel>0) {
				dmin=d;
				closest=info;
			}
		}
		if(closest!=null) {
			enemyLocation=closest.location;
			return ARMY_RETREAT;
		}
		else
			return NEVER;
	}

	public void tryToAccomplish() {
		Direction dir=player.myLoc.directionTo(enemyLocation).opposite();
		if(myRC.canMove(dir)) player.myNav.setDirectionAndMoveBackward(dir);
		else if(myRC.canMove(dir.rotateLeft())) player.myNav.setDirectionAndMoveBackward(dir.rotateLeft());
		else if(myRC.canMove(dir.rotateRight())) player.myNav.setDirectionAndMoveBackward(dir.rotateRight());
	}
	
}