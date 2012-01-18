package refplayer;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;

import refplayer.goal.*;
import refplayer.message.MessageSender;

public class ScorcherPlayer extends BasePlayer {

	public ScorcherPlayer(RobotController RC) {
		super(RC);
	}

	public void shoot() {
		if(myRC.isAttackActive())
			return;
		int i;
		try {
			if(enemies.size-enemyScouts.size>allies.size-alliedScouts.size)
				myRC.attackSquare(null,null);
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
			new ScorcherAttackGoal(),
			new FindEnemyGoal(),
			new FindNodeGoal(),
			new SeekFluxGoal()
		};

	}

}
