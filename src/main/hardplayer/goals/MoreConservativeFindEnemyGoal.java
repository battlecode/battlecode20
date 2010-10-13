package hardplayer.goals;

import hardplayer.message.SuperMessageStack;
import hardplayer.BasePlayer;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotInfo;

public class MoreConservativeFindEnemyGoal extends FindEnemyGoal {

	public MoreConservativeFindEnemyGoal(BasePlayer bp) {
		super(bp);
	}

	public void tryToAccomplish() {
		MapLocation enemyLoc=getEnemyLoc();
		int i;
		int dist=player.myLoc.distanceSquaredTo(enemyLoc);
		//System.out.println("Dist to enemy = "+dist);
		/*
		for(i=player.alliedWouts.size-1;i>=0;i--) {
			if(player.alliedWouts.robotInfos[i].location.distanceSquaredTo(enemyLoc)<dist) {
				player.myNav.moveToForward(enemyLoc);
				return;
			}
		}
		*/
		MapLocation [] archons = myRC.senseAlliedArchons();
		for(i=archons.length-1;i>=0;i--) {
			//int dist2=archons[i].distanceSquaredTo(enemyLoc);
			//System.out.println("Dist from archon to enemy = "+dist2);
			if(archons[i].distanceSquaredTo(enemyLoc)<dist){
				player.myNav.moveToForward(enemyLoc);
				return;
			}
		}
		try {
			Direction dir=player.myLoc.directionTo(enemyLoc);
			if(dir!=Direction.OMNI)
				myRC.setDirection(dir);
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
		}
	}

}