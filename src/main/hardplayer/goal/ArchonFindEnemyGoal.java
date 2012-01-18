package hardplayer.goal;

import hardplayer.ArchonPlayer;
import hardplayer.BasePlayer;
import hardplayer.message.SuperMessageStack;
import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.PowerNode;
import battlecode.common.RobotInfo;

public class ArchonFindEnemyGoal extends FindEnemyGoal {

	static PowerNode [] oldNodes = new PowerNode [0];

	public int priority() {
		for(PowerNode p : oldNodes) {
			if(!myRC.senseOwned(p)&&base.distanceSquaredTo(p.getLocation())<base.distanceSquaredTo(myLoc)) {
				setAtWar();
				add(p.getLocation(),200);
			}
		}
		oldNodes = myRC.senseAlliedPowerNodes();
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
