package refplayer.goals;

import refplayer.BasePlayer;
import refplayer.WoutPlayer;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

import java.util.Random;

public class GoScoutingGoal extends Goal {

	public static final int SCOUTING_TIME = 150;
	
	public Direction scoutingDir;
	int scoutingStartTime;

	WoutPlayer splayer;
	Random rand;

	public GoScoutingGoal(WoutPlayer sp) {
		super(sp);
		splayer=sp;
		rand = new Random(sp.myID);
	}

	public int getMaxPriority() {
		return GO_SCOUTING;
	}

	public int getPriority() {
		int t=Clock.getRoundNum();
		if(splayer.scouting) {
			// if we've seen an enemy, don't forget that we want
			// to get the message out
			if(t>=scoutingStartTime+SCOUTING_TIME&&!player.atWar) {
				splayer.scouting=false;
				return NEVER;
			}
			else return GO_SCOUTING;
		}
		else {
			if(rand.nextInt(player.atWar?15:5)==0) {
				scoutingStartTime=t;
				scoutingDir=BasePlayer.directions[rand.nextInt(8)];
				splayer.scouting=true;
				//BasePlayer.debug_println("going scouting");
				return GO_SCOUTING;
			}
			else return NEVER;
		}
	}

	public void tryToAccomplish() {
		if(myRC.getFlux()>=3000&&myRC.canMove(myRC.getDirection())) {
			try {
				if(myRC.getFlux()>=4500) {
					myRC.spawn(RobotType.AURA);
					return;
				}
				RobotInfo closest = player.nearestOneOf(player.alliedAuras);
				if(closest!=null) {
					MapLocation spawnLoc=player.myLoc.add(myRC.getDirection());
					int d=closest.location.distanceSquaredTo(spawnLoc);
					if(d<=25&&d>=16) {
						myRC.spawn(RobotType.AURA);
						return;
					}
				}
			} catch(Exception e) {
				BasePlayer.debug_stackTrace(e);
			}
		}
		if(scoutingDir.isDiagonal()) {
			if(myRC.senseTerrainTile(player.multipleAddDirection(player.myLoc,scoutingDir.rotateLeft(),5)).getType()==TerrainTile.TerrainType.OFF_MAP) {
				if(myRC.senseTerrainTile(player.multipleAddDirection(player.myLoc,scoutingDir.rotateRight(),5)).getType()==TerrainTile.TerrainType.OFF_MAP)
					scoutingDir=scoutingDir.opposite();
				else
					scoutingDir=scoutingDir.rotateRight().rotateRight();
			}
			else {
				if(myRC.senseTerrainTile(player.multipleAddDirection(player.myLoc,scoutingDir.rotateRight(),5)).getType()==TerrainTile.TerrainType.OFF_MAP)
					scoutingDir=scoutingDir.rotateLeft().rotateLeft();
			}
		}
		else {
			if(myRC.senseTerrainTile(player.multipleAddDirection(player.myLoc,scoutingDir,5)).getType()==TerrainTile.TerrainType.OFF_MAP)
				scoutingDir=scoutingDir.opposite();
		}
		if(scoutingStartTime>player.lastKnownEnemyTime) {
			player.myNav.moveToASAP(player.multipleAddDirection(player.myLoc,scoutingDir,8));
		}
		else {
			player.myNav.moveToASAP(player.nearestAlliedArchon());
		}
	}
}