package hardplayer.goal;

import battlecode.common.*;

import hardplayer.ArchonPlayer;
import hardplayer.Static;

public class MakeArmyGoal extends Static implements Goal {

	private static RobotType typeToSpawn;

	public RobotType chooseTypeToSpawn() {
		return RobotType.SOLDIER;
	}

	public int maxPriority() {
		return MAKE_ARMY;
	}

	public int priority() {
		typeToSpawn = chooseTypeToSpawn();
		if(myRC.getFlux()>=typeToSpawn.spawnCost+ArchonPlayer.MIN_FLUX)
			return MAKE_ARMY;
		else
			return 0;
	}
	
	static public boolean canSpawn(RobotLevel level, Direction dir) throws GameActionException {
		MapLocation loc = myLoc.add(dir);
		return myRC.senseTerrainTile(loc).isTraversableAtHeight(level)&&(myRC.senseObjectAtLocation(loc,level)==null);
	}

	public void execute() {
		try {
			if(canSpawn(typeToSpawn.level,myDir))
				myRC.spawn(typeToSpawn);
			else {
				for(int i=7;i>=0;i--) {
					if(canSpawn(typeToSpawn.level,Direction.values()[i]))
						myRC.setDirection(Direction.values()[i]);
				}
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

}
