package hardplayer.goals;

import hardplayer.BasePlayer;
import hardplayer.message.TeleporterMessageHandler;

import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

import java.util.Random;

import static battlecode.common.GameConstants.FLUX_RADIUS_SQUARED;

public class WoutAttackBuildingGoal extends Goal {

	RobotInfo target;

	public WoutAttackBuildingGoal(BasePlayer bp) {
		super(bp);
	}

	public int getMaxPriority() {
		return ATTACK_MOVE;
	}

	public int getPriority() {
		int i, d, dmin=99999;
		RobotInfo info;
		target=null;
		MapLocation myLoc=player.myLoc;
		for(i=player.enemyAuras.size-1;i>=0;i--) {
			info=player.enemyAuraInfos[i];
			if(info.energonLevel<60.&&(d=myLoc.distanceSquaredTo(info.location))<dmin) {
				dmin=d;
				target=info;
			}
		}
		for(i=player.enemyTeleporters.size-1;i>=0;i--) {
			info=player.enemyTeleporterInfos[i];
			if(info.energonLevel<25.&&(d=myLoc.distanceSquaredTo(info.location))<dmin) {
				dmin=d;
				target=info;
			}
		}
		for(i=player.enemyComms.size-1;i>=0;i--) {
			info=player.enemyCommInfos[i];
			if(info.energonLevel<25.&&(d=myLoc.distanceSquaredTo(info.location))<dmin) {
				dmin=d;
				target=info;
			}
		}
		if(target!=null)
			return ATTACK_MOVE;
		else
			return NEVER;
	}

	public void tryToAccomplish() {
		try {
			if(myRC.canAttackSquare(target.location)) {
				if(!myRC.isAttackActive())
					myRC.attackGround(target.location);
			}
			else {
				if(!myRC.isMovementActive())
					player.myNav.moveToASAPPreferFwd(target.location);
			}
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
		}
	}

}