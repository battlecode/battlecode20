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
		RobotInfo info;
		try {
			for(i=enemies.size;i>=0;i--) {
				info = enemyInfos[i];
				if(myRC.canAttackSquare(info.location)&&info.flux>0) {
					//System.out.println("shooting");
					myRC.attackSquare(info.location,info.type.level);
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
				if((!info.regen)&&(info.energon<info.type.maxEnergon)) {
					myRC.regenerate();
					return;
				}
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

	public void broadcast() {
		if(enemies.size>enemyTowers.size+enemyScouts.size+1)
			sendEnemies();
	}

	public void sendEnemies() {
		int i = enemies.size+1, j = enemies.size-enemyTowers.size-enemyScouts.size-1;
		MapLocation [] locs = new MapLocation [j];
		while(--i>=0) {
			if(enemyInfos[i].type!=RobotType.TOWER&&
				enemyInfos[i].type!=RobotType.SCOUT)
				locs[--j]=enemyInfos[i].location;
		}
		mySender.sendEnemyUnits(locs);
	}

	public void setGoals() {

		goals = new Goal [] {
			new HealGoal(),
			new SeekFluxGoal(),
			new ScoutFindNodeGoal()
		};

	}

}
