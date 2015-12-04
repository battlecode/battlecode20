package examplefuncsplayer;

import battlecode.common.*;
import java.util.*;

public class RobotPlayer {
	public static void run(RobotController rc) {
        
        if (rc.getType() == RobotType.ARCHON) {
            try {
            
                // #ARCHON #ARCHON #ARCHON #ARCHON #ARCHON #ARCHON #ARCHON #ARCHON #ARCHON #ARCHON #ARCHON #ARCHON #ARCHON
                System.out.println(rc.getTeamOre());
                System.out.println("Building started");
                rc.build(Direction.SOUTH_EAST, RobotType.GUARD);
                System.out.println(rc.getTeamOre());
                for (int i=0; i<50; i++) {
                    rc.yield();
                }
                rc.build(Direction.SOUTH, RobotType.SOLDIER);
                System.out.println(rc.getTeamOre());
            } catch (Exception e) {
                System.out.println("Archon initialization exception: " + e.getMessage());
                e.printStackTrace();
            }
            
            while (true) {
                try {
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
                    rc.yield();
                } catch (Exception e) {
                    System.out.println("Other exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
	}
}
