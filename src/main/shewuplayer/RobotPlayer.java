package shewuplayer;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;
import java.util.Map;
import java.util.HashMap;

class DetailedMapLocation {
	public MapLocation ml;
	public Direction d;

	public DetailedMapLocation(int x, int y, Direction dd) {
		ml = new MapLocation(x, y);
		d = dd;
	}

	public DetailedMapLocation(MapLocation mlml, Direction dd) {
		ml = new MapLocation(mlml.x, mlml.y);
		d = dd;
	}

	public String toString() {
		String s = "MapLocation = (" + ml.x + ", " + ml.y + ") Direction = " + d;
		return s;
	}
}

enum WallFollowingState {
	WFS_FINDING_WALL,
	WFS_FOUND_WALL,
	WFS_LOST_WALL,
}

enum ArchonState {
	AS_FINDING_ENEMY,
	AS_FOUND_ENEMY,
}

class RobotWallFollowingState implements Comparable {
	public WallFollowingState wfs;

	public RobotWallFollowingState(WallFollowingState wfs) {
		this.wfs = wfs;
	}

	public int compareTo(Object o) {
		RobotWallFollowingState oo = (RobotWallFollowingState)o;
		return this.wfs.ordinal() - oo.wfs.ordinal();
	}
}

public class RobotPlayer {
    static RobotController myRC;
	static Team myTeam;
	static Map<Integer, RobotWallFollowingState> wallFollowingStates;
	static int[] runCounters;

    public static void run(RobotController rc) {
		myRC = rc;
		myTeam = myRC.getTeam();
		wallFollowingStates = new HashMap<Integer, RobotWallFollowingState>();
		runCounters = new int[6];
        while (true) {
            try {
                /*** beginning of main loop ***/
                while (RobotPlayer.myRC.isMovementActive()) {
                    RobotPlayer.myRC.yield(); // ends robot's computation for current round
                }

				switch (RobotPlayer.myRC.getType()) {
					case ARCHON:
						ArchonRun();
						break;
					case DISRUPTER:
						DisrupterRun();
						break;
					case SCORCHER:
						ScorcherRun();
						break;
					case SOLDIER:
						SoldierRun();
						break;
				}

                myRC.yield();

                /*** end of main loop ***/
            } catch (Exception e) {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
    }

	static int compareDirections(int ax, int ay, int bx, int by) {
		int y = by - ay;
		int x2 = (ax - bx)*(ax - bx);
		int y2 = y*y;
		if (x2 + y2 != 0) {
			return (int)Math.asin(y/Math.sqrt(x2 + y2));
		} else {
			return 0;
		}
	}
	// want to keep wall to the right of the robot
	static boolean moveTo(MapLocation dest) throws Exception {
		// turn to direction of dest
		MapLocation myLoc = myRC.getLocation();
		Direction myDir = myRC.getDirection();
		int angleDiff = compareDirections(myDir.dx, myDir.dy, dest.x - myLoc.x, dest.y - myLoc.y);
		int num45s = (int)(angleDiff / 0.78539f); // pi / 4
		if (num45s > 0) {
			while (num45s > 0) {
				myDir = myDir.rotateLeft();
				num45s--;
			}
		}
		return true;
	}

	static void move() throws Exception {

	}

	static void airMove() throws Exception {
		assert myRC.getType() == RobotType.SCOUT;
		if (myRC.canMove(myRC.getDirection())) {
			myRC.moveForward();
		} else {
			myRC.setDirection(myRC.getDirection().rotateRight());
		}
	}

	static boolean canSpawn(RobotLevel level) throws Exception {
		assert myRC.getType() == RobotType.ARCHON;
		Direction d = myRC.getDirection();
		MapLocation ml = myRC.getLocation().add(d);
		return myRC.senseTerrainTile(ml).isTraversableAtHeight(level) && myRC.senseObjectAtLocation(ml, level) == null;
	}

	static void ArchonRun() throws Exception {
		// can't attack, only spawns
		assert myRC.getType() == RobotType.ARCHON;
		if (runCounters[RobotType.ARCHON.ordinal()] % 1024 != 0) {
			move();
		} else {
			if (myRC.getFlux() > RobotType.SCOUT.spawnCost && canSpawn(RobotType.SCOUT.level)) {
				myRC.spawn(RobotType.SCOUT);
				System.out.println("Spawned scout");
			}
		}
		++runCounters[RobotType.ARCHON.ordinal()];
	}

	static void DisrupterRun() throws Exception {
		if (runCounters[RobotType.DISRUPTER.ordinal()] % 8 != 0) {
			move();
		} else {
			Robot[] nearbyRobots = myRC.senseNearbyGameObjects(Robot.class);
			int i = 0;
			for (Robot r : nearbyRobots) {
				RobotInfo ri = myRC.senseRobotInfo(r);
				if (ri.team != myTeam) {
					myRC.attackSquare(ri.location, ri.robot.getRobotLevel());
				}
				if (++i == 5) {
					break;
				}
			}
		}
		++runCounters[RobotType.DISRUPTER.ordinal()];
	}

	static void ScorcherRun() throws Exception {
		// can only attack ground; attacks everywhere in attack range
		if (runCounters[RobotType.SCORCHER.ordinal()] % 8 != 0) {
			move();
		} else {
			Robot[] nearbyRobots = myRC.senseNearbyGameObjects(Robot.class);
			if (nearbyRobots.length > 0) {
				myRC.attackSquare(myRC.getLocation(), RobotLevel.ON_GROUND);
			}
		}
		++runCounters[RobotType.SCORCHER.ordinal()];
	}

	static void ScoutRun() throws Exception {
		if (runCounters[RobotType.SCOUT.ordinal()] % 8 != 0) {
			move();
		} else {
			Robot[] nearbyRobots = myRC.senseNearbyGameObjects(Robot.class);
			for (Robot r : nearbyRobots) {
				RobotInfo ri = myRC.senseRobotInfo(r);
				if (ri.team != myTeam) {
					myRC.attackSquare(ri.location, ri.robot.getRobotLevel());
				}
			}
		}
		++runCounters[RobotType.SCOUT.ordinal()];
	}

	static void SoldierRun() throws Exception {
		if (runCounters[RobotType.SOLDIER.ordinal()] % 8 != 0) {
			move();
		} else {
			Robot[] nearbyRobots = myRC.senseNearbyGameObjects(Robot.class);
			for (Robot r : nearbyRobots) {
				RobotInfo ri = myRC.senseRobotInfo(r);
				if (ri.team != myTeam) {
					myRC.attackSquare(ri.location, ri.robot.getRobotLevel());
				}
			}
		}
		++runCounters[RobotType.SOLDIER.ordinal()];
	}
}

