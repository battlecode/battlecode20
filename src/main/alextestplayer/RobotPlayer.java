package alextestplayer;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.*;
import java.util.*;

public class RobotPlayer {
	static Random rand;
	
	public static void run(RobotController rc) {
		rand = new Random();
		Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};

        MapLocation enemyHQLocation = rc.senseEnemyHQLocation();
        Direction lastDirection = null;

		while(true) {
            // do we know the enemy HQ location?
            try {
                if (enemyHQLocation == null) {
                    int test = rc.readBroadcast(0);
                    if (test != 0) {
                        enemyHQLocation = new MapLocation(rc.readBroadcast(1), rc.readBroadcast(2));
                    }
                }
            } catch (Exception e) {
                System.out.println("Early exception");
            }

			if (rc.getType() == RobotType.HQ) {
				try {					
					//Check if a robot is spawnable and spawn one if it is
					if (rc.canMove()) {
                        Direction moveDirection = enemyHQLocation == null ? directions[rand.nextInt(8)] : rc.getLocation().directionTo(enemyHQLocation);
						if (rc.senseObjectAtLocation(rc.getLocation().add(moveDirection)) == null && rc.getTeamOre() >= RobotType.FURBY.oreCost) {
							rc.spawn(moveDirection, RobotType.FURBY);
						}
					}
				} catch (Exception e) {
					System.out.println("HQ Exception");
                    e.printStackTrace();
				}
			}
			
			if (rc.getType() == RobotType.FURBY || rc.getType() == RobotType.SOLDIER) {
				try {
                    rc.setIndicatorString(0, "" + rc.senseOre(rc.getLocation()));
                    rc.setIndicatorString(1, "" + rc.getLocation());

					if (rc.isActive()) {
						int action = (rc.getRobot().getID()*rand.nextInt(101) + 50)%101;
						//Mine
						if (action < 2 && rc.getType() == RobotType.FURBY) {
                            rc.mine();
                        // build something
                        } else if (action < 20 && rc.getType() == RobotType.FURBY) {
                            Direction dir = directions[rand.nextInt(8)];
                            if (rc.canMove(dir) && rc.getTeamOre() >= RobotType.BARRACKS.oreCost) {
                                rc.build(dir, RobotType.BARRACKS);
                            }
						//Attack a random nearby enemy
						} else if (action < 50) {
							Robot[] nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,rc.getType().attackRadiusSquared,rc.getTeam().opponent());
							if (nearbyEnemies.length > 0) {
								RobotInfo robotInfo = rc.senseRobotInfo(nearbyEnemies[0]);
								rc.attackSquare(robotInfo.location);
							}

                            for (Robot r : nearbyEnemies) {
                                RobotInfo rr = rc.senseRobotInfo(r);
                                if (rr.type == RobotType.HQ) {
                                    rc.broadcast(0, 1);
                                    rc.broadcast(1, rr.location.x);
                                    rc.broadcast(2, rr.location.y);
                                }
                            }
						//Move in a random direction
						} else if (enemyHQLocation == null || action < 75 && !rc.canMove(rc.getLocation().directionTo(enemyHQLocation))) {
							Direction moveDirection = directions[rand.nextInt(directions.length)];
                            if (lastDirection != null && rc.canMove(lastDirection)) {
                                moveDirection = lastDirection;
                            }
							if (rc.canMove(moveDirection)) {
								rc.move(moveDirection);
                                lastDirection = moveDirection;
							}
                        //Move towards enemy headquarters
						} else if (action < 100) {
							Direction moveDirection = rc.getLocation().directionTo(enemyHQLocation);
							if (rc.canMove(moveDirection)) {
								rc.move(moveDirection);
							}
                        }
					}
				} catch (Exception e) {
					System.out.println("Soldier Exception");
                    e.printStackTrace();
				}
			}

            if (rc.getType() == RobotType.BARRACKS) {
				try {					
					//Check if a robot is spawnable and spawn one if it is
					if (rc.isActive()) {
                        Direction moveDirection = enemyHQLocation == null ? directions[rand.nextInt(8)] : rc.getLocation().directionTo(enemyHQLocation);
						if (rc.canMove(moveDirection) && rc.getTeamOre() > RobotType.SOLDIER.oreCost) {
							rc.spawn(moveDirection, RobotType.SOLDIER);
						}
					}
				} catch (Exception e) {
					System.out.println("Barracks Exception");
                    e.printStackTrace();
				}
            }
			
			rc.yield();
		}
	}
}
