package hardplayer.goal;

import hardplayer.Static;

import battlecode.common.Chassis;
import battlecode.common.RobotLevel;

public class ConstructorAttackGoal extends Static implements Goal {

	public int maxPriority() {
		return CONSTRUCTOR_ATTACK;
	}

	public int priority() {
		if(enemies.size>0)
			return CONSTRUCTOR_ATTACK;
		//else if(debris.size>0)
		//	return ATTACK_DEBRIS;
		else
			return 0;
	}

	public void execute() {}

}
