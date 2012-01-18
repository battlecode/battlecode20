package refplayer.goal;

import refplayer.ArchonPlayer;
import refplayer.BasePlayer;
import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotInfo;

public class AggroArchonFindEnemyGoal extends FindEnemyGoal {

	public int priority() {
		if(atWar&&armySize()>=18)
			return FIND_ENEMY;
		else
			return 0;
	}

	public void execute() {
		if(enemies.size>0) {
			int i;
			MapLocation enemyLoc=getEnemyLoc(), best=null, loc;
			int d, dmin=0x7FFFFFFF;
			for(i=allies.size;i>=0;i--) {
				if(!isFighter[alliedInfos[i].type.ordinal()])
					continue;
				loc=alliedInfos[i].location;
				d=loc.distanceSquaredTo(enemyLoc);
				if(d<dmin) {
					dmin=d;
					best=loc;
				}
			}
			if(best==null)
				return;
			if(dmin<=myLoc.distanceSquaredTo(enemyLoc))
				myNav.moveToForward(best);
			else
				myNav.moveToBackward(best);
		}
		else
			super.execute();

	}

}
