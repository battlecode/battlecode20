package hardplayer.goals;

import hardplayer.BasePlayer;
import battlecode.common.Clock;

import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;

public class GoodVisionBroadcastEnemyUnitLocationsGoal extends BroadcastEnemyUnitLocationsGoal {

	static final int expectedBroadcastsPerTurn=2;
	
	public GoodVisionBroadcastEnemyUnitLocationsGoal(BasePlayer bp, FindEnemyGoal g) {
		super(bp,g);
		//findEnemyGoal = g;
	}

	public int getPriority() {
		if(player.enemyArchons.size+
		   player.enemySoldiers.size+
		   player.enemyTurrets.size+
		   player.enemyChainers.size+
		   player.enemyWouts.size==0) {
			return NEVER;
		}
		int i, numCloser=0;
		MapLocation enemyLoc=findEnemyGoal.getEnemyLoc();
		RobotInfo [] robots = player.alliedArchonInfos;
		int myDist = player.myLoc.distanceSquaredTo(enemyLoc);
		for(i=player.alliedArchons.size-1;i>=0;i--) {
			if(robots[i].location.distanceSquaredTo(enemyLoc)<myDist) numCloser++;
		}
		robots = player.alliedWoutInfos;
		for(i=player.alliedWouts.size-1;i>=0;i--) {
			if(robots[i].location.distanceSquaredTo(enemyLoc)<myDist) numCloser++;
		}
		if(numCloser<=2) return BROADCAST_ENEMY_UNIT_LOCATIONS;
		else return NEVER;
	}

}