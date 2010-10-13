package refplayer.goals;

import refplayer.BasePlayer;
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

	static public final int ATTACK_SHOOT = 90;
	static public final int TELEPORT_ON_COMMAND = 85;
	static public final int SEEK_ENERGON_PRIORITY = 80;
	static public final int ARMY_RETREAT = 75;
	static public final int GO_SCOUTING = 75;
	static public final int ATTACK_TURN = 74;
	static public final int TURRET_DEPLOY = 73;
	static public final int SEEK_ENERGON_BY_ARCHON = 72;
	static public final int ATTACK_MOVE = 70;
	static public final int STAY_TOGETHER_VERY_HIGH = 70;
	static public final int CAMPING_SHARE_FLUX = 69;
	static public final int CAMPING_WOUT_SPREAD_OUT = 65;
	static public final int FIND_TELEPORTER = 65;
	static public final int TRANSPORT_ENERGON = 60;
	static public final int SPOT_FOR_TURRET = 50;
	static public final int DISTRIBUTE_FLUX_WOUT = 45;
	static public final int WOUT_SPREAD_OUT = 40;
	static public final int DEPLOY = 40;
	static public final int STAY_TOGETHER_HIGH = 40;
	static public final int MAKE_CHAIN = 35;
	static public final int FIND_ENEMY_PRIORITY = 35;
	static public final int DISTRIBUTE_FLUX_ARCHON = 32;
	static public final int EXPLORE_PRIORITY = 30;
	static public final int STAY_TOGETHER_MEDIUM = 20;
	static public final int NEVER = 0;

	static public final int BROADCAST_ENEMY_UNIT_LOCATIONS = 50;
	static public final int BROADCAST_TELEPORT = 30;
	static public final int BROADCAST_GO_IN_THIS_DIRECTION = 20;
	static public final int BROADCAST_SCOUTING_FIND = 15; 
	static public final int BROADCAST_EVIL = 5;

}
