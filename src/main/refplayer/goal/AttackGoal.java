package refplayer.goal;

import battlecode.common.*;

import refplayer.Static;
import refplayer.message.MessageHandler;
import refplayer.message.MessageSender;

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
