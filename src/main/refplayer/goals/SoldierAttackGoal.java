package refplayer.goals;

import static refplayer.goals.BroadcastEnemyUnitLocationsGoal.*;
import static refplayer.message.SuperMessageStack.KEEP_TIME;
import refplayer.message.SuperMessageStack;
import refplayer.BasePlayer;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class SoldierAttackGoal extends Goal {

	RobotInfo target;

	public SoldierAttackGoal(BasePlayer bp) {
		super(bp);
	}

	public int getMaxPriority() {
		//if(player.attacking) return ATTACK_TURN;
		//else return ATTACK_SHOOT;
		return ATTACK_SHOOT;
	}

	public void shootOnly() {
		try {
			target = player.nearestOneOf(player.enemyArchons);
			if(target!=null&&myRC.canAttackSquare(target.location)) {
				myRC.attackAir(target.location);
				return;
			}
			target = player.nearestEnemyGround();
			if(target!=null&&myRC.canAttackSquare(target.location)) {
				myRC.attackGround(target.location);
				return;
			}
		} catch(Exception e) {
		}
	}

	public int getPriority() {
		RobotInfo archonTarget=player.nearestOneOf(player.enemyArchons);
		if(archonTarget!=null&&myRC.canAttackSquare(archonTarget.location)) {
			target=archonTarget;
			return ATTACK_SHOOT;
		}
		RobotInfo groundTarget=player.nearestEnemyGround();
		if(groundTarget!=null&&myRC.canAttackSquare(groundTarget.location)) {
			target=groundTarget;
			return ATTACK_SHOOT;
		}
		else if(archonTarget!=null) {
			target=archonTarget;
			return ATTACK_MOVE;
		}
		else if(groundTarget!=null) {
			target=groundTarget;
			return ATTACK_MOVE;
		}
		else
			return NEVER;
	}

	public void tryToAccomplish() {
		try {
			if(myRC.canAttackSquare(target.location)) {
				if((target.type==RobotType.ARCHON)) {
					myRC.attackAir(target.location);
				}
				else {
					myRC.attackGround(target.location);
				}
			}
			else
				player.myNav.moveToForward(target.location);
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
		}
	}
}