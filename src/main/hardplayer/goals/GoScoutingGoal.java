package hardplayer.goals;

import hardplayer.BasePlayer;
import hardplayer.WoutPlayer;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
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
			if((!player.atWar)&&
			  rand.nextInt(5)==0) {
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