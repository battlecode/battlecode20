package refplayer.message;

import battlecode.common.Message;

public class SuperMessageStack implements MessageHandler {

	static public final int KEEP_TIME = 3;

	static public int t;
	public Message [][] messages = new Message [][] { new Message [100], new Message[100], new Message[100] };
	public int [] lengths = new int [KEEP_TIME];

	public SuperMessageStack() { }

	public void receivedMessage(Message m) {
		/*
		if(m.ints[0]==messageTypeIShotThat) {
			refplayer.players.BasePlayer.debug_println("I heard that a cannon shot: ");
			refplayer.goal.debug_printInt(m.ints[1]);
		}
		*/
		messages[t][lengths[t]++]=m;
	}

}