package hardplayer.component;

import hardplayer.Static;

import battlecode.common.RobotInfo;
import battlecode.common.MapLocation;
import battlecode.common.WeaponController;

public class BlasterAndSMG extends Static implements ComponentAI {

	public void execute() {
		RobotInfo info;
		MapLocation loc;
		boolean [] dead = new boolean [enemies.size];
		int weaponIndex = weapons.length;
		try {
			iterweapons:
			for(WeaponController weapon : weapons) {
				if(weapon.isActive()) continue;
				int i = enemies.size;
				while(--i>=0) {
					if(dead[i]) continue;
					info = enemyInfos[i];
					if(weapon.withinRange(info.location)) {
						weapon.attackSquare(info.location,info.chassis.level);
						dead[i] = sensor.senseObjectAtLocation(info.location,info.chassis.level)==null;
						continue iterweapons;
					}
				}
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

}
