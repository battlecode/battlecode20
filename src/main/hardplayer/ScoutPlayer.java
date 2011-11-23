package hardplayer;

import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

import hardplayer.goal.*;
import hardplayer.message.MessageSender;

public class ScoutPlayer extends BasePlayer {

	public ScoutPlayer(RobotController RC) {
		super(RC);
	}

	public void shoot() {
		if(myRC.isAttackActive())
			return;
		int i;
		try {
			for(i=enemies.size;i>=0;i--) {
				if(myRC.canAttackSquare(enemyInfos[i].location)&&enemyInfos[i].type!=RobotType.TOWER) {
					//System.out.println("shooting");
					myRC.attackSquare(enemyInfos[i].location,enemyInfos[i].type.level);
					return;
				}
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

	public void transferFlux() {
		if(myRC.getFlux()<GameConstants.REGEN_COST)
			return;
		int i;
		RobotInfo info;
		try {
			for(i=allies.size;i>=0;i--) {
				info = alliedInfos[i];
				if((!info.regen)&&(info.energon<info.maxEnergon)) {
					myRC.regenerate();
					return;
				}
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

	public void setGoals() {

		goals = new Goal [] {
			new HealGoal(),
			new SeekFluxGoal()
		};

	}

}
