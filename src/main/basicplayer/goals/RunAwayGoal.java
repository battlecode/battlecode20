package basicplayer.goals;

import basicplayer.BasePlayer;
import basicplayer.message.TeleporterMessageHandler;

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
		MapLocation teleLoc = tmh.locs[0];
		if(player.myLoc.distanceSquaredTo(teleLoc)>2) {
			player.myNav.moveToASAP(teleLoc);
		}
		
	}

}