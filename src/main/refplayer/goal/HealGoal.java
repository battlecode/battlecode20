package refplayer.goal;

import battlecode.common.RobotInfo;

import refplayer.Static;

public class HealGoal extends Static implements Goal {

	static RobotInfo target;

	public int maxPriority() {
		return HEAL;
	}

	public int priority() {
		int i;
		target = null;
		int d, bestd=37;
		RobotInfo info;
		for(i=allies.size;i>=0;i--) {
			info = alliedInfos[i];
			if(info.regen||(info.energon>=info.type.maxEnergon)) continue;
			d=myLoc.distanceSquaredTo(info.location);
			if(d<bestd) {
				target = info;
			}
		}
		if(target!=null) return HEAL;
		else return 0;
	}

	public void execute() {
		myNav.moveToASAP(target.location);
	}

}
