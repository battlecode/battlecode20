package hardplayer.goal;

import hardplayer.Static;
import battlecode.common.*;

public class BuildMineGoal extends Static implements Goal {

	Mine target = null;

	public int maxPriority() { return BUILD_MINE; }
	public int priority() {
		try {
			findtarget(mines);
			if(target==null)
				return 0;
			else
				return BUILD_MINE;
		} catch(Exception e) {
			debug_stackTrace(e);
			return 0;
		}
	}

	private void findtarget(Mine [] mines) throws GameActionException {
		Mine tmpTarget = null;
		for(Mine mine : mines) {
			if(sensor.senseObjectAtLocation(mine.getLocation(),RobotLevel.ON_GROUND)==null) {
				if(mine==target)
					return;
				else
					tmpTarget = mine;
			}
			else if(mine==target)
				target = null;
		}
		if(tmpTarget!=null)
			target = tmpTarget;
	}


	public void execute() {
		//System.out.println(motor.roundsUntilIdle());
		try {
			if(target!=null) {
				moveAdjacentTo(target.getLocation());
			}
			else {
				debug_println("We shouldn't be here!");
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
