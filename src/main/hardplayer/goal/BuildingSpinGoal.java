package hardplayer.goal;

import hardplayer.Static;

public class BuildingSpinGoal extends Static implements Goal {

	public int maxPriority() { return FALLBACK; }
	public int priority() { return FALLBACK; }

	public void execute() {
		if(enemies.size==0) {
			try {
				motor.setDirection(myRC.getDirection().rotateLeft().rotateLeft());
			} catch(Exception e) {
				debug_stackTrace(e);
			}
		}
	}

}
