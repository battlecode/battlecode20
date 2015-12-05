package examplefuncsplayer;

import battlecode.common.*;

public class RobotPlayer {
	public static void run(RobotController rc) {

        if (rc.getType() == RobotType.ARCHON) {
            try {
                System.out.println(rc.getTeamParts());
                System.out.println("Building started");
                rc.build(Direction.SOUTH_EAST, RobotType.GUARD);
                System.out.println(rc.getTeamParts());
                for (int i=0; i<50; i++) {
                    Clock.yield();
                }
                rc.build(Direction.SOUTH, RobotType.SOLDIER);
                System.out.println(rc.getTeamParts());
            } catch (Exception e) {
                System.out.println("Archon initialization exception: " + e.getMessage());
                e.printStackTrace();
            }
            
            while (true) {
                try {
                    Clock.yield();
                } catch (Exception e) {
                    System.out.println("Archon exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else if (rc.getType() == RobotType.SOLDIER) {
            try {
                
            } catch (Exception e) {
                System.out.println("Soldier initialization exception: " + e.getMessage());
                e.printStackTrace();
            }
            
            while (true) {
                try {
                    MapLocation hqloc = rc.getLocation().add(Direction.EAST);
                    if (rc.canAttackLocation(hqloc) && rc.isWeaponReady()) {
                        rc.attackLocation(hqloc);
                    }
                    Clock.yield();
                } catch (Exception e) {
                    System.out.println("Soldier exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            try {
                
            } catch (Exception e) {
                System.out.println("Other initialization exception: " + e.getMessage());
                e.printStackTrace();
            }
            
            while (true) {
                try {
                    MapLocation hqloc = rc.getLocation().add(Direction.WEST);
                    if (rc.canAttackLocation(hqloc) && rc.isWeaponReady()) {
                        rc.attackLocation(hqloc);
                    }
                    Clock.yield();
                } catch (Exception e) {
                    System.out.println("Other exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
	}
}
