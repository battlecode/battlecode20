package refplayer.goals;

import refplayer.BasePlayer;
import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class BroadcastEnemyUnitLocationsGoal extends Goal {

	// This is a nerfed version of the goal that hardplayer uses.
	// Most of the ints are always zero, because hardplayer fills them
	// with something and I didn't want to worry about braking the code.

	static final int expectedBroadcastsPerTurn=2;
	FindEnemyGoal findEnemyGoal;

	public BroadcastEnemyUnitLocationsGoal(BasePlayer bp, FindEnemyGoal g){
	    super(bp);
		findEnemyGoal=g;
	}
	
	public int getMaxPriority() { return BROADCAST_ENEMY_UNIT_LOCATIONS; }

	public int getPriority() {
		if(player.enemyArchons.size+
		   player.enemySoldiers.size+
		   player.enemyTurrets.size+
		   player.enemyChainers.size==0) {
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

	public void tryToAccomplish() {
		MapLocation [] locs;
		int [] ints;
		int unitHealth;
		RobotInfo info;
		// broadcast everything to cannons
		// cannons at front, archons at back for easy reference
		// shoot comms+teleporters if you see them, but don't bother messaging
		int numEnemies=player.enemyArchons.size+
			player.enemySoldiers.size+
			player.enemyChainers.size+
			player.enemyTurrets.size;
		locs = new MapLocation [numEnemies+1];
		ints = new int [numEnemies+3];
		//locs[0]=myRC.getLocation();
		int i,j=0;
		RobotInfo [] infos = player.enemyArchonInfos;
		for(i=player.enemyArchons.size-1;i>=0;i--) {
			locs[++j]=infos[i].location;
		}
		infos=player.enemySoldierInfos;
		for(i=player.enemySoldiers.size-1;i>=0;i--) {
			locs[++j]=infos[i].location;
		}
		infos=player.enemyChainerInfos;
		for(i=player.enemyChainers.size-1;i>=0;i--) {
			locs[++j]=infos[i].location;
		}
		infos=player.enemyTurretInfos;
		for(i=player.enemyTurrets.size-1;i>=0;i--) {
			locs[++j]=infos[i].location;
		}
		player.mySender.sendEnemyUnits(ints,locs);
	}	

}
