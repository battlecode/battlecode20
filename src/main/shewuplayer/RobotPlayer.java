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

	// want to keep wall to the right of the robot
	static void move() throws Exception {
		int myID = myRC.getRobot().getID();
		RobotWallFollowingState rwfs = wallFollowingStates.get(myID);
		if (rwfs == null) {
			rwfs = new RobotWallFollowingState(WallFollowingState.WFS_FINDING_WALL);
			wallFollowingStates.put(myID, rwfs);
		}
		assert rwfs != null;
		switch (rwfs.wfs) {
			case WFS_FINDING_WALL: {
				if (myRC.canMove(myRC.getDirection())) {
					myRC.moveForward();
				} else {
					rwfs.wfs = WallFollowingState.WFS_FOUND_WALL;
					myRC.setDirection(myRC.getDirection().rotateLeft().rotateLeft());
				}
				break;
			}
			case WFS_FOUND_WALL: {
				if (myRC.canMove(myRC.getDirection())) {
					myRC.moveForward();
					MapLocation newLoc = myRC.getLocation().add(myRC.getDirection()).add(myRC.getDirection().rotateRight().rotateRight());
					if (myRC.senseObjectAtLocation(newLoc, myRC.getRobot().getRobotLevel()) != null || myRC.senseTerrainTile(newLoc).isTraversableAtHeight(myRC.getRobot().getRobotLevel())) {
						rwfs.wfs = WallFollowingState.WFS_LOST_WALL;
					}
				} else {
					myRC.setDirection(myRC.getDirection().rotateLeft().rotateLeft());
				}
				break;
			}
			case WFS_LOST_WALL: {
				if (myRC.canMove(myRC.getDirection())) {
					myRC.setDirection(myRC.getDirection().rotateRight());
					myRC.yield();
					if (myRC.canMove(myRC.getDirection())) {
						myRC.moveForward();
					}
				} else {
					rwfs.wfs = WallFollowingState.WFS_FOUND_WALL;
					myRC.setDirection(myRC.getDirection().rotateLeft());
					myRC.yield();
					if (myRC.canMove(myRC.getDirection())) {
						myRC.moveForward();
					}
				}
				break;
			}
			default:
				break;
		}
	}

	static void airMove() throws Exception {
		assert myRC.getType() == RobotType.SCOUT;
		if (myRC.canMove(myRC.getDirection())) {
			myRC.moveForward();
		} else {
			myRC.setDirection(myRC.getDirection().rotateRight());
		}
	}

	static void ArchonRun() throws Exception {
		// can't attack, only spawns
		assert myRC.getType() == RobotType.ARCHON;
		if (runCounters[RobotType.ARCHON.ordinal()] % 1024 != 0) {
			move();
			/*
		} else if (runCounters[RobotType.ARCHON.ordinal()] % 512 == 0) {
			if (myRC.getFlux() > RobotType.ARCHON.spawnCost) {
				if (myRC.canMove(myRC.getDirection())) {
					myRC.spawn(RobotType.ARCHON);
					System.out.println("Spawned archon");
				}
			}
			*/
		} else {
			if (myRC.getFlux() > RobotType.SCOUT.spawnCost) {
				if (myRC.canMove(myRC.getDirection())) {
					myRC.spawn(RobotType.SCOUT);
					System.out.println("Spawned scout");
				}
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

