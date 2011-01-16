package hardplayer.goal;

import hardplayer.Static;
import battlecode.common.*;

public class BuildMineGoal extends Static implements Goal {

	Mine target = null;

	public int maxPriority() { return Goal.BUILD_MINE; }
	public int priority() { return Goal.BUILD_MINE; }

	private Mine findtarget(Mine [] mines) throws GameActionException {
		Mine tmpTarget = null;
		for(Mine mine : mines) {
			if(sensor.senseObjectAtLocation(mine.getLocation(),RobotLevel.ON_GROUND)==null) {
				if(mine==target)
					return target;
				else
					tmpTarget = mine;
			}
		}
		if(tmpTarget!=null)
			target = tmpTarget;
		return tmpTarget;
	}


	public void execute() {
		try {
			Mine target = findtarget(mines);
			if(target!=null) {
				int d = target.getLocation().distanceSquaredTo(myLoc);
				if(d>2)
					myNav.moveToForward(target.getLocation());
				else if(d>0)
					motor.setDirection(myLoc.directionTo(target.getLocation()));
				else
					myNav.moveToForward(target.getLocation().add(Direction.NORTH_EAST));
			}
			else {
				myNav.moveToForward(myLoc.add(myRC.getDirection(),6));
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

}
