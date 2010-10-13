package teleportingplayer.goals;

import teleportingplayer.BasePlayer;
import teleportingplayer.message.TeleporterMessageHandler;

import battlecode.common.MapLocation;
import battlecode.common.RobotLevel;

import static battlecode.common.GameConstants.*;

public class RunAwayGoal extends Goal {

	TeleporterMessageHandler tmh;

	public RunAwayGoal(BasePlayer bp, TeleporterMessageHandler tmh) {
		super(bp);
		this.tmh = tmh;
	}

	public int getMaxPriority() {
		return RUN_AWAY;
	}

	public int getPriority() {
		if(player.enemyArchons.size+player.enemySoldiers.size+player.enemyTurrets.size+player.enemyChainers.size+player.enemyWouts.size>0&&tmh.locs!=null)
			return RUN_AWAY;
		else
			return NEVER;
	}

	public void tryToAccomplish() {
		MapLocation teleLoc = tmh.locs[0], loc;
		int d, dmin=player.myLoc.distanceSquaredTo(teleLoc);
		int i;
		for(i=player.alliedTeleporters.size-1;i>=0;i--) {
			loc = player.alliedTeleporterInfos[i].location;
			d = player.myLoc.distanceSquaredTo(loc);
			if(d<dmin) {
				dmin=d;
				teleLoc = loc;
			}
		}
		if(player.myLoc.distanceSquaredTo(teleLoc)>2) {
			player.myNav.moveToASAP(teleLoc);
		}
		
	}

}