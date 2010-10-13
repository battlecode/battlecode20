/*
package basicplayer.goals;

import basicplayer.BasePlayer;

public class ChainerAttackGoal extends Goal {

	MapLocation target;

	public ChainerAttackGoal(BasePlayer bp) {
		super(bp);
	}

	public int getMaxPriority() {
		return CHAINER_ATTACK;
	}

	public int getPriority() {
		
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
*/