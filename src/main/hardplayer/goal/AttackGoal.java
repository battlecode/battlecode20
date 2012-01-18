package hardplayer.goal;

import battlecode.common.*;

import hardplayer.Static;
import hardplayer.message.MessageHandler;
import hardplayer.message.MessageSender;

public abstract class AttackGoal extends Static implements MessageHandler, Goal {

	static Message message;

	public AttackGoal() {
		handlers[MessageSender.MSG_ENEMY_UNITS] = this;
	}

	public int maxPriority() { return ATTACK; }

	public abstract int priority();

	public abstract void execute();

	public void receivedMessage(Message m) {
		if(message==null||m.ints[1]-message.ints[1]>1||myLoc.distanceSquaredTo(m.locations[0])<=myLoc.distanceSquaredTo(message.locations[0]))
			message = m;
	}
}
