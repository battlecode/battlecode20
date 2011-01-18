package hardplayer.goal;

import hardplayer.Static;

import battlecode.common.Chassis;
import battlecode.common.RobotLevel;

public class ConstructorAttackGoal extends Static implements Goal {

	public int maxPriority() {
		return CONSTRUCTOR_ATTACK;
	}

	public int priority() {
		int i = enemies.size;
		if(i==0)
			return 0;
		try {
			/*
			while(--i>=0) {
				if(enemyInfos[0].chassis!=Chassis.BUILDING||sensor.senseObjectAtLocation(enemyInfos[0].location,RobotLevel.MINE)==null)
					return 0;
			}
			*/
		} catch(Exception e) {
			debug_stackTrace(e);
			return 0;
		}
		return CONSTRUCTOR_ATTACK;
	}

	public void execute() {}

}
