package team001;

import battlecode.common.*;
import java.util.*;

//author: lygophile
public class RobotPlayer {
	public static void run(RobotController rc) {
        Team myTeam = rc.getTeam();
        Team enemy = myTeam.opponent();
		while(true) {
            try {
                if (rc.isWeaponReady()) {
                    // are there enemies in attack range?
                    int nTowers = rc.senseTowerLocations().length;
                    int attackRadius = rc.getType().attackRadiusSquared;
                    if (nTowers >= 2 && rc.getType() == RobotType.HQ) {
                        attackRadius = GameConstants.HQ_BUFFED_ATTACK_RADIUS_SQUARED;
                    }

                    RobotInfo[] enemies = rc.senseNearbyRobots(attackRadius, enemy);
                    if (enemies.length > 0) {
                        int lowestHealth = 0;
                        for (int i = 0; i < enemies.length; ++i) {
                            if (enemies[i].health < enemies[lowestHealth].health) {
                                lowestHealth = i;
                            }
                        }
                        rc.attackLocation(enemies[lowestHealth].location);
                    }
                }
            } catch (Exception e) {
                System.out.println("EXCEPTION");
                e.printStackTrace();
            }

            rc.yield();
		}
	}
}
