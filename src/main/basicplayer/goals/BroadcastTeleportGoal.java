package basicplayer.goals;

import java.util.Random;

import basicplayer.BasePlayer;
import basicplayer.message.TeleporterMessageHandler;

import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotLevel;

import static battlecode.common.GameConstants.*;

public class BroadcastTeleportGoal extends Goal {

	TeleporterMessageHandler tmh;
	Random rand = new Random();

	public BroadcastTeleportGoal(BasePlayer bp, TeleporterMessageHandler tmh) {
		super(bp);
		this.tmh = tmh;
	}

	public int getMaxPriority() {
		return BROADCAST_TELEPORT;
	}

	public int getPriority() {
		if(player.enemyArchons.size+player.enemySoldiers.size+player.enemyTurrets.size+player.enemyChainers.size+player.enemyWouts.size>0&&tmh.locs!=null&&tmh.locs.length>=2&&!myRC.isTeleporting())
			return BROADCAST_TELEPORT;
		else
			return NEVER;
	}

	public void tryToAccomplish() {
		MapLocation teleLoc = tmh.locs[0];
		if(player.myLoc.distanceSquaredTo(teleLoc)<=2) {
			try {
				Robot robot = myRC.senseGroundRobotAtLocation(teleLoc);
				if(robot==null) {
					tmh.locs=null;
					return;
				}
				/* ArchonDistributeFluxGoal handles flux distribution now
				int fluxneeded = Math.min(TELEPORT_FLUX_COST-myRC.senseRobotInfo(robot).flux,myRC.getFlux());
				if(fluxneeded>0)
					myRC.transferFlux(fluxneeded,teleLoc,RobotLevel.ON_GROUND);
				*/
			} catch(Exception e) {
				BasePlayer.debug_stackTrace(e);
			}
		}
		player.mySender.sendTeleport(tmh.locs[1+rand.nextInt(tmh.locs.length-1)]);
	}

}