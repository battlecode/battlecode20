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
		//System.out.println(motor.roundsUntilIdle());
		try {
			Mine target = findtarget(mines);
			if(target!=null) {
				moveAdjacentTo(target.getLocation());
			}
			else {
				int z = rnd.nextInt()%10;
				if(z==0)
					motor.setDirection(myRC.getDirection().rotateLeft());
				else if(z==1)
					motor.setDirection(myRC.getDirection().rotateRight());
				else
					myNav.moveToForward(myLoc.add(myRC.getDirection(),6));
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

}
