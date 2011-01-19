package hardplayer.goal;

import hardplayer.Static;
import hardplayer.sensor.MineSensor;

import battlecode.common.MapLocation;

public class WanderGoal extends Static implements Goal {

	MapLocation target;

	public WanderGoal() {
		newTarget();
	}
	
	public void newTarget() {
		int height = MineSensor.top - MineSensor.bot + 1;
		int width = MineSensor.right - MineSensor.left + 1;
		target = new MapLocation(rnd.nextInt(width)+MineSensor.left,rnd.nextInt(height)+MineSensor.bot);
	}

	public int maxPriority() {
		return WANDER;
	}

	public int priority() {
		return WANDER;
	}

	public void execute() {
		if(target.x<MineSensor.left||target.x>MineSensor.right||
			target.y<MineSensor.bot||target.y>MineSensor.top||
			myRC.senseTerrainTile(target)!=null||rnd.nextInt(200)==0)
			newTarget();
		myNav.moveToForward(target);

	}

}
