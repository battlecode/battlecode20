package hqherder;

import java.util.*;

import battlecode.common.*;

public class RobotPlayer {
    //static final int coverage = 3; // increase this to do more full circles
    static final int distance = 300; // radius squared to use

    public static class SortByAngle implements Comparator<MapLocation> {
        MapLocation center;
        public SortByAngle(MapLocation center) {
            this.center = center;
        }
        public int compare(MapLocation a, MapLocation b) {
            double angle1 = Math.atan2(a.y - center.y, a.x - center.x);
            double angle2 = Math.atan2(b.y - center.y, b.x - center.x);
            return angle1 - angle2 < 0 ? -1 : 1;
        }
    }

    public static boolean isValid(MapLocation m, int width, int height) {
        return m.x >= -1 && m.x <= width && m.y >= -1 && m.y <= height;
    }

    public static void run(RobotController rc) {
        try {
            MapLocation myHQ = rc.senseHQLocation();
            int height = rc.getMapHeight();
            int width = rc.getMapWidth();

            if (rc.getType() == RobotType.HQ) {
                boolean noiseToweSpawned = false;
                while (true) {
                    if (rc.isActive()) {
                        Direction dir = myHQ.directionTo(rc.senseEnemyHQLocation());
                        for (int i = 0; i < 8; i++) {
                            if (rc.canMove(dir)) {
                                rc.spawn(dir);
                                break;
                            } else {
                                dir = dir.rotateRight();
                            }
                        }
                    }

                    // do some attacking (does not depend on activity)

                    if (rc.isActive()) {
                        boolean attacked = false;
                        Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, RobotType.HQ.attackRadiusMaxSquared, rc.getTeam().opponent());
                        if (enemies.length > 0) {
                            // randomly attack one (should probably use some heuristics)
                            int idx = (int) (Math.random() * enemies.length);
                            RobotInfo info = rc.senseRobotInfo(enemies[idx]);
                            rc.attackSquare(info.location);
                            attacked = true;
                        }

                        if (!attacked) {
                            Robot[] enemies2 = rc.senseNearbyGameObjects(Robot.class, RobotType.HQ.sensorRadiusSquared, rc.getTeam().opponent());
                            for (Robot r : enemies2) {
                                if (attacked) {
                                    break;
                                }
                                RobotInfo info = rc.senseRobotInfo(r);
                                if (info.location.add(info.location.directionTo(myHQ)).distanceSquaredTo(myHQ) <= RobotType.HQ.attackRadiusMaxSquared) {
                                    rc.attackSquare(info.location.add(info.location.directionTo(myHQ)));
                                    attacked = true;
                                }
                            }
                        }
                    }

                    rc.yield();
                }
            } else if (rc.getType() == RobotType.NOISETOWER) {
                // FIND LOCATIONS TO ATTACK

                /*
                MapLocation[] allLocs = MapLocation.getAllMapLocationsWithinRadiusSq(rc.getLocation(), distance);
                int goodLocs = 0;
                for (MapLocation loc : allLocs) {
                    if (loc.equals(myHQ) || !isValid(loc, width, height)) {
                        continue;
                    }
                    Direction fromHQ = myHQ.directionTo(loc);
                    MapLocation next = loc.add(fromHQ);
                    if (next.distanceSquaredTo(rc.getLocation()) > distance || !isValid(next, width, height)) {
                        goodLocs++;
                    }
                }
                MapLocation[] toAttack = new MapLocation[goodLocs];
                int toAttackIndex = 0;
                for (MapLocation loc : allLocs) {
                    if (loc.equals(myHQ) || !isValid(loc, width, height)) {
                        continue;
                    }
                    Direction fromHQ = myHQ.directionTo(loc);
                    MapLocation next = loc.add(fromHQ);
                    if (next.distanceSquaredTo(rc.getLocation()) > distance || !isValid(next, width, height)) {
                        toAttack[toAttackIndex++] = loc;
                    }
                }
                Arrays.sort(toAttack, new SortByAngle(myHQ));
                */

                MapLocation[] toAttack = new MapLocation[8];
                Direction dir = myHQ.directionTo(rc.senseEnemyHQLocation());
                for (int i = 0; i < 8; i++) {
                    MapLocation cur = myHQ;
                    MapLocation next = cur.add(dir);
                    while (next.distanceSquaredTo(rc.getLocation()) < distance && isValid(next, width, height)) {
                        cur = next;
                        next = next.add(dir);
                    }
                    toAttack[i] = cur;
                    dir = dir.rotateRight();
                }

                // IN ORDER, ATTACK THEM
                int curPos = 0;
                MapLocation last = null;
                while (true) {
                    if (rc.isActive()) {
                        if (last == null) {
                            last = toAttack[curPos];
                        } else {
                            last = last.add(last.directionTo(myHQ));
                            if (last.equals(myHQ)) {
                                curPos = (curPos + 1) % toAttack.length;
                                last = toAttack[curPos];
                            }
                        }
                        rc.attackSquareLight(last);
                    }

                    rc.yield();
                }
            } else if (rc.getType() == RobotType.SOLDIER) {
                while (true) {
                    if (rc.isActive()) {
                        if (Clock.getRoundNum() < 10) {
                            rc.construct(RobotType.NOISETOWER);
                        } else {
                            rc.construct(RobotType.PASTR);
                        }
                    }

                    rc.yield();
               }
            } else if (rc.getType() == RobotType.PASTR) {
                while (true) {
                    rc.yield();
                }
            } else {
                while (true) {
                    rc.yield();
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
}
