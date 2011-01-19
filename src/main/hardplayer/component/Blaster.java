package hardplayer.component;

import hardplayer.Static;

import battlecode.common.RobotInfo;
import battlecode.common.WeaponController;

public class Blaster extends Static implements ComponentAI {

	WeaponController weapon;

	public Blaster(WeaponController weapon) {
		this.weapon = weapon;
	}

	public void execute() {
		if(weapon.isActive()) return;
		try {
			int i=enemies.size;
			while(--i>=0) {
				RobotInfo info = enemyInfos[i];
				if(weapon.withinRange(info.location)) {
					weapon.attackSquare(info.location,info.chassis.level);
					return;
				}
			}
			i=debris.size;
			while(--i>=0) {
				RobotInfo info = debris.robotInfos[i];
				if(weapon.withinRange(info.location)) {
					weapon.attackSquare(info.location,info.chassis.level);
					return;
				}
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

}
