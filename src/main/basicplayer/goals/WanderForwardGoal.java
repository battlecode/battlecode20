package basicplayer.goals;

import basicplayer.BasePlayer;

public class WanderForwardGoal extends Goal {

	public WanderForwardGoal(BasePlayer bp) {
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
			if (myRC.canMove(myRC.getDirection())) {
				myRC.moveForward();
			} else {
				myRC.setDirection(myRC.getDirection().rotateRight());
			}
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
		}
	}

}