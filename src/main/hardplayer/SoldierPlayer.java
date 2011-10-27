package hardplayer;

import battlecode.common.RobotController;

import hardplayer.goal.*;

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
			if(e.getCause()!=null)
				debug_stackTrace(e.getCause());
			else
				debug_stackTrace(e);
		}
	}

	public void setGoals() {

		goals = new Goal [] {
			new SoldierAttackGoal(),
			new FindNodeGoal(),
			new SeekFluxGoal()
		};

	}

}
