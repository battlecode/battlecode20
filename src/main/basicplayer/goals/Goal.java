package basicplayer.goals;

import basicplayer.BasePlayer;
import battlecode.common.RobotController;

public abstract class Goal
{
	protected RobotController myRC;
	protected BasePlayer player;
	
	public abstract void tryToAccomplish();
	public abstract int getPriority();
	public abstract int getMaxPriority();
	
	public Goal(BasePlayer bp){
		myRC = bp.myRC;
		player = bp;
	}

	static public final int WOUT_SUICIDE = 90;
	static public final int SEEK_ENERGON_PRIORITY = 80;
	static public final int ATTACK_TURN = 74;
	static public final int SEEK_ENERGON_BY_ARCHON = 72;
	static public final int RUN_AWAY = 75;
	static public final int DISTRIBUTE_FLUX = 65;
	static public final int TRANSPORT_ENERGON = 60;
	static public final int MAKE_CHAIN = 50;
	static public final int SPREAD_OUT = 50;
	static public final int WANDER = 30;
	static public final int NEVER = 0;

	static public final int BROADCAST_ENEMY_UNIT_LOCATIONS = 50;
	static public final int BROADCAST_TELEPORT = 30;
	static public final int BROADCAST_GO_IN_THIS_DIRECTION = 20;

}
