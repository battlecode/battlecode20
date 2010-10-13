package hardplayer.goals;

import hardplayer.BasePlayer;
import hardplayer.WoutPlayer;

import battlecode.common.Clock;
import battlecode.common.MapLocation;

public class BroadcastScoutingFindGoal extends Goal {

	public static final int UNSEEN_DELAY = 10;
	public static final int BROADCAST_INTERVAL = 10;

	int lastBroadcast;

	WoutFindEnemyGoal findEnemyGoal;
	WoutPlayer splayer;

	public BroadcastScoutingFindGoal(WoutPlayer sp, WoutFindEnemyGoal g) {
		super(sp);
		splayer = sp;
		findEnemyGoal = g;
	}

	public int getMaxPriority() {
		return BROADCAST_SCOUTING_FIND;
	}

	public int getPriority() {
		if(splayer.scouting&&
		   player.alliedArchons.size>0&&
		   player.atWar)
			return BROADCAST_SCOUTING_FIND;
		else return NEVER;
	}

	public void debug_print() {
		System.out.println(findEnemyGoal.absxsum);
		System.out.println(findEnemyGoal.absysum);
		System.out.println(findEnemyGoal.n);
	}

	public void tryToAccomplish() {
		//debug_print();
		player.mySender.sendScoutSawEnemy(new MapLocation((int)(findEnemyGoal.absxsum/findEnemyGoal.n),(int)(findEnemyGoal.absysum/findEnemyGoal.n)));
		//BasePlayer.debug_println("broadcasting scouting find");
		splayer.scouting=false;
	}
	

}