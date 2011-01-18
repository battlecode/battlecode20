package hardplayer.goal;

import hardplayer.Static;

import battlecode.common.RobotInfo;

public class DefenseSpinGoal extends Static implements Goal {

	public int maxPriority() { return FALLBACK; }
	public int priority() { return FALLBACK; }

	public void execute() {
		try {
			RobotInfo closest = closestEnemy();
			if(closest==null)
				motor.setDirection(myRC.getDirection().rotateLeft().rotateLeft());
			else
				motor.setDirection(myLoc.directionTo(closest.location));
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

}
