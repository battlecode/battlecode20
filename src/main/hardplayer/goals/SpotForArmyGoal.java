package hardplayer.goals;

import hardplayer.BasePlayer;

import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;

public class SpotForArmyGoal extends Goal {

	FindEnemyGoal findEnemyGoal;

	public SpotForArmyGoal(BasePlayer bp, FindEnemyGoal g) {
		super(bp);
		findEnemyGoal=g;
	}

	public int getMaxPriority() {
		return SPOT_FOR_TURRET;
	}

	public int getPriority() {
		if(player.alliedTurrets.size>0&&
		   battlecode.common.Clock.getRoundNum()<=player.lastKnownEnemyTime+15)
			return SPOT_FOR_TURRET;
		else
			return NEVER;
	}

	public void tryToAccomplish() {
		MapLocation enemyLoc=new MapLocation((int)(findEnemyGoal.xsum/findEnemyGoal.n)+player.myLoc.getX(),(int)(findEnemyGoal.ysum/findEnemyGoal.n)+player.myLoc.getY()), best=null;
		int i, d, dmin=0x7FFFFFFF;
		RobotInfo [] robots = player.alliedTurretInfos;
		for(i=player.alliedTurrets.size-1;i>=0;i--) {
			if((d=enemyLoc.distanceSquaredTo(robots[i].location))<dmin) {
				dmin=d;
				best=robots[i].location;
			}
		}
		robots = player.alliedChainerInfos;
		for(i=player.alliedChainers.size-1;i>=0;i--) {
			if((d=enemyLoc.distanceSquaredTo(robots[i].location))<dmin) {
				dmin=d;
				best=robots[i].location;
			}
		}
		robots = player.alliedSoldierInfos;
		for(i=player.alliedSoldiers.size-1;i>=0;i--) {
			if((d=enemyLoc.distanceSquaredTo(robots[i].location))<dmin) {
				dmin=d;
				best=robots[i].location;
			}
		}
		if(best!=null) {
			player.myNav.moveToASAPPreferFwd(best);
		}
	}

}