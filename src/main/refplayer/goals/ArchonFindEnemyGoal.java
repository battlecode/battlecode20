package refplayer.goals;

import refplayer.ArchonPlayer;
import refplayer.BasePlayer;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotInfo;

public class ArchonFindEnemyGoal extends FindEnemyGoal {

	public ArchonFindEnemyGoal(BasePlayer bp) {
		super(bp);
	}

	public int getPriority() {
		if(player.atWar&&
		   player.alliedSoldiers.size+
		   player.alliedTurrets.size+
		   player.alliedChainers.size>=2&&
		   myRC.getEnergonLevel()>=ArchonPlayer.MIN_ENERGON)
			return FIND_ENEMY_PRIORITY;
		else
			return NEVER;
	}

	public void tryToAccomplish() {
		if(player.enemyArchons.size+
		   player.enemySoldiers.size+
		   player.enemyChainers.size+
		   player.enemyTurrets.size>0) {
			int i;
			MapLocation enemyLoc=getEnemyLoc(), best=null, loc;
			int d, dmin=0x7FFFFFFF;
			for(i=player.alliedTurrets.size-1;i>=0;i--) {
				loc=player.alliedTurretInfos[i].location;
				d=loc.distanceSquaredTo(enemyLoc);
				if(d<dmin) {
					dmin=d;
					best=loc;
				}
			}
			if(best==null)
				return;
			if(dmin<=player.myLoc.distanceSquaredTo(enemyLoc))
				player.myNav.moveToForward(best);
			else
				player.myNav.moveToBackward(best);
		}
		else
			super.tryToAccomplish();
	}

}