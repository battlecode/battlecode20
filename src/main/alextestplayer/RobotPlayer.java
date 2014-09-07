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
						if (rc.canMove(moveDirection) && rc.getTeamOre() >= 500) {
							rc.spawn(moveDirection, RobotType.FURBY);
						}
					}
				} catch (Exception e) {
					System.out.println("HQ Exception");
                    e.printStackTrace();
				}
			}

			if (rc.getType() == RobotType.MINERFACTORY) {
				try {					
					//Check if a robot is spawnable and spawn one if it is
					if (rc.canMove()) {
                        Direction moveDirection = enemyHQLocation == null ? directions[rand.nextInt(8)] : rc.getLocation().directionTo(enemyHQLocation);
						if (rc.canMove(moveDirection) && rc.getTeamOre() >= 500) {
							//rc.spawn(moveDirection, RobotType.MINER);
						}
					}
				} catch (Exception e) {
					System.out.println("HQ Exception");
                    e.printStackTrace();
				}
			}

			if (rc.getType() == RobotType.BARRACKS) {
				try {					
					//Check if a robot is spawnable and spawn one if it is
					if (rc.canMove()) {
                        Direction moveDirection = enemyHQLocation == null ? directions[rand.nextInt(8)] : rc.getLocation().directionTo(enemyHQLocation);
						if (rc.canMove(moveDirection) && rc.getTeamOre() >= 500 && rand.nextInt(100) < 1) {
							rc.spawn(moveDirection, RobotType.BASHER);
						}
					}
				} catch (Exception e) {
					System.out.println("HQ Exception");
                    e.printStackTrace();
				}
			}

			if (rc.getType() == RobotType.HELIPAD) {
				try {					
					//Check if a robot is spawnable and spawn one if it is
					if (rc.canMove()) {
                        Direction moveDirection = enemyHQLocation == null ? directions[rand.nextInt(8)] : rc.getLocation().directionTo(enemyHQLocation);
						if (rc.canMove(moveDirection) && rc.getTeamOre() >= 500 && rand.nextInt(100) < 1) {
							//rc.spawn(moveDirection, RobotType.DRONE);
						}
					}
				} catch (Exception e) {
					System.out.println("helipad Exception");
                    e.printStackTrace();
				}
			}

			if (rc.getType() == RobotType.METABUILDER) {
				try {					
					//Check if a robot is spawnable and spawn one if it is
					if (rc.canMove()) {
                        Direction moveDirection = enemyHQLocation == null ? directions[rand.nextInt(8)] : rc.getLocation().directionTo(enemyHQLocation);
						if (rc.canMove(moveDirection) && rc.getTeamOre() >= 500) {
							rc.spawn(moveDirection, RobotType.BUILDER);
						}
					}
				} catch (Exception e) {
					System.out.println("metabuilder Exception");
                    e.printStackTrace();
				}
			}

			if (rc.getType() == RobotType.AEROSPACELAB) {
				try {					
					//Check if a robot is spawnable and spawn one if it is
					if (rc.canMove()) {
                        Direction moveDirection = enemyHQLocation == null ? directions[rand.nextInt(8)] : rc.getLocation().directionTo(enemyHQLocation);
						if (rc.canMove(moveDirection) && rc.getTeamOre() >= 500) {
							rc.spawn(moveDirection, RobotType.LAUNCHER);
						}
					}
				} catch (Exception e) {
					System.out.println("aerospacelab Exception");
                    e.printStackTrace();
				}
			}

            if (rc.getType() == RobotType.LAUNCHER) {
                try {
                    if (rc.isActive()) {
                        int action = (rc.getRobot().getID()*rand.nextInt(101) + 50)%101;
                        Direction moveDirection = directions[rand.nextInt(8)];
                        if (action < 50 && rc.getMissileCount() > 0 && rc.canMove(moveDirection)) {
                            // launch missile
                            rc.launchMissile(moveDirection);
						} else if (action < 100) {
                            if (rc.canMove(moveDirection)) {
                                rc.move(moveDirection);
                            }
                        }
                    }
				} catch (Exception e) {
					System.out.println("launcher Exception");
                    e.printStackTrace();
				}
            }

            if (rc.getType() == RobotType.MISSILE) {
                try {
                    if (rc.isActive()) {
                        int action = (rc.getRobot().getID()*rand.nextInt(101) + 50)%101;
                        Direction moveDirection = directions[rand.nextInt(8)];
                        if (action < 10) {
                            rc.explode();
						} else if (action < 100) {
                            if (rc.canMove(moveDirection)) {
                                rc.move(moveDirection);
                            }
                        }
                    }
				} catch (Exception e) {
					System.out.println("missile Exception");
                    e.printStackTrace();
				}
            }

            if (rc.getType() == RobotType.MINER) {
                try {
                    if (rc.isActive()) {
                        int action = (rc.getRobot().getID()*rand.nextInt(101) + 50)%101;
                        if (action < 50) {
                            rc.mine();
						} else if (action < 100) {
                            Direction moveDirection = directions[rand.nextInt(8)];
                            if (rc.canMove(moveDirection)) {
                                rc.move(moveDirection);
                            }
                        }
                    }
				} catch (Exception e) {
					System.out.println("miner Exception");
                    e.printStackTrace();
				}
            }

            if (rc.getType() == RobotType.SOLDIER || rc.getType() == RobotType.DRONE || rc.getType() == RobotType.BASHER) {
                try {
                    int action = (rc.getRobot().getID()*rand.nextInt(101) + 50)%101;
                    //Attack a random nearby enemy
                    if (action < 50) {
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
                    //Move towards enemy headquarters
                    } else if (action < 100) {
                        Direction moveDirection = rc.getLocation().directionTo(enemyHQLocation);
                        if (rc.canMove(moveDirection)) {
                            rc.move(moveDirection);
                        } else {
                            moveDirection = moveDirection.rotateLeft();
                            if (rc.canMove(moveDirection)) {
                                rc.move(moveDirection);
                            } else {
                                moveDirection = moveDirection.rotateRight().rotateRight();
                                if (rc.canMove(moveDirection)) {
                                    rc.move(moveDirection);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
			
			if (rc.getType() == RobotType.FURBY || rc.getType() == RobotType.BUILDER) {
				try {
                    rc.setIndicatorString(0, "" + rc.senseOre(rc.getLocation()));
                    rc.setIndicatorString(1, "" + rc.getLocation());

					if (rc.isActive()) {
						int action = (rc.getRobot().getID()*rand.nextInt(101) + 50)%101;
                        boolean shouldBuild = rc.getLocation().distanceSquaredTo(rc.senseHQLocation()) > 50 && rc.getTeamOre() > 500;
                        Direction dir = directions[rand.nextInt(8)];
						//Mine
						if (action < 30 && rc.getType() == RobotType.FURBY) {
                            rc.mine();
                        // build
                        } else if (action < 40 && shouldBuild) {
                            if (rc.canBuild(dir, RobotType.BARRACKS)) {
                                rc.build(dir, RobotType.BARRACKS);
                            }
                        } else if (action < 45 && shouldBuild) {
                            if (rc.canBuild(dir, RobotType.METABUILDER)) {
                                rc.build(dir, RobotType.METABUILDER);
                            }
                        } else if (action < 50 && shouldBuild) {
                            if (rc.canBuild(dir, RobotType.MINERFACTORY)) {
                                rc.build(dir, RobotType.MINERFACTORY);
                            }
                        } else if (action < 55 && shouldBuild) {
                            if (rc.canBuild(dir, RobotType.ENGINEERINGBAY)) {
                                rc.build(dir, RobotType.ENGINEERINGBAY);
                            }
                        } else if (action < 65 && shouldBuild) {
                            if (rc.canBuild(dir, RobotType.HANDWASHSTATION)) {
                                rc.build(dir, RobotType.HANDWASHSTATION);
                            }
                        } else if (action < 70 && shouldBuild) {
                            if (rc.canBuild(dir, RobotType.BIOMECHATRONICRESEARCHLAB)) {
                                rc.build(dir, RobotType.BIOMECHATRONICRESEARCHLAB);
                            }
                        } else if (action < 80 && shouldBuild) {
                            if (rc.canBuild(dir, RobotType.TECHNOLOGYINSTITUTE)) {
                                rc.build(dir, RobotType.TECHNOLOGYINSTITUTE);
                            }
                        } else if (action < 85 && shouldBuild) {
                            if (rc.canBuild(dir, RobotType.HELIPAD)) {
                                rc.build(dir, RobotType.HELIPAD);
                            }
                        } else if (action < 90 && shouldBuild) {
                            if (rc.canBuild(dir, RobotType.AEROSPACELAB)) {
                                rc.build(dir, RobotType.AEROSPACELAB);
                            }
                        //Move towards enemy headquarters
						} else if (action < 100) {
							Direction moveDirection = rc.getLocation().directionTo(enemyHQLocation);
							if (rc.canMove(moveDirection)) {
								rc.move(moveDirection);
							} else {
                                moveDirection = moveDirection.rotateLeft();
                                if (rc.canMove(moveDirection)) {
                                    rc.move(moveDirection);
                                } else {
                                    moveDirection = moveDirection.rotateRight().rotateRight();
                                    if (rc.canMove(moveDirection)) {
                                        rc.move(moveDirection);
                                    }
                                }
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
                    /*
					if (rc.isActive()) {
                        Direction moveDirection = enemyHQLocation == null ? directions[rand.nextInt(8)] : rc.getLocation().directionTo(enemyHQLocation);
						if (rc.canMove(moveDirection) && rc.getTeamOre() > RobotType.SOLDIER.oreCost) {
							rc.spawn(moveDirection, RobotType.SOLDIER);
						}
					}
                    */
				} catch (Exception e) {
					System.out.println("Barracks Exception");
                    e.printStackTrace();
				}
            }

            if (rc.getType() == RobotType.ENGINEERINGBAY) {
                try {
                    if (rc.canMove()) {
                        if (rc.checkResearchProgress(Upgrade.IMPROVEDBUILDING) == 0 && rc.getTeamOre() >= Upgrade.IMPROVEDBUILDING.oreCost) {
                            rc.researchUpgrade(Upgrade.IMPROVEDBUILDING);
                        } else if (rc.checkResearchProgress(Upgrade.IMPROVEDMINING) == 0 && rc.getTeamOre() >= Upgrade.IMPROVEDMINING.oreCost) {
                            rc.researchUpgrade(Upgrade.IMPROVEDMINING);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Engineering Bay exception");
                    e.printStackTrace();
                }
            }

            if (rc.getType() == RobotType.BIOMECHATRONICRESEARCHLAB) {
                try {
                    if (rc.canMove()) {
                        if (rc.checkResearchProgress(Upgrade.REGENERATIVEMACHINERY) == 0 && rc.getTeamOre() >= Upgrade.REGENERATIVEMACHINERY.oreCost) {
                            rc.researchUpgrade(Upgrade.REGENERATIVEMACHINERY);
                        } else if (rc.checkResearchProgress(Upgrade.NEUROMORPHICS) == 0 && rc.getTeamOre() >= Upgrade.NEUROMORPHICS.oreCost) {
                            rc.researchUpgrade(Upgrade.NEUROMORPHICS);
                        } else if (rc.checkResearchProgress(Upgrade.CONTROLLEDECOPHAGY) == 0 && rc.getTeamOre() >= Upgrade.CONTROLLEDECOPHAGY.oreCost) {
                            rc.researchUpgrade(Upgrade.CONTROLLEDECOPHAGY);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("BRL exception");
                    e.printStackTrace();
                }
            }
			
			rc.yield();
		}
	}
}
