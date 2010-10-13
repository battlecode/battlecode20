package teleportingplayer.goals;

import teleportingplayer.BasePlayer;

public class WanderGoal extends Goal {

	public WanderGoal(BasePlayer bp) {
		super(bp);
	}

	public int getMaxPriority() {
		return WANDER;
	}

	public int getPriority() {
		return WANDER;
	}

	public void tryToAccomplish() {

		try {
			if (myRC.canMove(myRC.getDirection().opposite())) {
				myRC.moveBackward();
			} else {
				myRC.setDirection(myRC.getDirection().rotateRight());
			}
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
		}
	}

}