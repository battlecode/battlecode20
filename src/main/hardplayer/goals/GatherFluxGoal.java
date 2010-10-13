
package hardplayer.goals;

import hardplayer.BasePlayer;
import hardplayer.WoutPlayer;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.TerrainTile;

import java.util.Random;

public class GatherFluxGoal extends Goal {

	public static final int SCOUTING_TIME = 150;
	
	public Direction scoutingDir;
	int scoutingStartTime;

	boolean gathering;

	Random rand;

	public GatherFluxGoal(BasePlayer sp) {
		super(sp);
		rand = new Random(sp.myID);
	}

	public int getMaxPriority() {
		return GATHER_FLUX;
	}

	public int getPriority() {
		int t=Clock.getRoundNum();
		if(gathering) {
			// if we've seen an enemy, don't forget that we want
			// to get the message out
			if(t>=scoutingStartTime+SCOUTING_TIME) {
				gathering=false;
				return NEVER;
			}
			else return GATHER_FLUX;
		}
		else {
			if((player.atWar)&&
			  rand.nextInt(5)==0) {
				scoutingStartTime=t;
				scoutingDir=BasePlayer.directions[rand.nextInt(8)];
				gathering=true;
				//BasePlayer.debug_println("going scouting");
				return GATHER_FLUX;
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
		player.myNav.moveToASAP(player.multipleAddDirection(player.myLoc,scoutingDir,8));

	}
}