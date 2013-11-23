package broadcastbot;

import battlecode.common.*;

/** The example funcs player is a player meant to demonstrate basic usage of the most common commands.
 * Robots will move around randomly, occasionally mining and writing useless messages.
 * The HQ will spawn soldiers continuously. 
 */
public class RobotPlayer {
	public static void run(RobotController rc) {
        int rounds = 0;
        double[][] cows = rc.senseCowGrowth();
        MapLocation best = new MapLocation(0, 0);
        for (int i = 0; i < rc.getMapWidth(); i++) {
            for (int j = 0; j < rc.getMapHeight(); j++) {
                if (cows[i][j] > cows[best.x][best.y]) {
                    best = new MapLocation(i, j);
                }
            }
        }
        int stallTurns = 0;
		while (true) {
            rounds++;
			try {
                // Team A builds stuff and attacks around it
                // Team B destroys what it sees
                if (!rc.isActive()) {
                    continue;
                }
                if (rc.getTeam() == Team.A) {
                    //System.out.println("1 Team A sensed " + rc.senseBroadcastingRobots().length);
                    //System.out.println("2 Team A sensed " + rc.senseBroadcastingRobots(Team.A).length);
                    System.out.println("3 Team A sensed " + rc.senseBroadcastingRobots(Team.B).length);
                    if (rc.getType() == RobotType.HQ) {
                        Direction dir = rc.getLocation().directionTo(best);
                        if (rc.canMove(dir)) {
                            rc.spawn(dir);
                        }
                    } else if (rc.getType() == RobotType.SOLDIER) {
                        if (rc.getLocation().equals(best) || rc.getLocation().distanceSquaredTo(best) <= 2) {
                            rc.construct(RobotType.PASTR);
                        } else {
                            Direction dir = rc.getLocation().directionTo(best);
                            if (rc.canMove(dir)) {
                                rc.move(dir);
                                stallTurns = 0;
                            } else {
                                stallTurns++;
                                if (stallTurns > 5) {
                                    rc.construct(RobotType.NOISETOWER);
                                }
                            }
                        }
                    } else if (rc.getType() == RobotType.NOISETOWER) {
                        rc.setIndicatorString(0, "I'm a noisetower!");
                    } else if (rc.getType() == RobotType.PASTR) {
                        rc.setIndicatorString(0, "I'm a pastr!");
                        rc.broadcast(1, 1);
                    }
                    
                } else {
                    MapLocation target = rc.senseEnemyHQLocation();
                    if (rc.getType() == RobotType.HQ) {
                        Direction dir = rc.getLocation().directionTo(target);
                        if (rc.canMove(dir)) {
                            rc.spawn(dir);
                        }
                    } else if (rc.getType() == RobotType.SOLDIER) {
                            Direction dir = rc.getLocation().directionTo(target);
                            if (rc.canMove(dir)) {
                                rc.move(dir);
                            } else {
                                // attack something
                                MapLocation[] attackable = MapLocation.getAllMapLocationsWithinRadiusSq(rc.getLocation(), RobotType.SOLDIER.attackRadiusMaxSquared);
                                // attack the one farthest from best
                                MapLocation farthest = attackable[0];
                                boolean found = false;
                                for (MapLocation m : attackable) {
                                    m = new MapLocation(6, 6);
                                    if (rc.canAttackSquare(m) && (!found || m.distanceSquaredTo(best) > farthest.distanceSquaredTo(best))) {
                                        GameObject obj = rc.senseObjectAtLocation(m);
                                        if (obj == null) {
                                            continue;
                                        }
                                        RobotInfo info = rc.senseRobotInfo((Robot) obj);
                                        if (info.team == Team.A && info.energon < 1000) {
                                            farthest = m;
                                            found = true;
                                        }
                                    }
                                }
                                if (Math.random() > 0.1) {
                                    if (found && rc.canAttackSquare(farthest)) {
                                        rc.attackSquare(farthest);
                                        rc.setIndicatorString(0, "I attacked " + farthest);
                                    }
                                } else {
                                    rc.broadcast(1, 1);
                                }
                            }
                    } else if (rc.getType() == RobotType.NOISETOWER) {
                    } else if (rc.getType() == RobotType.PASTR) {
                        rc.setIndicatorString(0, "I'm a pastr!");
                        rc.broadcast(1, 1);
                    }
                }

				// End turn
				rc.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
