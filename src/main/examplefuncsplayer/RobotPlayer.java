package examplefuncsplayer;

import battlecode.common.*;
import java.util.*;

public class RobotPlayer {
	public static void run(RobotController rc) {
        
        if (rc.getType() == RobotType.ARCHON) {
            try {
            
                // #ARCHON #ARCHON #ARCHON #ARCHON #ARCHON #ARCHON #ARCHON #ARCHON #ARCHON #ARCHON #ARCHON #ARCHON #ARCHON
                System.out.println(rc.getTeamParts());
                System.out.println("Building started");
                rc.build(Direction.SOUTH_EAST, RobotType.TURRET);
                System.out.println(rc.getTeamParts());
                for (int i=0; i<50; i++) {
                    if (rc.getRoundNum() < 200 && rc.getRoundNum() % 10 == 1) {
                        System.out.println(" ");
                    }
                    rc.yield();
                }
                System.out.println(rc.getTeamParts());
                rc.build(Direction.SOUTH, RobotType.SOLDIER);
                System.out.println(rc.getTeamParts());
            } catch (Exception e) {
                System.out.println("Archon initialization exception: " + e.getMessage());
                e.printStackTrace();
            }
            
            while (true) {
                try {
                    if (rc.getRoundNum() < 200 && rc.getRoundNum() % 10 == 1) {
                        System.out.println(" ");
                    }
                    rc.yield();
                } catch (Exception e) {
                    System.out.println("Archon exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else if (rc.getType() == RobotType.SOLDIER) {
            // #SOLDIER #SOLDIER #SOLDIER #SOLDIER #SOLDIER #SOLDIER #SOLDIER #SOLDIER #SOLDIER #SOLDIER #SOLDIER
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
                    rc.yield();
                } catch (Exception e) {
                    System.out.println("Soldier exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            // #OTHER #OTHER #OTHER #OTHER #OTHER #OTHER #OTHER #OTHER #OTHER #OTHER #OTHER #OTHER #OTHER #OTHER #OTHER
            try {
                rc.pack();
                System.out.println(rc.getType());
            } catch (Exception e) {
                System.out.println("Other initialization exception: " + e.getMessage());
                e.printStackTrace();
            }
            
            while (true) {
                try {
                    MapLocation hqloc = rc.getLocation().add(Direction.WEST);
                    if (rc.canMove(Direction.SOUTH_EAST) && rc.isCoreReady()) {
                        rc.move(Direction.SOUTH_EAST);
                    }
                    rc.yield();
                } catch (Exception e) {
                    System.out.println("Other exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
	}
}
