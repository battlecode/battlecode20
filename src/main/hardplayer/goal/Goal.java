package hardplayer.goal;

public interface Goal {

	public int maxPriority();

	public int priority();

	public void execute();

	// Archon goals
	static public final int FLEE = 100;
	static public final int MAKE_ARMY = 95;
	static public final int GET_HELP = 90;
	static public final int STAY_TOGETHER_HIGH = 55;
	static public final int ARCHON_FIND_ENEMY = 40;
	static public final int EXPLORE = 30;
	static public final int AGGRO_EXPLORE = 25;

	// Non-archon goals
	static public final int ATTACK = 90;
	static public final int FIND_ENEMY = 85;
	static public final int HEAL = 70;
	static public final int FIND_NODE = 30;
	static public final int SEEK_FLUX_LOW = 20;

}
