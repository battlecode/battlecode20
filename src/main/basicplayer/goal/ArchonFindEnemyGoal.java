package basicplayer.goal;

import basicplayer.ArchonPlayer;
import basicplayer.BasePlayer;
import basicplayer.message.SuperMessageStack;
import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotInfo;

public class ArchonFindEnemyGoal extends FindEnemyGoal {

	public int priority() {
		if(atWar&&allies.size>=5)
			return ARCHON_FIND_ENEMY;
		else
			return 0;
	}

	public void execute() {
		if(enemies.size>0) {
			MapLocation enemyLoc=getEnemyLoc(), best=null, loc;
			int i;
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
