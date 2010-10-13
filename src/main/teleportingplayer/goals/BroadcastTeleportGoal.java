package teleportingplayer.goals;

import java.util.Random;

import teleportingplayer.BasePlayer;
import teleportingplayer.message.TeleporterMessageHandler;

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
		if(player.enemyArchons.size+player.enemySoldiers.size+player.enemyTurrets.size+player.enemyChainers.size+player.enemyWouts.size>0&&tmh.locs!=null&&tmh.locs.length>=2&&!myRC.isTeleporting()&&player.alliedTeleporters.size>0)
			return BROADCAST_TELEPORT;
		else
			return NEVER;
	}

	public void tryToAccomplish() {
		int i;
		MapLocation dest = tmh.locs[1+rand.nextInt(tmh.locs.length-1)];
		if(player.myLoc.distanceSquaredTo(dest)<=9) return;
		player.mySender.sendTeleport(dest);
	}

}