package hardplayer;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;

import hardplayer.goal.*;
import hardplayer.message.MessageSender;

public class SoldierPlayer extends BasePlayer {

	public SoldierPlayer(RobotController RC) {
		super(RC);
	}

	public void shoot() {
		if(myRC.isAttackActive())
			return;
		int i;
		try {
			for(i=enemies.size;i>=0;i--) {
				if(myRC.canAttackSquare(enemyInfos[i].location)) {
					//System.out.println("shooting");
					myRC.attackSquare(enemyInfos[i].location,enemyInfos[i].type.level);
					return;
				}
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

	public void broadcast() {
		if(justNowAtWar&&enemies.size>=0) {
			MapLocation enemyLoc = closest(enemies,base).location;
			mySender.sendFindEnemy(enemyLoc,enemies.size+1);
		}
	}

	public void setGoals() {

		goals = new Goal [] {
			new SoldierAttackGoal(),
			new FindEnemyGoal(),
			new FindNodeGoal(),
			new SeekFluxGoal()
		};

	}

}
