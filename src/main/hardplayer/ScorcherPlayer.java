package hardplayer;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;

import hardplayer.goal.*;
import hardplayer.message.MessageSender;

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

		FindNodeGoal fng = new FindNodeGoal();
		FindEnemyGoal feg = new FindEnemyGoal();

		goals = new Goal [] {
			new ScorcherAttackGoal(),
			feg,
			fng,
			new SeekFluxGoal()
		};

		handlers[MessageSender.MSG_EXPLORE] = fng;
		handlers[MessageSender.MSG_ENEMY_2] = feg;

	}

}
