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
		RobotInfo info;
		if(enemies.size>0)
			info = enemyInfos[0];
		else if(debris.size>0)
			info = debris.robotInfos[0];
		else
			return;
		try {
			weapon.attackSquare(info.location,info.chassis.level);
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

}
